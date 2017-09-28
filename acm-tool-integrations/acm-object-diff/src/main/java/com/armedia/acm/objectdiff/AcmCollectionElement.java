package com.armedia.acm.objectdiff;

public abstract class AcmCollectionElement extends AcmChange {
    private Object affectedObject;

    public Object getAffectedObject() {
        return affectedObject;
    }

    public void setAffectedObject(Object affectedObject) {
        this.affectedObject = affectedObject;
    }
}
