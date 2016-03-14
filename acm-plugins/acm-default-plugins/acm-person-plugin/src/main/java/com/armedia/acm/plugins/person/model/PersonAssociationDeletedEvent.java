package com.armedia.acm.plugins.person.model;

public class PersonAssociationDeletedEvent extends PersonAssociationPersistenceEvent
{
    private static final String EVENT_TYPE = "com.armedia.acm.personAssociation.deleted";

    public PersonAssociationDeletedEvent(PersonAssociation source)
    {
        super(source);
    }

    @Override
    public String getEventType()
    {
        return EVENT_TYPE;
    }
}