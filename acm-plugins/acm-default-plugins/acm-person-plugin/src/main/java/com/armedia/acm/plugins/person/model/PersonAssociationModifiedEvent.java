package com.armedia.acm.plugins.person.model;


public class PersonAssociationModifiedEvent extends PersonAssociationPersistenceEvent
{
    private static final String EVENT_TYPE = "com.armedia.acm.personAssociation";

    private String eventAction;

    public PersonAssociationModifiedEvent(PersonAssociation source, String ipAddress)
    {
        super(source);
        setIpAddress(ipAddress);
        setParentObjectId(source.getParentId());
        setParentObjectType(source.getParentType());
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
