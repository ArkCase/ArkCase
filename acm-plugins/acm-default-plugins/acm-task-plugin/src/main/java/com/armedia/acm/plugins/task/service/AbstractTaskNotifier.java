package com.armedia.acm.plugins.task.service;

/*-
 * #%L
 * ACM Default Plugin: Tasks
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

import com.armedia.acm.plugins.task.model.AcmTask;
import com.armedia.acm.plugins.task.model.TaskNotificationConfig;
import com.armedia.acm.services.notification.dao.NotificationDao;
import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;

import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Oct 12, 2016
 *
 */
public abstract class AbstractTaskNotifier
{

    private transient final Logger log = LogManager.getLogger(getClass());

    private TaskService activitiTaskService;

    private TaskDao activitiTaskDao;

    private NotificationDao notificationDao;

    private UserDao userDao;

    private TaskNotificationConfig taskNotificationConfig;

    /**
     * @param activitiTaskService
     *            the activitiTaskService to set
     */
    public void setActivitiTaskService(TaskService activitiTaskService)
    {
        this.activitiTaskService = activitiTaskService;
    }

    /**
     * @param activitiTaskDao
     *            the activitiTaskDao to set
     */
    public void setActivitiTaskDao(TaskDao activitiTaskDao)
    {
        this.activitiTaskDao = activitiTaskDao;
    }

    public void notifyTaskAssignees()
    {
        if (getTaskNotificationConfig().getDueTasksNotificationEnabled())
        {
            Date now = new Date();

            List<AcmTask> tasks = queryTasks().collect(Collectors.toList());
            for (AcmTask task : tasks)
            {
                AcmUser user = userDao.findByUserId(task.getAssignee());
                Notification notification = new Notification();
                notification.setParentType(task.getObjectType());
                notification.setParentId(task.getTaskId());
                notification.setEmailAddresses(user.getMail());
                notification.setAttachFiles(false);
                notification.setTitle("Task overdue notification");

                if (task.getDueDate().compareTo(now) > 0)
                {
                    notification.setTemplateModelName("taskUpcoming");
                }
                else
                {
                    notification.setTemplateModelName("taskOverdue");
                }
                notificationDao.save(notification);
            }

        }

    }

    /**
     * @return
     */
    public Stream<AcmTask> queryTasks()
    {
        Stream<Task> taskList = tasksDueBetween(
                activitiTaskService.createTaskQuery().includeProcessVariables().includeTaskLocalVariables().active()).list().stream();

        Stream<AcmTask> tasks = taskList.map(activitiTaskDao::acmTaskFromActivitiTask).filter(task -> task instanceof AcmTask)
                .map(AcmTask.class::cast);

        return tasks;
    }

    protected abstract TaskQuery tasksDueBetween(TaskQuery query);

    public NotificationDao getNotificationDao()
    {
        return notificationDao;
    }

    public void setNotificationDao(NotificationDao notificationDao)
    {
        this.notificationDao = notificationDao;
    }

    public UserDao getUserDao()
    {
        return userDao;
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    public TaskNotificationConfig getTaskNotificationConfig()
    {
        return taskNotificationConfig;
    }

    public void setTaskNotificationConfig(TaskNotificationConfig taskNotificationConfig)
    {
        this.taskNotificationConfig = taskNotificationConfig;
    }
}
