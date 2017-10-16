package com.armedia.acm.objectdiff.model;

import com.armedia.acm.objectdiff.model.interfaces.AcmChangeDisplayable;
import com.armedia.acm.objectdiff.model.interfaces.AcmDiffConstants;

public class AcmCollectionElementAdded extends AcmCollectionElementChange implements AcmChangeDisplayable
{
    private Object affectedObject;
    private String displayOldValue;
    private String displayNewValue;

    public AcmCollectionElementAdded(Object object)
    {
        setAction(AcmDiffConstants.COLLECTION_ELEMENT_ADDED);
        setAffectedObject(object);
    }

    public Object getAffectedObject()
    {
        return affectedObject;
    }

    public void setAffectedObject(Object affectedObject)
    {
        this.affectedObject = affectedObject;
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
