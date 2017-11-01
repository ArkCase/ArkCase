package com.armedia.acm.objectdiff.model;

import com.armedia.acm.objectdiff.model.interfaces.AcmChangeContainer;
import com.armedia.acm.objectonverter.ObjectConverter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class AcmObjectDiff extends AcmDiff
{
    private AcmObjectChange acmObjectChange;

    public AcmObjectDiff(AcmObjectChange acmObjectChange, ObjectConverter objectConverter)
    {
        super(objectConverter);
        this.acmObjectChange = acmObjectChange;
    }

    @Override
    public AcmChange getChangesAsTree()
    {
        return acmObjectChange;
    }

    @Override
    public List<AcmChange> getChangesAsList()
    {
        if (acmObjectChange instanceof AcmObjectModified)
        {
            return getChangesForChangeContainer((AcmObjectModified) acmObjectChange);
        }
        else
        {
            ArrayList<AcmChange> acmChanges = new ArrayList<>();
            if (acmObjectChange != null)
            {
                acmChanges.add(acmObjectChange);
            }
            return acmChanges;
        }
    }

    private List<AcmChange> getChangesForChangeContainer(AcmChangeContainer acmChangeContainer)
    {
        List<AcmChange> changes = new LinkedList<>();
        for (AcmChange change : acmChangeContainer.getChanges())
        {
            if (change.isLeaf())
            {
                changes.add(change);
            }
            else if (change instanceof AcmChangeContainer)
            {
                changes.addAll(getChangesForChangeContainer((AcmChangeContainer) change));
            }
        }
        return changes;
    }
}
