package com.armedia.acm.services.notification.service;

/*-
 * #%L
 * ACM Service: Email
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

import com.armedia.acm.core.AcmApplication;
import com.armedia.acm.core.model.ApplicationConfig;
import com.armedia.acm.services.email.model.EmailMentionsDTO;
import com.armedia.acm.services.email.service.AcmEmailServiceException;
import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.notification.model.NotificationConstants;
import com.armedia.acm.services.users.model.AcmUser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author ivana.shekerova on 11/13/2018.
 */
public class AcmEmailMentionsService
{
    private final Logger log = LogManager.getLogger(getClass());

    private AcmApplication acmAppConfiguration;
    private ApplicationConfig applicationConfig;
    private NotificationService notificationService;

    private String buildObjectUrl(EmailMentionsDTO in)
    {
        String baseUrl = applicationConfig.getBaseUrl();
        if (in.getUrlPath() != null)
        {
            return baseUrl + in.getUrlPath();
        }
        else
        {
            String subType = !in.getSubType().isEmpty() ? in.getSubType() : in.getObjectType();
            Optional<String> objectUrl = acmAppConfiguration.getObjectTypes().stream()
                    .filter(acmObjectType -> acmObjectType.getName().equals(in.getObjectType()))
                    .map(acmObjectType -> acmObjectType.getUrl().get(subType))
                    .collect(Collectors.toList()).stream().findFirst();
            return objectUrl.map(s -> baseUrl + String.format(s, in.getObjectId())).orElse(baseUrl);
        }
    }

    public void sendMentionsEmail(EmailMentionsDTO in, AcmUser user) throws AcmEmailServiceException
    {
        Notification notification = notificationService.getNotificationBuilder()
                .newNotification("mentions", NotificationConstants.EMAIL_MENTIONS, in.getObjectType(), in.getObjectId(),
                        user.getUserId())
                .forObjectWithNumber(in.getObjectId().toString())
                .withNote(in.getTextMentioned())
                .withData(buildObjectUrl(in))
                .withEmailAddresses(String.join(",", in.getEmailAddresses()))
                .build(user.getFullName());

        notificationService.saveNotification(notification);
    }

    public void setAcmAppConfiguration(AcmApplication acmAppConfiguration)
    {
        this.acmAppConfiguration = acmAppConfiguration;
    }

    public void setApplicationConfig(ApplicationConfig applicationConfig)
    {
        this.applicationConfig = applicationConfig;
    }

    public NotificationService getNotificationService()
    {
        return notificationService;
    }

    public void setNotificationService(NotificationService notificationService)
    {
        this.notificationService = notificationService;
    }
}
