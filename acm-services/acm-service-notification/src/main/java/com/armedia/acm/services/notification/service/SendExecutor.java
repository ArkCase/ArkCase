/**
 * 
 */
package com.armedia.acm.services.notification.service;

/*-
 * #%L
 * ACM Service: Notification
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

import com.armedia.acm.core.provider.TemplateModelProvider;
import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.notification.model.NotificationConstants;
import com.armedia.acm.services.templateconfiguration.model.Template;
import com.armedia.acm.services.templateconfiguration.service.TemplateConfigurationManager;
import com.armedia.acm.services.templateconfiguration.service.TemplatingEngine;
import com.armedia.acm.spring.SpringContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;

/**
 * @author riste.tutureski
 *
 */
public class SendExecutor implements Executor
{
    private SpringContextHolder springContextHolder;
    private TemplateConfigurationManager templateConfigurationManager;

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public Notification execute(Notification notification)
    {
        String templateModelProviderClassPath = getTemplateModelProviderClassName(notification.getTemplateModelName() + ".html");

        Class templateModelProviderClass = null;
        try
        {
            templateModelProviderClass = Class.forName(templateModelProviderClassPath);
        }
        catch (Exception e)
        {
            log.error("Can not find class for provided classpath {}. Error: {}", templateModelProviderClassPath, e.getMessage());
        }
        TemplateModelProvider templateModelProvider = getTemplateModelProvider(templateModelProviderClass);

        Class templateModelProviderClass = null;
        try
        {
            templateModelProviderClass = Class.forName(templateModelProviderClassPath);
        }
        catch (Exception e)
        {
            log.error("Can not find class for provided classpath {}. Error: {}", templateModelProviderClassPath, e.getMessage());
        }
        TemplateModelProvider templateModelProvider = getTemplateModelProvider(templateModelProviderClass);

        Class templateModelProviderClass = null;
        try
        {

            if (notification.getState() == null || !notification.getState().equals(NotificationConstants.STATE_SENT))
            {
                try
                {
                    notification = senderFactory.getNotificationSender().send(notification, templateModelProvider.getModel(notification));
                }
                catch (Exception e)
                {
                    log.error("Can not send the notification. Error: {}", e.getMessage());
                    notification.setStatus(NotificationConstants.STATE_NOT_SENT);
                }
            }
        }

        Class<?> templateModelProviderClass;
        try
        {
            templateModelProviderClass = Class.forName(templateModelProviderClassPath);
        }
        catch (ClassNotFoundException | ExceptionInInitializerError e)
        {
            log.error("Can not find class for provided classname [{}]. Error: {}", templateModelProviderClassPath, e.getMessage());
            notification.setState(NotificationConstants.STATE_TEMPLATE_ERROR);
            return notification;
        }

        TemplateModelProvider<?> templateModelProvider = getTemplateModelProvider(templateModelProviderClass);
        if (templateModelProvider == null)
        {
            log.warn("Template model provider not found for provider class [{}] configured for notification with template [{}]",
                    templateModelProviderClass.getName(), notification.getTemplateModelName());
            notification.setState(NotificationConstants.STATE_TEMPLATE_ERROR);
            return notification;
        }

        try
        {
            notification = notificationSenderFactory.getNotificationSender()
                    .send(notification, templateModelProvider.getModel(notification));
        }
        catch (Exception e)
        {
            log.error("Failed to send the notification [{}]. Error: {}", notification.getSubject(), e.getMessage());
            notification.setState(NotificationConstants.STATE_NOT_SENT);
        }
        return notification;
    }


    private String getTemplateModelProviderClassName(String templateFileName)
    {
        String templateModelProvider = getTemplateConfigurationManager().getTemplateConfigurations().stream()
                .filter(t -> t.getTemplateFilename().equals(templateFileName) && t.getTemplateType().equals("emailTemplate"))
                .findFirst().get().getTemplateModelProvider();

        return templateModelProvider;
    }

    private TemplateModelProvider getTemplateModelProvider(Class templateModelProviderClass)
    {
        Map<String, TemplateModelProvider> templateModelproviders = getSpringContextHolder().getAllBeansOfType(templateModelProviderClass);
        if (templateModelproviders.size() > 1)
        {
            for (TemplateModelProvider provider : templateModelproviders.values())
            {
                if (provider.getClass().equals(templateModelProviderClass))
                {
                    return provider;
                }
            }
        }
        return templateModelproviders.values().iterator().hasNext() ? templateModelproviders.values().iterator().next() : null;
    }

    public SpringContextHolder getSpringContextHolder()
    {
        return springContextHolder;
    }

    public void setSpringContextHolder(SpringContextHolder springContextHolder)
    {
        this.springContextHolder = springContextHolder;
    }

    public TemplateConfigurationManager getTemplateConfigurationManager()
    {
        return templateConfigurationManager;
    }

    public void setTemplateConfigurationManager(TemplateConfigurationManager templateConfigurationManager)
    {
        this.templateConfigurationManager = templateConfigurationManager;
    }
}
