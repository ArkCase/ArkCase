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
import com.armedia.acm.spring.SpringContextHolder;

import java.util.Map;

/**
 * @author riste.tutureski
 *
 */
public class SendExecutor implements Executor
{

    private SpringContextHolder springContextHolder;

    private TemplateModelProvider<Notification> templateModelProvider;

    @Override
    public Notification execute(Notification notification)
    {

        // Get all registered senders
        Map<String, NotificationSenderFactory> senderFactoryList = getSpringContextHolder()
                .getAllBeansOfType(NotificationSenderFactory.class);

        if (senderFactoryList != null)
        {
            for (NotificationSenderFactory senderFactory : senderFactoryList.values())
            {
                // Send notification
                notification = senderFactory.getNotificationSender().send(notification, templateModelProvider.getModel(notification));
            }
        }

        return notification;
    }

    public SpringContextHolder getSpringContextHolder()
    {
        return springContextHolder;
    }

    public void setSpringContextHolder(SpringContextHolder springContextHolder)
    {
        this.springContextHolder = springContextHolder;
    }

    public TemplateModelProvider getTemplateModelProvider() {
        return templateModelProvider;
    }

    public void setTemplateModelProvider(TemplateModelProvider templateModelProvider) {
        this.templateModelProvider = templateModelProvider;
    }
}
