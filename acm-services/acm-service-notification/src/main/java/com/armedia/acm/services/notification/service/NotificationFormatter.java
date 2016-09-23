package com.armedia.acm.services.notification.service;

import com.armedia.acm.core.AcmApplication;
import com.armedia.acm.core.AcmObjectType;
import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.notification.model.NotificationConstants;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Properties;


public class NotificationFormatter
{

    private Properties notificationProperties;

    private AcmApplication acmAppConfiguration;

    public Notification replaceFormatPlaceholders(Notification notification)
    {
        String objectTypeLabelPlaceholder = NotificationConstants.OBJECT_TYPE_LABEL_PLACEHOLDER;
        String parentTypeLabelPlaceholder = NotificationConstants.PARENT_TYPE_LABEL_PLACEHOLDER;
        String anchorPlaceholder = NotificationConstants.ANCHOR_PLACEHOLDER;

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
            notificationNote = updatedNote;
        }

        if (notificationNote != null && notificationNote.contains(anchorPlaceholder))
        {
            String notificationLink = buildNotificationLink(notification.getParentType(), notification.getParentId(),
                    notification.getRelatedObjectType(), notification.getRelatedObjectId());
            String updatedNote = replaceAnchor(notificationNote, anchorPlaceholder, notificationLink);
            notification.setLink(notificationLink);
            notification.setNote(updatedNote);
        }

        return notification;

    }

    private String buildNotificationLink(String parentType, Long parentId, String relatedObjectType, Long relatedObjectId)
    {
        String baseUrl = getNotificationProperties().getProperty(NotificationConstants.BASE_URL_KEY);

        String url = null;
        // find the object type from the ACM application configuration, and get the URL from the object type
        for (AcmObjectType objectType : getAcmAppConfiguration().getObjectTypes())
        {
            Map<String, String> urlValues = objectType.getUrl();

            // If relatedObjectType is null, the parent object is TOP LEVEL (Case File or Complaint)
            // else parent object is nested in top level object
            String linkedObjectType = StringUtils.isEmpty(relatedObjectType) ? parentType : relatedObjectType;
            Long linkedObjectId = StringUtils.isEmpty(relatedObjectType) ? parentId : relatedObjectId;

            if (objectType.getName().equals(parentType) && StringUtils.isNotEmpty(linkedObjectType))
            {
                String objectUrl = urlValues.get(linkedObjectType);
                if (StringUtils.isNotEmpty(objectUrl))
                {
                    objectUrl = String.format(objectUrl, linkedObjectId);
                    url = String.format("%s%s", baseUrl, objectUrl);
                }
            }
        }

        return url;
    }

    private String replaceAnchor(String withPlaceholder, String anchorPlaceholder, String notificationLink)
    {
        if (StringUtils.isNotEmpty(notificationLink))
        {
            return withPlaceholder.replace(anchorPlaceholder, notificationLink);
        }
        return withPlaceholder;
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

    public AcmApplication getAcmAppConfiguration()
    {
        return acmAppConfiguration;
    }

    public void setAcmAppConfiguration(AcmApplication acmAppConfiguration)
    {
        this.acmAppConfiguration = acmAppConfiguration;
    }

}
