package com.armedia.acm.services.notification.service;

import com.armedia.acm.core.AcmNotifiableEntity;
import com.armedia.acm.core.AcmNotificationReceiver;
import com.armedia.acm.data.AcmNotificationDao;
import com.armedia.acm.data.service.AcmDataService;
import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.notification.model.NotificationConstants;
import com.armedia.acm.services.users.dao.group.AcmGroupDao;
import com.armedia.acm.services.users.dao.ldap.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.group.AcmGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by ncuculova
 */
public class ParticipantsNotified implements UsersNotified
{
    private UserDao userDao;
    private AcmGroupDao groupDao;
    private AcmDataService acmDataService;

    private Notification setNewNotification(Object obj[])
    {
        Notification notification = new Notification();
        notification.setTitle((String) obj[0]);
        notification.setNote((String) obj[1]);
        notification.setType((String) obj[2]);
        notification.setParentId((Long) obj[3]);
        notification.setParentType((String) obj[4]);
        notification.setParentName((String) obj[5]);
        notification.setParentTitle((String) obj[6]);
        notification.setRelatedObjectId((Long) obj[7]);
        notification.setRelatedObjectType((String) obj[8]);
        notification.setActionDate((Date) obj[9]);
        notification.setStatus(NotificationConstants.STATUS_NEW);
        notification.setAction(NotificationConstants.ACTION_DEFAULT);
        notification.setData("{\"usr\":\"/plugin/" + ((String) obj[4]).toLowerCase() + "/" + obj[3] + "\"}");
        return notification;
    }

    /**
     * Find the users that should be notified on specific event for TASK, CASE_FILE or COMPLAINT and set notification
     * for each
     *
     * @param notification     The result row of the notification
     * @param parentObjectId   The id of the parent
     * @param parentObjectType The object type of the parent
     * @return List of notifications
     */

    @Override
    public List<Notification> getNotifications(Object[] notification, Long parentObjectId, String parentObjectType)
    {
        AcmNotificationDao notificationDao = getAcmDataService().getNotificationDaoByObjectType(parentObjectType);
        if (notificationDao != null){
            AcmNotifiableEntity entity = notificationDao.findEntity(parentObjectId);
            if (entity != null){
                Set<AcmNotificationReceiver> participants = entity.getReceivers();
                Set<AcmUser> receivers = getUsers(participants);
                return setNotificationForUsers(notification, receivers);
            }
        }
        return Collections.emptyList();
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
                    receivers.addAll(group.getMembers());
                }

            } else if (!participant.getReceiverType().equals(NotificationConstants.SPECIAL_PARTICIPANT_TYPE))
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

    private List<Notification> setNotificationForUsers(Object obj[], Set<AcmUser> users)
    {
        List<Notification> notifications = new ArrayList<>();
        for (AcmUser user : users)
        {
            Notification notification = setNewNotification(obj);
            notification.setUserEmail(user.getMail());
            notification.setUser(user.getUserId());
            notifications.add(notification);
        }
        return notifications;
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
