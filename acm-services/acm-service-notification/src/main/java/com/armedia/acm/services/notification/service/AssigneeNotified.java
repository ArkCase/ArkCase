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
import com.armedia.acm.services.users.dao.UserDao;

import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class AssigneeNotified implements UsersNotified
{

    private UserDao userDao;

    @Override
    public List<Notification> getNotifications(Object[] notification, Long parentObjectId, String parentObjectType)
    {
        Notification customNotification = new Notification();

        customNotification.setTitle((String) notification[0]);
        customNotification.setNote((String) notification[1]);
        customNotification.setType((String) notification[2]);
        customNotification.setParentId((Long) notification[3]);
        customNotification.setParentType((String) notification[4]);
        customNotification.setParentName((String) notification[5]);
        customNotification.setParentTitle(StringUtils.left((String) notification[6], 1000));
        customNotification.setRelatedObjectId((Long) notification[7]);
        customNotification.setRelatedObjectType((String) notification[8]);
        customNotification.setActionDate((Date) notification[9]);
        customNotification.setUser(getUserId((String) notification[10]));
        customNotification.setEmailAddresses(getEmailForUser((String) notification[10]));
        customNotification.setTemplateModelName((String) notification[11]);
        customNotification.setStatus(NotificationConstants.STATUS_NEW);
        customNotification.setAction(NotificationConstants.ACTION_DEFAULT);
        customNotification.setData("{\"usr\":\"/plugin/" + ((String) notification[4]).toLowerCase() + "/" + notification[3] + "\"}");
        customNotification.setAttachFiles(false);

        return Arrays.asList(customNotification);
    }

    private String getEmailForUser(String user)
    {
        String userEmail = getUserDao().findByUserId(user).getMail();
        return !userEmail.isEmpty() ? userEmail : "";
    }

    private String getUserId(String user)
    {
        String userId = getUserDao().findByUserId(user).getUserId();
        return !userId.isEmpty() ? userId : "";
    }

    public UserDao getUserDao()
    {
        return userDao;
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }
}
