package com.armedia.acm.plugins.consultation.utility;

/*-
 * #%L
 * ACM Default Plugin: Consultation
 * %%
 * Copyright (C) 2014 - 2020 ArkCase LLC
 * %%
 * This file is part of the ArkCase software.
 *
 * If the software was purchased under a paid ArkCase license, the terms of
 * the paid license agreement will prevail.  Otherwise, the software is
 * provided under the following open source license terms:
 *
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import com.armedia.acm.auth.AcmAuthenticationDetails;
import com.armedia.acm.plugins.consultation.model.Consultation;
import com.armedia.acm.plugins.consultation.model.ConsultationConstants;
import com.armedia.acm.plugins.consultation.model.ConsultationEvent;
import com.armedia.acm.plugins.consultation.model.ConsultationModifiedEvent;
import com.armedia.acm.plugins.consultation.model.ConsultationParticipantsModifiedEvent;
import com.armedia.acm.plugins.person.model.PersonAssociation;
import com.armedia.acm.plugins.person.model.PersonAssociationAddEvent;
import com.armedia.acm.plugins.person.model.PersonAssociationDeletedEvent;
import com.armedia.acm.services.participants.model.AcmParticipant;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.security.core.Authentication;

import java.util.Date;

/**
 * Created by Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com> on May, 2020
 */
public class ConsultationEventUtility implements ApplicationEventPublisherAware
{
    private Logger log = LogManager.getLogger(getClass());

    private ApplicationEventPublisher applicationEventPublisher;

    public void raiseEvent(Consultation consultation, String consultationState, Date eventDate, String ipAddress, String userId,
            Authentication auth)
    {
        String eventType = "com.armedia.acm.consultation." + consultationState;
        eventDate = eventDate == null ? new Date() : eventDate;
        ConsultationEvent event = new ConsultationEvent(consultation, ipAddress, userId, eventType, eventDate, true, auth);

        applicationEventPublisher.publishEvent(event);
    }

    public void raiseCustomEvent(Consultation consultation, String consultationState, String eventDescription, Date eventDate,
            String ipAddress,
            String userId, Authentication auth)
    {
        String eventType = "com.armedia.acm.consultation." + consultationState;
        eventDate = eventDate == null ? new Date() : eventDate;
        ConsultationEvent event = new ConsultationEvent(consultation, ipAddress, userId, eventType, eventDescription, eventDate, true,
                auth);

        applicationEventPublisher.publishEvent(event);
    }

    public void raiseConsultationModifiedEvent(Consultation source, String ipAddress, String eventStatus)
    {

        ConsultationModifiedEvent event = new ConsultationModifiedEvent(source);
        event.setSucceeded(true);
        event.setIpAddress(ipAddress);
        event.setEventStatus(eventStatus);
        applicationEventPublisher.publishEvent(event);
    }

    public void raiseConsultationModifiedEvent(Consultation source, String ipAddress, String eventStatus, String description)
    {

        ConsultationModifiedEvent event = new ConsultationModifiedEvent(source);
        event.setSucceeded(true);
        event.setIpAddress(ipAddress);
        event.setEventStatus(eventStatus);
        event.setEventDescription(description);
        applicationEventPublisher.publishEvent(event);
    }

    public void raisePersonAssociationsAddEvent(PersonAssociation personAssociation, Consultation source, String ipAddress)
    {
        PersonAssociationAddEvent event = new PersonAssociationAddEvent(personAssociation, personAssociation.getParentType(),
                personAssociation.getParentId(), ipAddress);
        event.setSucceeded(true);
        event.setIpAddress(ipAddress);
        event.setParentObjectId(source.getId());
        event.setParentObjectType(source.getObjectType());
        event.setParentObjectName(source.getConsultationNumber());
        event.setEventDescription(personAssociation.getPerson().getFullName());
        applicationEventPublisher.publishEvent(event);
    }

    public void raisePersonAssociationsDeletedEvent(PersonAssociation personAssociation, Consultation source, String ipAddress)
    {
        PersonAssociationDeletedEvent event = new PersonAssociationDeletedEvent(personAssociation, personAssociation.getParentType(),
                personAssociation.getParentId(), ipAddress);
        event.setSucceeded(true);
        event.setIpAddress(ipAddress);
        event.setParentObjectId(source.getId());
        event.setParentObjectType(source.getObjectType());
        event.setParentObjectName(source.getConsultationNumber());
        event.setEventDescription(personAssociation.getPerson().getFullName());
        applicationEventPublisher.publishEvent(event);
    }

    public void raiseParticipantsModifiedInConsultation(AcmParticipant participant, Consultation source, String ipAddress,
            String eventStatus)
    {
        ConsultationParticipantsModifiedEvent event = new ConsultationParticipantsModifiedEvent(participant);
        event.setEventStatus(eventStatus);
        event.setSucceeded(true);
        event.setIpAddress(ipAddress);
        event.setParentObjectId(source.getId());
        event.setParentObjectType(source.getObjectType());
        event.setParentObjectName(source.getConsultationNumber());
        applicationEventPublisher.publishEvent(event);
    }

    public void raiseParticipantsModifiedInConsultation(AcmParticipant participant, Consultation source, String ipAddress,
            String eventStatus,
            String description)
    {
        ConsultationParticipantsModifiedEvent event = new ConsultationParticipantsModifiedEvent(participant);
        event.setEventStatus(eventStatus);
        event.setSucceeded(true);
        event.setIpAddress(ipAddress);
        event.setParentObjectId(source.getId());
        event.setParentObjectType(source.getObjectType());
        event.setParentObjectName(source.getConsultationNumber());
        event.setEventDescription(description);
        applicationEventPublisher.publishEvent(event);
    }

    public void raiseConsultationCreated(Consultation source, Authentication authentication)
    {

        String ipAddress = null;
        if (authentication.getDetails() != null && authentication.getDetails() instanceof AcmAuthenticationDetails)
        {
            ipAddress = ((AcmAuthenticationDetails) authentication.getDetails()).getRemoteAddress();
        }

        ConsultationEvent event = new ConsultationEvent(source, ipAddress, authentication.getName(),
                ConsultationConstants.EVENT_TYPE_CREATED, new Date(), true,
                authentication);
        applicationEventPublisher.publishEvent(event);
    }

    public void raiseConsultationViewed(Consultation source, Authentication authentication)
    {
        String ipAddress = null;
        if (authentication.getDetails() != null && authentication.getDetails() instanceof AcmAuthenticationDetails)
        {
            ipAddress = ((AcmAuthenticationDetails) authentication.getDetails()).getRemoteAddress();
        }

        ConsultationEvent event = new ConsultationEvent(source, ipAddress, authentication.getName(),
                ConsultationConstants.EVENT_TYPE_VIEWED, new Date(), true,
                authentication);
        applicationEventPublisher.publishEvent(event);
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        this.applicationEventPublisher = applicationEventPublisher;
    }
}
