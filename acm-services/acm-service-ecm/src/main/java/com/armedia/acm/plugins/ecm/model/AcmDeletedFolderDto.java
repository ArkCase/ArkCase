package com.armedia.acm.plugins.ecm.model;

/**
 * Created by marjan.stefanoski on 10.04.2015.
 */
public class AcmDeletedFolderDto {

    private String deletedFolderId;
    private String message;

    public String getDeletedFolderId() {
        return deletedFolderId;
    }

    public void setDeletedFolderId(String deletedFolderId) {
        this.deletedFolderId = deletedFolderId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
