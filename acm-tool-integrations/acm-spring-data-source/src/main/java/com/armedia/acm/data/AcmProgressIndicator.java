package com.armedia.acm.data;


public class AcmProgressIndicator
{
    private int progress;
    private int total;
    private String user;
    private String objectType;
    private Long objectId;

    public int getProgress()
    {
        return progress;
    }

    public void setProgress(int progress)
    {
        this.progress = progress;
    }

    public int getTotal()
    {
        return total;
    }

    public void setTotal(int total)
    {
        this.total = total;
    }

    public String getUser()
    {
        return user;
    }

    public void setUser(String user)
    {
        this.user = user;
    }

    public String getObjectType()
    {
        return objectType;
    }

    public void setObjectType(String objectType)
    {
        this.objectType = objectType;
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
