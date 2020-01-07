package com.armedia.acm.plugins.ecm.utils;

/*-
 * #%L
 * ACM Service: Enterprise Content Management
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

import com.armedia.acm.muletools.mulecontextmanager.MuleContextManager;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.mule.api.ExceptionPayload;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by joseph.mcgrady on 9/17/2015.
 */
public class EcmFileMuleUtils
{
    private transient final Logger log = LogManager.getLogger(getClass());

    private MuleContextManager muleContextManager;

    private CmisConfigUtils cmisConfigUtils;

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
            log.debug("downloading document using vm://downloadFileFlow.in mule flow");
            Map<String, Object> properties = new HashMap<>();
            properties.put(EcmFileConstants.CONFIGURATION_REFERENCE, cmisConfigUtils.getCmisConfiguration(cmisRepositoryId));
            MuleMessage downloadResponse = getMuleContextManager().send("vm://downloadFileFlow.in", cmisDocumentId, properties);
            ContentStream contentStream = (ContentStream) downloadResponse.getPayload();
            fileContentStream = contentStream.getStream();
        }
        catch (Exception e)
        {
            log.error("Failed to get document: {}", e.getMessage(), e);
        }
        return fileContentStream;
    }

    /**
     * Adds a new file to the repository using the addFile mule flow.
     *
     * @param newEcmFile
     *            - contains metadata for the file whose contents will be added to the repository
     * @param cmisFolderId
     *            - cmis id of the folder in which the new file will be added
     * @param fileInputStream
     *            - binary content data for the new file version
     * @throws MuleException
     *             if the mule call to save the file to the repository fails
     * @returns Cmis Document object for the new repository document
     */
    public Document addFile(EcmFile newEcmFile, String cmisFolderId, InputStream fileInputStream) throws MuleException
    {
        // Mule does not support file names with trailing space(s)
        newEcmFile.setFileName(newEcmFile.getFileName().trim());
        // Mule upload request payload setup (specifies the folder in which to upload the supplied content stream)
        Map<String, Object> messageProps = new HashMap<>();
        messageProps.put("cmisFolderId", cmisFolderId);
        messageProps.put("inputStream", fileInputStream);
        messageProps.put(EcmFileConstants.CONFIGURATION_REFERENCE, cmisConfigUtils.getCmisConfiguration(newEcmFile.getCmisRepositoryId()));
        messageProps.put(EcmFileConstants.VERSIONING_STATE, cmisConfigUtils.getVersioningState(newEcmFile.getCmisRepositoryId()));

        log.debug("invoking the mule add file flow");
        MuleMessage received = getMuleContextManager().send("vm://addFile.in", newEcmFile, messageProps);
        MuleException e = received.getInboundProperty("saveException");
        if (e != null)
        {
            throw e;
        }

        // Extracts CMIS document data from the Mule response
        return received.getPayload(Document.class);
    }

    /**
     * Updates the contents of an existing repository item using the mule updateFile flow.
     *
     * @param newEcmFile
     *            - metadata for the new file which will replace the old version
     * @param originalFile
     *            - metadata for the old file whose contents will be replaced
     * @param fileInputStream
     *            - the binary data content which will be written to the repository
     * @throws MuleException
     *             if the mule call to replace the file contents in the repository fails
     * @returns Cmis Document object for the updated repository document
     */
    public Document updateFile(EcmFile newEcmFile, EcmFile originalFile, InputStream fileInputStream) throws MuleException
    {
        // Assembles the mule payload metadata for the file to update and the new content stream
        Map<String, Object> messageProps = new HashMap<>();
        messageProps.put("ecmFileId", originalFile.getVersionSeriesId());
        messageProps.put("fileName", originalFile.getFileName());
        messageProps.put("mimeType", originalFile.getFileActiveVersionMimeType());
        messageProps.put("inputStream", fileInputStream);
        messageProps.put(EcmFileConstants.CONFIGURATION_REFERENCE, cmisConfigUtils.getCmisConfiguration(newEcmFile.getCmisRepositoryId()));

        // Uses Mule to transfer the updated file contents to the Alfresco content repository
        log.debug("invoking the mule replace file flow");
        MuleMessage received = getMuleContextManager().send("vm://updateFile.in", newEcmFile, messageProps);
        MuleException e = received.getInboundProperty("updateException");
        if (e != null)
        {
            throw e;
        }

        // Extracts CMIS document data from the Mule response
        return received.getPayload(Document.class);
    }

    /**
     * Removes a file from the Alfresco content repository
     *
     * @param ecmFile
     *            - metadata for the file to delete
     * @param cmisFileId
     *            - cmis id associated with the document which will be removed
     * @throws Exception
     *             if the mule call to delete the document from the repository fails
     */
    public void deleteFile(EcmFile ecmFile, String cmisFileId) throws Exception
    {
        // This is the request payload for mule including the unique cmis id for the document to delete
        Map<String, Object> messageProps = new HashMap<>();
        messageProps.put("ecmFileId", cmisFileId);
        messageProps.put(EcmFileConstants.CONFIGURATION_REFERENCE, cmisConfigUtils.getCmisConfiguration(ecmFile.getCmisRepositoryId()));

        // Invokes the mule flow to delete the file contents from the repository
        log.debug("rolling back file upload for cmis id: " + cmisFileId + " using vm://deleteFile.in mule flow");
        MuleMessage fileDeleteResponse = getMuleContextManager().send("vm://deleteFile.in", ecmFile, messageProps);
        ExceptionPayload exceptionPayload = fileDeleteResponse.getExceptionPayload();
        if (exceptionPayload != null)
        {
            throw new Exception(exceptionPayload.getRootException());
        }
    }

    public MuleContextManager getMuleContextManager()
    {
        return muleContextManager;
    }

    public void setMuleContextManager(MuleContextManager muleContextManager)
    {
        this.muleContextManager = muleContextManager;
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
