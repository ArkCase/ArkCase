package com.armedia.acm.services.notification.service;

/*-
 * #%L
 * ACM Service: Notification
 * %%
 * Copyright (C) 2014 - 2019 ArkCase LLC
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

import com.armedia.acm.core.AcmSpringActiveProfile;
import com.armedia.acm.services.notification.dao.NotificationDao;
import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.PasswordResetToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResetPasswordService {

    private NotificationDao notificationDao;
    private UserDao userDao;
    private AcmSpringActiveProfile acmSpringActiveProfile;
    private final Logger log = LoggerFactory.getLogger(getClass());
    
    public void sendPasswordResetNotification(AcmUser user)
    {
        if (acmSpringActiveProfile.isSAMLEnabledEnvironment())
        {
            throw new UnsupportedOperationException("Won't send password reset email when SSO environment");
        }
        user.setPasswordResetToken(new PasswordResetToken());
        userDao.save(user);
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

    public void setAcmSpringActiveProfile(AcmSpringActiveProfile acmSpringActiveProfile) 
    {
        this.acmSpringActiveProfile = acmSpringActiveProfile;
    }
}
