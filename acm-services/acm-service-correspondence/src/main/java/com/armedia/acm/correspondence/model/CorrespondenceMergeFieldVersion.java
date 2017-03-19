package com.armedia.acm.correspondence.model;

import java.util.Date;

/**
 * @author sasko.tanaskoski
 *
 */

/**
 * This POJO stores parameters for mergeFieldVersion
 */
public class CorrespondenceMergeFieldVersion
{

    private String mergingVersion;

    private boolean mergingActiveVersion;

    private String mergingType;

    private String modifier;

    private Date modified;

    /**
     * @return the mergingVersion
     */
    public String getMergingVersion()
    {
        return mergingVersion;
    }

    /**
     * @param mergingVersion
     *            the mergingVersion to set
     */
    public void setMergingVersion(String mergingVersion)
    {
        this.mergingVersion = mergingVersion;
    }

    /**
     * @return the mergingActiveVersion
     */
    public boolean isMergingActiveVersion()
    {
        return mergingActiveVersion;
    }

    /**
     * @param mergingActiveVersion
     *            the mergingActiveVersion to set
     */
    public void setMergingActiveVersion(boolean mergingActiveVersion)
    {
        this.mergingActiveVersion = mergingActiveVersion;
    }

    /**
     * @return the modifier
     */
    public String getModifier()
    {
        return modifier;
    }

    /**
     * @return the mergingType
     */
    public String getMergingType()
    {
        return mergingType;
    }

    /**
     * @param fieldId
     *            the fieldId to set
     */
    public void setMergingType(String mergingType)
    {
        this.mergingType = mergingType;
    }

    /**
     * @param modifier
     *            the modifier to set
     */
    public void setModifier(String modifier)
    {
        this.modifier = modifier;
    }

    /**
     * @return the modified
     */
    public Date getModified()
    {
        return modified;
    }

    /**
     * @param modified
     *            the modified to set
     */
    public void setModified(Date modified)
    {
        this.modified = modified;
    }

}
