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
import com.armedia.acm.services.notification.model.NotificationConstants;

public class ObjectNameTitleFormatter implements CustomTitleFormatter
{
    private NotificationUtils notificationUtils;

    @Override
    public String format(Notification notification)
    {
        String parentObjectType = notification.getRelatedObjectType() != null ? notification.getRelatedObjectType()
                : notification.getParentType();
        Long parentObjectId = notification.getRelatedObjectId() != null ? notification.getRelatedObjectId() : notification.getParentId();
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
