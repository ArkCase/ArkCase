package com.armedia.acm.objectdiff.model;

import java.util.LinkedList;
import java.util.List;

public class AcmObjectModified extends AcmPropertyChange
{
    private Long affectedObjectId;
    private String affectedObjectType;

    public AcmObjectModified()
    {
        setAction(AcmDiffConstants.OBJECT_MODIFIED);
    }

    public AcmObjectModified(String path, Long objectId, String objectType)
    {
        this();
        setPath(path);
        setAffectedObjectId(objectId);
        setAffectedObjectType(objectType);
    }

    private List<AcmPropertyChange> changes = new LinkedList<>();

    public List<AcmPropertyChange> getChanges()
    {
        return changes;
    }

    public void setChanges(List<AcmPropertyChange> changes)
    {
        this.changes = changes;
    }

    public void addChange(AcmPropertyChange change)
    {
        changes.add(change);
    }

    public Long getAffectedObjectId()
    {
        return affectedObjectId;
    }

    public void setAffectedObjectId(Long affectedObjectId)
    {
        this.affectedObjectId = affectedObjectId;
    }

    public String getAffectedObjectType()
    {
        return affectedObjectType;
    }

    public void setAffectedObjectType(String affectedObjectType)
    {
        this.affectedObjectType = affectedObjectType;
    }
}
