package com.armedia.acm.plugins.person.service;

import com.armedia.acm.auth.AcmAuthenticationDetails;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.plugins.person.model.PersonAddEvent;
import com.armedia.acm.plugins.person.model.PersonPersistenceEvent;
import com.armedia.acm.plugins.person.model.PersonUpdatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.security.core.Authentication;

/**
 * Created by armdev on 4/10/14.
 */
public class PersonEventPublisher implements ApplicationEventPublisherAware
{
    private ApplicationEventPublisher eventPublisher;

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        eventPublisher = applicationEventPublisher;
    }

    public void publishPersonEvent(
            Person source,
            Authentication authentication,
            boolean newPerson,
            boolean succeeded)
    {
        if ( log.isDebugEnabled() )
        {
            log.debug("Publishing a person event.");
        }

        PersonPersistenceEvent personPersistenceEvent =
                newPerson ? new PersonAddEvent(source) : new PersonUpdatedEvent(source);
        personPersistenceEvent.setSucceeded(succeeded);
        if ( authentication.getDetails() != null && authentication.getDetails() instanceof AcmAuthenticationDetails)
        {
            personPersistenceEvent.setIpAddress(((AcmAuthenticationDetails) authentication.getDetails()).getRemoteAddress());
        }

        eventPublisher.publishEvent(personPersistenceEvent);
    }

}
