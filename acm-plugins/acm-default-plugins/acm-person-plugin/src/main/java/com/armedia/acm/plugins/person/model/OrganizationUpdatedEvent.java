package com.armedia.acm.plugins.person.model;


public class OrganizationUpdatedEvent extends OrganizationPersistenceEvent
{
    private static final String EVENT_TYPE = "com.armedia.acm.organization.updated";

    public OrganizationUpdatedEvent(Organization source, String ipAddress)
    {
        super(source);
        setIpAddress(ipAddress);
    }

    @Override
    public String getEventType()
    {
        return EVENT_TYPE;
    }

}
