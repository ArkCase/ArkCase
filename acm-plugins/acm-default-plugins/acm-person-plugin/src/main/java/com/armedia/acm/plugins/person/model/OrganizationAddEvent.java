package com.armedia.acm.plugins.person.model;


public class OrganizationAddEvent extends OrganizationPersistenceEvent
{
    private static final String EVENT_TYPE = "com.armedia.acm.organization.created";

    public OrganizationAddEvent(Organization source)
    {
        super(source);
    }

    @Override
    public String getEventType()
    {
        return EVENT_TYPE;
    }

}
