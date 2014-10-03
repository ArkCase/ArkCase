package com.armedia.acm.plugins.person.model;


public class PersonAssociationAddEvent extends PersonAssociationPersistenceEvent
{
    private static final String EVENT_TYPE = "com.armedia.acm.personAssociation.created";

    public PersonAssociationAddEvent(PersonAssociation source)
    {
        super(source);
    }

    @Override
    public String getEventType()
    {
        return EVENT_TYPE;
    }

}
