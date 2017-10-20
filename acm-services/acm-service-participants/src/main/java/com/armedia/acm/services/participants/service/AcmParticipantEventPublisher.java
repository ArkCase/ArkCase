package com.armedia.acm.services.participants.service;

import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.services.participants.model.AcmParticipantCreatedEvent;
import com.armedia.acm.services.participants.model.AcmParticipantDeletedEvent;
import com.armedia.acm.services.participants.model.AcmParticipantUpdatedEvent;
import com.armedia.acm.web.api.MDCConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

public class AcmParticipantEventPublisher implements ApplicationEventPublisherAware
{

    private ApplicationEventPublisher eventPublisher;

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        eventPublisher = applicationEventPublisher;
    }

    public void publishParticipantCreatedEvent(AcmParticipant source, boolean succeeded)
    {

        if (log.isDebugEnabled())
        {
            log.debug("Publishing a participant event.");
        }
        AcmParticipantCreatedEvent participantCreatedEvent = new AcmParticipantCreatedEvent(source,
                MDC.get(MDCConstants.EVENT_MDC_REQUEST_USER_ID_KEY));
        participantCreatedEvent.setIpAddress(MDC.get(MDCConstants.EVENT_MDC_REQUEST_REMOTE_ADDRESS_KEY));

        participantCreatedEvent.setSucceeded(succeeded);

        eventPublisher.publishEvent(participantCreatedEvent);
    }

    public void publishParticipantDeletedEvent(AcmParticipant source, boolean succeeded)
    {
        if (log.isDebugEnabled())
        {
            log.debug("Publishing a Participant deleted event.");
        }
        AcmParticipantDeletedEvent participantDeletedEvent = new AcmParticipantDeletedEvent(source,
                MDC.get(MDCConstants.EVENT_MDC_REQUEST_USER_ID_KEY));
        participantDeletedEvent.setSucceeded(succeeded);

        eventPublisher.publishEvent(participantDeletedEvent);
    }

    public void publishParticipantUpdatedEvent(AcmParticipant source, boolean succeeded)
    {
        if (log.isDebugEnabled())
        {
            log.debug("Publishing a Participant updated event.");
        }
        AcmParticipantUpdatedEvent participantUpdatedEvent = new AcmParticipantUpdatedEvent(source,
                MDC.get(MDCConstants.EVENT_MDC_REQUEST_USER_ID_KEY));
        participantUpdatedEvent.setSucceeded(succeeded);

        eventPublisher.publishEvent(participantUpdatedEvent);
    }
}
