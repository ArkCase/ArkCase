package com.armedia.acm.plugins.person.model;


public class PersonModifiedEvent extends PersonPersistenceEvent
{
    private static final String EVENT_TYPE = "com.armedia.acm.person";

    private String eventAction;

    public PersonModifiedEvent(Person source, String ipAddress)
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
