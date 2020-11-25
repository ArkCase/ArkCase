package com.armedia.acm.webdav;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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

import com.armedia.acm.camelcontext.arkcase.cmis.ArkCaseCMISActions;
import com.armedia.acm.camelcontext.arkcase.cmis.ArkCaseCMISConstants;
import com.armedia.acm.camelcontext.exception.ArkCaseFileRepositoryException;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.web.api.MDCConstants;

import org.apache.camel.component.cmis.CamelCMISConstants;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

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

    public AcmFileResource(String host, EcmFile acmFile, String fileType, String lockType, String acmTicket,
            AcmFileSystemResourceFactory resourceFactory)
    {
        super(host, resourceFactory);
        this.acmFile = acmFile;
        this.fileType = fileType;
        this.lockType = lockType;
        this.acmTicket = acmTicket;
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
            messageProps.put(ArkCaseCMISConstants.CMIS_REPOSITORY_ID, acmFile.getCmisRepositoryId() == null ? ArkCaseCMISConstants.DEFAULT_CMIS_REPOSITORY_ID : acmFile.getCmisRepositoryId());
            messageProps.put(CamelCMISConstants.CMIS_OBJECT_ID, getResourceFactory().getCmisFileId(acmFile));
            messageProps.put(MDCConstants.EVENT_MDC_REQUEST_ALFRESCO_USER_ID_KEY, MDC.get(MDCConstants.EVENT_MDC_REQUEST_ALFRESCO_USER_ID_KEY));
            
            LOGGER.info("User {} is sending object id {} in repository {}", 
                messageProps.get(MDCConstants.EVENT_MDC_REQUEST_ALFRESCO_USER_ID_KEY),
                messageProps.get(CamelCMISConstants.CMIS_OBJECT_ID),
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
            Authentication auth = getResourceFactory().getSecurityManager().getAuthenticationForTicket(acmTicket);
            LOGGER.info("Authenticated user {} replacing file content for file {}", 
                auth.getName(), acmFile.getFileId());
            SecurityContextHolder.getContext().setAuthentication(auth);
            getResourceFactory().getEcmFileTransaction().updateFileTransactionEventAware(auth, acmFile, in);
        }
        catch (ArkCaseFileRepositoryException | IOException e)
        {
            LOGGER.error("Error while uploading file via Camel.", e);
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
