package com.armedia.acm.objectdiff.model;

import com.armedia.acm.objectdiff.model.interfaces.AcmChangeContainer;
import com.armedia.acm.objectdiff.model.interfaces.AcmDiffConstants;

import java.util.LinkedList;
import java.util.List;

public class AcmCollectionChange extends AcmPropertyChange implements AcmChangeContainer
{
    private List<AcmChange> changes = new LinkedList<>();

    public AcmCollectionChange(String path, String property)
    {
        setProperty(property);
        setPath(path);
        setAction(AcmDiffConstants.COLLECTION_CHANGED);
    }

    public List<AcmChange> getChanges()
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
