package com.armedia.acm.plugins.person.service;

import com.armedia.acm.plugins.person.model.PersonAssociation;
import com.armedia.acm.plugins.person.model.PersonAssociationAddEvent;
import com.armedia.acm.plugins.person.model.PersonAssociationDeletedEvent;
import com.armedia.acm.plugins.person.model.PersonAssociationPersistenceEvent;
import com.armedia.acm.plugins.person.model.PersonAssociationUpdatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

/**
 * Created by armdev on 4/10/14.
 */
public class PersonAssociationEventPublisher implements ApplicationEventPublisherAware
{
    private transient final Logger log = LoggerFactory.getLogger(getClass());
    private ApplicationEventPublisher eventPublisher;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        eventPublisher = applicationEventPublisher;
    }

    public void publishPersonAssociationEvent(
            PersonAssociation source,
            String ipAddress,
            boolean newPersonAssociation,
            boolean succeeded)
    {
        log.debug("Publishing a person event.");

        PersonAssociationPersistenceEvent personAssociationPersistenceEvent =
                newPersonAssociation ? new PersonAssociationAddEvent(source, source.getParentType(), source.getParentId()) :
                        new PersonAssociationUpdatedEvent(source, source.getParentType(), source.getParentId());
        personAssociationPersistenceEvent.setSucceeded(succeeded);

        eventPublisher.publishEvent(personAssociationPersistenceEvent);
    }

    public void publishPersonAssociationDeletedEvent(PersonAssociation source)
    {
        PersonAssociationDeletedEvent event = new PersonAssociationDeletedEvent(source, source.getParentType(), source.getParentId());
        event.setSucceeded(true);
        eventPublisher.publishEvent(event);
    }

}
