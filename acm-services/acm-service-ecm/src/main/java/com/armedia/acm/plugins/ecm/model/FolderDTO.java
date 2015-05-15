package com.armedia.acm.plugins.ecm.model;

/**
 * Created by marjan.stefanoski on 15.05.2015.
 */
public class FolderDTO {
    private Long originalFolderId;
    private AcmFolder newFolder;

    public Long getOriginalFolderId() {
        return originalFolderId;
    }

    public void setOriginalFolderId(Long originalFolderId) {
        this.originalFolderId = originalFolderId;
    }

    public AcmFolder getNewFolder() {
        return newFolder;
    }

    public void setNewFolder(AcmFolder newFolder) {
        this.newFolder = newFolder;
    }
}
