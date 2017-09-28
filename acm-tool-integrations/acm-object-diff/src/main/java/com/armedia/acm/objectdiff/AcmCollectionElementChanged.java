package com.armedia.acm.objectdiff;

import java.util.LinkedList;
import java.util.List;

public class AcmCollectionElementChanged extends AcmCollectionElement {
    private List<AcmChange> changes = new LinkedList<>();

    public AcmCollectionElementChanged(AcmObjectChange acmObjectChange) {
        setChanges(acmObjectChange.getChanges());
        setAction(acmObjectChange.getAction());
        setAffectedObjectId(acmObjectChange.getAffectedObjectId());
        setAffectedObjectType(acmObjectChange.getAffectedObjectType());
        setPath(acmObjectChange.getPath());
        setProperty(acmObjectChange.getProperty());
    }

    public List<AcmChange> getChanges() {
        return changes;
    }

    public void addChange(AcmChange change) {
        this.changes.add(change);
    }

    protected void setChanges(List<AcmChange> changes) {
        this.changes = changes;
    }
}
