package com.armedia.acm.plugins.ecm.pipeline.presave;

import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.pipeline.EcmFileTransactionPipelineContext;
import com.armedia.acm.plugins.ecm.utils.EcmFileMuleUtils;
import com.armedia.acm.plugins.ecm.utils.FolderAndFilesUtils;
import com.armedia.acm.plugins.ecm.utils.GenericUtils;
import com.armedia.acm.plugins.ecm.utils.PDFUtils;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

/**
 * Created by joseph.mcgrady on 9/24/2015.
 */
public class EcmFileMergeHandler implements PipelineHandler<EcmFile, EcmFileTransactionPipelineContext>
{
    private transient final Logger log = LoggerFactory.getLogger(getClass());

    private String fileFormatsToMerge;
    private String fileTypesToMerge;
    private EcmFileDao ecmFileDao;
    private FolderAndFilesUtils folderAndFilesUtils;
    private EcmFileMuleUtils ecmFileMuleUtils;

    @Override
    public void execute(EcmFile entity, EcmFileTransactionPipelineContext pipelineContext) throws PipelineProcessException
    {
        if (entity == null)
        {
            throw new PipelineProcessException("ecmFile is null");
        }

        try
        {
            pipelineContext.setIsAppend(false);

            // Only certain file formats can be merged (PDF at this point is the only one supported)
            String fileExtension = entity.getFileExtension();

            // Only certain file types (authorization, abstract, etc.) are merged directly within the Bactes extension application
            boolean isFileTypeMergeable = GenericUtils.isFileTypeInList(entity.getFileType(), fileTypesToMerge);

            // Only certain file formats (tiff, jpg, etc.) are merged directly within the Bactes extension application
            boolean isFileFormatMergeable = GenericUtils.isFileTypeInList(fileExtension, fileFormatsToMerge);

            if (isFileTypeMergeable && isFileFormatMergeable)
            {

                // Checks for an existing repository file of a mergeable type
                EcmFile matchFile = getDuplicateFile(pipelineContext.getContainer().getId(), entity.getFileType());

                // Appends new PDF to old one if a PDF of the appropriate matching type exists
                if (matchFile != null)
                {
                    log.debug("pdf document type match found, need to merge the type: " + matchFile.getFileType());

                    // We need to pull the original file contents from Alfresco in order to merge with the new file
                    log.debug("Pulling original document contents from repository");
                    InputStream originalFileStream = ecmFileMuleUtils.downloadFile(matchFile.getVersionSeriesId());
                    if (originalFileStream == null)
                    {
                        throw new Exception("Failed to pull document " + matchFile.getFileId() + " from the repository");
                    }

                    // Appends the new PDF to the end of the old one
                    log.debug("merging the new document and the original");
                    byte[] mergedFileByteArray = PDFUtils.mergeFiles(originalFileStream, new ByteArrayInputStream(pipelineContext.getFileByteArray()));

                    // The merged PDF content will be available to the next pipeline stage
                    if (mergedFileByteArray != null)
                    {
                        pipelineContext.setMergedFileByteArray(mergedFileByteArray);
                        pipelineContext.setIsAppend(true);
                        pipelineContext.setEcmFile(matchFile);
                    } else
                    {
                        throw new Exception("The document merge failed");
                    }
                }
            }
        } catch (Exception e)
        {
            log.error("mule pre save handler failed: {}", e.getMessage(), e);
            throw new PipelineProcessException(e);
        }
    }

    @Override
    public void rollback(EcmFile entity, EcmFileTransactionPipelineContext pipelineContext) throws PipelineProcessException
    {

    }

    /**
     * Obtains a PDF file from the given container with the given ArkCase type if present
     *
     * @param containerId - unique identifier of the container in which the search will be performed
     * @param fileType    - this type will be searched for matches in the acm container
     * @return - PDF file object matching the requested type, or null if not found
     */
    private EcmFile getDuplicateFile(Long containerId, String fileType)
    {
        EcmFile matchFile = null;
        try
        {
            // Obtains the set of files (metadata only) in the current container
            List<EcmFile> containerList = ecmFileDao.findForContainer(containerId);

            // Determines if the container has a file with the same type as the new file being uploaded
            matchFile = folderAndFilesUtils.findMatchingPDFFileType(containerList, fileType);
        } catch (Exception e)
        {
            log.error("failed to lookup files: {}", e.getMessage(), e);
        }
        return matchFile;
    }

    public String getFileFormatsToMerge()
    {
        return fileFormatsToMerge;
    }

    public void setFileFormatsToMerge(String fileFormatsToMerge)
    {
        this.fileFormatsToMerge = fileFormatsToMerge;
    }

    public String getFileTypesToMerge()
    {
        return fileTypesToMerge;
    }

    public void setFileTypesToMerge(String mergeableTypes)
    {
        this.fileTypesToMerge = mergeableTypes;
    }

    public EcmFileDao getEcmFileDao()
    {
        return ecmFileDao;
    }

    public void setEcmFileDao(EcmFileDao ecmFileDao)
    {
        this.ecmFileDao = ecmFileDao;
    }

    public FolderAndFilesUtils getFolderAndFilesUtils()
    {
        return folderAndFilesUtils;
    }

    public void setFolderAndFilesUtils(FolderAndFilesUtils folderAndFilesUtils)
    {
        this.folderAndFilesUtils = folderAndFilesUtils;
    }

    public EcmFileMuleUtils getEcmFileMuleUtils()
    {
        return ecmFileMuleUtils;
    }

    public void setEcmFileMuleUtils(EcmFileMuleUtils ecmFileMuleUtils)
    {
        this.ecmFileMuleUtils = ecmFileMuleUtils;
    }
}