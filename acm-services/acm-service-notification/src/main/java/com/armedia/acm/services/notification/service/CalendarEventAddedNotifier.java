package com.armedia.acm.services.notification.service;

/*-
 * #%L
 * ACM Service: Notification
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

import com.armedia.acm.core.model.AcmEvent;
import com.armedia.acm.services.notification.model.NotificationConstants;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationListener;

public class CalendarEventAddedNotifier implements ApplicationListener<AcmEvent>
{
    private NotificationService notificationService;
    private NotificationUtils notificationUtils;

    private static final Logger logger = LogManager.getLogger(CalendarEventAddedNotifier.class);

    @Override
    public void onApplicationEvent(AcmEvent event)
    {
        String eventType = event.getEventType();
        if (eventType != null && (eventType.equals("com.armedia.acm.outlook.calendar.event.added") ||
                eventType.equals("com.armedia.acm.exchange.calendar.event.added")))
        {
            String emailAddresses = notificationUtils.getEmailForUser(event.getUserId());
            logger.debug("On 'Calendar event added' event create notification for participants.");

            notificationService.createNotification("calendarEventAdded", NotificationConstants.CALENDAR_EVENT_ADDED,
                    event.getObjectType(), event.getObjectId(), null, null, event.getParentObjectId(), event.getParentObjectType(),
                    event.getParentObjectName(), emailAddresses, event.getUserId(), null, null);

            logger.debug("Notification 'Calendar event added' created for object [{}] with id [{}] for participants with addresses [{}].",
                    event.getParentObjectType(), event.getParentObjectId(), emailAddresses);
        }
    }

    public NotificationService getNotificationService()
    {
        return notificationService;
    }

    public void setNotificationService(NotificationService notificationService)
    {
        this.notificationService = notificationService;
    }

    public NotificationUtils getNotificationUtils()
    {
        return notificationUtils;
    }

    public void setNotificationUtils(NotificationUtils notificationUtils)
    {
        this.notificationUtils = notificationUtils;
    }
}
