package com.armedia.acm.plugins.ecm.model;

/**
 * Created by marjan.stefanoski on 15.05.2015.
 */
public class FileDTO {
    private String originalId;
    private EcmFile newFile;

    public String getOriginalId() {
        return originalId;
    }

    public void setOriginalId(String originalId) {
        this.originalId = originalId;
    }

    public EcmFile getNewFile() {
        return newFile;
    }

    public void setNewFile(EcmFile newFile) {
        this.newFile = newFile;
    }
}
