package com.armedia.acm.plugins.ecm.pipeline.presave;

import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.pipeline.EcmFileTransactionPipelineContext;
import com.armedia.acm.plugins.ecm.utils.GenericUtils;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Created by joseph.mcgrady on 9/28/2015.
 */
public class EcmFileDuplicateContentHandler implements PipelineHandler<EcmFile, EcmFileTransactionPipelineContext> {
    private transient final Logger log = LoggerFactory.getLogger(getClass());

    private String fileFormatsToBeConvertedToPDF;

    @Override
    public void execute(EcmFile entity, EcmFileTransactionPipelineContext pipelineContext) throws PipelineProcessException {
        if (entity == null) {
            throw new PipelineProcessException("ecmFile is null");
        }

        // Determines if this file will be copied to the capture hot folder for conversion to PDF format
        String fileExtension = FilenameUtils.getExtension(entity.getFileName());
        boolean fileNeedsToBeConvertedToPDF = GenericUtils.isFileTypeInList(fileExtension, fileFormatsToBeConvertedToPDF);

        if (fileNeedsToBeConvertedToPDF) {
            try {
                // We need to read the data more than once (to save to Alfresco and the capture conversion folder)
                // To allow the data to be read multiple times, it needs to be copied to a byte array stream which supports stream resetting
                ByteArrayOutputStream fileData = new ByteArrayOutputStream();
                IOUtils.copy(pipelineContext.getFileInputStream(), fileData);
                pipelineContext.setFileInputStream(new ByteArrayInputStream(fileData.toByteArray()));
            } catch (Exception e) {
                log.error("mule pre save handler failed: {}", e.getMessage(), e);
                throw new PipelineProcessException(e);
            }
        }
    }

    @Override
    public void rollback(EcmFile entity, EcmFileTransactionPipelineContext pipelineContext) throws PipelineProcessException
    {

    }

    public String getFileFormatsToBeConvertedToPDF() {
        return fileFormatsToBeConvertedToPDF;
    }
    public void setFileFormatsToBeConvertedToPDF(String fileFormatsToBeConvertedToPDF) {
        this.fileFormatsToBeConvertedToPDF = fileFormatsToBeConvertedToPDF;
    }
}