package com.armedia.acm.plugins.objectassociation.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

import java.io.Serializable;

/**
 * 
 * @author vladimir.radeski
 *
 */
@JsonIdentityInfo(generator = JSOGGenerator.class)
public class Reference implements Serializable
{

    private static final long serialVersionUID = 3662277624014771785L;
    private Long referenceId;
    private String referenceNumber;
    private String referenceType;
    private String referenceTitle;
    private String referenceStatus;
    private Long parentId;
    private String parentType;

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

    public String getReferenceStatus()
    {
        return referenceStatus;
    }

    public void setReferenceStatus(String referenceStatus)
    {
        this.referenceStatus = referenceStatus;
    }

    public Long getParentId()
    {
        return parentId;
    }

    public void setParentId(Long parentId)
    {
        this.parentId = parentId;
    }

    public String getParentType()
    {
        return parentType;
    }

    public void setParentType(String parentType)
    {
        this.parentType = parentType;
    }

}
