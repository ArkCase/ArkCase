package com.armedia.acm.services.tag.service;

/*-
 * #%L
 * ACM Service: Tag
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
import com.armedia.acm.services.tag.model.AcmAssociatedTag;
import com.armedia.acm.services.tag.model.AcmAssociatedTagCreatedEvent;
import com.armedia.acm.services.tag.model.AcmAssociatedTagDeletedEvent;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.security.core.Authentication;

public class AssociatedTagEventPublisher implements ApplicationEventPublisherAware
{
    private transient final Logger log = LogManager.getLogger(getClass());
    private ApplicationEventPublisher eventPublisher;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        eventPublisher = applicationEventPublisher;
    }

    public void publishAssociatedTagCreatedEvent(AcmAssociatedTag source, Authentication auth, boolean succeeded)
    {
        if (log.isDebugEnabled())
        {
            log.debug("Publishing an associated tag event.");
        }
        AcmAssociatedTagCreatedEvent associatedTagCreatedEvent = new AcmAssociatedTagCreatedEvent(source, auth.getName());
        if (auth.getDetails() != null && auth.getDetails() instanceof AcmAuthenticationDetails)
        {
            associatedTagCreatedEvent.setIpAddress(((AcmAuthenticationDetails) auth.getDetails()).getRemoteAddress());
        }
        associatedTagCreatedEvent.setSucceeded(succeeded);

        eventPublisher.publishEvent(associatedTagCreatedEvent);
    }

    public void publishAssociatedTagDeletedEvent(AcmAssociatedTag source, Authentication auth, boolean succeeded)
    {
        if (log.isDebugEnabled())
        {
            log.debug("Publishing an associated tag deleted event.");
        }
        AcmAssociatedTagDeletedEvent associatedTagDeletedEvent = new AcmAssociatedTagDeletedEvent(source, auth.getName(),
                AuthenticationUtils.getUserIpAddress());
        associatedTagDeletedEvent.setSucceeded(succeeded);

        eventPublisher.publishEvent(associatedTagDeletedEvent);
    }
}
