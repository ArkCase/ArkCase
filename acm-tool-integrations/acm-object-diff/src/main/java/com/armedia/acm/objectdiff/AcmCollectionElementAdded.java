package com.armedia.acm.objectdiff;

public class AcmCollectionElementAdded extends AcmCollectionElement {
    public AcmCollectionElementAdded(Object object) {
        setAction("added");
        setAffectedObject(object);
    }
}
