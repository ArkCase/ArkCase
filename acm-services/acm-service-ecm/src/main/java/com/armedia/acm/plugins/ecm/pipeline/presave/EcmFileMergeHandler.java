package com.armedia.acm.plugins.ecm.pipeline.presave;

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

import com.armedia.acm.files.capture.CaptureConfig;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.pipeline.EcmFileTransactionPipelineContext;
import com.armedia.acm.plugins.ecm.utils.EcmFileMuleUtils;
import com.armedia.acm.plugins.ecm.utils.FolderAndFilesUtils;
import com.armedia.acm.plugins.ecm.utils.PDFUtils;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

/**
 * Created by joseph.mcgrady on 9/24/2015.
 */
public class EcmFileMergeHandler implements PipelineHandler<EcmFile, EcmFileTransactionPipelineContext>
{
    private transient final Logger log = LogManager.getLogger(getClass());

    private CaptureConfig captureConfig;
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

            // Only certain file types (authorization, abstract, etc.) are merged directly within the Bactes extension
            // application
            boolean isFileTypeMergeable = captureConfig.getFileTypesToMerge().contains(entity.getFileType());

            // Only certain file formats (tiff, jpg, etc.) are merged directly within the Bactes extension application
            boolean isFileFormatMergeable = captureConfig.getFileFormatsToMerge().contains(fileExtension);

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
                    try (InputStream updatedFileStream = new FileInputStream(pipelineContext.getFileContents());
                            InputStream originalFileStream = ecmFileMuleUtils.downloadFile(matchFile.getCmisRepositoryId(),
                                    matchFile.getVersionSeriesId()))
                    {

                        if (originalFileStream == null)
                        {
                            throw new Exception("Failed to pull document " + matchFile.getFileId() + " from the repository");
                        }

                        // Appends the new PDF to the end of the old one
                        log.debug("merging the new document and the original");
                        File mergedFile = PDFUtils.mergeFileStreams(originalFileStream, updatedFileStream);

                        // The merged PDF content will be available to the next pipeline stage
                        pipelineContext.setMergedFile(mergedFile);
                        pipelineContext.setIsAppend(true);
                        pipelineContext.setEcmFile(matchFile);
                    }
                }
            }
        }
        catch (Exception e)
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
     * @param containerId
     *            - unique identifier of the container in which the search will be performed
     * @param fileType
     *            - this type will be searched for matches in the acm container
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
        }
        catch (Exception e)
        {
            log.error("failed to lookup files: {}", e.getMessage(), e);
        }
        return matchFile;
    }

    public CaptureConfig getCaptureConfig()
    {
        return captureConfig;
    }

    public void setCaptureConfig(CaptureConfig captureConfig)
    {
        this.captureConfig = captureConfig;
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
