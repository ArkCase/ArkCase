package com.armedia.acm.plugins.task.listener;

/*-
 * #%L
 * ACM Default Plugin: Tasks
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

import com.armedia.acm.plugins.task.model.AcmApplicationTaskEvent;
import com.armedia.acm.plugins.task.model.AcmTask;
import com.armedia.acm.plugins.task.model.TaskConfig;
import com.armedia.acm.services.notification.dao.NotificationDao;
import com.armedia.acm.services.notification.model.Notification;
import org.springframework.context.ApplicationListener;
import org.springframework.security.core.context.SecurityContextHolder;

public class AdHocTaskCompletedListener implements ApplicationListener<AcmApplicationTaskEvent>
{
    private NotificationDao notificationDao;
    private TaskConfig taskConfig;

    @Override
    public void onApplicationEvent(AcmApplicationTaskEvent event)
    {
        if (event.getTaskEvent().equals("complete") && event.isAdhocTask())
        {
            if (getTaskConfig().getSendCompleteEmail() && event.getAcmTask().getStatus().equals("CLOSED")
                    && (event.getAcmTask().getOwner() != null && !event.getAcmTask().getOwner().isEmpty()))
            {
                sendCompleteTaskEmail(event.getAcmTask());
            }
        }
    }

    public void sendCompleteTaskEmail(AcmTask acmTask)
    {
        Notification notification = new Notification();
        notification.setTemplateModelName("taskCompletedNotifyCreator");
        notification.setTitle("Task has been completed");
        notification.setUser(SecurityContextHolder.getContext().getAuthentication().getName());
        notification.setParentType(acmTask.getObjectType());
        notification.setParentId(acmTask.getTaskId());
        notification.setEmailAddresses(acmTask.getOwner());
        notificationDao.save(notification);
    }

    public NotificationDao getNotificationDao()
    {
        return notificationDao;
    }

    public void setNotificationDao(NotificationDao notificationDao)
    {
        this.notificationDao = notificationDao;
    }

    public TaskConfig getTaskConfig()
    {
        return taskConfig;
    }

    public void setTaskConfig(TaskConfig taskConfig)
    {
        this.taskConfig = taskConfig;
    }
}
