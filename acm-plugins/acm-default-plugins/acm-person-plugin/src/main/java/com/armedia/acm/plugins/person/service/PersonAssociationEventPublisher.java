package com.armedia.acm.plugins.person.service;

import com.armedia.acm.auth.AcmAuthenticationDetails;
import com.armedia.acm.plugins.person.model.PersonAssociation;
import com.armedia.acm.plugins.person.model.PersonAssociationAddEvent;
import com.armedia.acm.plugins.person.model.PersonAssociationPersistenceEvent;
import com.armedia.acm.plugins.person.model.PersonAssociationUpdatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.security.core.Authentication;

/**
 * Created by armdev on 4/10/14.
 */
public class PersonAssociationEventPublisher implements ApplicationEventPublisherAware
{
    private ApplicationEventPublisher eventPublisher;

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        eventPublisher = applicationEventPublisher;
    }

    public void publishPersonAssociationEvent(
            PersonAssociation source,
            Authentication authentication,
            boolean newPersonAssociation,
            boolean succeeded)
    {
        if ( log.isDebugEnabled() )
        {
            log.debug("Publishing a person event.");
        }

        PersonAssociationPersistenceEvent personAssociationPersistenceEvent =
                newPersonAssociation ? new PersonAssociationAddEvent(source) : new PersonAssociationUpdatedEvent(source);
        personAssociationPersistenceEvent.setSucceeded(succeeded);
        if ( authentication.getDetails() != null && authentication.getDetails() instanceof AcmAuthenticationDetails)
        {
            personAssociationPersistenceEvent.setIpAddress(((AcmAuthenticationDetails) authentication.getDetails()).getRemoteAddress());
        }

        eventPublisher.publishEvent(personAssociationPersistenceEvent);
    }

}
