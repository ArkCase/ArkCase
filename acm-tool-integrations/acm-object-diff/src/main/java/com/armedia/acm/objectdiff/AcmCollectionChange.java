package com.armedia.acm.objectdiff;

import java.util.LinkedList;
import java.util.List;

public class AcmCollectionChange extends AcmChange {
    private List<AcmCollectionElement> changes = new LinkedList<>();

    public AcmCollectionChange() {
    }

    public AcmCollectionChange(String path, String property, String action, Long affectedObjectId, String affectedObjectType) {
        setProperty(property);
        setPath(path);
        setAction(action);
        setAffectedObjectId(affectedObjectId);
        setAffectedObjectType(affectedObjectType);
    }

    public List<AcmCollectionElement> getChanges() {
        return changes;
    }

    public void addChange(AcmCollectionElement elementChange) {
        //TODO set parent path
        changes.add(elementChange);
    }
}
