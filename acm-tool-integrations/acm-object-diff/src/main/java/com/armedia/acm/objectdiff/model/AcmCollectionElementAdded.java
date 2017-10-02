package com.armedia.acm.objectdiff.model;

public class AcmCollectionElementAdded extends AcmCollectionElementChange
{
    private Object affectedObject;

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
}
