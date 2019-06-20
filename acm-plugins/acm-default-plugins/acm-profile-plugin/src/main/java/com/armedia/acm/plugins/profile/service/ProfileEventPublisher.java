package com.armedia.acm.plugins.profile.service;

/*-
 * #%L
 * ACM Default Plugin: Profile
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
import com.armedia.acm.plugins.profile.model.OutlookPasswordChangedEvent;
import com.armedia.acm.plugins.profile.model.UserOrg;
import com.armedia.acm.plugins.profile.model.UserOrgCreatedEvent;
import com.armedia.acm.plugins.profile.model.UserOrgPersistentEvent;
import com.armedia.acm.plugins.profile.model.UserOrgUpdateEvent;
import com.armedia.acm.service.outlook.model.OutlookPassword;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.security.core.Authentication;

public class ProfileEventPublisher implements ApplicationEventPublisherAware
{

    private transient final Logger log = LogManager.getLogger(getClass());
    private ApplicationEventPublisher eventPublisher;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        eventPublisher = applicationEventPublisher;
    }

    public void publishProfileEvent(UserOrg source, Authentication authentication, boolean newUserOrg, boolean succeeded)
    {
        log.debug("Publishing a widget event.");
        UserOrgPersistentEvent userOrgPersistenceEvent = newUserOrg ? new UserOrgCreatedEvent(source) : new UserOrgUpdateEvent(source);
        userOrgPersistenceEvent.setSucceeded(succeeded);
        if (authentication.getDetails() != null && authentication.getDetails() instanceof AcmAuthenticationDetails)
        {
            userOrgPersistenceEvent.setIpAddress(((AcmAuthenticationDetails) authentication.getDetails()).getRemoteAddress());
        }
        eventPublisher.publishEvent(userOrgPersistenceEvent);
    }

    public void outlookPasswordSavedEvent(OutlookPassword source, Authentication auth, String ipAddress, boolean succeeded)
    {
        OutlookPasswordChangedEvent event = new OutlookPasswordChangedEvent(source, auth.getName(), ipAddress, succeeded);
        eventPublisher.publishEvent(event);
    }
}
