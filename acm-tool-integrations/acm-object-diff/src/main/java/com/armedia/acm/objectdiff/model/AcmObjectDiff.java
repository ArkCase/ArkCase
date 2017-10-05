package com.armedia.acm.objectdiff.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class AcmObjectDiff extends AcmDiff
{
    private AcmObjectChange acmObjectChange;

    public AcmObjectDiff(AcmObjectChange acmObjectChange)
    {
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
            return getChangesForObjectModified((AcmObjectModified) acmObjectChange);
        } else
        {
            return new ArrayList<>();
        }
    }

    private List<AcmChange> getChangesForObjectModified(AcmObjectModified objectModified)
    {
        List<AcmChange> changes = new LinkedList<>();
        for (AcmChange change : objectModified.getChanges())
        {
            if (change.isLeaf())
            {
                changes.add(change);
            } else if (change instanceof AcmObjectModified)
            {
                changes.addAll(getChangesForObjectModified((AcmObjectModified) change));
            } else if (change instanceof AcmCollectionElementModified)
            {
                changes.addAll(getChangesForObjectModified(((AcmCollectionElementModified) change).getAcmObjectModified()));
            }
        }
        return changes;
    }
}
