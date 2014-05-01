package com.armedia.acm.plugins.alfrescorma.model;


import java.io.Serializable;

public class AcmRecordFolder implements Serializable
{
    private static final long serialVersionUID = -2838758910535448686L;
    private String folderType;
    private String folderName;

    public String getFolderType()
    {
        return folderType;
    }

    public void setFolderType(String folderType)
    {
        this.folderType = folderType;
    }

    public String getFolderName()
    {
        return folderName;
    }

    public void setFolderName(String folderName)
    {
        this.folderName = folderName;
    }
}
