package com.armedia.acm.services.notification.service;

/*-
 * #%L
 * ACM Service: Notification
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

import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.notification.model.NotificationConfig;
import com.armedia.acm.services.notification.model.NotificationConstants;

public class NotificationFormatter
{
    private NotificationConfig notificationConfig;

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

    public String replaceSubscriptionTitle(String title, String placeholder, String objectType)
    {
        return replaceObjectTypeLabel(title, placeholder, objectType);
    }

    private String replaceObjectTypeLabel(String withPlaceholder, String placeholder, String parentType)
    {
        String objectTypeLabel = notificationConfig.getLabelForObjectType(parentType);
        return withPlaceholder.replace(placeholder, objectTypeLabel);
    }

    private String replaceParentTypeLabel(String withPlaceholder, String placeholder, String relatedType)
    {
        String parentTypeLabel = notificationConfig.getLabelForObjectType(relatedType);
        return withPlaceholder.replace(placeholder, parentTypeLabel);
    }

    public NotificationConfig getNotificationConfig()
    {
        return notificationConfig;
    }

    public void setNotificationConfig(NotificationConfig notificationConfig)
    {
        this.notificationConfig = notificationConfig;
    }
}
