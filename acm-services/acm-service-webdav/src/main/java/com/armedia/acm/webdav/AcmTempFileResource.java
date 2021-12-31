package com.armedia.acm.webdav;

/*-
 * #%L
 * ACM Service: WebDAV Integration Library
 * %%
 * Copyright (C) 2014 - 2021 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import com.armedia.acm.camelcontext.exception.ArkCaseFileRepositoryException;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import io.milton.common.ContentTypeUtils;
import io.milton.http.Auth;
import io.milton.http.Range;
import io.milton.http.exceptions.BadRequestException;
import io.milton.http.exceptions.ConflictException;
import io.milton.http.exceptions.NotAuthorizedException;
import io.milton.http.exceptions.NotFoundException;
import io.milton.http.http11.auth.DigestResponse;
import io.milton.resource.CollectionResource;
import io.milton.resource.DeletableResource;
import io.milton.resource.DigestResource;
import io.milton.resource.GetableResource;
import io.milton.resource.MoveableResource;
import io.milton.resource.PropFindableResource;
import io.milton.resource.ReplaceableResource;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Map;

/**
 * WebDAV .tmp file handling.
 *
 * WebDAV clients use .tmp files for atomic file handling on normal (block device based) file systems.  To avoid
 * possibly corrupting the target file, they first write new content to a temp file, then move the target file
 * to another temp file, then move this temp file onto the target file.  That way, if any step fails, the
 * original file is still there, and has not been corrupted by a failed write.
 *
 * ArkCase creates AcmTempFileResource instances when a WebDAV client asks to create a .tmp file.  The
 * instances are backed by regular operating system files (not ArkCase-stored ECM files), stored in the
 * operating system temp folder.  This class writes real files in the operating system temp folder.
 * When the WebDAV client asks to move the .tmp file onto the original file, then this class calls the
 * ArkCase file service to version the ArkCase ECM file.
 *
 * After this move is complete (meaning that the ArkCase ECM file has been versioned), the WebDAV client
 * will ask to delete this .tmp file, thus avoiding orphaned / unnecessary files in the temp folder.
 */
public class AcmTempFileResource extends AcmFileSystemResource
        implements PropFindableResource, ReplaceableResource, DigestResource, GetableResource, MoveableResource,
        DeletableResource
{
    private static final Logger LOGGER = LogManager.getLogger(AcmTempFileResource.class);

    private String fileType;
    private String lockType;
    private String tempFilename;
    private Long targetFileId;

    public AcmTempFileResource(String tempFilename, String host, Long targetFileId, String fileType, String lockType, String userId, String containerObjectType,
            String containerObjectId,
            AcmFileSystemResourceFactory resourceFactory)
    {
        super(host, resourceFactory, userId, containerObjectType, containerObjectId);
        this.fileType = fileType;
        this.lockType = lockType;
        this.tempFilename = tempFilename;
        this.targetFileId = targetFileId;
    }

    public Long getId()
    {
        File tempFile = new File(System.getProperty("java.io.tmpdir"), tempFilename);
        if ( tempFile.exists() )
        {
            return (long) tempFilename.hashCode();
        }
        else {
            return null;
        }

    }

    public Long getParentId()
    {
        return getId();
    }

    public String getFileType()
    {
        return fileType;
    }

    public String getLockType()
    {
        return lockType;
    }

    // Resource interface methods implementation
    @Override
    public String getUniqueId()
    {
        return tempFilename;
    }

    @Override
    public String getName()
    {
        return tempFilename;
    }

    @Override
    public Date getModifiedDate()
    {
        File tempFile = new File(System.getProperty("java.io.tmpdir"), tempFilename);
        if ( tempFile.exists() )
        {
            return new Date(tempFile.lastModified());
        }
        else {
            return null;
        }
    }

    // PropFindableResource interface methods implementation

    @Override
    public Date getCreateDate()
    {
        return getModifiedDate();
    }

    @Override
    public void sendContent(OutputStream out, Range range, Map<String, String> params, String contentType)
            throws IOException, NotAuthorizedException, BadRequestException, NotFoundException
    {
        LOGGER.info("sendContent for filename [{}]", tempFilename);
        File tempFile = new File(System.getProperty("java.io.tmpdir"), tempFilename);
        if ( tempFile.exists() ) {
            try ( InputStream is = new FileInputStream(tempFile)) {
                IOUtils.copy(is, out);
            }
        }

    }

    @Override
    public Long getContentLength()
    {
        File tempFile = new File(System.getProperty("java.io.tmpdir"), tempFilename);
        if ( tempFile.exists() ) {
            return tempFile.length();
        }
        else {
            return 0L;
        }
    }

    @Override
    public Long getMaxAgeSeconds(Auth auth)
    {
        return getResourceFactory().getMaxAgeSeconds();
    }

    /**
     * The content type of the original ArkCase ECM file ... we will assume that WebDAV will write
     * the same format as the file that was originally requested.
     * @param accepts this must be the "Accepts" header sent by the browser?  Not sure.
     * @return content type of the original ArkCase file, or "application/octet-stream" if we didn't find
     * an ArkCase file.
     */
    @Override
    public String getContentType(String accepts)
    {
        EcmFile arkcaseFile = getResourceFactory().getFileDao().find(targetFileId);
        if ( arkcaseFile != null )
        {
            String mime = arkcaseFile.getFileActiveVersionMimeType();
            return ContentTypeUtils.findAcceptableContentType(mime, accepts);
        }

        // we should never reach here since we should always be starting from some ArkCase file,
        // and then the WebDAV client should have created a .tmp file in order to store the updated
        // contents of the ArkCase file.  But just in case we return a default type.
        return  "application/octet-stream";
    }

    /**
     * Write new content to this .tmp file.
     *
     * The application first creates the temp file, then writes content to it (this method), then
     * moves the temp file onto the original file (moveTo method).  So all we have to do here is
     * write the contents into the temp file.  We don't want to version the ArkCase file here; that only
     * happens when the application moves the temp file onto the original file.
     *
     * @param in The content to be written.
     * @param length Length of the content
     * @throws BadRequestException When we can't create or write to the temp file (should happen rarely, if ever)
     * @throws ConflictException Never thrown
     * @throws NotAuthorizedException Never thrown
     */
    @Override
    public void replaceContent(InputStream in, Long length) throws BadRequestException, ConflictException, NotAuthorizedException
    {
        LOGGER.info("Got request to replace temp file [{}] with length of [{}]", tempFilename, length);
        File tempFile = new File(System.getProperty("java.io.tmpdir"), tempFilename);
        if ( !tempFile.exists() ) {
            try
            {
                tempFile.createNewFile();
                LOGGER.info("Created temp file [{}]", tempFilename);
            }
            catch (IOException e)
            {
                LOGGER.error("Could not create temp file [{}]", tempFilename);
                throw new BadRequestException("Could not create temp file", e);
            }
        }
        try ( OutputStream os = new FileOutputStream(tempFile)) {
            IOUtils.copy(in, os);
            LOGGER.info("Wrote contents of temp file [{}]", tempFilename);
        }
        catch (IOException e) {
            LOGGER.error("Could not write temp file contents for [{}]", tempFilename, e);
            throw new BadRequestException("Could not write temp file contents", e);
        }

    }

    // DigestResource interface methods implementation

    @Override
    public Object authenticate(DigestResponse digestRequest)
    {
        return getResourceFactory().getSecurityManager().authenticate(digestRequest);
    }

    @Override
    public boolean isDigestAllowed()
    {
        return getResourceFactory().getSecurityManager().isDigestAllowed();
    }

    /**
     * Version the original ArkCase content file based on this .tmp file.
     *
     * When a .tmp file is moved to a regular file e.g. PDF, this really means that the regular file
     * should be updated with the contents of the .tmp file.  In ArkCase this means that we should create
     * a new version of the regular file.
     *
     * @param collectionResource Represents the target folder; not useful in ArkCase.
     * @param tempName Name of the .tmp file; we already have a field with this value (tempFilename)
     * @throws ConflictException TODO We should throw this when the current ArkCase version is higher
     * than the version we started with.
     * @throws NotAuthorizedException TODO we should throw this when the user isn't authorized to update the
     * target file ... but it's not clear when this would ever happen in our use case.
     * @throws BadRequestException if the temp file (i.e. the source file) doesn't exist or is empty; or if the
     * target ArkCase file could not be found; or if we couldn't version the ArkCase file for some reason.
     */
    @Override public void moveTo(CollectionResource collectionResource, String tempName)
            throws ConflictException, NotAuthorizedException, BadRequestException
    {

        LOGGER.info("Got a request to move [{}] to collection [{}], filename [{}], ArkCase file id [{}]",
                tempFilename, collectionResource.getName(), tempName, targetFileId);

        File tempFile = new File(System.getProperty("java.io.tmpdir"), tempFilename);
        if ( !tempFile.exists() || !tempFile.isFile() || tempFile.length() == 0 )
        {
            throw new BadRequestException("Source file does not exist, is not a file, or is empty: " +
                    tempFilename);
        }

        EcmFile arkcaseFile = getResourceFactory().getFileDao().find(targetFileId);
        if ( arkcaseFile == null )
        {
            throw new BadRequestException("The ArkCase file does not exist; could not find file with id: " +
                    targetFileId);
        }

        Authentication auth = getResourceFactory().getSecurityManager().getAuthenticationForTicket(getUserId());
        LOGGER.info("Authenticated user [{}] uploading temp file [{}] for file [{}]",
                auth.getName(), tempFilename, targetFileId);
        SecurityContextHolder.getContext().setAuthentication(auth);
        getResourceFactory().getAuditPropertyEntityAdapter().setUserId(auth.getName());

        try
        {
            getResourceFactory().getEcmFileTransaction().updateFileTransactionEventAware(auth, arkcaseFile, tempFile);
        }
        catch (ArkCaseFileRepositoryException | IOException e)
        {
            LOGGER.error("Could not upload temp file content for file [{}]: [{}]", tempFilename, e.getMessage(), e);
            throw new BadRequestException("Could not create new version of this file", e);
        }


    }

    /**
     * Remove the .tmp file since it is not needed any more.
     *
     * @throws NotAuthorizedException Never thrown
     * @throws ConflictException Never thrown
     * @throws BadRequestException Never thrown
     */
    @Override public void delete() throws NotAuthorizedException, ConflictException, BadRequestException
    {
        LOGGER.debug("Deleting temp file [{}]", tempFilename);
        File tempFile = new File(System.getProperty("java.io.tmpdir"), tempFilename);
        FileUtils.deleteQuietly(tempFile);
    }

    public Long getTargetFileId()
    {
        return targetFileId;
    }


}
