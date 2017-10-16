package com.armedia.acm.services.notification.service;

import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.notification.model.NotificationConstants;


public class ObjectNameTitleFormatter implements CustomTitleFormatter
{
    private NotificationUtils notificationUtils;

    @Override
    public String format(Notification notification)
    {
        String parentObjectType = notification.getRelatedObjectType() != null ?
                notification.getRelatedObjectType() : notification.getParentType();
        Long parentObjectId = notification.getRelatedObjectId() != null ?
                notification.getRelatedObjectId() : notification.getParentId();
        String title = notification.getTitle();

        if (title != null)
        {
            String objectTitle = getNotificationUtils().getNotificationParentOrRelatedObjectNumber(parentObjectType,
                    parentObjectId);

            title = replacePlaceholder(objectTitle, title, NotificationConstants.NAME_LABEL);

        }
        return title;
    }

    private String replacePlaceholder(String objectName, String titlePlaceholder, String placeholder)
    {
        return titlePlaceholder.replace(placeholder, objectName);
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
