package com.armedia.acm.plugins.person.model;


public class OrganizationModifiedEvent extends OrganizationPersistenceEvent
{
    private static final String EVENT_TYPE = "com.armedia.acm.organization";

    private String eventAction;

    public OrganizationModifiedEvent(Organization source, String ipAddress)
    {
        super(source);
        setIpAddress(ipAddress);
    }

    @Override
    public String getEventType()
    {
        return String.format("%s.%s", EVENT_TYPE, eventAction);
    }

    public void setEventAction(String eventAction)
    {
        this.eventAction = eventAction;
    }
}
