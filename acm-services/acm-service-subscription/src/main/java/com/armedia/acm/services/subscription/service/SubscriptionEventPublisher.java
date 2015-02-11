package com.armedia.acm.services.subscription.service;

import com.armedia.acm.auth.AcmAuthenticationDetails;
import com.armedia.acm.services.subscription.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.security.core.Authentication;

/**
 * Created by marjan.stefanoski on 11.02.2015.
 */
public class SubscriptionEventPublisher  implements ApplicationEventPublisherAware {

    private ApplicationEventPublisher eventPublisher;

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void setApplicationEventPublisher( ApplicationEventPublisher applicationEventPublisher ) {
        eventPublisher = applicationEventPublisher;
    }

    public void publishSubscriptionCreatedEvent( AcmSubscription source, Authentication auth, boolean succeeded ) {
        if ( log.isDebugEnabled() ) {
            log.debug("Publishing a subscription event.");
        }
        SubscriptionCreatedEvent subscriptionPersistenceEvent = new SubscriptionCreatedEvent(source);
        if ( auth.getDetails() != null && auth.getDetails() instanceof AcmAuthenticationDetails) {
            subscriptionPersistenceEvent.setIpAddress(((AcmAuthenticationDetails) auth.getDetails()).getRemoteAddress());
        }
        subscriptionPersistenceEvent.setSucceeded(succeeded);

        eventPublisher.publishEvent(subscriptionPersistenceEvent);
    }

    public void publishSubscriptionDeletedEvent( String userId, Long objectId,String objectType, boolean succeeded ) {
        if ( log.isDebugEnabled() ) {
            log.debug("Publishing a subscription deleted event.");
        }
        SubscriptionDeletedEvent  subscriptionDeletedEvent = new SubscriptionDeletedEvent( userId, objectId, objectType );
        subscriptionDeletedEvent.setSucceeded(succeeded);

        eventPublisher.publishEvent(subscriptionDeletedEvent);
    }

    public void publishAcmSubscriptionEventCreatedEvent( AcmSubscriptionEvent source, boolean succeeded ) {
        if ( log.isDebugEnabled() ) {
            log.debug("Publishing a AcmSubscriptionEvent created event.");
        }
        AcmSubscriptionEventPersistenceEvent acmSubscriptionEventPersistenceEvent = new AcmSubscriptionEventCreatedEvent(source);
        acmSubscriptionEventPersistenceEvent.setSucceeded(succeeded);

        eventPublisher.publishEvent(acmSubscriptionEventPersistenceEvent);
    }
}
