/**
 *
 */
package com.armedia.acm.service.objecthistory.service;

/*-
 * #%L
 * ACM Service: Object History Service
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

import com.armedia.acm.service.objecthistory.model.AcmAssigneeChangeEvent;
import com.armedia.acm.service.objecthistory.model.AcmAssignment;
import com.armedia.acm.service.objecthistory.model.AcmObjectHistory;
import com.armedia.acm.service.objecthistory.model.AcmObjectHistoryEvent;
import com.armedia.acm.service.objecthistory.model.AcmObjectHistoryEventType;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

/**
 * @author riste.tutureski
 */
public class AcmObjectHistoryEventPublisher implements ApplicationEventPublisherAware
{

    private ApplicationEventPublisher eventPublisher;

    public void publishCreatedEvent(AcmObjectHistory source, String ipAddress)
    {
        AcmObjectHistoryEvent event = new AcmObjectHistoryEvent(source);

        event.setEventType(AcmObjectHistoryEventType.CREATED);
        event.setSucceeded(true);
        event.setIpAddress(ipAddress);

        getEventPublisher().publishEvent(event);
    }

    public void publishAssigneeChangeEvent(AcmAssignment source, String userId, String ipAddress)
    {
        AcmAssigneeChangeEvent event = new AcmAssigneeChangeEvent(source, userId);

        event.setIpAddress(ipAddress);
        event.setSucceeded(true);

        getEventPublisher().publishEvent(event);
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        eventPublisher = applicationEventPublisher;
    }

    public ApplicationEventPublisher getEventPublisher()
    {
        return eventPublisher;
    }

}
