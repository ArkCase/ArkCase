package com.armedia.acm.objectdiff;

import java.util.LinkedList;
import java.util.List;

public class AcmObjectChange extends AcmChange {

    public AcmObjectChange() {
    }

    public AcmObjectChange(String path, Long objectId, String objectType) {
        setPath(path);
        setAction("objectChange");
        setAffectedObjectId(objectId);
        setAffectedObjectType(objectType);
    }

    private List<AcmChange> changes = new LinkedList<>();

    public List<AcmChange> getChanges() {
        return changes;
    }

    public void setChanges(List<AcmChange> changes) {
        this.changes = changes;
    }

    public void addChange(AcmChange change) {
        //TODO set parent path...
        changes.add(change);
    }
}
