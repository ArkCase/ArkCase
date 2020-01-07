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
import com.armedia.acm.core.ApplicationConfig;
import com.armedia.acm.services.email.model.EmailMentionsDTO;
import com.armedia.acm.services.email.service.AcmEmailServiceException;
import com.armedia.acm.services.email.service.TemplatingEngine;
import com.armedia.acm.services.notification.dao.NotificationDao;
import com.armedia.acm.services.notification.model.Notification;

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
    private TemplatingEngine templatingEngine;
    private NotificationDao notificationDao;
    private ApplicationConfig applicationConfig;

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
            return objectUrl.isPresent() ? baseUrl + String.format(objectUrl.get(), in.getObjectId()) : baseUrl;
        }
    }

    public void sendMentionsEmail(EmailMentionsDTO in, String userFullName) throws AcmEmailServiceException
    {

        Notification notification = new Notification();
        notification.setTemplateModelName("mentions");
        notification.setTitle(String.format("%s mentioned you in %s %d", userFullName, in.getObjectType(), in.getObjectId()));
        notification.setAttachFiles(false);
        notification.setParentType(in.getObjectType());
        notification.setParentId(in.getObjectId());
        notification.setNote(in.getTextMentioned());
        notification.setData(buildObjectUrl(in));
        notification.setUser(userFullName);
        notification.setEmailAddresses(in.getEmailAddresses().stream().collect(Collectors.joining(",")));
        notificationDao.save(notification);

    }

    public void setAcmAppConfiguration(AcmApplication acmAppConfiguration)
    {
        this.acmAppConfiguration = acmAppConfiguration;
    }

    public TemplatingEngine getTemplatingEngine()
    {
        return templatingEngine;
    }

    public void setTemplatingEngine(TemplatingEngine templatingEngine)
    {
        this.templatingEngine = templatingEngine;
    }

    public NotificationDao getNotificationDao()
    {
        return notificationDao;
    }

    public void setNotificationDao(NotificationDao notificationDao)
    {
        this.notificationDao = notificationDao;
    }

    public void setApplicationConfig(ApplicationConfig applicationConfig)
    {
        this.applicationConfig = applicationConfig;
    }
}
