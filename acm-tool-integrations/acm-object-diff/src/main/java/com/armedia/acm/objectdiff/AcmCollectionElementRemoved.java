package com.armedia.acm.objectdiff;

public class AcmCollectionElementRemoved extends AcmCollectionElement {
    public AcmCollectionElementRemoved(Object object) {
        setAction("removed");
        setAffectedObject(object);
    }
}
