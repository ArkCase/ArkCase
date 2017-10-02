package com.armedia.acm.objectdiff.model;

public class AcmCollectionElementAdded extends AcmCollectionElementChange
{
    public AcmCollectionElementAdded(Object object)
    {
        setAction(AcmDiffConstants.COLLECTION_ELEMENT_ADDED);
        setAffectedObject(object);
    }
}
