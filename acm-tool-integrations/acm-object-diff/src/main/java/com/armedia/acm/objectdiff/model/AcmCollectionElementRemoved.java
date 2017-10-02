package com.armedia.acm.objectdiff.model;

public class AcmCollectionElementRemoved extends AcmCollectionElementChange
{
    public AcmCollectionElementRemoved(Object object)
    {
        setAction(AcmDiffConstants.COLLECTION_ELEMENT_REMOVED);
        setAffectedObject(object);
    }
}
