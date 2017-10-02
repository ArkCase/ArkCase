package com.armedia.acm.objectdiff.model;

public abstract class AcmObjectChange extends AcmPropertyChange
{
    private Long affectedObjectId;
    private String affectedObjectType;

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
