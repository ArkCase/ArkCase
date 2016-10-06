package com.armedia.acm.plugins.ecm.pipeline.presave;

import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.pipeline.EcmFileTransactionPipelineContext;
import com.armedia.acm.plugins.ecm.service.SendForPdfConversion;
import com.armedia.acm.plugins.ecm.utils.GenericUtils;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Created by joseph.mcgrady on 9/24/2015.
 */
public class EcmFileSendForPdfConversionHandler implements PipelineHandler<EcmFile, EcmFileTransactionPipelineContext>
{
    private transient final Logger log = LoggerFactory.getLogger(getClass());

    private SendForPdfConversion sendForPdfConversion;
    private String fileTypesToBeConvertedToPDF;
    private String fileFormatsToBeConvertedToPDF;

    @Override
    public void execute(EcmFile entity, EcmFileTransactionPipelineContext pipelineContext) throws PipelineProcessException
    {
        // Any file format which cannot be merged by ArkCase will be sent for external format conversion
        String fileExtension = entity.getFileExtension();

        // Only certain file types (authorization, abstract, etc.) are converted to PDF
        boolean isFileTypeConvertibleToPdf = GenericUtils.isFileTypeInList(entity.getFileType(), fileTypesToBeConvertedToPDF);

        // Only certain file formats (tiff, jpg, etc.) are converted to PDF
        boolean isFileFormatConvertibleToPdf = GenericUtils.isFileTypeInList(fileExtension, fileFormatsToBeConvertedToPDF);

        if (isFileFormatConvertibleToPdf && isFileTypeConvertibleToPdf)
        {
            try
            {
                EcmFile toBeConverted = pipelineContext.getEcmFile();
                if (toBeConverted == null)
                {
                    throw new Exception("the conversion file is null");
                }
                InputStream fileInputStream = new ByteArrayInputStream(pipelineContext.getFileByteArray());
                if (fileInputStream == null)
                {
                    throw new Exception("the conversion file input stream is null");
                }

                // Drops the file into the shared drive folder for Ephesoft
                sendForPdfConversion.copyToCaptureHotFolder(toBeConverted, fileInputStream);
            } catch (Exception e)
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
        // other watcher process might see the document and try to pick it up, so a consistent end result may not be possible
    }

    public SendForPdfConversion getCaptureFolderService()
    {
        return sendForPdfConversion;
    }

    public void setCaptureFolderService(SendForPdfConversion sendForPdfConversion)
    {
        this.sendForPdfConversion = sendForPdfConversion;
    }

    public String getFileTypesToBeConvertedToPDF()
    {
        return fileTypesToBeConvertedToPDF;
    }

    public void setFileTypesToBeConvertedToPDF(String fileTypesToBeConvertedToPDF)
    {
        this.fileTypesToBeConvertedToPDF = fileTypesToBeConvertedToPDF;
    }

    public String getFileFormatsToBeConvertedToPDF()
    {
        return fileFormatsToBeConvertedToPDF;
    }

    public void setFileFormatsToBeConvertedToPDF(String fileFormatsToBeConvertedToPDF)
    {
        this.fileFormatsToBeConvertedToPDF = fileFormatsToBeConvertedToPDF;
    }
}