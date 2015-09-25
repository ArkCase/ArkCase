package com.armedia.acm.plugins.ecm.pipeline.presave;

import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.pipeline.EcmFileTransactionPipelineContext;
import com.armedia.acm.plugins.ecm.service.CaptureFolderService;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

/**
 * Created by joseph.mcgrady on 9/24/2015.
 */
public class EcmFileCopyToEphesoftHandler implements PipelineHandler<EcmFile, EcmFileTransactionPipelineContext> {
    private transient final Logger log = LoggerFactory.getLogger(getClass());

    private CaptureFolderService captureFolderService;

    @Override
    public void execute(EcmFile entity, EcmFileTransactionPipelineContext pipelineContext) throws PipelineProcessException
    {
        // Non-pdf format documents need to be copied to the Ephesoft hot folder for processing
        if (!pipelineContext.getIsPDF()) {
            try {
                EcmFile ephesoftFile = pipelineContext.getEcmFile();
                if (ephesoftFile == null) {
                    throw new Exception("the ephesoft file is null");
                }
                InputStream fileInputStream = pipelineContext.getFileInputStream();
                if (fileInputStream == null) {
                    throw new Exception("the ephesoft file input stream is null");
                }

                // Drops the file into the shared drive folder for Ephesoft
                captureFolderService.copyToCaptureHotFolder(ephesoftFile, fileInputStream);
            } catch (Exception e) {
                log.error("Failed to copy file to Ephesoft hot folder: {}", e.getMessage(), e);
                throw new PipelineProcessException("Failed to copy file to Ephesoft hot folder: " + e.getMessage());
            }
        }
    }

    @Override
    public void rollback(EcmFile entity, EcmFileTransactionPipelineContext pipelineContext) throws PipelineProcessException
    {
        // I don't know if the copy can be rolled back, because as soon as the copy is completed the
        // other watcher process might see the document and try to pick it up, so a consistent end result may not be possible
    }

    public CaptureFolderService getCaptureFolderService() {
        return captureFolderService;
    }
    public void setCaptureFolderService(CaptureFolderService captureFolderService) {
        this.captureFolderService = captureFolderService;
    }
}