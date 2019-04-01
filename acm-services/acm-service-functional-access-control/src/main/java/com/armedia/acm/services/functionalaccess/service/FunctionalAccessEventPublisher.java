package com.armedia.acm.services.functionalaccess.service;

/*-
 * #%L
 * ACM Service: Functional Access Control
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
import com.armedia.acm.services.functionalaccess.model.FunctionalAccessUpdatedEvent;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.security.core.Authentication;

/**
 * @author riste.tutureski
 *
 */
public class FunctionalAccessEventPublisher implements ApplicationEventPublisherAware
{

    private ApplicationEventPublisher eventPublisher;

    public void publishFunctionalAccessUpdateEvent(Object source, Authentication auth)
    {
        publishFunctionalAccessUpdateEventOnRolesToGroupMap(source, auth.getName());
    }

    public void publishFunctionalAccessUpdateEventOnRolesToGroupMap(Object source, String userId)
    {
        FunctionalAccessUpdatedEvent event = new FunctionalAccessUpdatedEvent(source, userId, AuthenticationUtils.getUserIpAddress());
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
