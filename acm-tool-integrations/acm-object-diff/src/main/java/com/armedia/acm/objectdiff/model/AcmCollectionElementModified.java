package com.armedia.acm.objectdiff.model;

import java.util.List;

public class AcmCollectionElementModified extends AcmCollectionElementChange
{
    private AcmObjectModified acmObjectModified;

    public AcmCollectionElementModified(AcmObjectModified acmObjectModified)
    {
        this.acmObjectModified = acmObjectModified;
        setAction(AcmDiffConstants.COLLECTION_ELEMENT_MODIFIED);
        setAffectedObjectId(acmObjectModified.getAffectedObjectId());
        setAffectedObjectType(acmObjectModified.getAffectedObjectType());
        setPath(acmObjectModified.getPath());
    }

    public AcmObjectModified getAcmObjectModified()
    {
        return acmObjectModified;
    }


    public List<AcmPropertyChange> getChanges()
    {
        return acmObjectModified.getChanges();
    }

    public void addChange(AcmPropertyChange change)
    {
        this.acmObjectModified.addChange(change);
    }

    @Override
    public boolean isLeaf()
    {
        return false;
    }
}
