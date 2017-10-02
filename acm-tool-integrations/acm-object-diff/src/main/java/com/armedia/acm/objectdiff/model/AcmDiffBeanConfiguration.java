package com.armedia.acm.objectdiff.model;

import java.io.Serializable;
import java.util.List;

public class AcmDiffBeanConfiguration implements Serializable
{
    private String className;
    private String name;
    private List<String> includeFields;
    private List<String> skipFields;
    private List<String> id;

    public String getClassName()
    {
        return className;
    }

    public void setClassName(String className)
    {
        this.className = className;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public List<String> getIncludeFields()
    {
        return includeFields;
    }

    public void setIncludeFields(List<String> includeFields)
    {
        this.includeFields = includeFields;
    }

    public List<String> getSkipFields()
    {
        return skipFields;
    }

    public void setSkipFields(List<String> skipFields)
    {
        this.skipFields = skipFields;
    }

    public List<String> getId()
    {
        return id;
    }

    public void setId(List<String> id)
    {
        this.id = id;
    }
}
