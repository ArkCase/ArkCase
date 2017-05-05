package com.armedia.acm.plugins.person.model;

public class OrganizationAssociationDeletedEvent extends OrganizationAssociationPersistenceEvent
{
    private static final String EVENT_TYPE = "com.armedia.acm.organizationAssociation.deleted";

    public OrganizationAssociationDeletedEvent(OrganizationAssociation source, String parentType, Long parentId)
    {
        super(source);
        setParentObjectId(parentId);
        setParentObjectType(parentType);
        setEventDescription("Organization deleted (" + source.getOrganization().getOrganizationValue() + ")");
    }

    @Override
    public String getEventType()
    {
        return EVENT_TYPE;
    }
}