package com.armedia.acm.objectdiff.model;

import com.armedia.acm.objectdiff.model.interfaces.AcmChangeContainer;
import com.armedia.acm.objectdiff.model.interfaces.AcmDiffConstants;

import java.util.LinkedList;
import java.util.List;

public class AcmObjectModified extends AcmObjectChange implements AcmChangeContainer
{
    private Long affectedObjectId;
    private String affectedObjectType;

    public AcmObjectModified()
    {
        setAction(AcmDiffConstants.OBJECT_MODIFIED);
    }

    private List<AcmChange> changes = new LinkedList<>();

    public List<AcmChange> getChanges()
    {
        return changes;
    }

    public void setChanges(List<AcmChange> changes)
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

    @Override
    public boolean isLeaf()
    {
        return false;
    }
}
