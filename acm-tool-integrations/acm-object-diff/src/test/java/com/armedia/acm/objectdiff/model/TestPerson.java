package com.armedia.acm.objectdiff.model;

import com.armedia.acm.core.AcmObject;

import java.io.Serializable;
import java.util.List;

public class TestPerson implements AcmObject, Serializable
{
    private Long id;
    private String name;
    private String lastName;
    private String toBeIgnored;
    private TestAttribute defaultAttribute;
    private List<TestAttribute> attributeList;

    @Override
    public String getObjectType()
    {
        return "TEST_PERSON";
    }

    @Override
    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getLastName()
    {
        return lastName;
    }

    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

    public String getToBeIgnored()
    {
        return toBeIgnored;
    }

    public void setToBeIgnored(String toBeIgnored)
    {
        this.toBeIgnored = toBeIgnored;
    }

    public List<TestAttribute> getAttributeList()
    {
        return attributeList;
    }

    public void setAttributeList(List<TestAttribute> attributeList)
    {
        this.attributeList = attributeList;
    }

    public TestAttribute getDefaultAttribute()
    {
        return defaultAttribute;
    }

    public void setDefaultAttribute(TestAttribute defaultAttribute)
    {
        this.defaultAttribute = defaultAttribute;
    }
}
