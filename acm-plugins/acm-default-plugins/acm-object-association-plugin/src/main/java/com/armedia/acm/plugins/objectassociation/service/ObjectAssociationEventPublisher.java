package com.armedia.acm.plugins.objectassociation.service;

import com.armedia.acm.core.model.AcmEvent;
import com.armedia.acm.plugins.objectassociation.model.*;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.security.core.Authentication;

/**
 * 
 * @author vladimir.radeski
 *
 */

public class ObjectAssociationEventPublisher implements ApplicationEventPublisherAware
{

    private ApplicationEventPublisher eventPublisher;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        eventPublisher = applicationEventPublisher;
    }

    public void publishAddReferenceEvent(Reference source, Authentication authentication, boolean succeeded)
    {
        AddReferenceEvent event = new AddReferenceEvent(source);
        publishEvent(event, authentication, succeeded);
    }

    public void publishUpdateReferenceEvent(Reference source, Authentication authentication, boolean succeeded)
    {
        UpdateReferenceEvent event = new UpdateReferenceEvent(source);
        publishEvent(event, authentication, succeeded);
    }

    public void publishDeleteReferenceEvent(Reference source, Authentication authentication, boolean succeeded)
    {
        DeleteReferenceEvent event = new DeleteReferenceEvent(source);
        publishEvent(event, authentication, succeeded);
    }

    public void publishObjectAssociationEvent(ObjectAssociation source, Authentication authentication, boolean succeeded, String objectAssociationState)
    {
        ObjectAssociationEvent event = new ObjectAssociationEvent(source);
        event.setAuthentication(authentication);
        event.setObjectAssociationState(ObjectAssociationEvent.ObjectAssociationState.valueOf(objectAssociationState));
        publishEvent(event, authentication, succeeded);
    }

    private void publishEvent(AcmEvent event, Authentication authentication, boolean succeeded)
    {
        event.setUserId(authentication.getName());
        event.setSucceeded(succeeded);
        eventPublisher.publishEvent(event);
    }
}
