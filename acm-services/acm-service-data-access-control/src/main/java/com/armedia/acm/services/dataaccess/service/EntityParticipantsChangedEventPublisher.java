package com.armedia.acm.services.dataaccess.service;

import com.armedia.acm.core.AcmObject;
import com.armedia.acm.services.dataaccess.model.AcmEntityParticipantsChangedEvent;
import com.armedia.acm.services.participants.model.AcmParticipant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

import java.util.List;

public class EntityParticipantsChangedEventPublisher implements ApplicationEventPublisherAware
{
    private Logger LOG = LoggerFactory.getLogger(getClass());

    private ApplicationEventPublisher applicationEventPublisher;

    public void publishEvent(AcmObject source, List<AcmParticipant> originalParticipants)
    {
        LOG.debug("Publishing AcmTimesheet event.");

        AcmEntityParticipantsChangedEvent event = new AcmEntityParticipantsChangedEvent(source, originalParticipants);

        getApplicationEventPublisher().publishEvent(event);
    }

    public ApplicationEventPublisher getApplicationEventPublisher()
    {
        return applicationEventPublisher;
    }

    @Override
    public void setApplicationEventPublisher(
            ApplicationEventPublisher applicationEventPublisher)
    {
        this.applicationEventPublisher = applicationEventPublisher;
    }
}
