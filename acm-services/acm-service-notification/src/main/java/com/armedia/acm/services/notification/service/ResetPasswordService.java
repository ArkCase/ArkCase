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
import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.notification.model.NotificationConstants;
import com.armedia.acm.services.templateconfiguration.model.Template;
import com.armedia.acm.services.templateconfiguration.service.CorrespondenceTemplateManager;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.PasswordResetToken;
import com.armedia.acm.web.api.MDCConstants;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slf4j.MDC;

public class ResetPasswordService
{
    private UserDao userDao;
    private AcmSpringActiveProfile acmSpringActiveProfile;
    private final Logger log = LogManager.getLogger(getClass());
    private NotificationService notificationService;
    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;
    private CorrespondenceTemplateManager templateManager;

    public void sendPasswordResetNotification(AcmUser user)
    {
        if (acmSpringActiveProfile.isSSOEnabledEnvironment())
        {
            throw new UnsupportedOperationException("Won't send password reset email when SSO environment");
        }
        log.debug("Create password reset notification for user [{}]", user.getUserId());
        user.setPasswordResetToken(new PasswordResetToken());
        userDao.save(user);

        MDC.put(MDCConstants.EVENT_MDC_REQUEST_USER_ID_KEY, user.getUserId());
        auditPropertyEntityAdapter.setUserId(user.getUserId());

        String emailSubject = "";
        Template template = templateManager.findTemplate("changePassword.html");
        if (template != null)
        {
            emailSubject = template.getEmailSubject();
        }
        Notification notification = notificationService.getNotificationBuilder()
                .newNotification("changePassword", NotificationConstants.PASSWORD_RESET, "USER", user.getIdentifier(),
                        user.getUserId())
                .forObjectWithNumber(user.getUserId())
                .withEmailAddresses(user.getMail())
                .withSubject(emailSubject)
                .build();

        notificationService.saveNotification(notification);
    }

    public boolean isUserPasswordExpired(String userId)
    {
        return userDao.isUserPasswordExpired(userId);
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

    public NotificationService getNotificationService()
    {
        return notificationService;
    }

    public void setNotificationService(NotificationService notificationService)
    {
        this.notificationService = notificationService;
    }

    public AuditPropertyEntityAdapter getAuditPropertyEntityAdapter()
    {
        return auditPropertyEntityAdapter;
    }

    public void setAuditPropertyEntityAdapter(AuditPropertyEntityAdapter auditPropertyEntityAdapter)
    {
        this.auditPropertyEntityAdapter = auditPropertyEntityAdapter;
    }

    public CorrespondenceTemplateManager getTemplateManager()
    {
        return templateManager;
    }

    public void setTemplateManager(CorrespondenceTemplateManager templateManager)
    {
        this.templateManager = templateManager;
    }
}
