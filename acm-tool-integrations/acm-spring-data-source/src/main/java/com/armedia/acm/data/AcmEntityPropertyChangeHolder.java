package com.armedia.acm.data;


public class AcmEntityPropertyChangeHolder
{
    private final Object oldValue;
    private final Object newValue;

    public AcmEntityPropertyChangeHolder(Object oldValue, Object newValue)
    {
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public Object getOldValue()
    {
        return oldValue;
    }

    public Object getNewValue()
    {
        return newValue;
    }
}
