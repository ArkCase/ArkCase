package com.armedia.acm.plugins.ecm.utils;

import com.armedia.acm.muletools.mulecontextmanager.MuleContextManager;
import com.armedia.acm.plugins.ecm.model.EcmFile;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.mule.api.ExceptionPayload;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by joseph.mcgrady on 9/17/2015.
 */
public class EcmFileMuleUtils
{
    private transient final Logger log = LoggerFactory.getLogger(getClass());

    private MuleContextManager muleContextManager;

    /**
     * Downloads the contents of the specified document from the repository
     * 
     * @param cmisDocumentId
     *            - cmis id of the document to download
     * @return InputStream for the document contents
     */
    public InputStream downloadFile(String cmisDocumentId)
    {
        InputStream fileContentStream = null;
        try
        {
            log.debug("downloading document using vm://downloadFileFlow.in mule flow");
            MuleMessage downloadResponse = getMuleContextManager().send("vm://downloadFileFlow.in", cmisDocumentId);
            ContentStream contentStream = (ContentStream) downloadResponse.getPayload();
            fileContentStream = contentStream.getStream();
        } catch (Exception e)
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
        // Mule upload request payload setup (specifies the folder in which to upload the supplied content stream)
        Map<String, Object> messageProps = new HashMap<>();
        messageProps.put("cmisFolderId", cmisFolderId);
        messageProps.put("inputStream", fileInputStream);

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
}