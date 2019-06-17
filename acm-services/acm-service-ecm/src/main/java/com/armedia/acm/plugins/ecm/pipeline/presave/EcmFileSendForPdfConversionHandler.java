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

import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.files.capture.CaptureConfig;
import com.armedia.acm.plugins.ecm.pipeline.EcmFileTransactionPipelineContext;
import com.armedia.acm.plugins.ecm.service.SendForPdfConversion;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Created by joseph.mcgrady on 9/24/2015.
 */
public class EcmFileSendForPdfConversionHandler implements PipelineHandler<EcmFile, EcmFileTransactionPipelineContext>
{
    private transient final Logger log = LogManager.getLogger(getClass());

    private SendForPdfConversion sendForPdfConversion;
    private CaptureConfig captureConfig;

    @Override
    public void execute(EcmFile entity, EcmFileTransactionPipelineContext pipelineContext) throws PipelineProcessException
    {
        // Any file format which cannot be merged by ArkCase will be sent for external format conversion
        String fileExtension = entity.getFileExtension();

        // Only certain file types (authorization, abstract, etc.) are converted to PDF
        boolean isFileTypeConvertibleToPdf = captureConfig.getFileTypesToBeConvertedToPdf().contains(entity.getFileType());

        // Only certain file formats (tiff, jpg, etc.) are converted to PDF
        boolean isFileFormatConvertibleToPdf = captureConfig.getFileFormatsToBeConvertedToPdf().contains(fileExtension);

        if (isFileFormatConvertibleToPdf && isFileTypeConvertibleToPdf)
        {
            try (InputStream fileInputStream = new FileInputStream(pipelineContext.getFileContents()))
            {
                EcmFile toBeConverted = pipelineContext.getEcmFile();
                if (toBeConverted == null)
                {
                    throw new Exception("the conversion file is null");
                }

                // Drops the file into the shared drive folder for Ephesoft
                sendForPdfConversion.copyToCaptureHotFolder(toBeConverted, fileInputStream);
            }
            catch (Exception e)
            {
                log.error("Failed to copy file to Ephesoft hot folder: {}", e.getMessage(), e);
                throw new PipelineProcessException(e);
            }
        }
    }

    @Override
    public void rollback(EcmFile entity, EcmFileTransactionPipelineContext pipelineContext) throws PipelineProcessException
    {
        // I don't know if the copy can be rolled back, because as soon as the copy is completed the
        // other watcher process might see the document and try to pick it up, so a consistent end result may not be
        // possible
    }

    public SendForPdfConversion getCaptureFolderService()
    {
        return sendForPdfConversion;
    }

    public void setCaptureFolderService(SendForPdfConversion sendForPdfConversion)
    {
        this.sendForPdfConversion = sendForPdfConversion;
    }

    public CaptureConfig getCaptureConfig()
    {
        return captureConfig;
    }

    public void setCaptureConfig(CaptureConfig captureConfig)
    {
        this.captureConfig = captureConfig;
    }
}
