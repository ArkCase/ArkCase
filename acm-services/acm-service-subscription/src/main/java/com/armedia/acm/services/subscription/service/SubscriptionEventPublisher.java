package com.armedia.acm.services.subscription.service;

/*-
 * #%L
 * ACM Service: Subscription
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
import com.armedia.acm.data.AcmDatabaseChangesEvent;
import com.armedia.acm.data.AcmObjectChangelist;
import com.armedia.acm.services.subscription.model.AcmSubscription;
import com.armedia.acm.services.subscription.model.AcmSubscriptionEvent;
import com.armedia.acm.services.subscription.model.AcmSubscriptionEventCreatedEvent;
import com.armedia.acm.services.subscription.model.SubscriptionCreatedEvent;
import com.armedia.acm.services.subscription.model.SubscriptionDeletedEvent;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.security.core.Authentication;

/**
 * Created by marjan.stefanoski on 11.02.2015.
 */
public class SubscriptionEventPublisher implements ApplicationEventPublisherAware
{

    private transient final Logger log = LogManager.getLogger(getClass());
    private ApplicationEventPublisher eventPublisher;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        eventPublisher = applicationEventPublisher;
    }

    public void publishSubscriptionCreatedEvent(AcmSubscription source, Authentication auth, boolean succeeded)
    {
        log.debug("Publishing a subscription event.");
        SubscriptionCreatedEvent subscriptionPersistenceEvent = new SubscriptionCreatedEvent(source,
                AuthenticationUtils.getUserIpAddress());
        if (auth.getDetails() != null && auth.getDetails() instanceof AcmAuthenticationDetails)
        {
            subscriptionPersistenceEvent.setIpAddress(((AcmAuthenticationDetails) auth.getDetails()).getRemoteAddress());
        }
        subscriptionPersistenceEvent.setSucceeded(succeeded);

        eventPublisher.publishEvent(subscriptionPersistenceEvent);
    }

    public void publishSubscriptionDeletedEvent(String userId, Long objectId, String objectType, boolean succeeded)
    {
        log.debug("Publishing a subscription deleted event.");
        SubscriptionDeletedEvent subscriptionDeletedEvent = new SubscriptionDeletedEvent(userId, objectId, objectType,
                AuthenticationUtils.getUserIpAddress());
        subscriptionDeletedEvent.setSucceeded(succeeded);

        eventPublisher.publishEvent(subscriptionDeletedEvent);
    }

    public void publishAcmSubscriptionEventCreatedEvent(AcmSubscriptionEvent source, boolean succeeded)
    {
        log.debug("Publishing a AcmSubscriptionEvent created event.");
        AcmSubscriptionEventCreatedEvent acmSubscriptionEventPersistenceEvent = new AcmSubscriptionEventCreatedEvent(source,
                AuthenticationUtils.getUserIpAddress());
        acmSubscriptionEventPersistenceEvent.setSucceeded(succeeded);

        eventPublisher.publishEvent(acmSubscriptionEventPersistenceEvent);
    }

    public void publishDeletedSubscriptionEvents(AcmObjectChangelist changelist)
    {
        AcmDatabaseChangesEvent databaseChangesEvent = new AcmDatabaseChangesEvent(changelist);
        eventPublisher.publishEvent(databaseChangesEvent);
    }

}
