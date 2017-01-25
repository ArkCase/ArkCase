package com.armedia.acm.plugins.person.model;


public class PersonAssociationAddEvent extends PersonAssociationPersistenceEvent
{
    private static final String EVENT_TYPE = "com.armedia.acm.personAssociation.created";

    public PersonAssociationAddEvent(PersonAssociation source, String parentType, Long parentId)
    {
        super(source);
        setParentObjectId(parentId);
        setParentObjectType(parentType);
        setEventDescription(parentType + " Updated - Person created (" + source.getPerson().getFullName() + ")");
    }

    @Override
    public String getEventType()
    {
        return EVENT_TYPE;
    }

}
