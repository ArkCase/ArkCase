package com.armedia.acm.objectdiff.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;

public abstract class AcmChange implements Serializable
{
    /**
     * what action was performed. Constants are used from AcmDiffConstants
     */
    private String action;
    /**
     * path from the root object
     */
    private String path;
    /**
     * path of the parent object
     */
    private String shortPath;


    public String getAction()
    {
        return action;
    }

    public void setAction(String action)
    {
        this.action = action;
    }

    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    public String getShortPath()
    {
        return shortPath;
    }

    public void setShortPath(String shortPath)
    {
        this.shortPath = shortPath;
    }

    @JsonIgnore
    public abstract boolean isLeaf();

    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString(this);
    }
}
