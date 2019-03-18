package com.armedia.acm.services.notification.service.provider;

import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.notification.service.NotificationUtils;

public class NotificationTemplateModelProvider implements TemplateModelProvider
{

    protected NotificationUtils notificationUtils;

    @Override
    public Object getModel(Notification notification)
    {
        notification.setObjectLink(notificationUtils.buildNotificationLink(notification.getParentType(),
                notification.getParentId(), notification.getRelatedObjectType(), notification.getRelatedObjectId()));
        return notification;
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
