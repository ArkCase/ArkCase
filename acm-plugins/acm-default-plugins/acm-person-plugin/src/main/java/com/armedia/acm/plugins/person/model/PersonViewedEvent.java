package com.armedia.acm.plugins.person.model;

public class PersonViewedEvent extends PersonPersistenceEvent
{
    private static final String EVENT_TYPE = "com.armedia.acm.person.viewed";

    public PersonViewedEvent(Person source, String ipAddress)
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