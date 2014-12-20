package com.armedia.acm.services.notification.model;

import com.armedia.acm.event.AcmEvent;


public class ApplicationNotificationEvent extends AcmEvent
{

    private static final long serialVersionUID = -320828483774515322L;

    public ApplicationNotificationEvent(Notification source, String notificationEvent, boolean succeeded, String ipAddress)
    {
        super(source);

        setObjectId(source.getId());
        setEventDate(source.getCreated());
        setUserId(source.getCreator());
        setEventType(notificationEvent);
        setSucceeded(succeeded);
        setIpAddress(ipAddress);
    }
}


