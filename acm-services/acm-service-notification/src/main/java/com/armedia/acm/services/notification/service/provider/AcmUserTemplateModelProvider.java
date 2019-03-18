package com.armedia.acm.services.notification.service.provider;

import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.users.dao.UserDao;

public class AcmUserTemplateModelProvider implements TemplateModelProvider
{

    private UserDao userDao;

    @Override
    public Object getModel(Notification notification)
    {
        return userDao.findByUserId(notification.getParentName());
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
