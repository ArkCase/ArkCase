package com.armedia.acm.objectdiff.model;

import com.armedia.acm.objectdiff.model.interfaces.AcmChangeDisplayable;
import com.armedia.acm.objectdiff.model.interfaces.AcmDiffConstants;

public class AcmValueChanged extends AcmPropertyChange implements AcmChangeDisplayable
{
    private String oldValue;
    private String newValue;

    public AcmValueChanged(String path, String property)
    {
        setProperty(property);
        setPath(path);
        setAction(AcmDiffConstants.VALUE_CHANGED);
    }

    @Override
    public boolean isLeaf()
    {
        return true;
    }

    @Override
    public String getOldValue()
    {
        return oldValue;
    }

    @Override
    public String getNewValue()
    {
        return newValue;
    }

    @Override
    public void setOldValue(String oldValue)
    {
        this.oldValue = oldValue;
    }

    @Override
    public void setNewValue(String newValue)
    {
        this.newValue = newValue;
    }
}
