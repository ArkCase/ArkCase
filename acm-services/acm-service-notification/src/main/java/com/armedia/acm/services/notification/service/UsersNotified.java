/**
 * 
 */
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

import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * @author ncuculova
 *
 */
public interface UsersNotified
{
    List<Notification> getNotifications(Object[] notification, Long parentObjectId, String parentObjectType);

    default Notification setNewNotification(Object obj[])
    {
        Notification notification = new Notification();
        notification.setTitle((String) obj[0]);
        notification.setNote((String) obj[1]);
        notification.setType((String) obj[2]);
        notification.setParentId((Long) obj[3]);
        notification.setParentType((String) obj[4]);
        notification.setParentName((String) obj[5]);
        notification.setParentTitle(StringUtils.left((String) obj[6], 1000));
        notification.setRelatedObjectId((Long) obj[7]);
        notification.setRelatedObjectType((String) obj[8]);
        notification.setActionDate((Date) obj[9]);
        if (10 < obj.length)
        {
            notification.setTemplateModelName((String) obj[10]);
        }
        notification.setStatus(NotificationConstants.STATUS_NEW);
        notification.setAction(NotificationConstants.ACTION_DEFAULT);
        notification.setData("{\"usr\":\"/plugin/" + ((String) obj[4]).toLowerCase() + "/" + obj[3] + "\"}");
        return notification;
    }
}
