package com.armedia.acm.plugins.ecm.model;


public class DeleteFolderInfo
{
    private int foldersToDeleteNum;

    private int filesToDeleteNum;

    public int getFoldersToDeleteNum()
    {
        return foldersToDeleteNum;
    }

    public void setFoldersToDeleteNum(int foldersToDeleteNum)
    {
        this.foldersToDeleteNum = foldersToDeleteNum;
    }

    public int getFilesToDeleteNum()
    {
        return filesToDeleteNum;
    }

    public void setFilesToDeleteNum(int filesToDeleteNum)
    {
        this.filesToDeleteNum = filesToDeleteNum;
    }
}
