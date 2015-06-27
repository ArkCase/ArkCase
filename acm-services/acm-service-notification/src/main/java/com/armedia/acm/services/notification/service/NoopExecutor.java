package com.armedia.acm.services.notification.service;

import com.armedia.acm.services.notification.model.Notification;

/**
 * Useful when we just want to store the notification.  We don't actually want to send it by e-mail or anything.
 */
public class NoopExecutor implements Executor
{
    @Override
    public Notification execute(Notification notification)
    {
        notification.setState("SENT");
        return notification;
    }
}
