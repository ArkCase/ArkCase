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
import com.armedia.acm.service.objecthistory.dao.AcmAssignmentDao;
import com.armedia.acm.service.objecthistory.model.AcmAssignment;
import com.armedia.acm.services.notification.model.NotificationConstants;
import com.armedia.acm.services.notification.service.NotificationService;
import com.armedia.acm.services.notification.service.NotificationUtils;
import com.armedia.acm.services.users.dao.UserDao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationListener;

import java.util.List;
import java.util.Optional;

public class TaskUpdatedNotifier implements ApplicationListener<AcmApplicationTaskEvent>
{
    private TaskConfig taskConfig;
    private UserDao userDao;
    private AcmAssignmentDao assignmentDao;
    private NotificationService notificationService;
    private NotificationUtils notificationUtils;

    private static final Logger logger = LogManager.getLogger(TaskUpdatedNotifier.class);

    @Override
    public void onApplicationEvent(AcmApplicationTaskEvent event)
    {
        String eventType = event.getEventType();
        String eventDescription = event.getEventDescription();
        AcmTask acmTask = event.getAcmTask();

        if (eventType.equals("com.armedia.acm.app.task.status.changed"))
        {
            logger.debug("On 'Task status changed' event create notification for participants.");

            String emailAddresses = notificationUtils.getEmailsCommaSeparatedForParticipants(acmTask.getParticipants());
            notificationService.createNotification("taskStatusChanged", NotificationConstants.TASK_STATUS_CHANGED,
                    acmTask.getObjectType(), acmTask.getId(), String.format("%s-%s", acmTask.getObjectType(), acmTask.getId()),
                    acmTask.getTitle(), emailAddresses, event.getUserId(), null);

            logger.debug("Notification 'Task status changed' created for task [{}] for participants with addresses [{}].",
                    acmTask.getId(), emailAddresses);
        }
        else if (eventType.equals("com.armedia.acm.app.task.priority.changed"))
        {
            logger.debug("On 'Task priority changed' event create notification for participants.");

            String emailAddresses = notificationUtils.getEmailsCommaSeparatedForParticipants(acmTask.getParticipants());
            notificationService.createNotification("taskPriorityChanged", NotificationConstants.TASK_PRIORITY_CHANGED,
                    acmTask.getObjectType(), acmTask.getId(), String.format("%s-%s", acmTask.getObjectType(), acmTask.getId()),
                    acmTask.getTitle(), emailAddresses, event.getUserId(), null);

            logger.debug("Notification 'Task priority changed' created for task [{}] for participants with addresses [{}].",
                    acmTask.getId(), emailAddresses);
        }
        else if ((eventDescription != null && eventDescription.equals("CONCUR")) ||
                (eventDescription != null && eventDescription.equals("NON_CONCUR")))
        {
            List<AcmAssignment> taskAssignments = assignmentDao.findByObjectTypeAndObjectId(event.getObjectType(), event.getObjectId());

            Optional<AcmAssignment> assignment = taskAssignments.stream()
                    .min((o1, o2) -> o2.getCreated().compareTo(o1.getCreated()));

            String note = assignment.map(it -> {
                if (eventDescription.equals("CONCUR"))
                {
                    return NotificationConstants.TASK_CONCUR_NOTE;
                }
                else
                    return NotificationConstants.TASK_NON_CONCUR_NOTE;
            }).orElse(null);

            List<AcmAssignment> parentAssignments = assignmentDao.findByObjectTypeAndObjectId(event.getParentObjectType(),
                    event.getParentObjectId());

            Optional<AcmAssignment> parentAssignment = parentAssignments.stream()
                    .filter(it -> {
                        String newAssignee = it.getNewAssignee();
                        return (newAssignee != null && !newAssignee.equals("")
                                && !newAssignee.equals("None"));
                    })
                    .min((o1, o2) -> o2.getCreated().compareTo(o1.getCreated()));

            if (parentAssignment.isPresent())
            {
                String emailAddress = notificationUtils.getEmailForUser(parentAssignment.get().getNewAssignee());
                notificationService.createNotification("concurNonConcur", NotificationConstants.TASK_CONCUR_NONCONCUR,
                        event.getObjectType(), event.getObjectId(), String.format("%s-%s", event.getObjectType(), event.getObjectId()),
                        null, emailAddress, event.getUserId(), assignment.map(AcmAssignment::getNewAssignee).orElse(""), note);
            }
        }
    }

    public AcmAssignmentDao getAssignmentDao()
    {
        return assignmentDao;
    }

    public void setAssignmentDao(AcmAssignmentDao assignmentDao)
    {
        this.assignmentDao = assignmentDao;
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

    public UserDao getUserDao()
    {
        return userDao;
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    public NotificationUtils getNotificationUtils()
    {
        return notificationUtils;
    }

    public void setNotificationUtils(NotificationUtils notificationUtils)
    {
        this.notificationUtils = notificationUtils;
    }
}
