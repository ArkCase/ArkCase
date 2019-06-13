package com.armedia.acm.webdav;

/*-
 * #%L
 * ACM Service: WebDAV Integration Library
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
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

import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.plugins.ecm.utils.CmisConfigUtils;

import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.commons.io.IOUtils;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.milton.common.ContentTypeUtils;
import io.milton.common.RangeUtils;
import io.milton.http.Range;
import io.milton.http.exceptions.BadRequestException;
import io.milton.http.exceptions.ConflictException;
import io.milton.http.exceptions.NotAuthorizedException;
import io.milton.http.exceptions.NotFoundException;
import io.milton.http.http11.auth.DigestResponse;
import io.milton.resource.DigestResource;
import io.milton.resource.PropFindableResource;
import io.milton.resource.ReplaceableResource;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity
 */
public class AcmFileResource extends AcmFileSystemResource implements PropFindableResource, ReplaceableResource, DigestResource
{
    private static final Logger LOGGER = LogManager.getLogger(AcmFileResource.class);

    private EcmFile acmFile;
    private String fileType;
    private String lockType;
    private String acmTicket;
    private CmisConfigUtils cmisConfigUtils;

    public AcmFileResource(String host, EcmFile acmFile, String fileType, String lockType, String acmTicket,
            AcmFileSystemResourceFactory resourceFactory, CmisConfigUtils cmisConfigUtils)
    {
        super(host, resourceFactory);
        this.acmFile = acmFile;
        this.fileType = fileType;
        this.lockType = lockType;
        this.acmTicket = acmTicket;
        this.cmisConfigUtils = cmisConfigUtils;
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

    public String getAcmTicket()
    {
        return acmTicket;
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
        return acmFile.getFileName();
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
            messageProps.put(EcmFileConstants.CONFIGURATION_REFERENCE, cmisConfigUtils.getCmisConfiguration(acmFile.getCmisRepositoryId()));

            MuleMessage downloadedFile = getResourceFactory().getMuleContextManager().send("vm://downloadFileFlow.in",
                    getResourceFactory().getCmisFileId(acmFile), messageProps);
            if (downloadedFile.getPayload() instanceof ContentStream)
            {
                ContentStream filePayload = (ContentStream) downloadedFile.getPayload();
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
        catch (MuleException e)
        {
            LOGGER.error("Error while downloading file via Mule.", e);
        }
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
        try
        {
            getResourceFactory().getEcmFileTransaction().updateFileTransactionEventAware(
                    getResourceFactory().getSecurityManager().getAuthenticationForTicket(acmTicket), acmFile, in);
        }
        catch (MuleException | IOException e)
        {
            LOGGER.error("Error while uploading file via Mule.", e);
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
}
