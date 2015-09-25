package com.armedia.acm.plugins.ecm.pipeline.presave;

import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.pipeline.EcmFileTransactionPipelineContext;
import com.armedia.acm.plugins.ecm.utils.EcmFileMuleUtils;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Created by joseph.mcgrady on 9/11/2015.
 */
public class EcmFileAppendContentHandler implements PipelineHandler<EcmFile, EcmFileTransactionPipelineContext> {
    private transient final Logger log = LoggerFactory.getLogger(getClass());

    private EcmFileMuleUtils ecmFileMuleUtils;

    @Override
    public void execute(EcmFile entity, EcmFileTransactionPipelineContext pipelineContext) throws PipelineProcessException
    {
        log.debug("mule pre save handler called");
        if (entity == null) {
            throw new PipelineProcessException("ecmFile is null");
        }

        if (pipelineContext.getIsPDF()) {
            try {
                // Appends new PDF to old one if a PDF of the appropriate matching type exists
                if (pipelineContext.getIsAppend() && pipelineContext.getMergedFileInputStream() != null) {

                    // Updates the Alfresco content repository with the new merged version of the file
                    Document updatedDocument = ecmFileMuleUtils.updateFile(entity, pipelineContext.getEcmFile(), pipelineContext.getMergedFileInputStream());
                    pipelineContext.setCmisDocument(updatedDocument);
                } else {
                    log.debug("no match found, uploading as a separate document");
                    pipelineContext.setIsAppend(false);

                    // Adds the file to the Alfresco content repository as a new document
                    Document newDocument = ecmFileMuleUtils.addFile(entity, pipelineContext.getCmisFolderId(), pipelineContext.getFileInputStream());
                    pipelineContext.setCmisDocument(newDocument);
                }
            } catch (Exception e) {
                log.error("mule pre save handler failed: {}", e.getMessage(), e);
                throw new PipelineProcessException("mule pre save handler failed: " + e.getMessage());
            }
        } else { // non-pdf file processing
            try {
                // We need to read the data more than once, so it needs to be copied to a byte array
                ByteArrayOutputStream fileData = new ByteArrayOutputStream();
                IOUtils.copy(pipelineContext.getFileInputStream(), fileData);
                pipelineContext.setFileInputStream(new ByteArrayInputStream(fileData.toByteArray()));

                // Adds the non-pdf file to the repository
                Document newDocument = ecmFileMuleUtils.addFile(entity, pipelineContext.getCmisFolderId(), pipelineContext.getFileInputStream());
                pipelineContext.setCmisDocument(newDocument);
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

            // Removes the document from the Alfresco content repository
            ecmFileMuleUtils.deleteFile(entity, cmisDocument.getId());

        } catch (Exception e) { // since the rollback failed an orphan document will exist in Alfresco
            log.error("rollback of file upload failed: {}", e.getMessage(), e);
            throw new PipelineProcessException("rollback of file upload failed: " + e.getMessage());
        }
        log.debug("mule pre save handler rollback ended");
    }

    public EcmFileMuleUtils getEcmFileMuleUtils() {
        return ecmFileMuleUtils;
    }
    public void setEcmFileMuleUtils(EcmFileMuleUtils ecmFileMuleUtils) {
        this.ecmFileMuleUtils = ecmFileMuleUtils;
    }
}