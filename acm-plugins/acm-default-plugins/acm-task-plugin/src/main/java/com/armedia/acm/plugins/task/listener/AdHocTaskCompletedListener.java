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
import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.notification.model.NotificationConstants;
import com.armedia.acm.services.notification.service.NotificationService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationListener;

public class AdHocTaskCompletedListener implements ApplicationListener<AcmApplicationTaskEvent>
{
    private NotificationService notificationService;
    private TaskConfig taskConfig;

    private static final Logger logger = LogManager.getLogger(TaskUpdatedNotifier.class);

    @Override
    public void onApplicationEvent(AcmApplicationTaskEvent event)
    {
        if (event.getTaskEvent().equals("complete"))
        {
            AcmTask acmTask = event.getAcmTask();
            String taskOwner = acmTask.getOwner();
            if (getTaskConfig().getSendCompleteEmail() && event.getAcmTask().getStatus().equals("CLOSED")
                    && (taskOwner != null && !taskOwner.isEmpty()))
            {
                logger.debug("On 'Task completed event' create notification for creator [{}].", taskOwner);

                Notification notification = notificationService.getNotificationBuilder()
                        .newNotification("taskCompletedNotifyCreator", NotificationConstants.NOTIFICATION_TASK_COMPLETED,
                                acmTask.getObjectType(), acmTask.getId(), taskOwner)
                        .withEmailAddressForUser(taskOwner)
                        .forObjectWithNumber(String.format("%s-%s", acmTask.getObjectType(), acmTask.getId()))
                        .forObjectWithTitle(acmTask.getTitle())
                        .build(taskOwner);

                notificationService.saveNotification(notification);
            }
        }
    }

    public NotificationService getNotificationService()
    {
        return notificationService;
    }

    public void setNotificationService(NotificationService notificationService)
    {
        this.notificationService = notificationService;
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
