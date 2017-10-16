package com.armedia.acm.plugins.person.model;


public class OrganizationAssociationUpdatedEvent extends OrganizationAssociationPersistenceEvent
{
    private static final String EVENT_TYPE = "com.armedia.acm.organizationAssociation.updated";

    public OrganizationAssociationUpdatedEvent(OrganizationAssociation source, String parentType, Long parentId)
    {
        super(source);
        setParentObjectId(parentId);
        setParentObjectType(parentType);
    }

    @Override
    public String getEventType()
    {
        return EVENT_TYPE;
    }

}
