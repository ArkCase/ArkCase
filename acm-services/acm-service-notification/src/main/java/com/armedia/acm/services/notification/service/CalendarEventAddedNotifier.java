package com.armedia.acm.services.notification.service;

import com.armedia.acm.service.outlook.model.CalendarEventAddedEvent;
import com.armedia.acm.services.notification.model.NotificationConstants;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationListener;

public class CalendarEventAddedNotifier implements ApplicationListener<CalendarEventAddedEvent>
{
    private NotificationService notificationService;
    private NotificationUtils notificationUtils;

    private static final Logger logger = LogManager.getLogger(CalendarEventAddedNotifier.class);

    @Override
    public void onApplicationEvent(CalendarEventAddedEvent event)
    {
        String eventType = event.getEventType();
        if (eventType.equals("com.armedia.acm.outlook.calendar.event.added"))
        {
            String emailAddresses = notificationUtils.getEmailsCommaSeparatedForParticipantsForObject(event.getParentObjectId(),
                    event.getParentObjectType());
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
