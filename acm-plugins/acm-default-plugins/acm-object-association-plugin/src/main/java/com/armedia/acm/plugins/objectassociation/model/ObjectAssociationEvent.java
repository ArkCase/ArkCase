package com.armedia.acm.plugins.objectassociation.model;

import com.armedia.acm.core.model.AcmEvent;
import org.springframework.security.core.Authentication;

import java.util.Date;

public class ObjectAssociationEvent extends AcmEvent
{
    public enum ObjectAssociationState {
        NEW,
        UPDATE,
        DELETE
    }

    public ObjectAssociationEvent(ObjectAssociation source)
    {
        super(source);
        setObjectId(source.getParentId());
        setObjectType(source.getParentType());
        setEventDate(new Date());
    }

    private static final String EVENT_TYPE = "com.armedia.acm.objectassociation.created";

    private ObjectAssociationState objectAssociationState;

    private Authentication authentication;

    @Override
    public String getEventType()
    {
        return EVENT_TYPE;
    }

    public ObjectAssociationState getObjectAssociationState()
    {
        return objectAssociationState;
    }

    public void setObjectAssociationState(ObjectAssociationState objectAssociationState)
    {
        this.objectAssociationState = objectAssociationState;
    }

    public Authentication getAuthentication()
    {
        return authentication;
    }

    public void setAuthentication(Authentication authentication)
    {
        this.authentication = authentication;
    }
}
