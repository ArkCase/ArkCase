package com.armedia.acm.plugins.objectassociation.model;

import java.io.Serializable;

/**
 * 
 * @author vladimir.radeski
 *
 */

public class Reference implements Serializable
{

    private static final long serialVersionUID = 3662277624014771785L;
    private Long referenceId;
    private String referenceNumber;
    private String referenceType;
    private String referenceTitle;
    private Long targetId;
    private String targetType;

    public Long getReferenceId()
    {
        return referenceId;
    }

    public void setReferenceId(Long referenceId)
    {
        this.referenceId = referenceId;
    }

    public String getReferenceNumber()
    {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber)
    {
        this.referenceNumber = referenceNumber;
    }

    public String getReferenceType()
    {
        return referenceType;
    }

    public void setReferenceType(String referenceType)
    {
        this.referenceType = referenceType;
    }

    public String getReferenceTitle()
    {
        return referenceTitle;
    }

    public void setReferenceTitle(String referenceTitle)
    {
        this.referenceTitle = referenceTitle;
    }

    public Long getTargetId()
    {
        return targetId;
    }

    public void setTargetId(Long targetId)
    {
        this.targetId = targetId;
    }

    public String getTargetType()
    {
        return targetType;
    }

    public void setTargetType(String targetType)
    {
        this.targetType = targetType;
    }

}
