package com.armedia.acm.plugins.person.model;

import com.armedia.acm.core.model.AcmEvent;

import java.util.Date;

public abstract class OrganizationPersistenceEvent extends AcmEvent
{
    public OrganizationPersistenceEvent(Organization source)
    {
        super(source);
        setObjectId(source.getOrganizationId());
        setEventDate(new Date());
        setUserId(source.getModifier());

    }

    @Override
    public String getObjectType()
    {
        return PersonOrganizationConstants.ORGANIZATION_OBJECT_TYPE;
    }


}
