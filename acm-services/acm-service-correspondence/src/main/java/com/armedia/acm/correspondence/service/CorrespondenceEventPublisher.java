package com.armedia.acm.correspondence.service;

import com.armedia.acm.correspondence.model.CorrespondenceAddedEvent;
import com.armedia.acm.plugins.ecm.model.EcmFile;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.security.core.Authentication;

public class CorrespondenceEventPublisher implements ApplicationEventPublisherAware
{

    private ApplicationEventPublisher eventPublisher;

    public void publishCorrespondenceAdded(EcmFile in, Authentication authentication, boolean succeeded)
    {
        CorrespondenceAddedEvent event = new CorrespondenceAddedEvent(in, authentication, succeeded);
        eventPublisher.publishEvent(event);
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        eventPublisher = applicationEventPublisher;
    }
}
