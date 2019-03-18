package com.armedia.acm.services.notification.service;

import com.armedia.acm.services.notification.dao.NotificationDao;
import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;

public class ResetPasswordService {

    private NotificationDao notificationDao;
    private UserDao userDao;
    
    public void sendPasswordResetNotification(AcmUser user)
    {
        
        
        Notification notification = new Notification();
        notification.setCreator(user.getUserId());
        notification.setModifier(user.getUserId());
        notification.setTemplateModelName("changePassword");
        notification.setParentType("USER");
        notification.setParentName(user.getUserId());
        notification.setAttachFiles(false);
        notification.setEmailAddresses(user.getMail());
        notification.setTitle("Reset password");
        notificationDao.save(notification);
    }

    public boolean isUserPasswordExpired(String userId)
    {
        return userDao.isUserPasswordExpired(userId);
    }

    public NotificationDao getNotificationDao()
    {
        return notificationDao;
    }

    public void setNotificationDao(NotificationDao notificationDao)
    {
        this.notificationDao = notificationDao;
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
