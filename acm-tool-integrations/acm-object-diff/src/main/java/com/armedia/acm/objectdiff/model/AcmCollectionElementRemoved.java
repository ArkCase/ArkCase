package com.armedia.acm.objectdiff.model;

public class AcmCollectionElementRemoved extends AcmCollectionElementChange
{
    private Object affectedObject;

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

    @Override
    public boolean isLeaf()
    {
        return true;
    }
}
