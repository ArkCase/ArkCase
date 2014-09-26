package com.armedia.acm.plugins.person.model;


public class PersonAddEvent extends PersonPersistenceEvent
{
    private static final String EVENT_TYPE = "com.armedia.acm.person.created";

    public PersonAddEvent(Person source)
    {
        super(source);
    }

    @Override
    public String getEventType()
    {
        return EVENT_TYPE;
    }

}
