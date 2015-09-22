package com.armedia.acm.plugins.ecm.pipeline.presave;

import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.pipeline.EcmFileTransactionPipelineContext;
import com.armedia.acm.plugins.ecm.utils.EcmFileMuleUtils;
import com.armedia.acm.plugins.ecm.utils.FolderAndFilesUtils;
import com.armedia.acm.plugins.ecm.utils.PDFUtils;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

/**
 * Created by joseph.mcgrady on 9/11/2015.
 */
public class EcmFileAppendHandler implements PipelineHandler<EcmFile, EcmFileTransactionPipelineContext> {
    private transient final Logger log = LoggerFactory.getLogger(getClass());

    private EcmFileMuleUtils ecmFileMuleUtils;
    private EcmFileDao ecmFileDao;
    private FolderAndFilesUtils folderAndFilesUtils;

    /**
     * Obtains a PDF file from the given container with the given ArkCase type if present
     * @param containerId - unique identifier of the container in which the search will be performed
     * @param fileType - this type will be searched for matches in the acm container
     * @return - PDF file object matching the requested type, or null if not found
     */
    private EcmFile getDuplicateFile(Long containerId, String fileType)
    {
        EcmFile matchFile = null;
        try {
            // Obtains the set of files (metadata only) in the current container
            List<EcmFile> containerList = ecmFileDao.findForContainer(containerId);

            // Determines if the container has a file with the same type as the new file being uploaded
            matchFile = folderAndFilesUtils.findMatchingPDFFileType(containerList, fileType);
        } catch (Exception e) {
            log.error("failed to lookup files: {}", e.getMessage(), e);
        }
        return matchFile;
    }

    /**
     * Determines if the file is of a type which can be merged/sent to Ephesoft.
     * The types (authorization, abstract) can be merged/sent to Ephesoft
     * @param fileType - ArkCase type of the uploaded file (authorization, abstract)
     * @return true if the given file type can be merged/sent to Ephesoft, false otherwise
     */
    private boolean isFileTypeAuthorizationOrAbstract(String fileType) {
        boolean isAuthorizationOrAbstract = false;
        if (fileType != null) {
            if (fileType.trim().equalsIgnoreCase("authorization") || fileType.trim().equalsIgnoreCase("abstract")) {
                isAuthorizationOrAbstract = true;
            }
        }
        return isAuthorizationOrAbstract;
    }

    @Override
    public void execute(EcmFile entity, EcmFileTransactionPipelineContext pipelineContext) throws PipelineProcessException
    {
        log.debug("mule pre save handler called");
        if (entity == null) {
            throw new PipelineProcessException("ecmFile is null");
        }

        // PDF and non-pdf files are handled differently (non-pdf are sent to Ephesoft)
        boolean isPDF = entity.getFileMimeType().equals("application/pdf");
        pipelineContext.setIsPDF(isPDF);
        log.debug("isPDF: " + isPDF);

        // Authorization and Abstract files are treated differently from Correspondence files
        pipelineContext.setIsAuthorizationOrAbstract(isFileTypeAuthorizationOrAbstract(entity.getFileType()));

        if (isPDF) {
            try {
                // If the new PDF upload has an ArkCase type of either (authorization or abstract)
                // and another PDF with the same ArkCase type can be found in Alfresco then that document
                // will be pulled and merged together with the new one
                EcmFile matchFile = null;
                if (pipelineContext.getIsAuthorizationOrAbstract()) {
                    matchFile = getDuplicateFile(pipelineContext.getContainer().getId(), entity.getFileType());
                }

                // Appends new PDF to old one if a PDF of the appropriate matching type exists
                if (matchFile != null) {
                    log.debug("pdf document type match found, need to merge the type: " + matchFile.getFileType());
                    pipelineContext.setIsAppend(true);
                    pipelineContext.setEcmFile(matchFile);

                    // We need to pull the original file contents from Alfresco in order to merge with the new file
                    log.debug("Pulling original document contents from repository");
                    InputStream originalFileStream = ecmFileMuleUtils.downloadFile(matchFile.getVersionSeriesId());

                    // Appends the new PDF to the end of the old one
                    log.debug("merging the new document and the original");
                    InputStream fileInputStream = PDFUtils.mergeFiles(originalFileStream, pipelineContext.getFileInputStream());

                    // Updates the Alfresco content repository with the new merged version of the file
                    Document updatedDocument = ecmFileMuleUtils.updateFile(entity, matchFile, fileInputStream);
                    pipelineContext.setCmisDocument(updatedDocument);
                } else {
                    log.debug("no match found, uploading as a separate document");
                    pipelineContext.setIsAppend(false);

                    // Adds the new file to the Alfresco content repository
                    Document newDocument = ecmFileMuleUtils.addFile(entity, pipelineContext.getCmisFolderId(), pipelineContext.getFileInputStream());
                    pipelineContext.setCmisDocument(newDocument);
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