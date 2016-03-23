package com.armedia.acm.plugins.objectassociation.model;

import java.util.Collection;

/**
 * 
 * @author vladimir.radeski
 *
 */

public interface AcmChildObjectEntity
{
    public Collection<ObjectAssociation> getChildObjects();

    public void addChildObject(ObjectAssociation childObject);
}
