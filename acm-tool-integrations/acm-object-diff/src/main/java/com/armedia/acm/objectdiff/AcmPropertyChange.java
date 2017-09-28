package com.armedia.acm.objectdiff;

public class AcmPropertyChange extends AcmChange {
    private Object oldValue;
    private Object newValue;

    public AcmPropertyChange(String path, String property, String action) {
        setProperty(property);
        setPath(path);
        setAction(action);
    }

    public AcmPropertyChange(String path, String property, String action, Long affectedObjectId, String affectedObjectType) {
        setProperty(property);
        setPath(path);
        setAction(action);
        setAffectedObjectId(affectedObjectId);
        setAffectedObjectType(affectedObjectType);
    }

    public Object getOldValue() {
        return oldValue;
    }

    public void setOldValue(Object oldValue) {
        this.oldValue = oldValue;
    }

    public Object getNewValue() {
        return newValue;
    }

    public void setNewValue(Object newValue) {
        this.newValue = newValue;
    }
}
