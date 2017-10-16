package com.armedia.acm.objectdiff.model;

public abstract class AcmCollectionElementChange extends AcmChange
{
    protected Long affectedObjectId;
    protected String affectedObjectType;

    public Long getAffectedObjectId()
    {
        return affectedObjectId;
    }

    public void setAffectedObjectId(Long affectedObjectId)
    {
        this.affectedObjectId = affectedObjectId;
    }

    public String getAffectedObjectType()
    {
        return affectedObjectType;
    }

    public void setAffectedObjectType(String affectedObjectType)
    {
        this.affectedObjectType = affectedObjectType;
    }
}
