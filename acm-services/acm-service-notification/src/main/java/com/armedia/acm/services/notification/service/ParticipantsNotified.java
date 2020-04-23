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

import com.armedia.acm.core.AcmNotifiableEntity;
import com.armedia.acm.core.AcmNotificationReceiver;
import com.armedia.acm.data.AcmNotificationDao;
import com.armedia.acm.data.service.AcmDataService;
import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.notification.model.NotificationConstants;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.dao.group.AcmGroupDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.group.AcmGroup;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class ParticipantsNotified implements UsersNotified
{
    private UserDao userDao;
    private AcmGroupDao groupDao;
    private AcmDataService acmDataService;

    /**
     * Find the users that should be notified on specific event for TASK, CASE_FILE or COMPLAINT and set notification
     * for each
     *
     * @param notification
     *            The result row of the notification
     * @param parentObjectId
     *            The id of the parent
     * @param parentObjectType
     *            The object type of the parent
     * @return List of notifications
     */

    @Override
    public Notification getNotification(Object[] notification, Long parentObjectId, String parentObjectType)
    {
        AcmNotificationDao notificationDao = getAcmDataService().getNotificationDaoByObjectType(parentObjectType);
        if (notificationDao != null)
        {
            AcmNotifiableEntity entity = notificationDao.findEntity(parentObjectId);
            if (entity != null)
            {
                Set<AcmNotificationReceiver> participants = entity.getReceivers();
                Set<AcmUser> receivers = getUsers(participants);
                return setNotificationForUsers(notification, receivers);
            }
        }
        return null;
    }

    private Set<AcmUser> getUsers(Set<AcmNotificationReceiver> participants)
    {
        Set<AcmUser> receivers = new HashSet<>();
        for (AcmNotificationReceiver participant : participants)
        {
            if (participant.getReceiverType().equals(NotificationConstants.PARTICIPANT_TYPE_GROUP))
            {
                AcmGroup group = getGroupDao().findByName(participant.getReceiverLdapId());
                if (group != null)
                {
                    receivers.addAll(group.getUserMembers());
                }

            }
            else if (!participant.getReceiverType().equals(NotificationConstants.SPECIAL_PARTICIPANT_TYPE))
            {
                AcmUser user = getUserDao().findByUserId(participant.getReceiverLdapId());
                if (user != null)
                {
                    receivers.add(user);
                }
            }
        }
        return receivers;
    }

    private Notification setNotificationForUsers(Object obj[], Set<AcmUser> users)
    {
        Notification notification = setNewNotification(obj);
        notification.setEmailAddresses(users.stream().map(user -> user.getMail()).collect(Collectors.joining(",")));
        // the userId is only used for audit, so any user will do here
        notification.setUser(users.stream().findAny().get().getUserId());
        return notification;
    }

    public AcmDataService getAcmDataService()
    {
        return acmDataService;
    }

    public void setAcmDataService(AcmDataService acmDataService)
    {
        this.acmDataService = acmDataService;
    }

    public UserDao getUserDao()
    {
        return userDao;
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    public AcmGroupDao getGroupDao()
    {
        return groupDao;
    }

    public void setGroupDao(AcmGroupDao groupDao)
    {
        this.groupDao = groupDao;
    }
}
