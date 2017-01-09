package com.armedia.acm.plugins.task.service;

import com.armedia.acm.core.exceptions.AcmEncryptionException;
import com.armedia.acm.files.AbstractConfigurationFileEvent;
import com.armedia.acm.files.ConfigurationFileAddedEvent;
import com.armedia.acm.files.ConfigurationFileChangedEvent;
import com.armedia.acm.plugins.task.model.AcmTask;
import com.armedia.acm.services.notification.service.EmailBodyBuilder;
import com.armedia.acm.services.notification.service.EmailBuilder;
import com.armedia.acm.services.notification.service.SmtpNotificationSender;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Stream;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Oct 12, 2016
 *
 */
public abstract class AbstractTaskNotifier
        implements EmailBuilder<AcmTask>, EmailBodyBuilder<AcmTask>, ApplicationListener<AbstractConfigurationFileEvent>
{

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    private TaskService activitiTaskService;

    private TaskDao activitiTaskDao;

    private SmtpNotificationSender smtpNotificationSender;

    private boolean notificationsEnabled;

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
     * @param smtpNotificationSender
     *            the smtpNotificationSender to set
     */
    public void setSmtpNotificationSender(SmtpNotificationSender smtpNotificationSender)
    {
        this.smtpNotificationSender = smtpNotificationSender;
    }

    /*
     * (non-Javadoc)
     *
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

            } catch (IOException e)
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
            Stream<AcmTask> tasks = queryTasks();
            sendEmails(tasks);
        }

    }

    /**
     * @return
     */
    public Stream<AcmTask> queryTasks()
    {
        Stream<Task> taskList = tasksDueBetween(
                activitiTaskService.createTaskQuery().includeProcessVariables().includeTaskLocalVariables().active()).list().stream();

        Stream<AcmTask> tasks = taskList.map(activitiTaskDao::acmTaskFromActivitiTask)
                .filter(task -> task instanceof AcmTask).map(AcmTask.class::cast);

        return tasks;
    }

    protected abstract TaskQuery tasksDueBetween(TaskQuery query);

    private void sendEmails(Stream<AcmTask> tasks)
    {
        try
        {
            smtpNotificationSender.sendPlainEmail(tasks, this::buildEmail, this::buildEmailBody);
        } catch (AcmEncryptionException e)
        {
            log.error("Error while trying to send task due notifications.", e);
        }
    }

    @Override
    public abstract void buildEmail(AcmTask emailData, Map<String, Object> messageProps);

    @Override
    public abstract String buildEmailBody(AcmTask emailData);
}
