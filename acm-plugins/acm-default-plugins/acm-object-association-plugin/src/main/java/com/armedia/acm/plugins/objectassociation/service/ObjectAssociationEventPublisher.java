package com.armedia.acm.plugins.objectassociation.service;

/*-
 * #%L
 * ACM Default Plugin: Object Associations
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
import com.armedia.acm.core.model.AcmEvent;
import com.armedia.acm.plugins.objectassociation.model.AddReferenceEvent;
import com.armedia.acm.plugins.objectassociation.model.DeleteReferenceEvent;
import com.armedia.acm.plugins.objectassociation.model.ObjectAssociation;
import com.armedia.acm.plugins.objectassociation.model.ObjectAssociationEvent;
import com.armedia.acm.plugins.objectassociation.model.UpdateReferenceEvent;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.security.core.Authentication;

/**
 * 
 * @author vladimir.radeski
 *
 */

public class ObjectAssociationEventPublisher implements ApplicationEventPublisherAware
{

    private ApplicationEventPublisher eventPublisher;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        eventPublisher = applicationEventPublisher;
    }

    public void publishAddReferenceEvent(ObjectAssociation source, Authentication authentication, boolean succeeded)
    {
        String ipAddress = "";
        if (authentication != null && authentication.getDetails() != null)
        {
            ipAddress = ((AcmAuthenticationDetails) authentication.getDetails()).getRemoteAddress();
        }
        AddReferenceEvent event = new AddReferenceEvent(source, ipAddress);
        publishEvent(event, authentication, succeeded);
    }

    public void publishUpdateReferenceEvent(ObjectAssociation source, Authentication authentication, boolean succeeded)
    {
        String ipAddress = "";
        if (authentication != null && authentication.getDetails() != null)
        {
            ipAddress = ((AcmAuthenticationDetails) authentication.getDetails()).getRemoteAddress();
        }
        UpdateReferenceEvent event = new UpdateReferenceEvent(source, ipAddress);
        publishEvent(event, authentication, succeeded);
    }

    public void publishDeleteReferenceEvent(ObjectAssociation source, Authentication authentication, boolean succeeded)
    {
        String ipAddress = "";
        if (authentication != null && authentication.getDetails() != null)
        {
            ipAddress = ((AcmAuthenticationDetails) authentication.getDetails()).getRemoteAddress();
        }
        DeleteReferenceEvent event = new DeleteReferenceEvent(source, ipAddress);
        publishEvent(event, authentication, succeeded);
    }

    public void publishObjectAssociationEvent(ObjectAssociation source, Authentication authentication, boolean succeeded,
            String objectAssociationState)
    {
        ObjectAssociationEvent event = new ObjectAssociationEvent(source, AuthenticationUtils.getUserIpAddress());
        event.setAuthentication(authentication);
        event.setObjectAssociationState(ObjectAssociationEvent.ObjectAssociationState.valueOf(objectAssociationState));
        publishEvent(event, authentication, succeeded);
    }

    private void publishEvent(AcmEvent event, Authentication authentication, boolean succeeded)
    {
        String userId = authentication != null ? authentication.getName() : "";
        event.setUserId(userId);
        event.setSucceeded(succeeded);
        eventPublisher.publishEvent(event);
    }
}
