package com.armedia.acm.services.exemption.model;

/*-
 * #%L
 * ACM Service: Exemption
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

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

import com.armedia.acm.auth.AuthenticationUtils;

/**
 * Created by ana.serafimoska
 */
public class ExemptionCodeAndStatuteEventPublisher implements ApplicationEventPublisherAware
{
    private ApplicationEventPublisher applicationEventPublisher;

    public void publishExemptionCodeCreatedEvent(ExemptionCode source)
    {
        ExemptionCodeCreatedEvent exemptionCodeCreatedEvent = new ExemptionCodeCreatedEvent(source);
        exemptionCodeCreatedEvent.setUserId(AuthenticationUtils.getUsername());
        exemptionCodeCreatedEvent.setIpAddress(AuthenticationUtils.getUserIpAddress());
        exemptionCodeCreatedEvent.setParentObjectId(source.getParentObjectId());
        exemptionCodeCreatedEvent.setParentObjectType(source.getParentObjectType());
        exemptionCodeCreatedEvent.setSucceeded(true);
        getApplicationEventPublisher().publishEvent(exemptionCodeCreatedEvent);

    }

    public void publishExemptionCodeDeletedEvent(ExemptionCode source)
    {
        ExemptionCodeDeletedEvent exemptionCodeDeletedEvent = new ExemptionCodeDeletedEvent(source);
        exemptionCodeDeletedEvent.setUserId(AuthenticationUtils.getUsername());
        exemptionCodeDeletedEvent.setIpAddress(AuthenticationUtils.getUserIpAddress());
        exemptionCodeDeletedEvent.setParentObjectId(source.getParentObjectId());
        exemptionCodeDeletedEvent.setParentObjectType(source.getParentObjectType());
        exemptionCodeDeletedEvent.setSucceeded(true);
        getApplicationEventPublisher().publishEvent(exemptionCodeDeletedEvent);
    }

    public void publishExemptionCodeUpdatedEvent(ExemptionCode source)
    {
        ExemptionCodeUpdatedEvent exemptionCodeUpdatedEvent = new ExemptionCodeUpdatedEvent(source);
        exemptionCodeUpdatedEvent.setUserId(AuthenticationUtils.getUsername());
        exemptionCodeUpdatedEvent.setIpAddress(AuthenticationUtils.getUserIpAddress());
        exemptionCodeUpdatedEvent.setParentObjectId(source.getParentObjectId());
        exemptionCodeUpdatedEvent.setParentObjectType(source.getParentObjectType());
        exemptionCodeUpdatedEvent.setSucceeded(true);
        getApplicationEventPublisher().publishEvent(exemptionCodeUpdatedEvent);

    }

    public void publishExemptionStatuteCreatedEvent(ExemptionStatute source)
    {
        ExemptionStatuteCreatedEvent exemptionStatuteCreatedEvent = new ExemptionStatuteCreatedEvent(source);
        exemptionStatuteCreatedEvent.setUserId(AuthenticationUtils.getUsername());
        exemptionStatuteCreatedEvent.setIpAddress(AuthenticationUtils.getUserIpAddress());
        exemptionStatuteCreatedEvent.setParentObjectId(source.getParentObjectId());
        exemptionStatuteCreatedEvent.setParentObjectType(source.getParentObjectType());
        exemptionStatuteCreatedEvent.setSucceeded(true);
        getApplicationEventPublisher().publishEvent(exemptionStatuteCreatedEvent);
    }

    public void publishExemptionStatuteDeletedEvent(ExemptionStatute source)
    {
        ExemptionStatuteDeletedEvent exemptionStatuteDeletedEvent = new ExemptionStatuteDeletedEvent(source);
        exemptionStatuteDeletedEvent.setUserId(AuthenticationUtils.getUsername());
        exemptionStatuteDeletedEvent.setIpAddress(AuthenticationUtils.getUserIpAddress());
        exemptionStatuteDeletedEvent.setParentObjectId(source.getParentObjectId());
        exemptionStatuteDeletedEvent.setParentObjectType(source.getParentObjectType());
        exemptionStatuteDeletedEvent.setSucceeded(true);
        getApplicationEventPublisher().publishEvent(exemptionStatuteDeletedEvent);
    }

    public ApplicationEventPublisher getApplicationEventPublisher()
    {
        return applicationEventPublisher;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        this.applicationEventPublisher = applicationEventPublisher;
    }
}
