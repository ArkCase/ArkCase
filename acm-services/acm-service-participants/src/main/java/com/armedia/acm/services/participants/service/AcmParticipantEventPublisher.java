package com.armedia.acm.services.participants.service;

import com.armedia.acm.auth.AcmAuthenticationDetails;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.services.participants.model.AcmParticipantCreatedEvent;
import com.armedia.acm.services.participants.model.AcmParticipantDeletedEvent;
import com.armedia.acm.services.participants.model.AcmParticipantUpdatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.security.core.Authentication;

public class AcmParticipantEventPublisher implements ApplicationEventPublisherAware {

    private ApplicationEventPublisher eventPublisher;

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void setApplicationEventPublisher( ApplicationEventPublisher applicationEventPublisher ) {
        eventPublisher = applicationEventPublisher;
    }

    public void publishParticipantCreatedEvent( AcmParticipant source, Authentication auth, boolean succeeded ) {

        if ( log.isDebugEnabled() ) {
            log.debug("Publishing a participant event.");
        }
        AcmParticipantCreatedEvent participantCreatedEvent = new AcmParticipantCreatedEvent(source, auth.getName());
        if ( auth.getDetails() != null && auth.getDetails() instanceof AcmAuthenticationDetails) {
            participantCreatedEvent.setIpAddress(((AcmAuthenticationDetails) auth.getDetails()).getRemoteAddress());
        }
        participantCreatedEvent.setSucceeded(succeeded);

        eventPublisher.publishEvent(participantCreatedEvent);
    }

    public void publishParticipantDeletedEvent( AcmParticipant source, Authentication auth, boolean succeeded ) {
        if ( log.isDebugEnabled() ) {
            log.debug("Publishing a Participant deleted event.");
        }
        AcmParticipantDeletedEvent participantDeletedEvent = new AcmParticipantDeletedEvent(source,auth.getName());
        participantDeletedEvent.setSucceeded(succeeded);

        eventPublisher.publishEvent(participantDeletedEvent);
    }

    public void publishParticipantUpdatedEvent( AcmParticipant source, Authentication auth, boolean succeeded ) {
        if ( log.isDebugEnabled() ) {
            log.debug("Publishing a Participant updated event.");
        }
        AcmParticipantUpdatedEvent participantUpdatedEvent = new AcmParticipantUpdatedEvent(source,auth.getName());
        participantUpdatedEvent.setSucceeded(succeeded);

        eventPublisher.publishEvent(participantUpdatedEvent);
    }
}
