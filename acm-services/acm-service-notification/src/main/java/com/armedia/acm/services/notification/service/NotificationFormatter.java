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

import com.armedia.acm.services.labels.service.TranslationService;
import com.armedia.acm.services.notification.model.NotificationConfig;
import com.armedia.acm.services.notification.model.NotificationConstants;

public class NotificationFormatter
{
    private NotificationConfig notificationConfig;
    private TranslationService translationService;

    public String buildTitle(String title, String parentName, String parentType, String userId)
    {
        String notificationTitle = translationService.translate(title);
        return replaceFormatPlaceholders(notificationTitle, parentName, parentType, userId);
    }

    public String replaceFormatPlaceholders(String notificationTitle, String parentName, String parentType, String userId)
    {
        if (notificationTitle != null && notificationTitle.contains(NotificationConstants.NAME_LABEL))
        {
            notificationTitle = replacePlaceholderLabel(notificationTitle, NotificationConstants.NAME_LABEL, parentName);
        }

        if (notificationTitle != null && notificationTitle.contains(NotificationConstants.TYPE_LABEL))
        {
            notificationTitle = replaceObjectTypeLabel(notificationTitle, NotificationConstants.TYPE_LABEL, parentType);
        }

        if (notificationTitle != null && notificationTitle.contains(NotificationConstants.USER_LABEL))
        {
            notificationTitle = replacePlaceholderLabel(notificationTitle, NotificationConstants.USER_LABEL, userId);
        }
        return notificationTitle;
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

    private String replacePlaceholderLabel(String withPlaceholder, String placeholder, String value)
    {
        return withPlaceholder.replace(placeholder, value);
    }

    public NotificationConfig getNotificationConfig()
    {
        return notificationConfig;
    }

    public void setNotificationConfig(NotificationConfig notificationConfig)
    {
        this.notificationConfig = notificationConfig;
    }

    public TranslationService getTranslationService()
    {
        return translationService;
    }

    public void setTranslationService(TranslationService translationService)
    {
        this.translationService = translationService;
    }
}
