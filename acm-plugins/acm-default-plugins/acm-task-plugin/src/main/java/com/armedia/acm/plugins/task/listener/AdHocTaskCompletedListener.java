package com.armedia.acm.plugins.task.listener;

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
