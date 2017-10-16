package com.armedia.acm.plugins.objectassociation.model;

import java.util.Collection;


public interface AcmChildObjectEntity
{
    Collection<ObjectAssociation> getChildObjects();

    void addChildObject(ObjectAssociation childObject);
}
