package com.armedia.acm.plugins.person.model;


public class PersonAssociationUpdatedEvent extends PersonAssociationPersistenceEvent
{
    private static final String EVENT_TYPE = "com.armedia.acm.personAssociation.updated";

    public PersonAssociationUpdatedEvent(PersonAssociation source)
    {
        super(source);
    }

    @Override
    public String getEventType()
    {
        return EVENT_TYPE;
    }

}
