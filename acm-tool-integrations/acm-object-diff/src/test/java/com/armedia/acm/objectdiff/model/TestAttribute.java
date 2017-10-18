package com.armedia.acm.objectdiff.model;

import com.armedia.acm.core.AcmObject;

import java.io.Serializable;

public class TestAttribute implements AcmObject, Serializable
{
    private Long id;
    private String value;

    @Override
    public String getObjectType()
    {
        return "TEST_ATTRIBUTE";
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }
}
