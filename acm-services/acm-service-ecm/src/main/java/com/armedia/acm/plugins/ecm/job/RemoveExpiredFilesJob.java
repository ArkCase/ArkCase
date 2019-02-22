package com.armedia.acm.plugins.ecm.job;

import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.plugins.ecm.service.impl.FileChunkServiceImpl;
import com.armedia.acm.scheduler.AcmSchedulableBean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RemoveExpiredFilesJob implements AcmSchedulableBean {
    private final Logger LOG = LoggerFactory.getLogger(getClass());
    private FileChunkServiceImpl fileChunkService;
    private EcmFileService ecmFileService;

    @Override
    public void executeTask() {

        getFileChunkService().delete

    }

    public FileChunkServiceImpl getFileChunkService() {
        return fileChunkService;
    }

    public void setFileChunkService(FileChunkServiceImpl fileChunkService) {
        this.fileChunkService = fileChunkService;
    }

    public EcmFileService getEcmFileService() {
        return ecmFileService;
    }

    public void setEcmFileService(EcmFileService ecmFileService) {
        this.ecmFileService = ecmFileService;
    }
}
