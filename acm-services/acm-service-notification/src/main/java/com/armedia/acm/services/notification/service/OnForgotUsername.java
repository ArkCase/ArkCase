package com.armedia.acm.services.notification.service;

import com.armedia.acm.auth.web.ForgotUsernameEvent;
import com.armedia.acm.services.notification.dao.NotificationDao;
import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import org.springframework.context.ApplicationListener;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;


public class OnForgotUsername implements ApplicationListener<ForgotUsernameEvent>
{
    private NotificationDao notificationDao;
    private UserDao userDao;

    @Override
    public void onApplicationEvent(ForgotUsernameEvent forgotUsernameEvent)
    {
        if (forgotUsernameEvent.isSucceeded())
        {
            AcmUser user = userDao.findByUserId(forgotUsernameEvent.getUserId());

            AbstractMap.SimpleImmutableEntry<String, List<String>> usernames = (AbstractMap.SimpleImmutableEntry) forgotUsernameEvent.getSource();
            
            Notification notification = new Notification();
            notification.setCreator(user.getUserId());
            notification.setModifier(user.getUserId());
            notification.setTemplateModelName("forgotUsername");
            notification.setParentType(notification.getObjectType());
            notification.setUser(user.getUserId());
            notification.setAttachFiles(false);
            notification.setEmailAddresses(user.getMail());
            notification.setTitle("Forgot Username");
            notificationDao.save(notification);
        }
    }

    public NotificationDao getNotificationDao()
    {
        return notificationDao;
    }

    public void setNotificationDao(NotificationDao notificationDao)
    {
        this.notificationDao = notificationDao;
    }

    public UserDao getUserDao() {
        return userDao;
    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }
}
