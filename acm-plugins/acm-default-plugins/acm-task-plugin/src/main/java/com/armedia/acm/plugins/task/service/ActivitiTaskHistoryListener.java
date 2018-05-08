package com.armedia.acm.plugins.task.service;

import com.armedia.acm.activiti.AcmTaskActivitiEvent;
import com.armedia.acm.plugins.task.model.AcmTask;
import com.armedia.acm.service.objecthistory.service.AcmObjectHistoryService;

import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

/**
 * Save Task to acm_object_history when created from activiti
 */
public class ActivitiTaskHistoryListener implements ApplicationListener<AcmTaskActivitiEvent>
{
    private static final String OBJECT_TYPE = "TASK";
    private final Logger LOG = LoggerFactory.getLogger(getClass());
    private AcmObjectHistoryService acmObjectHistoryService;
    private TaskDao activitiTaskDao;

    @Override
    public void onApplicationEvent(AcmTaskActivitiEvent event)
    {
        LOG.debug("Task event raised. Start adding it to the object history ...");
        if (event != null)
        {
            boolean execute = isActivitiTask(event.getEventType());
            if (execute)
            {
                Task task = (Task) event.getSource();
                AcmTask acmTask = getActivitiTaskDao().acmTaskFromActivitiTask(task);
                getAcmObjectHistoryService().save(event.getUserId(), event.getEventType(),
                        acmTask, acmTask.getId(), OBJECT_TYPE, event.getEventDate(), event.getIpAddress());
                LOG.debug("Task History added to database.");
            }
        }
    }

    private boolean isActivitiTask(String eventType)
    {
        return "com.armedia.acm.activiti.task.create".equals(eventType);
    }

    public AcmObjectHistoryService getAcmObjectHistoryService()
    {
        return acmObjectHistoryService;
    }

    public void setAcmObjectHistoryService(AcmObjectHistoryService acmObjectHistoryService)
    {
        this.acmObjectHistoryService = acmObjectHistoryService;
    }

    public TaskDao getActivitiTaskDao()
    {
        return activitiTaskDao;
    }

    public void setActivitiTaskDao(TaskDao activitiTaskDao)
    {
        this.activitiTaskDao = activitiTaskDao;
    }
}
