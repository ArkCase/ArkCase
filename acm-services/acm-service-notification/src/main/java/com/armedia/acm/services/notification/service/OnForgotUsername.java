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
import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.notification.model.NotificationConstants;
import com.armedia.acm.services.templateconfiguration.model.Template;
import com.armedia.acm.services.templateconfiguration.service.CorrespondenceTemplateManager;
import com.armedia.acm.services.users.model.AcmUser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationListener;

public class OnForgotUsername implements ApplicationListener<ForgotUsernameEvent>
{
    private final Logger log = LogManager.getLogger(getClass());
    private AcmSpringActiveProfile acmSpringActiveProfile;
    private NotificationService notificationService;
    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;
    private CorrespondenceTemplateManager templateManager;

    @Override
    public void onApplicationEvent(ForgotUsernameEvent forgotUsernameEvent)
    {
        if (forgotUsernameEvent.isSucceeded())
        {
            AcmUser user = forgotUsernameEvent.getAcmUser();

            if (acmSpringActiveProfile.isSSOEnabledEnvironment())
            {
                throw new UnsupportedOperationException("Won't send forgot username email when SSO environment");
            }

            log.debug("Create 'Forgot username' notification for user [{}] with email [{}].", user.getUserId(), user.getMail());

            auditPropertyEntityAdapter.setUserId(user.getUserId());

            String emailSubject = "";
            Template template = templateManager.findTemplate("forgotUsername.html");
            if (template != null)
            {
                emailSubject = template.getEmailSubject();
            }
            Notification notification = notificationService.getNotificationBuilder()
                    .newNotification("forgotUsername", NotificationConstants.FORGOT_USERNAME, "USER", user.getIdentifier(),
                            user.getUserId())
                    .forObjectWithNumber(user.getUserId())
                    .withEmailAddresses(user.getMail())
                    .withSubject(emailSubject)
                    .build();

            notificationService.saveNotification(notification);
        }
    }

    public void setAcmSpringActiveProfile(AcmSpringActiveProfile acmSpringActiveProfile)
    {
        this.acmSpringActiveProfile = acmSpringActiveProfile;
    }

    public AcmSpringActiveProfile getAcmSpringActiveProfile()
    {
        return acmSpringActiveProfile;
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
