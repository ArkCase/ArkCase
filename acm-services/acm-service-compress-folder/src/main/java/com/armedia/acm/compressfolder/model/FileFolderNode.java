package com.armedia.acm.compressfolder.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FileFolderNode
{
    private boolean folder;
    private Long objectId;

    public boolean isFolder()
    {
        return folder;
    }

    public void setFolder(boolean folder)
    {
        this.folder = folder;
    }

    public Long getObjectId()
    {
        return objectId;
    }

    public void setObjectId(Long objectId)
    {
        this.objectId = objectId;
    }
}
