package com.armedia.acm.services.notification.service;

import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.notification.model.NotificationConstants;
import com.armedia.acm.services.users.dao.UserDao;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class AssigneeNotified implements UsersNotified
{

    private UserDao userDao;

    @Override public List<Notification> getNotifications(Object[] notification, Long parentObjectId, String parentObjectType)
    {
        Notification customNotification = new Notification();

        customNotification.setTitle((String) notification[0]);
        customNotification.setNote((String) notification[1]);
        customNotification.setType((String) notification[2]);
        customNotification.setParentId((Long) notification[3]);
        customNotification.setParentType((String) notification[4]);
        customNotification.setParentName((String) notification[5]);
        customNotification.setParentTitle((String) notification[6]);
        customNotification.setRelatedObjectId((Long) notification[7]);
        customNotification.setRelatedObjectType((String) notification[8]);
        customNotification.setActionDate((Date) notification[9]);
        customNotification.setUserEmail(getEmailForUser((String) notification[10]));
        customNotification.setStatus(NotificationConstants.STATUS_NEW);
        customNotification.setAction(NotificationConstants.ACTION_DEFAULT);
        customNotification.setData("{\"usr\":\"/plugin/" + ((String) notification[4]).toLowerCase() + "/" + notification[3] + "\"}");

        return Arrays.asList(customNotification);
    }

    private String getEmailForUser(String user)
    {
        String userEmail = getUserDao().findByUserId(user).getMail();
        return !userEmail.isEmpty() ? userEmail : "";
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
