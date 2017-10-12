package com.armedia.acm.objectdiff.model;

import com.armedia.acm.objectdiff.model.interfaces.AcmChangeDisplayable;
import com.armedia.acm.objectdiff.model.interfaces.AcmDiffConstants;

public class AcmCollectionElementRemoved extends AcmCollectionElementChange implements AcmChangeDisplayable
{
    private Object affectedObject;
    private String displayOldValue;
    private String displayNewValue;

    public AcmCollectionElementRemoved(Object object)
    {
        setAction(AcmDiffConstants.COLLECTION_ELEMENT_REMOVED);
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
