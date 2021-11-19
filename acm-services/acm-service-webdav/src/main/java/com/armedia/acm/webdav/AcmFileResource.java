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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.camel.component.cmis.CamelCMISConstants;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.armedia.acm.camelcontext.arkcase.cmis.ArkCaseCMISActions;
import com.armedia.acm.camelcontext.arkcase.cmis.ArkCaseCMISConstants;
import com.armedia.acm.camelcontext.exception.ArkCaseFileRepositoryException;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.web.api.MDCConstants;

import io.milton.common.ContentTypeUtils;
import io.milton.common.RangeUtils;
import io.milton.http.Auth;
import io.milton.http.Range;
import io.milton.http.exceptions.BadRequestException;
import io.milton.http.exceptions.ConflictException;
import io.milton.http.exceptions.NotAuthorizedException;
import io.milton.http.exceptions.NotFoundException;
import io.milton.http.http11.auth.DigestResponse;
import io.milton.resource.CollectionResource;
import io.milton.resource.DigestResource;
import io.milton.resource.GetableResource;
import io.milton.resource.MoveableResource;
import io.milton.resource.PropFindableResource;
import io.milton.resource.ReplaceableResource;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity
 */
public class AcmFileResource extends AcmFileSystemResource
        implements PropFindableResource, ReplaceableResource, DigestResource, GetableResource, MoveableResource
{
    private static final Logger LOGGER = LogManager.getLogger(AcmFileResource.class);

    private EcmFile acmFile;
    private String fileType;
    private String lockType;

    public AcmFileResource(String host, EcmFile acmFile, String fileType, String lockType, String userId, String containerObjectType,
            String containerObjectId,
            AcmFileSystemResourceFactory resourceFactory)
    {
        super(host, resourceFactory, userId, containerObjectType, containerObjectId);
        this.acmFile = acmFile;
        this.fileType = fileType;
        this.lockType = lockType;
    }

    public Long getId()
    {
        return acmFile.getId();
    }

    public Long getParentId()
    {
        return acmFile.getFolder().getId();
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
        return (acmFile.getId() + "_" + acmFile.getFileName() + "_" + acmFile.getCreator()).hashCode() + "";
    }

    @Override
    public String getName()
    {
        return acmFile.getFileName() + acmFile.getFileExtension();
    }

    @Override
    public Date getModifiedDate()
    {
        return acmFile.getModified();
    }

    // PropFindableResource interface methods implementation

    @Override
    public Date getCreateDate()
    {
        return acmFile.getCreated();
    }

    // GetableResource interface methods implementation

    @Override
    public void sendContent(OutputStream out, Range range, Map<String, String> params, String contentType)
            throws IOException, NotAuthorizedException, BadRequestException, NotFoundException
    {

        try
        {
            Map<String, Object> messageProps = new HashMap<>();
            messageProps.put(ArkCaseCMISConstants.CMIS_REPOSITORY_ID,
                    acmFile.getCmisRepositoryId() == null ? ArkCaseCMISConstants.DEFAULT_CMIS_REPOSITORY_ID
                            : acmFile.getCmisRepositoryId());
            messageProps.put(CamelCMISConstants.CMIS_OBJECT_ID, getResourceFactory().getCmisFileId(acmFile));
            messageProps.put(MDCConstants.EVENT_MDC_REQUEST_ALFRESCO_USER_ID_KEY,
                    MDC.get(MDCConstants.EVENT_MDC_REQUEST_ALFRESCO_USER_ID_KEY));

            LOGGER.info("User {} is sending object id {} with Content Type {} in repository {}",
                    messageProps.get(MDCConstants.EVENT_MDC_REQUEST_ALFRESCO_USER_ID_KEY),
                    messageProps.get(CamelCMISConstants.CMIS_OBJECT_ID),
                    contentType,
                    messageProps.get(ArkCaseCMISConstants.CMIS_REPOSITORY_ID));
            Object result = getResourceFactory().getCamelContextManager().send(ArkCaseCMISActions.DOWNLOAD_DOCUMENT, messageProps);

            if (result instanceof ContentStream)
            {
                ContentStream filePayload = (ContentStream) result;
                try (InputStream fileIs = filePayload.getStream())
                {
                    if (range != null)
                    {
                        RangeUtils.writeRange(fileIs, range, out);
                    }
                    else
                    {
                        IOUtils.copy(fileIs, out);
                    }
                    out.flush();
                }
            }
            else
            {
                throw new NotFoundException("Could not locate content");
            }

        }
        catch (ArkCaseFileRepositoryException e)
        {
            LOGGER.error("Error while downloading file via Camel, reason: [{}]", e.getMessage(), e);
        }
    }
    // GetableResource interface methods implementation

    @Override
    public Long getContentLength()
    {
        // TODO We should be able to return the size of the file
        return null;
    }

    @Override
    public Long getMaxAgeSeconds(Auth auth)
    {
        return getResourceFactory().getMaxAgeSeconds();
    }

    @Override
    public String getContentType(String accepts)
    {
        String mime = acmFile.getFileActiveVersionMimeType();
        return ContentTypeUtils.findAcceptableContentType(mime, accepts);
    }

    // ReplaceableResource interface methods implementation

    @Override
    public void replaceContent(InputStream in, Long length) throws BadRequestException, ConflictException, NotAuthorizedException
    {
        if (length != 0)
        {
            File file = null;
            try
            {
                file = File.createTempFile("arkcase-webdav-file-transaction-", null);
                FileUtils.copyInputStreamToFile(in, file);
                Authentication auth = getResourceFactory().getSecurityManager().getAuthenticationForTicket(getUserId());
                LOGGER.info("Authenticated user {} replacing file content for file {}",
                        auth.getName(), acmFile.getFileId());
                SecurityContextHolder.getContext().setAuthentication(auth);
                getResourceFactory().getAuditPropertyEntityAdapter().setUserId(auth.getName());
                getResourceFactory().getEcmFileTransaction().updateFileTransactionEventAware(auth, acmFile, file);
            }
            catch (ArkCaseFileRepositoryException | IOException e)
            {
                LOGGER.error("Error while uploading file via Camel.", e);
            }
            finally
            {
                FileUtils.deleteQuietly(file);
            }
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
     * No-op method implemented to prevent Milton from complaining that this resource is incompatible
     * with move requests.
     *
     * Milton calls this method since the client application is trying to backup the original resource
     * by moving it to a temp file.  After this, the client application would move a temp file (with the
     * new file contents) onto the original filename.
     *
     * For ArkCase files, we don't follow the pattern.  We will take no action here, leaving the original
     * resource unchanged.  When Milton moves the temp file onto the original filename, then we version the
     * ArkCase file (this is implemented in AcmTempFileResource).
     *
     * @param collectionResource
     * @param s
     * @throws ConflictException
     * @throws NotAuthorizedException
     * @throws BadRequestException
     * @see AcmTempFileResource
     */
    @Override public void moveTo(CollectionResource collectionResource, String s)
            throws ConflictException, NotAuthorizedException, BadRequestException
    {
        LOGGER.info("Got a request to move [{}] to collection [{}], String [{}]", acmFile.getFileName(), collectionResource.getName(), s);
    }
}
