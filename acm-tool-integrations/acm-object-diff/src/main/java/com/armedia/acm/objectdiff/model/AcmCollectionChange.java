package com.armedia.acm.objectdiff.model;

import java.util.LinkedList;
import java.util.List;

public class AcmCollectionChange extends AcmPropertyChange
{
    private List<AcmCollectionElementChange> changes = new LinkedList<>();

    public AcmCollectionChange(String path, String property)
    {
        setProperty(property);
        setPath(path);
        setAction(AcmDiffConstants.COLLECTION_CHANGED);
    }

    public List<AcmCollectionElementChange> getChanges()
    {
        return changes;
    }

    public void addChange(AcmCollectionElementChange elementChange)
    {
        changes.add(elementChange);
    }

    @Override
    public boolean isLeaf()
    {
        return false;
    }
}
