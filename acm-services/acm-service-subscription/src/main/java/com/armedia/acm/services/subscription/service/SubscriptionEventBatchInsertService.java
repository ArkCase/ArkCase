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

import com.armedia.acm.core.exceptions.AcmEncryptionException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.files.propertymanager.PropertyFileManager;
import com.armedia.acm.services.subscription.dao.SubscriptionDao;
import com.armedia.acm.services.subscription.dao.SubscriptionEventDao;
import com.armedia.acm.services.subscription.model.AcmSubscriptionEvent;
import com.armedia.acm.services.subscription.model.SubscriptionConstants;
import com.armedia.acm.spring.SpringContextHolder;

import org.apache.commons.lang3.StringUtils;
import org.apache.xmlbeans.SystemProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by marjan.stefanoski on 29.01.2015.
 */
public class SubscriptionEventBatchInsertService
{

    private SubscriptionDao subscriptionDao;
    private SubscriptionEventDao subscriptionEventDao;
    private PropertyFileManager propertyFileManager;
    private String lastBatchInsertPropertyFileLocation;
    private SubscriptionEventPublisher subscriptionEventPublisher;
    private String userHomeDir;
    private String fileSeparator = SystemProperties.getProperty("file.separator");
    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;
    private String fullPath;
    private SpringContextHolder springContextHolder;
    private Map<String, String> subscriptionProperties;

    private Logger log = LoggerFactory.getLogger(getClass());

    // this method is used by scheduled jobs in Spring beans loaded dynamically from the ACM configuration
    // folder ($HOME/.acm).
    public void insertNewSubscriptionEvents() throws AcmEncryptionException
    {
        setFullPath(getUserHomeDir() + getLastBatchInsertPropertyFileLocation().replace("/", getFileSeparator()));
        getAuditPropertyEntityAdapter().setUserId(SubscriptionConstants.SUBSCRIPTION_USER);

        String lastRunDate = getPropertyFileManager().load(
                getFullPath(),
                SubscriptionConstants.SUBSCRIPTION_EVENT_LAST_RUN_DATE_PROPERTY_KEY,
                SubscriptionConstants.DEFAULT_LAST_RUN_DATE);
        DateFormat dateFormat = new SimpleDateFormat(SubscriptionConstants.DATE_FORMAT);

        try
        {
            String eventTypesString = getSubscriptionProperties().get(SubscriptionConstants.SUBSCRIPTION_EVENT_TYPES_TO_BE_REMOVED);
            List<String> eventsToBeRemoved = null;
            if (StringUtils.isNotEmpty(eventTypesString))
            {
                eventTypesString = eventTypesString.trim();
                eventsToBeRemoved = Arrays.asList(eventTypesString.split("\\s*,\\s*"));
            }
            Date lastBatchRunDate = getLastBatchRunDate(lastRunDate, dateFormat);
            storeCurrentDateForNextBatchRun(dateFormat);
            List<AcmSubscriptionEvent> subscriptionEventList = null;
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
        catch (ParseException e)
        {
            if (log.isErrorEnabled())
            {
                log.error("Parsing exception occurred while fetching lastBatchRunDate ", e);
            }
        }
    }

    private void storeCurrentDateForNextBatchRun(DateFormat dateFormat)
    {
        // store the current time as the last run date to use the next time this job runs. This allows us to
        // scan only for objects updated since this date.
        String solrNow = dateFormat.format(new Date());
        getPropertyFileManager().store(SubscriptionConstants.SUBSCRIPTION_EVENT_LAST_RUN_DATE_PROPERTY_KEY, solrNow, getFullPath());
    }

    private Date getLastBatchRunDate(String lastRunDate, DateFormat dateFormat) throws ParseException
    {
        Date sinceWhen = dateFormat.parse(lastRunDate);

        // back up one minute just to be sure we get everything
        Calendar cal = Calendar.getInstance();
        cal.setTime(sinceWhen);
        cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE) - 1);
        sinceWhen = cal.getTime();
        return sinceWhen;
    }

    public String getLastBatchInsertPropertyFileLocation()
    {
        return lastBatchInsertPropertyFileLocation;
    }

    public void setLastBatchInsertPropertyFileLocation(String lastBatchInsertPropertyFileLocation)
    {
        this.lastBatchInsertPropertyFileLocation = lastBatchInsertPropertyFileLocation;
    }

    public PropertyFileManager getPropertyFileManager()
    {
        return propertyFileManager;
    }

    public void setPropertyFileManager(PropertyFileManager propertyFileManager)
    {
        this.propertyFileManager = propertyFileManager;
    }

    public String getUserHomeDir()
    {
        return userHomeDir;
    }

    public void setUserHomeDir(String userHomeDir)
    {
        this.userHomeDir = userHomeDir;
    }

    public String getFileSeparator()
    {
        return fileSeparator;
    }

    public void setFileSeparator(String fileSeparator)
    {
        this.fileSeparator = fileSeparator;
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

    public String getFullPath()
    {
        return fullPath;
    }

    public void setFullPath(String fullPath)
    {
        this.fullPath = fullPath;
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

    public SpringContextHolder getSpringContextHolder()
    {
        return springContextHolder;
    }

    public void setSpringContextHolder(SpringContextHolder springContextHolder)
    {
        this.springContextHolder = springContextHolder;
    }

    public Map<String, String> getSubscriptionProperties()
    {
        return subscriptionProperties;
    }

    public void setSubscriptionProperties(Map<String, String> subscriptionProperties)
    {
        this.subscriptionProperties = subscriptionProperties;
    }
}
