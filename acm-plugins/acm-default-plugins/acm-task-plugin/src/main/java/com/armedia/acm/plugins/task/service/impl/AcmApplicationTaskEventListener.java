package com.armedia.acm.plugins.task.service.impl;

import com.armedia.acm.objectonverter.DateFormats;
import com.armedia.acm.plugins.task.model.TaskConstants;
import com.armedia.acm.service.objecthistory.dao.AcmAssignmentDao;
import com.armedia.acm.service.objecthistory.model.AcmAssignment;
import com.armedia.acm.service.objecthistory.service.AcmObjectHistoryEventPublisher;
import com.armedia.acm.services.participants.utils.ParticipantUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationListener;

import com.armedia.acm.objectonverter.AcmUnmarshaller;
import com.armedia.acm.objectonverter.ObjectConverter;
import com.armedia.acm.plugins.task.model.AcmApplicationTaskEvent;
import com.armedia.acm.plugins.task.model.AcmTask;
import com.armedia.acm.plugins.task.service.TaskEventPublisher;
import com.armedia.acm.service.objecthistory.model.AcmObjectHistory;
import com.armedia.acm.service.objecthistory.model.AcmObjectHistoryEvent;
import com.armedia.acm.service.objecthistory.service.AcmObjectHistoryService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class AcmApplicationTaskEventListener implements ApplicationListener<AcmObjectHistoryEvent>
{
    private AcmObjectHistoryService acmObjectHistoryService;
    private AcmObjectHistoryEventPublisher acmObjectHistoryEventPublisher;
    private TaskEventPublisher taskEventPublisher;
    private AcmAssignmentDao acmAssignmentDao;

    @Override public void onApplicationEvent(AcmObjectHistoryEvent event)
    {

        if (event != null && event.getSource() != null)
        {
            AcmObjectHistory acmObjectHistory = (AcmObjectHistory) event.getSource();
            boolean isTask = checkExecution(acmObjectHistory.getObjectType());

            if (isTask)
            {
                // Converter for JSON string to Object
                AcmUnmarshaller converter = ObjectConverter.createJSONUnmarshaller();

                String jsonUpdatedTask = acmObjectHistory.getObjectString();
                AcmTask updatedTask = (AcmTask) converter.unmarshall(jsonUpdatedTask, AcmTask.class);

                AcmAssignment acmAssignment = createAcmAssignment(updatedTask);

                AcmObjectHistory acmObjectHistoryExisting = getAcmObjectHistoryService().getAcmObjectHistory(updatedTask.getId(), TaskConstants.OBJECT_TYPE);

                if (acmObjectHistoryExisting != null)
                {
                    String json = acmObjectHistoryExisting.getObjectString();
                    AcmTask existing = (AcmTask) converter.unmarshall(json, AcmTask.class);

                    acmAssignment.setOldAssignee(ParticipantUtils.getAssigneeIdFromParticipants(existing.getParticipants()));

                    if (detailsChanged(existing, updatedTask))
                    {
                        AcmApplicationTaskEvent taskEvent = new AcmApplicationTaskEvent(updatedTask, "details.changed", event.getUserId(), true, event.getIpAddress());
                        getTaskEventPublisher().publishTaskEvent(taskEvent);
                    }

                    if (priorityChanged(existing, updatedTask))
                    {
                        AcmApplicationTaskEvent taskEvent = new AcmApplicationTaskEvent(updatedTask, "priority.changed", event.getUserId(), true, event.getIpAddress());
                        getTaskEventPublisher().publishTaskEvent(taskEvent);
                    }
                }

                if (isAssigneeChanged(acmAssignment))
                {
                    // Save assignment change in the database
                    getAcmAssignmentDao().save(acmAssignment);

                    // Raise an event
                    getAcmObjectHistoryEventPublisher().publishAssigneeChangeEvent(acmAssignment, event.getUserId(), event.getIpAddress());
                }
            }
        }

    }

    public boolean isAssigneeChanged(AcmAssignment assignment)
    {
        if (assignment.getNewAssignee() != null && assignment.getOldAssignee() == null)
        {
            return true;
        }

        if (assignment.getNewAssignee() != null && assignment.getOldAssignee() != null)
        {
            if (!assignment.getNewAssignee().equals(assignment.getOldAssignee()))
            {
                return true;
            }
        }
        return false;
    }

    private AcmAssignment createAcmAssignment(AcmTask updatedTask)
    {
        AcmAssignment assignment = new AcmAssignment();
        assignment.setObjectId(updatedTask.getTaskId());
        assignment.setObjectTitle(updatedTask.getTitle());
        assignment.setObjectName(getAssignmentObjectName(updatedTask));
        assignment.setNewAssignee(updatedTask.getAssignee());
        assignment.setObjectType(TaskConstants.OBJECT_TYPE);

        return assignment;
    }

    private String getAssignmentObjectName(AcmTask updatedTask)
    {
        SimpleDateFormat formatter = new SimpleDateFormat(DateFormats.TASK_NAME_DATE_FORMAT);

        String dueDate = "";

        if (updatedTask.getDueDate() != null)
        {
            dueDate = formatter.format(updatedTask.getDueDate());
        }

        String id = updatedTask.getId().toString();

        return String.format("%s_%s", dueDate, id);

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

    public AcmObjectHistoryEventPublisher getAcmObjectHistoryEventPublisher()
    {
        return acmObjectHistoryEventPublisher;
    }

    public void setAcmObjectHistoryEventPublisher(AcmObjectHistoryEventPublisher acmObjectHistoryEventPublisher)
    {
        this.acmObjectHistoryEventPublisher = acmObjectHistoryEventPublisher;
    }

    public AcmAssignmentDao getAcmAssignmentDao()
    {
        return acmAssignmentDao;
    }

    public void setAcmAssignmentDao(AcmAssignmentDao acmAssignmentDao)
    {
        this.acmAssignmentDao = acmAssignmentDao;
    }
}
