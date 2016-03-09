package com.armedia.acm.plugins.task.service.impl;

import com.armedia.acm.plugins.task.model.TaskConstants;
import org.springframework.context.ApplicationListener;

import com.armedia.acm.objectonverter.AcmUnmarshaller;
import com.armedia.acm.objectonverter.ObjectConverter;
import com.armedia.acm.plugins.task.model.AcmApplicationTaskEvent;
import com.armedia.acm.plugins.task.model.AcmTask;
import com.armedia.acm.plugins.task.service.TaskEventPublisher;
import com.armedia.acm.service.objecthistory.model.AcmObjectHistory;
import com.armedia.acm.service.objecthistory.model.AcmObjectHistoryEvent;
import com.armedia.acm.service.objecthistory.service.AcmObjectHistoryService;

public class AcmApplicationTaskEventListener implements ApplicationListener<AcmObjectHistoryEvent>
{
    private AcmObjectHistoryService acmObjectHistoryService;
    private TaskEventPublisher taskEventPublisher;

    @Override
    public void onApplicationEvent(AcmObjectHistoryEvent event)
    {
       
        if (event != null && event.getSource() != null)
        {
            AcmObjectHistory acmObjectHistory = (AcmObjectHistory) event.getSource();
            boolean execute = checkExecution(acmObjectHistory.getObjectType());

            if (execute)
            {
                // Converter for JSON string to Object
                AcmUnmarshaller converter = ObjectConverter.createJSONUnmarshaller();
               
                String jsonUpdatedTask = acmObjectHistory.getObjectString();
                AcmTask updatedTask = (AcmTask) converter.unmarshall(jsonUpdatedTask, AcmTask.class);
                
                AcmObjectHistory acmObjectHistoryExisting = getAcmObjectHistoryService().getAcmObjectHistory(updatedTask.getId(), TaskConstants.OBJECT_TYPE);

                String json = acmObjectHistoryExisting.getObjectString();
                AcmTask existing = (AcmTask) converter.unmarshall(json, AcmTask.class);

                if (detailsChanged(existing, updatedTask))
                {
                    AcmApplicationTaskEvent taskEvent = new AcmApplicationTaskEvent(updatedTask, "details.changed", event.getUserId(), true,
                            event.getIpAddress());
                    getTaskEventPublisher().publishTaskEvent(taskEvent);
                }

                if (priorityChanged(existing, updatedTask))
                {
                    AcmApplicationTaskEvent taskEvent = new AcmApplicationTaskEvent(updatedTask, "priority.changed", event.getUserId(),
                            true, event.getIpAddress());
                    getTaskEventPublisher().publishTaskEvent(taskEvent);
                }
            }
        }

    }

    public boolean detailsChanged(AcmTask existing, AcmTask updatedTask)
    {
        String updatedDetails = updatedTask.getDetails();
        String details = existing.getDetails();
        if (updatedDetails != null && details != null)
        {
            return !details.equals(updatedDetails);
        } else if (updatedDetails != null)
        {
            return true;
        }
        return false;
    }

    private boolean priorityChanged(AcmTask existing, AcmTask updatedTask)
    {
        String updatedPriority = updatedTask.getPriority();
        String priority = existing.getPriority();
        return !updatedPriority.equals(priority);
    }

    private boolean checkExecution(String objectType)
    {
        return objectType.equals(TaskConstants.OBJECT_TYPE);
    }

    public AcmObjectHistoryService getAcmObjectHistoryService()
    {
        return acmObjectHistoryService;
    }

    public void setAcmObjectHistoryService(AcmObjectHistoryService acmObjectHistoryService)
    {
        this.acmObjectHistoryService = acmObjectHistoryService;
    }

    public TaskEventPublisher getTaskEventPublisher()
    {
        return taskEventPublisher;
    }

    public void setTaskEventPublisher(TaskEventPublisher taskEventPublisher)
    {
        this.taskEventPublisher = taskEventPublisher;
    }

}
