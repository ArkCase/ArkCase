package com.armedia.acm.objectdiff.model;

import com.armedia.acm.objectdiff.model.interfaces.AcmChangeDisplayable;
import com.armedia.acm.objectdiff.model.interfaces.AcmDiffConstants;

public class AcmObjectReplaced extends AcmObjectChange implements AcmChangeDisplayable
{
    private Object oldObject;
    private Object newObject;
    private String displayOldValue;
    private String displayNewValue;

    public AcmObjectReplaced(String path, String property)
    {
        setProperty(property);
        setPath(path);
        setAction(AcmDiffConstants.OBJECT_REPLACED);
    }

    public Object getOldObject()
    {
        return oldObject;
    }

    public void setOldObject(Object oldObject)
    {
        this.oldObject = oldObject;
    }

    public Object getNewObject()
    {
        return newObject;
    }

    public void setNewObject(Object newObject)
    {
        this.newObject = newObject;
    }

    public void setOldValue(String displayOldValue)
    {
        this.displayOldValue = displayOldValue;
    }

    public void setNewValue(String displayNewValue)
    {
        this.displayNewValue = displayNewValue;
    }

    @Override
    public boolean isLeaf()
    {
        return true;
    }

    @Override
    public String getOldValue()
    {
        return displayOldValue;
    }

    @Override
    public String getNewValue()
    {
        return displayNewValue;
    }
}
