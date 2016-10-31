package com.armedia.acm.services.notification.service;

import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.notification.model.NotificationConstants;

import java.util.Properties;


public class NotificationFormatter
{

    private Properties notificationProperties;

    public Notification replaceFormatPlaceholders(Notification notification)
    {
        String objectTypeLabelPlaceholder = NotificationConstants.OBJECT_TYPE_LABEL_PLACEHOLDER;
        String parentTypeLabelPlaceholder = NotificationConstants.PARENT_TYPE_LABEL_PLACEHOLDER;

        String notificationTitle = notification.getTitle();
        if (notificationTitle != null && notificationTitle.contains(objectTypeLabelPlaceholder))
        {
            String updatedTitle = replaceObjectTypeLabel(notificationTitle, objectTypeLabelPlaceholder,
                    notification.getParentType());
            notification.setTitle(updatedTitle);
            notificationTitle = updatedTitle;
        }

        if (notificationTitle != null && notificationTitle.contains(parentTypeLabelPlaceholder))
        {
            String updatedTitle = replaceObjectTypeLabel(notificationTitle, parentTypeLabelPlaceholder,
                    notification.getRelatedObjectType());
            notification.setTitle(updatedTitle);
        }

        String notificationNote = notification.getNote();
        if (notificationNote != null && notificationNote.contains(objectTypeLabelPlaceholder))
        {
            String updatedNote = replaceObjectTypeLabel(notificationNote, objectTypeLabelPlaceholder,
                    notification.getParentType());
            notification.setNote(updatedNote);
            notificationNote = updatedNote;
        }

        if (notificationNote != null && notificationNote.contains(parentTypeLabelPlaceholder))
        {
            String updatedNote = replaceObjectTypeLabel(notificationNote, parentTypeLabelPlaceholder,
                    notification.getRelatedObjectType());
            notification.setNote(updatedNote);
        }

        return notification;

    }

    private String replaceObjectTypeLabel(String withPlaceholder, String placeholder, String parentType)
    {
        String keyLabel = parentType + ".label";
        String objectTypeLabel = getNotificationProperties().getProperty(keyLabel);
        return withPlaceholder.replace(placeholder, objectTypeLabel);
    }

    private String replaceParentTypeLabel(String withPlaceholder, String placeholder, String relatedType)
    {
        String keyLabel = relatedType + ".label";
        String parentTypeLabel = getNotificationProperties().getProperty(keyLabel);
        return withPlaceholder.replace(placeholder, parentTypeLabel);
    }

    public Properties getNotificationProperties()
    {
        return notificationProperties;
    }

    public void setNotificationProperties(Properties notificationProperties)
    {
        this.notificationProperties = notificationProperties;
    }

}
