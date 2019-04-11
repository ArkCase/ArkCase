package com.armedia.acm.services.notification.service.provider;

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

import com.armedia.acm.services.notification.event.DueDateReminderSentEvent;
import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.notification.service.provider.model.GenericTemplateModel;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

public class DueDateReminderModelProvider implements TemplateModelProvider, ApplicationEventPublisherAware
{
    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public Object getModel(Notification notification)
    {
        GenericTemplateModel genericTemplateModel = new GenericTemplateModel();

        genericTemplateModel.setObjectNumber(notification.getParentName());
        genericTemplateModel.setObjectTitle(notification.getParentTitle());
        genericTemplateModel.setOtherObjectValue(notification.getNote());

        generateDueDateReminderSentEvent(notification);

        return genericTemplateModel;
    }

    private void generateDueDateReminderSentEvent(Notification notification)
    {
        DueDateReminderSentEvent dueDateReminderSentEvent =
                new DueDateReminderSentEvent(notification, notification.getParentType(), notification.getParentId(), Long.valueOf(notification.getNote()));
        applicationEventPublisher.publishEvent(dueDateReminderSentEvent);
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }
}
