package com.armedia.acm.plugins.ecm.model;

public class DeleteFileResult
{
    private String deletedFileName;
    private boolean success;

    public String getDeletedFileName()
    {
        return deletedFileName;
    }

    public void setDeletedFileName(String deletedFileName)
    {
        this.deletedFileName = deletedFileName;
    }

    public boolean isSuccess()
    {
        return success;
    }

    public void setSuccess(boolean success)
    {
        this.success = success;
    }
}