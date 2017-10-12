package com.armedia.acm.objectdiff.model;

public abstract class AcmPropertyChange extends AcmChange
{
    protected String property;

    public String getProperty()
    {
        return property;
    }

    public void setProperty(String property)
    {
        this.property = property;
    }
}
