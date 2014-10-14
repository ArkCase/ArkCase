package com.armedia.acm.services.notification.service;

import com.armedia.acm.event.AcmEvent;
import com.armedia.acm.services.notification.model.Notification;

/**
 * Created by manoj.dhungana on 10/10/2014.
 */
public class ApplicationNotificationEvent extends AcmEvent
{
    /**
     *
     */
    private static final long serialVersionUID = -320828483774515322L;

    public ApplicationNotificationEvent(Notification source, String notificationEvent, boolean succeeded, String ipAddress)
    {
        super(source);

        setObjectId(source.getId());
        setEventDate(source.getModified());
        setUserId(source.getUser());
        setEventType(notificationEvent);
        setSucceeded(succeeded);
        setIpAddress(ipAddress);
    }
}


