package com.armedia.acm.services.tag.service;

import com.armedia.acm.auth.AcmAuthenticationDetails;
import com.armedia.acm.services.tag.model.AcmTag;
import com.armedia.acm.services.tag.model.AcmTagCreatedEvent;
import com.armedia.acm.services.tag.model.AcmTagDeletedEvent;
import com.armedia.acm.services.tag.model.AcmTagUpdatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.security.core.Authentication;

/**
 * Created by marjan.stefanoski on 11.02.2015.
 */
public class TagEventPublisher implements ApplicationEventPublisherAware {

    private ApplicationEventPublisher eventPublisher;

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void setApplicationEventPublisher( ApplicationEventPublisher applicationEventPublisher ) {
        eventPublisher = applicationEventPublisher;
    }

    public void publishTagCreatedEvent( AcmTag source, Authentication auth, boolean succeeded ) {
        if ( log.isDebugEnabled() ) {
            log.debug("Publishing a tag event.");
        }
        AcmTagCreatedEvent tagPersistenceEvent = new AcmTagCreatedEvent(source, auth.getName());
        if ( auth.getDetails() != null && auth.getDetails() instanceof AcmAuthenticationDetails) {
            tagPersistenceEvent.setIpAddress(((AcmAuthenticationDetails) auth.getDetails()).getRemoteAddress());
        }
        tagPersistenceEvent.setSucceeded(succeeded);

        eventPublisher.publishEvent(tagPersistenceEvent);
    }

    public void publishTagDeletedEvent( AcmTag source, Authentication auth, boolean succeeded ) {
        if ( log.isDebugEnabled() ) {
            log.debug("Publishing a tag deleted event.");
        }
        AcmTagDeletedEvent tagDeletedEvent = new AcmTagDeletedEvent(source,auth.getName());
        tagDeletedEvent.setSucceeded(succeeded);

        eventPublisher.publishEvent(tagDeletedEvent);
    }

    public void publishTagUpdatedEvent( AcmTag source, Authentication auth, boolean succeeded ) {
        if ( log.isDebugEnabled() ) {
            log.debug("Publishing a tag updated event.");
        }
        AcmTagUpdatedEvent tagUpdatedEvent = new AcmTagUpdatedEvent(source,auth.getName());
        tagUpdatedEvent.setSucceeded(succeeded);

        eventPublisher.publishEvent(tagUpdatedEvent);
    }
}
