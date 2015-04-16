package com.armedia.acm.plugins.ecm.model;

/**
 * Created by marjan.stefanoski on 03.04.2015.
 */
public class MoveCopyFileDto {

    private Long id;
    private Long folderId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFolderId() {
        return folderId;
    }

    public void setFolderId(Long folderId) {
        this.folderId = folderId;
    }
}
