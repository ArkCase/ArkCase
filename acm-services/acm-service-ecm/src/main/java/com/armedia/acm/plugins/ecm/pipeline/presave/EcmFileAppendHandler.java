package com.armedia.acm.plugins.ecm.pipeline.presave;

import com.armedia.acm.muletools.mulecontextmanager.MuleContextManager;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.pipeline.EcmFileTransactionPipelineContext;
import com.armedia.acm.plugins.ecm.utils.FolderAndFilesUtils;
import com.armedia.acm.plugins.ecm.utils.PDFUtils;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.commons.io.IOUtils;
import org.mule.api.ExceptionPayload;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by joseph.mcgrady on 9/11/2015.
 */
public class EcmFileAppendHandler implements PipelineHandler<EcmFile, EcmFileTransactionPipelineContext> {
    private transient final Logger log = LoggerFactory.getLogger(getClass());

    private MuleContextManager muleContextManager;
    private EcmFileDao ecmFileDao;
    private FolderAndFilesUtils folderAndFilesUtils;

    /**
     * Downloads the contents of the specified document from the repository
     * @param cmisDocumentId - cmis id of the document to download
     * @return InputStream for the document contents
     */
    private InputStream getFileFromRepository(String cmisDocumentId)
    {
        InputStream fileContentStream = null;
        try {
            log.debug("downloading document using vm://downloadFileFlow.in mule flow");
            MuleMessage downloadResponse = getMuleContextManager().send("vm://downloadFileFlow.in", cmisDocumentId);
            ContentStream contentStream = (ContentStream) downloadResponse.getPayload();
            fileContentStream = contentStream.getStream();
        } catch (Exception e) {
            log.error("Failed to get document: {}", e.getMessage(), e);
        }
        return fileContentStream;
    }

    /**
     * Obtains a file from the given container with the given type if present
     * @param containerId - unique identifier of the container in which the search will be performed
     * @param fileType - this type will be searched for matches in the acm container
     * @return - file object matching the requested type, or null if not found
     */
    private EcmFile getDuplicateFile(Long containerId, String fileType)
    {
        EcmFile matchFile = null;
        try {
            // Obtains the set of files (metadata only) in the current container
            List<EcmFile> containerList = ecmFileDao.findForContainer(containerId);

            // Determines if the container has a file with the same type as the new file being uploaded
            matchFile = folderAndFilesUtils.findMatchFileType(containerList, fileType);
        } catch (Exception e) {
            log.error("failed to lookup files: {}", e.getMessage(), e);
        }
        return matchFile;
    }

    /**
     * Adds a new file to the repository using the addFile mule flow.
     * @param newEcmFile - contains metadata for the file whose contents will be added to the repository
     * @param pipelineContext - contains the data stream for the file contents and the cmis id of the drop folder
     * @throws MuleException if the mule call to save the file to the repository fails
     */
    private void invokeAddFileFlow(EcmFile newEcmFile, EcmFileTransactionPipelineContext pipelineContext) throws MuleException
    {
        // Mule upload request payload setup (specifies the folder in which to upload the supplied content stream)
        Map<String, Object> messageProps = new HashMap<>();
        messageProps.put("cmisFolderId", pipelineContext.getCmisFolderId());
        messageProps.put("inputStream", pipelineContext.getFileInputStream());

        log.debug("invoking the mule add file flow");
        MuleMessage received = getMuleContextManager().send("vm://addFile.in", newEcmFile, messageProps);
        MuleException e = received.getInboundProperty("saveException");
        if (e != null) {
            throw e;
        }

        // The next pipeline stage needs to have access to the cmis document returned from mule
        pipelineContext.setCmisDocument(received.getPayload(Document.class));
    }

    /**
     * Updates the contents of an existing repository item using the mule updateFile flow.
     * @param newEcmFile - metadata for the new file which will replace the old version
     * @param originalFile - metadata for the old file whose contents will be replaced
     * @param fileInputStream - the binary data content which will be written to the repository
     * @param pipelineContext - contains variables for managing the add/append file pipeline process
     * @throws MuleException if the mule call to replace the file contents in the repository fails
     */
    private void invokeUpdateFileFlow(EcmFile newEcmFile, EcmFile originalFile, InputStream fileInputStream,
                                      EcmFileTransactionPipelineContext pipelineContext) throws MuleException
    {
        // mule payload
        Map<String, Object> messageProps = new HashMap<>();
        messageProps.put("ecmFileId", originalFile.getVersionSeriesId());
        messageProps.put("fileName", originalFile.getFileName());
        messageProps.put("mimeType", originalFile.getFileMimeType());
        messageProps.put("inputStream", fileInputStream);

        log.debug("invoking the mule replace file flow");
        MuleMessage received = getMuleContextManager().send("vm://updateFile.in", newEcmFile, messageProps);
        MuleException e = received.getInboundProperty("updateException");
        if (e != null) {
            throw e;
        }

        // The next pipeline stage needs to have access to the cmis document returned from mule
        pipelineContext.setCmisDocument(received.getPayload(Document.class));
    }

    @Override
    public void execute(EcmFile entity, EcmFileTransactionPipelineContext pipelineContext) throws PipelineProcessException
    {
        log.debug("mule pre save handler called");
        if (entity == null) {
            throw new PipelineProcessException("ecmFile is null");
        }

        // Must be PDF, need to check
        boolean isPDF = entity.getFileMimeType().equals("application/pdf");
        pipelineContext.setIsPDF(isPDF);
        log.debug("isPDF: " + isPDF);

        if (isPDF) {
            // Is the same file type already present? (if so, then the new PDF will be appended to the end)
            EcmFile matchFile = getDuplicateFile(pipelineContext.getContainer().getId(), entity.getFileType());
            pipelineContext.setEcmFile(matchFile);

            try {
                // Appends new PDF to old one if found
                if (matchFile != null) {
                    log.debug("pdf document type match found, need to merge!");
                    pipelineContext.setIsAppend(true);

                    // We need to pull the original file contents from Alfresco
                    log.debug("Pulling original document contents from repository");
                    InputStream originalFileStream = getFileFromRepository(matchFile.getVersionSeriesId());

                    // Appends the new PDF to the end of the old one
                    log.debug("merging the new document and the original");
                    InputStream fileInputStream = PDFUtils.mergeFiles(originalFileStream, pipelineContext.getFileInputStream());

                    // Updates the Alfresco content repository with the new merged version of the file
                    invokeUpdateFileFlow(entity, matchFile, fileInputStream, pipelineContext);
                } else {
                    log.debug("no match found, uploading as a separate document");
                    pipelineContext.setIsAppend(false);

                    // Adds the new file to the Alfresco content repository
                    invokeAddFileFlow(entity, pipelineContext);
                }
            } catch (Exception e) {
                log.error("mule pre save handler failed: {}", e.getMessage(), e);
                throw new PipelineProcessException("mule pre save handler failed: " + e.getMessage());
            }
        } else { // non-pdf file processing
            pipelineContext.setIsAppend(false); // for non-pdf we don't append the files
            try {
                // We need to read the data more than once, so it needs to be copied to a byte array
                ByteArrayOutputStream fileData = new ByteArrayOutputStream();
                IOUtils.copy(pipelineContext.getFileInputStream(), fileData);
                pipelineContext.setFileInputStream(new ByteArrayInputStream(fileData.toByteArray()));

                // Adds the non-pdf file to the repository
                invokeAddFileFlow(entity, pipelineContext);
            } catch (Exception e) {
                log.error("mule pre save handler failed: {}", e.getMessage(), e);
                throw new PipelineProcessException("mule pre save handler failed: " + e.getMessage());
            }
        }
        log.debug("mule pre save handler ended");
    }

    @Override
    public void rollback(EcmFile entity, EcmFileTransactionPipelineContext pipelineContext) throws PipelineProcessException
    {
        log.debug("mule pre save handler rollback called");
        // Since the mule flow creates a file in the repository, JPA cannot roll it back and it needs to be deleted manually
        try {
            // We need the cmis id of the file in order to delete it
            Document cmisDocument = pipelineContext.getCmisDocument();
            if (cmisDocument == null) {
                throw new Exception("cmisDocument is null");
            }

            // This is the request payload for mule including the unique cmis id for the document to delete
            Map<String, Object> messageProps = new HashMap<>();
            messageProps.put("ecmFileId", cmisDocument.getId());

            // Invokes the mule flow to delete the file contents from the repository
            log.debug("rolling back file upload for cmis id: " + cmisDocument.getId() + " using vm://deleteFile.in mule flow");
            MuleMessage fileDeleteResponse = getMuleContextManager().send("vm://deleteFile.in", entity, messageProps);
            ExceptionPayload exceptionPayload = fileDeleteResponse.getExceptionPayload();
            if (exceptionPayload != null) {
                throw new Exception(exceptionPayload.getRootException());
            }

        } catch (Exception e) { // since the rollback failed an orphan document will exist in Alfresco
            log.error("rollback of file upload failed: {}", e.getMessage(), e);
            throw new PipelineProcessException("rollback of file upload failed: " + e.getMessage());
        }
        log.debug("mule pre save handler rollback ended");
    }

    public MuleContextManager getMuleContextManager() {
        return muleContextManager;
    }
    public void setMuleContextManager(MuleContextManager muleContextManager) {
        this.muleContextManager = muleContextManager;
    }
    public EcmFileDao getEcmFileDao() {
        return ecmFileDao;
    }
    public void setEcmFileDao(EcmFileDao ecmFileDao) {
        this.ecmFileDao = ecmFileDao;
    }
    public FolderAndFilesUtils getFolderAndFilesUtils() {
        return folderAndFilesUtils;
    }
    public void setFolderAndFilesUtils(FolderAndFilesUtils folderAndFilesUtils) {
        this.folderAndFilesUtils = folderAndFilesUtils;
    }
}