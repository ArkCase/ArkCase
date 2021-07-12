package com.armedia.acm.plugins.casefile.utility;

/*-
 * #%L
 * ACM Default Plugin: Case File
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
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
import com.armedia.acm.auth.AuthenticationUtils;
import com.armedia.acm.plugins.casefile.model.CaseEvent;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.model.CaseFileConstants;
import com.armedia.acm.plugins.casefile.model.CaseFileDueDateUpdatedEvent;
import com.armedia.acm.plugins.casefile.model.CaseFileModifiedEvent;
import com.armedia.acm.plugins.casefile.model.CaseFileParticipantsModifiedEvent;
import com.armedia.acm.plugins.person.model.PersonAssociation;
import com.armedia.acm.plugins.person.model.PersonAssociationAddEvent;
import com.armedia.acm.plugins.person.model.PersonAssociationDeletedEvent;
import com.armedia.acm.services.participants.model.AcmParticipant;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.security.core.Authentication;

import java.util.Date;

/**
 * Created by armdev on 9/4/14.
 */
public class CaseFileEventUtility implements ApplicationEventPublisherAware
{
    private Logger log = LogManager.getLogger(getClass());

    private ApplicationEventPublisher applicationEventPublisher;

    public void raiseEvent(CaseFile caseFile, String caseState, Date eventDate, String ipAddress, String userId, Authentication auth)
    {
        String eventType = "com.armedia.acm.casefile." + caseState;
        eventDate = eventDate == null ? new Date() : eventDate;
        CaseEvent event = new CaseEvent(caseFile, ipAddress, userId, eventType, eventDate, true, auth);

        applicationEventPublisher.publishEvent(event);
    }

    public void raiseCustomEvent(CaseFile caseFile, String caseState, String eventDescription, Date eventDate, String ipAddress,
                                 String userId, Authentication auth)
    {
        String eventType = "com.armedia.acm.casefile." + caseState;
        eventDate = eventDate == null ? new Date() : eventDate;
        CaseEvent event = new CaseEvent(caseFile, ipAddress, userId, eventType, eventDescription, eventDate, true, auth);

        applicationEventPublisher.publishEvent(event);
    }

    public void raiseCaseFileModifiedEvent(CaseFile source, String ipAddress, String eventStatus)
    {

        CaseFileModifiedEvent event = new CaseFileModifiedEvent(source);
        event.setSucceeded(true);
        event.setIpAddress(ipAddress);
        event.setEventStatus(eventStatus);
        applicationEventPublisher.publishEvent(event);
    }

    public void raiseDueDateUpdatedEvent(CaseFile source, String oldDate, String newDate, String timeZone, String ipAddress)
    {
        String eventDescription = String.format("%s %s %s %s %s %s", "- Due Date Changed from ", oldDate, " to ", newDate, " ", timeZone);

        CaseFileDueDateUpdatedEvent event = new CaseFileDueDateUpdatedEvent(source);
        event.setSucceeded(true);
        event.setIpAddress(ipAddress);
        event.setEventDescription(eventDescription);
        applicationEventPublisher.publishEvent(event);
    }

    public void raiseCaseFileModifiedEvent(CaseFile source, String ipAddress, String eventStatus, String description)
    {

        CaseFileModifiedEvent event = new CaseFileModifiedEvent(source);
        event.setSucceeded(true);
        event.setIpAddress(ipAddress);
        event.setEventStatus(eventStatus);
        event.setEventDescription(description);
        applicationEventPublisher.publishEvent(event);
    }

    public void raisePersonAssociationsAddEvent(PersonAssociation personAssociation, CaseFile source, String ipAddress)
    {
        PersonAssociationAddEvent event = new PersonAssociationAddEvent(personAssociation, personAssociation.getParentType(),
                personAssociation.getParentId(), ipAddress);
        event.setSucceeded(true);
        event.setIpAddress(ipAddress);
        event.setParentObjectId(source.getId());
        event.setParentObjectType(source.getObjectType());
        event.setParentObjectName(source.getCaseNumber());
        event.setEventDescription(personAssociation.getPerson().getFullName());
        applicationEventPublisher.publishEvent(event);
    }

    public void raisePersonAssociationsDeletedEvent(PersonAssociation personAssociation, CaseFile source, String ipAddress)
    {
        PersonAssociationDeletedEvent event = new PersonAssociationDeletedEvent(personAssociation, personAssociation.getParentType(),
                personAssociation.getParentId(), ipAddress);
        event.setSucceeded(true);
        event.setIpAddress(ipAddress);
        event.setParentObjectId(source.getId());
        event.setParentObjectType(source.getObjectType());
        event.setParentObjectName(source.getCaseNumber());
        event.setEventDescription(personAssociation.getPerson().getFullName());
        applicationEventPublisher.publishEvent(event);
    }

    public void raiseParticipantsModifiedInCaseFile(AcmParticipant participant, CaseFile source, String ipAddress, String eventStatus)
    {
        CaseFileParticipantsModifiedEvent event = new CaseFileParticipantsModifiedEvent(participant);
        event.setEventStatus(eventStatus);
        event.setSucceeded(true);
        event.setIpAddress(ipAddress);
        event.setParentObjectId(source.getId());
        event.setParentObjectType(source.getObjectType());
        event.setParentObjectName(source.getCaseNumber());

        // We need to get the user id from the authentication for a deleted event
        // since the participant entity modifier field is not updated
        if ("deleted".equalsIgnoreCase(eventStatus))
        {
            event.setUserId(getAuthenticatedUserId());
        }

        applicationEventPublisher.publishEvent(event);
    }

    public void raiseParticipantsModifiedInCaseFile(AcmParticipant participant, CaseFile source, String ipAddress, String eventStatus,
                                                    String description)
    {
        CaseFileParticipantsModifiedEvent event = new CaseFileParticipantsModifiedEvent(participant);
        event.setEventStatus(eventStatus);
        event.setSucceeded(true);
        event.setIpAddress(ipAddress);
        event.setParentObjectId(source.getId());
        event.setParentObjectType(source.getObjectType());
        event.setParentObjectName(source.getCaseNumber());
        event.setEventDescription(description);

        // We need to get the user id from the authentication for a deleted event
        // since the participant entity modifier field is not updated
        if ("deleted".equalsIgnoreCase(eventStatus))
        {
            event.setUserId(getAuthenticatedUserId());
        }

        applicationEventPublisher.publishEvent(event);
    }

    public void raiseCaseFileCreated(CaseFile source, Authentication authentication)
    {

        String ipAddress = null;
        if (authentication.getDetails() != null && authentication.getDetails() instanceof AcmAuthenticationDetails)
        {
            ipAddress = ((AcmAuthenticationDetails) authentication.getDetails()).getRemoteAddress();
        }

        CaseEvent event = new CaseEvent(source, ipAddress, authentication.getName(), CaseFileConstants.EVENT_TYPE_CREATED, new Date(), true,
                authentication);
        applicationEventPublisher.publishEvent(event);
    }

    public void raiseCaseFileViewed(CaseFile source, Authentication authentication)
    {
        String ipAddress = null;
        if (authentication.getDetails() != null && authentication.getDetails() instanceof AcmAuthenticationDetails)
        {
            ipAddress = ((AcmAuthenticationDetails) authentication.getDetails()).getRemoteAddress();
        }

        CaseEvent event = new CaseEvent(source, ipAddress, authentication.getName(), CaseFileConstants.EVENT_TYPE_VIEWED, new Date(), true,
                authentication);
        applicationEventPublisher.publishEvent(event);
    }

    protected String getAuthenticatedUserId()
    {
        if (StringUtils.isNotEmpty(AuthenticationUtils.getUsername()))
        {
            return AuthenticationUtils.getUsername();
        }
        return "SYSTEM_USER";
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public ApplicationEventPublisher getApplicationEventPublisher()
    {
        return applicationEventPublisher;
    }

}
