package com.armedia.acm.plugins.person.service;

import com.armedia.acm.auth.AuthenticationUtils;
import com.armedia.acm.objectdiff.model.AcmDiff;
import com.armedia.acm.objectdiff.service.AcmDiffService;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.plugins.person.model.PersonModifiedEvent;
import com.armedia.acm.plugins.person.model.PersonPersistenceEvent;
import com.armedia.acm.plugins.person.model.PersonUpdatedImageEvent;
import com.armedia.acm.plugins.person.model.PersonViewedEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

public class PersonEventPublisher implements ApplicationEventPublisherAware
{
    private transient final Logger log = LoggerFactory.getLogger(getClass());
    private ApplicationEventPublisher eventPublisher;
    private AcmDiffService acmDiffService;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        eventPublisher = applicationEventPublisher;
    }

    public void publishEvent(Person updatedPerson)
    {
        eventPublisher.publishEvent(updatedPerson);
    }

    public void publishPersonViewedEvent(Person updatedPerson, boolean succeeded)
    {
        log.debug("Publishing a Person viewed event.");
        String ipAddress = AuthenticationUtils.getUserIpAddress();
        PersonPersistenceEvent event = new PersonViewedEvent(updatedPerson, ipAddress);
        event.setSucceeded(succeeded);
        eventPublisher.publishEvent(event);
    }

    public void publishPersonUpsertEvents(Person updatedPerson, Person oldPerson, boolean newPerson, boolean succeeded)
    {
        log.debug("Publishing a person event.");
        String ipAddress = AuthenticationUtils.getUserIpAddress();

        PersonModifiedEvent personPersistenceEvent = new PersonModifiedEvent(updatedPerson, ipAddress);
        if (newPerson)
        {
            personPersistenceEvent.setEventAction("created");
        } else
        {
            AcmDiff acmDiff = acmDiffService.compareObjects(oldPerson, updatedPerson);
            if (acmDiff != null)
            {
                try
                {
                    personPersistenceEvent.setDiffDetailsAsJson(acmDiff.getChangesAsListJson());
                } catch (JsonProcessingException e)
                {
                    log.warn("can't process diff details for [{}].", updatedPerson, e);
                }
            }
            personPersistenceEvent.setEventAction("updated");
        }
        personPersistenceEvent.setSucceeded(succeeded);
        eventPublisher.publishEvent(personPersistenceEvent);
    }

    public void publishPersonImageEvent(Person updatedPerson, boolean succeeded)
    {
        log.debug("Publishing a Person image upload event.");
        String ipAddress = AuthenticationUtils.getUserIpAddress();
        PersonPersistenceEvent event = new PersonUpdatedImageEvent(updatedPerson, ipAddress);
        event.setSucceeded(succeeded);
        eventPublisher.publishEvent(event);
    }

    public void setAcmDiffService(AcmDiffService acmDiffService)
    {
        this.acmDiffService = acmDiffService;
    }
}
