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

import com.armedia.acm.files.AbstractConfigurationFileEvent;
import com.armedia.acm.files.ConfigurationFileAddedEvent;
import com.armedia.acm.files.ConfigurationFileChangedEvent;
import com.armedia.acm.plugins.task.model.AcmTask;
import com.armedia.acm.services.notification.dao.NotificationDao;
import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.notification.service.NotificationSenderFactory;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;

import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Oct 12, 2016
 *
 */
public abstract class AbstractTaskNotifier
        implements ApplicationListener<AbstractConfigurationFileEvent>
{

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    private TaskService activitiTaskService;

    private TaskDao activitiTaskDao;

    private NotificationSenderFactory senderFactory;

    private boolean notificationsEnabled;

    private NotificationDao notificationDao;

    private UserDao userDao;

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

    /**
     * @param senderFactory
     *            the senderFactory to set
     */
    public void setSenderFactory(NotificationSenderFactory senderFactory)
    {
        this.senderFactory = senderFactory;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.springframework.context.ApplicationListener#onApplicationEvent(org.springframework.context.ApplicationEvent)
     */
    @Override
    public void onApplicationEvent(AbstractConfigurationFileEvent event)
    {
        if (isPropertyFileChange(event))
        {
            File configFile = event.getConfigFile();
            try (FileInputStream fis = new FileInputStream(configFile))
            {
                log.debug("Loading configaration for {} from {} file.", getClass().getName(), configFile.getName());

                Properties dueTasksNotifierProperties = new Properties();
                dueTasksNotifierProperties.load(fis);

                notificationsEnabled = Boolean.parseBoolean(dueTasksNotifierProperties.getProperty("due.tasks.notification.enabled"));

            }
            catch (IOException e)
            {
                log.error("Could not load configuration for {} from {} file.", getClass().getName(), configFile.getName(), e);
            }
        }
    }

    private boolean isPropertyFileChange(AbstractConfigurationFileEvent abstractConfigurationFileEvent)
    {
        return (abstractConfigurationFileEvent instanceof ConfigurationFileAddedEvent
                || abstractConfigurationFileEvent instanceof ConfigurationFileChangedEvent)
                && abstractConfigurationFileEvent.getConfigFile().getName().equals("dueTasksNotifier.properties");
    }

    public void notifyTaskAssignees()
    {
        if (notificationsEnabled)
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
}
