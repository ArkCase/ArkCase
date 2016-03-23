package com.armedia.acm.plugins.objectassociation.service;

import com.armedia.acm.plugins.objectassociation.model.AddReferenceEvent;
import com.armedia.acm.plugins.objectassociation.model.Reference;

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

        String user = authentication.getName();
        event.setUserId(user);
        event.setSucceeded(succeeded);

        eventPublisher.publishEvent(event);
    }

}
