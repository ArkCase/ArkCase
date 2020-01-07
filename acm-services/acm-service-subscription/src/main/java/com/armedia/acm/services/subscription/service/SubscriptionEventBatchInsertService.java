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

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.services.subscription.dao.SubscriptionDao;
import com.armedia.acm.services.subscription.dao.SubscriptionEventDao;
import com.armedia.acm.services.subscription.model.AcmSubscriptionEvent;
import com.armedia.acm.services.subscription.model.SubscriptionConfig;
import com.armedia.acm.services.subscription.model.SubscriptionConstants;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by marjan.stefanoski on 29.01.2015.
 */
public class SubscriptionEventBatchInsertService
{
    private SubscriptionDao subscriptionDao;
    private SubscriptionEventDao subscriptionEventDao;
    private SubscriptionEventPublisher subscriptionEventPublisher;
    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;
    private SubscriptionConfig subscriptionConfig;

    private Logger log = LogManager.getLogger(getClass());

    public void insertNewSubscriptionEvents(Date lastRunDate)
    {
        getAuditPropertyEntityAdapter().setUserId(SubscriptionConstants.SUBSCRIPTION_USER);

        String eventTypesString = subscriptionConfig.getRemovedEventTypes();
        List<String> eventsToBeRemoved = null;
        if (StringUtils.isNotEmpty(eventTypesString))
        {
            eventTypesString = eventTypesString.trim();
            eventsToBeRemoved = Arrays.asList(eventTypesString.split("\\s*,\\s*"));
        }
        Date lastBatchRunDate = getLastBatchRunDate(lastRunDate);
        List<AcmSubscriptionEvent> subscriptionEventList;
        try
        {
            subscriptionEventList = getSubscriptionDao().createListOfNewSubscriptionEventsForInserting(lastBatchRunDate,
                    eventsToBeRemoved);
            for (AcmSubscriptionEvent subscriptionEvent : subscriptionEventList)
            {
                AcmSubscriptionEvent subscriptionEventSaved = getSubscriptionEventDao().save(subscriptionEvent);
                subscriptionEventPublisher.publishAcmSubscriptionEventCreatedEvent(subscriptionEventSaved, true);
            }
        }
        catch (AcmObjectNotFoundException e)
        {
            log.debug("There are no new events to be added");
        }
    }

    private Date getLastBatchRunDate(Date lastRunDate)
    {
        lastRunDate = lastRunDate == null ? new Date() : lastRunDate;

        // back up one minute just to be sure we get everything
        Calendar cal = Calendar.getInstance();
        cal.setTime(lastRunDate);
        cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE) - 1);
        lastRunDate = cal.getTime();
        return lastRunDate;
    }

    public SubscriptionEventDao getSubscriptionEventDao()
    {
        return subscriptionEventDao;
    }

    public void setSubscriptionEventDao(SubscriptionEventDao subscriptionEventDao)
    {
        this.subscriptionEventDao = subscriptionEventDao;
    }

    public AuditPropertyEntityAdapter getAuditPropertyEntityAdapter()
    {
        return auditPropertyEntityAdapter;
    }

    public void setAuditPropertyEntityAdapter(AuditPropertyEntityAdapter auditPropertyEntityAdapter)
    {
        this.auditPropertyEntityAdapter = auditPropertyEntityAdapter;
    }

    public SubscriptionDao getSubscriptionDao()
    {
        return subscriptionDao;
    }

    public void setSubscriptionDao(SubscriptionDao subscriptionDao)
    {
        this.subscriptionDao = subscriptionDao;
    }

    public SubscriptionEventPublisher getSubscriptionEventPublisher()
    {
        return subscriptionEventPublisher;
    }

    public void setSubscriptionEventPublisher(SubscriptionEventPublisher subscriptionEventPublisher)
    {
        this.subscriptionEventPublisher = subscriptionEventPublisher;
    }

    public SubscriptionConfig getSubscriptionConfig()
    {
        return subscriptionConfig;
    }

    public void setSubscriptionConfig(SubscriptionConfig subscriptionConfig)
    {
        this.subscriptionConfig = subscriptionConfig;
    }
}
