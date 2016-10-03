package com.armedia.acm.plugins.person.service;

import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.plugins.person.model.PersonAddEvent;
import com.armedia.acm.plugins.person.model.PersonPersistenceEvent;
import com.armedia.acm.plugins.person.model.PersonUpdatedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;


public class PersonEventPublisher implements ApplicationEventPublisherAware
{
    private ApplicationEventPublisher eventPublisher;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        eventPublisher = applicationEventPublisher;
    }

    public void publishPersonEvent(Person source, String ipAddress, boolean newPerson, boolean succeeded)
    {
        PersonPersistenceEvent personPersistenceEvent =
                newPerson ? new PersonAddEvent(source) : new PersonUpdatedEvent(source, ipAddress);
        personPersistenceEvent.setSucceeded(succeeded);
        eventPublisher.publishEvent(personPersistenceEvent);
    }

}
