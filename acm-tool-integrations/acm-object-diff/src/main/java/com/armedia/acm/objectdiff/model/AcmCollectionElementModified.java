package com.armedia.acm.objectdiff.model;

import java.util.LinkedList;
import java.util.List;

public class AcmCollectionElementModified extends AcmCollectionElementChange
{
    private List<AcmPropertyChange> changes = new LinkedList<>();

    public AcmCollectionElementModified(AcmObjectModified acmObjectModified)
    {
        setChanges(acmObjectModified.getChanges());
        setAction(AcmDiffConstants.COLLECTION_ELEMENT_MODIFIED);
        setAffectedObjectId(acmObjectModified.getAffectedObjectId());
        setAffectedObjectType(acmObjectModified.getAffectedObjectType());
        setPath(acmObjectModified.getPath());
    }


    public List<AcmPropertyChange> getChanges()
    {
        return changes;
    }

    public void addChange(AcmPropertyChange change)
    {
        this.changes.add(change);
    }

    protected void setChanges(List<AcmPropertyChange> changes)
    {
        this.changes = changes;
    }
}
