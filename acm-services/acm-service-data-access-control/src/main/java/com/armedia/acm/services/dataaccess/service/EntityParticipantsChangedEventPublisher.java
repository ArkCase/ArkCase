package com.armedia.acm.services.dataaccess.service;

/*-
 * #%L
 * ACM Service: Data Access Control
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
import com.armedia.acm.core.AcmObject;
import com.armedia.acm.services.dataaccess.model.AcmEntityParticipantsChangedEvent;
import com.armedia.acm.services.participants.model.AcmParticipant;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

import java.util.List;

public class EntityParticipantsChangedEventPublisher implements ApplicationEventPublisherAware
{
    private Logger LOG = LogManager.getLogger(getClass());

    private ApplicationEventPublisher applicationEventPublisher;

    public void publishEvent(AcmObject source, List<AcmParticipant> originalParticipants)
    {
        LOG.debug("Publishing EntityParticipantsChangedEvent event.");

        AcmEntityParticipantsChangedEvent event = new AcmEntityParticipantsChangedEvent(source, originalParticipants,
                AuthenticationUtils.getUserIpAddress());

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
