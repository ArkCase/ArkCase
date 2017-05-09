package com.armedia.acm.plugins.person.model;


public class OrganizationAssociationAddEvent extends OrganizationAssociationPersistenceEvent
{
    private static final String EVENT_TYPE = "com.armedia.acm.organizationAssociation.created";

    public OrganizationAssociationAddEvent(OrganizationAssociation source, String parentType, Long parentId)
    {
        super(source);
        setParentObjectId(parentId);
        setParentObjectType(parentType);
        setEventDescription("Organization created (" + source.getOrganization().getOrganizationValue() + ")");
    }

    @Override
    public String getEventType()
    {
        return EVENT_TYPE;
    }

}
