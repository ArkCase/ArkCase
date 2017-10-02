package com.armedia.acm.objectdiff.model;

public class AcmObjectReplaced extends AcmPropertyChange
{
    private Object oldValue;
    private Object newValue;

    public AcmObjectReplaced(String path, String property)
    {
        setProperty(property);
        setPath(path);
        setAction(AcmDiffConstants.VALUE_CHANGED);
    }

    public Object getOldValue()
    {
        return oldValue;
    }

    public void setOldValue(Object oldValue)
    {
        this.oldValue = oldValue;
    }

    public Object getNewValue()
    {
        return newValue;
    }

    public void setNewValue(Object newValue)
    {
        this.newValue = newValue;
    }
}
