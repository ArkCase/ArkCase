package com.armedia.acm.plugins.person.model;

import com.armedia.acm.core.model.AcmEvent;

import java.util.Date;

public abstract class OrganizationAssociationPersistenceEvent extends AcmEvent
{
    public OrganizationAssociationPersistenceEvent(OrganizationAssociation source)
    {
        super(source);
        setObjectId(source.getId());
        setEventDate(new Date());
        setUserId(source.getModifier());

    }

    @Override
    public String getObjectType()
    {
        return PersonOrganizationConstants.ORGANIZATION_ASSOCIATION_OBJECT_TYPE;
    }


}
