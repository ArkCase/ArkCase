package com.armedia.acm.services.participants.service;

/*-
 * #%L
 * ACM Service: Participants
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

import com.armedia.acm.auth.AuthenticationUtils;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.services.participants.model.AcmParticipantCreatedEvent;
import com.armedia.acm.services.participants.model.AcmParticipantDeletedEvent;
import com.armedia.acm.services.participants.model.AcmParticipantUpdatedEvent;
import com.armedia.acm.web.api.MDCConstants;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.slf4j.MDC;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

public class AcmParticipantEventPublisher implements ApplicationEventPublisherAware
{

    private transient final Logger log = LogManager.getLogger(getClass());
    private ApplicationEventPublisher eventPublisher;

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
                MDC.get(MDCConstants.EVENT_MDC_REQUEST_USER_ID_KEY), AuthenticationUtils.getUserIpAddress());
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
                MDC.get(MDCConstants.EVENT_MDC_REQUEST_USER_ID_KEY), AuthenticationUtils.getUserIpAddress());
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
                MDC.get(MDCConstants.EVENT_MDC_REQUEST_USER_ID_KEY), AuthenticationUtils.getUserIpAddress());

        participantUpdatedEvent.setSucceeded(succeeded);

        eventPublisher.publishEvent(participantUpdatedEvent);
    }
}
