package com.armedia.acm.plugins.ecm.utils;

/*-
 * #%L
 * ACM Service: Enterprise Content Management
 * %%
 * Copyright (C) 2014 - 2019 ArkCase LLC
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

import com.armedia.acm.auth.AcmAuthenticationDetails;
import com.armedia.acm.camelcontext.arkcase.cmis.ArkCaseCMISActions;
import com.armedia.acm.camelcontext.arkcase.cmis.ArkCaseCMISConstants;
import com.armedia.acm.camelcontext.context.CamelContextManager;
import com.armedia.acm.camelcontext.exception.ArkCaseFileRepositoryException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.web.api.MDCConstants;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.camel.component.cmis.CamelCMISConstants;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com>
 */
public class EcmFileCamelUtils
{
    private static final Logger log = LogManager.getLogger(EcmFileCamelUtils.class);

    private CamelContextManager camelContextManager;

    private CmisConfigUtils cmisConfigUtils;

    public static String getCmisUser()
    {
        try
        {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null)
            {
                return ((AcmAuthenticationDetails) authentication.getDetails()).getCmisUserId();
            }
        }
        catch (Exception e)
        {
            log.info("There is no authenticated user.");
        }
        return "";
    }

    /**
     * Removes a file from the Alfresco content repository
     *
     * @param ecmFile
     *            - metadata for the file to delete
     * @param cmisFileId
     *            - cmis id associated with the document which will be removed
     * @throws ArkCaseFileRepositoryException
     *             if the camel call to delete the document from the repository fails
     */
    public void deleteFile(EcmFile ecmFile, String cmisFileId) throws ArkCaseFileRepositoryException
    {
        // This is the request payload for camel including the unique cmis id for the document to delete
        Map<String, Object> messageProps = new HashMap<>();
        messageProps.put(EcmFileConstants.CMIS_DOCUMENT_ID, cmisFileId);
        messageProps.put(EcmFileConstants.CMIS_REPOSITORY_ID, ArkCaseCMISConstants.CAMEL_CMIS_DEFAULT_REPO_ID);
        messageProps.put(MDCConstants.EVENT_MDC_REQUEST_ALFRESCO_USER_ID_KEY, EcmFileCamelUtils.getCmisUser());

        // Invokes the camel route to delete the file contents from the repository
        log.debug("Invoking delete Camel route to delete document with cmis id: [{}] using [{}]", cmisFileId,
                ArkCaseCMISActions.DELETE_DOCUMENT.getQueueName());
        getCamelContextManager().send(ArkCaseCMISActions.DELETE_DOCUMENT, messageProps);
    }

    /**
     * Adds a new file to the repository using the createDocument camel route.
     *
     * @param newEcmFile
     *            - contains metadata for the file whose contents will be added to the repository
     * @param cmisFolderId
     *            - cmis id of the folder in which the new file will be added
     * @param fileInputStream
     *            - binary content data for the new file version
     * @throws AcmUserActionFailedException
     *             if the Camel call to save the file to the repository fails
     * @returns Cmis Document object for the new repository document
     */
    public Document addFile(EcmFile newEcmFile, String cmisFolderId, InputStream fileInputStream)
            throws AcmUserActionFailedException
    {
        newEcmFile.setFileName(newEcmFile.getFileName().trim());
        // Camel upload request payload setup (specifies the folder in which to upload the supplied content stream)
        Map<String, Object> messageProps = new HashMap<>();
        messageProps.put("cmisFolderId", cmisFolderId);
        messageProps.put("inputStream", fileInputStream);
        messageProps.put(EcmFileConstants.CMIS_REPOSITORY_ID, ArkCaseCMISConstants.CAMEL_CMIS_DEFAULT_REPO_ID);
        messageProps.put(EcmFileConstants.VERSIONING_STATE, getCamelContextManager().getRepositoryConfigs()
                .get(ArkCaseCMISConstants.CAMEL_CMIS_DEFAULT_REPO_ID).getCmisVersioningState());
        messageProps.put(PropertyIds.NAME, newEcmFile.getFileName());
        messageProps.put(PropertyIds.CONTENT_STREAM_MIME_TYPE, newEcmFile.getFileActiveVersionMimeType());
        messageProps.put(MDCConstants.EVENT_MDC_REQUEST_ALFRESCO_USER_ID_KEY, EcmFileCamelUtils.getCmisUser());

        log.debug("Invoking Camel add document route");
        try
        {
            return (Document) getCamelContextManager().send(ArkCaseCMISActions.CREATE_DOCUMENT, messageProps);
        }
        catch (ArkCaseFileRepositoryException e)
        {
            log.error("Could not create document {} ", e.getMessage(), e);
            throw new AcmUserActionFailedException(EcmFileConstants.USER_ACTION_UPLOAD_FILE, EcmFileConstants.OBJECT_FILE_TYPE, null,
                    "Could not create document", e);
        }
    }

    /**
     * Updates the contents of an existing repository item using the camel update document route.
     *
     * @param newEcmFile
     *            - metadata for the new file which will replace the old version
     * @param originalFile
     *            - metadata for the old file whose contents will be replaced
     * @param fileInputStream
     *            - the binary data content which will be written to the repository
     * @throws ArkCaseFileRepositoryException
     *             if the camel call to replace the file contents in the repository fails
     * @returns Cmis Document object for the updated repository document
     */
    public Document updateFile(EcmFile newEcmFile, EcmFile originalFile, InputStream fileInputStream) throws ArkCaseFileRepositoryException
    {
        Map<String, Object> messageProps = new HashMap<>();
        messageProps.put(EcmFileConstants.CMIS_DOCUMENT_ID, originalFile.getVersionSeriesId());
        messageProps.put("mimeType", originalFile.getFileActiveVersionMimeType());
        messageProps.put("inputStream", fileInputStream);
        messageProps.put("checkinComment", "");
        messageProps.put(EcmFileConstants.CMIS_REPOSITORY_ID, ArkCaseCMISConstants.CAMEL_CMIS_DEFAULT_REPO_ID);
        messageProps.put(MDCConstants.EVENT_MDC_REQUEST_ALFRESCO_USER_ID_KEY, EcmFileCamelUtils.getCmisUser());

        return (Document) getCamelContextManager().send(ArkCaseCMISActions.UPDATE_DOCUMENT, messageProps);
    }

    /**
     * Downloads the contents of the specified document from the repository
     *
     * @param cmisRepositoryId
     *            - cmis repository id of the document to download
     * @param cmisDocumentId
     *            - cmis id of the document to download
     * @return InputStream for the document contents
     */
    public InputStream downloadFile(String cmisRepositoryId, String cmisDocumentId)
    {
        InputStream fileContentStream = null;
        try
        {
            log.debug("downloading document using download document route");
            Map<String, Object> messageProps = new HashMap<>();
            messageProps.put(EcmFileConstants.CMIS_REPOSITORY_ID, ArkCaseCMISConstants.CAMEL_CMIS_DEFAULT_REPO_ID);
            messageProps.put(MDCConstants.EVENT_MDC_REQUEST_ALFRESCO_USER_ID_KEY, EcmFileCamelUtils.getCmisUser());
            messageProps.put(CamelCMISConstants.CMIS_OBJECT_ID, cmisDocumentId);
            ContentStream result = (ContentStream) getCamelContextManager().send(ArkCaseCMISActions.DOWNLOAD_DOCUMENT, messageProps);

            fileContentStream = result.getStream();
        }
        catch (Exception e)
        {
            log.error("Failed to get document: {}", e.getMessage(), e);
        }
        return fileContentStream;
    }

    public CamelContextManager getCamelContextManager()
    {
        return camelContextManager;
    }

    public void setCamelContextManager(CamelContextManager camelContextManager)
    {
        this.camelContextManager = camelContextManager;
    }

    public CmisConfigUtils getCmisConfigUtils()
    {
        return cmisConfigUtils;
    }

    public void setCmisConfigUtils(CmisConfigUtils cmisConfigUtils)
    {
        this.cmisConfigUtils = cmisConfigUtils;
    }
}
