package com.armedia.acm.correspondence.model;

import java.util.Date;

/**
 * @author sasko.tanaskoski
 *
 */
public class CorrespondenceMergeFieldVersion
{

    private String mergingVersion;

    private boolean mergingActiveVersion;

    private String mergingType;

    private String modifier;

    private Date modified;

    public String getMergingVersion()
    {
        return mergingVersion;
    }

    public void setMergingVersion(String mergingVersion)
    {
        this.mergingVersion = mergingVersion;
    }

    public boolean isMergingActiveVersion()
    {
        return mergingActiveVersion;
    }

    public void setMergingActiveVersion(boolean mergingActiveVersion)
    {
        this.mergingActiveVersion = mergingActiveVersion;
    }

    public String getModifier()
    {
        return modifier;
    }

    public String getMergingType()
    {
        return mergingType;
    }

    public void setMergingType(String mergingType)
    {
        this.mergingType = mergingType;
    }

    public void setModifier(String modifier)
    {
        this.modifier = modifier;
    }

    public Date getModified()
    {
        return modified;
    }

    public void setModified(Date modified)
    {
        this.modified = modified;
    }

}
