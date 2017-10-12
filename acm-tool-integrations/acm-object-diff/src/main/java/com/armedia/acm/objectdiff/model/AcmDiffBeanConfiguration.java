package com.armedia.acm.objectdiff.model;

import java.io.Serializable;
import java.util.List;

public class AcmDiffBeanConfiguration implements Serializable
{
    /**
     * which class should be processed
     */
    private String className;
    /**
     * name in the path
     */
    private String name;
    /**
     * Spring expression
     */
    private String displayExpression;
    /**
     * which fields should be included for comparing
     */
    private List<String> includeFields;
    /**
     * which fields should be skipped for comparing
     */
    private List<String> skipFields;
    /**
     * which fields are constructing the id
     */
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

    public String getDisplayExpression()
    {
        return displayExpression;
    }

    public void setDisplayExpression(String displayExpression)
    {
        this.displayExpression = displayExpression;
    }
}
