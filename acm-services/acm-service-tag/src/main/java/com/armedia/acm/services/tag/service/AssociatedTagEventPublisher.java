package com.armedia.acm.services.tag.service;

import com.armedia.acm.auth.AcmAuthenticationDetails;
import com.armedia.acm.services.tag.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.security.core.Authentication;

public class AssociatedTagEventPublisher implements ApplicationEventPublisherAware {
    private ApplicationEventPublisher eventPublisher;

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void setApplicationEventPublisher( ApplicationEventPublisher applicationEventPublisher ) {
        eventPublisher = applicationEventPublisher;
    }

    public void publishAssociatedTagCreatedEvent( AcmAssociatedTag source, Authentication auth, boolean succeeded ) {
        if ( log.isDebugEnabled() ) {
            log.debug("Publishing an associated tag event.");
        }
        AcmAssociatedTagCreatedEvent associatedTagCreatedEvent = new AcmAssociatedTagCreatedEvent(source, auth.getName());
        if ( auth.getDetails() != null && auth.getDetails() instanceof AcmAuthenticationDetails) {
            associatedTagCreatedEvent.setIpAddress(((AcmAuthenticationDetails) auth.getDetails()).getRemoteAddress());
        }
        associatedTagCreatedEvent.setSucceeded(succeeded);

        eventPublisher.publishEvent(associatedTagCreatedEvent);
    }

    public void publishAssociatedTagDeletedEvent( AcmAssociatedTag source, Authentication auth, boolean succeeded ) {
        if ( log.isDebugEnabled() ) {
            log.debug("Publishing an associated tag deleted event.");
        }
        AcmAssociatedTagDeletedEvent associatedTagDeletedEvent = new AcmAssociatedTagDeletedEvent(source,auth.getName());
        associatedTagDeletedEvent.setSucceeded(succeeded);

        eventPublisher.publishEvent(associatedTagDeletedEvent);
    }
}
