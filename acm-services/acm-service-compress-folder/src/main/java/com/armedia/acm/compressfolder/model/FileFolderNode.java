package com.armedia.acm.compressfolder.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FileFolderNode
{
    private boolean folder;
    private String objectId;

    public boolean isFolder()
    {
        return folder;
    }

    public void setFolder(boolean folder)
    {
        this.folder = folder;
    }

    public String getObjectId()
    {
        return objectId;
    }

    public void setObjectId(String objectId)
    {
        this.objectId = objectId;
    }
}
