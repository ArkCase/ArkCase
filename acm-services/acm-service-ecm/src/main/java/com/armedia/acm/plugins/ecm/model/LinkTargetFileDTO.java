package com.armedia.acm.plugins.ecm.model;

public class LinkTargetFileDTO
{
    private Long originalFileId;
    private Long parentObjectId;
    private String parentObjectType;

    public Long getOriginalFileId()
    {
        return originalFileId;
    }

    public void setOriginalFileId(Long originalFileId)
    {
        this.originalFileId = originalFileId;
    }

    public Long getParentObjectId()
    {
        return parentObjectId;
    }

    public void setParentObjectId(Long parentObjectId)
    {
        this.parentObjectId = parentObjectId;
    }

    public String getParentObjectType()
    {
        return parentObjectType;
    }

    public void setParentObjectType(String parentObjectType)
    {
        this.parentObjectType = parentObjectType;
    }
}
