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

import com.armedia.acm.auth.web.ForgotUsernameEvent;
import com.armedia.acm.core.AcmSpringActiveProfile;
import com.armedia.acm.services.notification.dao.NotificationDao;
import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;



public class OnForgotUsername implements ApplicationListener<ForgotUsernameEvent>
{
    private NotificationDao notificationDao;
    private UserDao userDao;
    private final Logger log = LoggerFactory.getLogger(getClass());
    private AcmSpringActiveProfile acmSpringActiveProfile;

    @Override
    public void onApplicationEvent(ForgotUsernameEvent forgotUsernameEvent)
    {
        if (forgotUsernameEvent.isSucceeded())
        {
            AcmUser user = userDao.findByUserId(forgotUsernameEvent.getUserId());

            if (acmSpringActiveProfile.isSAMLEnabledEnvironment())
            {
                log.info("Won't send forgot username email when SSO environment");
                return;
            }
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

    public void setAcmSpringActiveProfile(AcmSpringActiveProfile acmSpringActiveProfile) 
    {
        this.acmSpringActiveProfile = acmSpringActiveProfile;
    }
}
