package com.armedia.acm.plugins.ecm.job;

import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.plugins.ecm.service.impl.FileChunkServiceImpl;
import com.armedia.acm.scheduler.AcmSchedulableBean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileFilter;
import java.time.LocalDate;
import java.util.Properties;

public class RemoveExpiredFilesJob implements AcmSchedulableBean {
    private final Logger LOG = LoggerFactory.getLogger(getClass());
    private FileChunkServiceImpl fileChunkService;
    private EcmFileService ecmFileService;
    private Properties ecmFileServiceProperties;


    public void deleteExpiredFiles(){
        String dirPath = System.getProperty("java.io.tmpdir");
        String uniqueArkCaseHashFileIdentifier = ecmFileServiceProperties.getProperty("ecm.arkcase.hash.file.identifier");
        File directory = new File(dirPath);
        LocalDate today = LocalDate.now();
        LocalDate weekAgo = today.minusDays(7);

        LOG.debug("Mark files that should be deleted.");
        FileFilter filter = file -> {
            if(!(file.lastModified() < weekAgo.toEpochDay())){
                return false;
            }
            return file.getName().contains(uniqueArkCaseHashFileIdentifier);
        };

        File [] files = directory.listFiles(filter);
        LOG.debug("Found {} files to delete.", files.length);
        int deletedFiles = 0;
        for(File file : files){
            if(file.delete()){
                deletedFiles++;
            } else {
                LOG.warn("The file {} could not be deleted.", file.getName());
            }
        }
        LOG.info("{} files have been deleted.", deletedFiles);
    }

    @Override
    public void executeTask() {
        deleteExpiredFiles();
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

    public Properties getEcmFileServiceProperties() {
        return ecmFileServiceProperties;
    }

    public void setEcmFileServiceProperties(Properties ecmFileServiceProperties) {
        this.ecmFileServiceProperties = ecmFileServiceProperties;
    }
}
