package com.armedia.acm.plugins.person.model;


public class OrganizationAssociationModifiedEvent extends OrganizationAssociationPersistenceEvent
{
    private static final String EVENT_TYPE = "com.armedia.acm.organizationAssociation";

    private String eventAction;

    public OrganizationAssociationModifiedEvent(OrganizationAssociation source, String ipAddress)
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
