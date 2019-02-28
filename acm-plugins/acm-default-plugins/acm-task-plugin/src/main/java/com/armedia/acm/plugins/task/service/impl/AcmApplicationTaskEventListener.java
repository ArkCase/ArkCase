package com.armedia.acm.plugins.task.service.impl;

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

import com.armedia.acm.objectonverter.AcmUnmarshaller;
import com.armedia.acm.objectonverter.DateFormats;
import com.armedia.acm.objectonverter.ObjectConverter;
import com.armedia.acm.plugins.task.model.AcmApplicationTaskEvent;
import com.armedia.acm.plugins.task.model.AcmTask;
import com.armedia.acm.plugins.task.model.TaskConstants;
import com.armedia.acm.plugins.task.service.TaskEventPublisher;
import com.armedia.acm.service.objecthistory.dao.AcmAssignmentDao;
import com.armedia.acm.service.objecthistory.model.AcmAssignment;
import com.armedia.acm.service.objecthistory.model.AcmObjectHistory;
import com.armedia.acm.service.objecthistory.model.AcmObjectHistoryEvent;
import com.armedia.acm.service.objecthistory.service.AcmObjectHistoryEventPublisher;
import com.armedia.acm.service.objecthistory.service.AcmObjectHistoryService;

import org.springframework.context.ApplicationListener;

import java.text.SimpleDateFormat;

public class AcmApplicationTaskEventListener implements ApplicationListener<AcmObjectHistoryEvent>
{
    private AcmObjectHistoryService acmObjectHistoryService;
    private AcmObjectHistoryEventPublisher acmObjectHistoryEventPublisher;
    private TaskEventPublisher taskEventPublisher;
    private AcmAssignmentDao acmAssignmentDao;
    private ObjectConverter objectConverter;

    @Override
    public void onApplicationEvent(AcmObjectHistoryEvent event)
    {

        if (event != null && event.getSource() != null)
        {
            AcmObjectHistory acmObjectHistory = (AcmObjectHistory) event.getSource();
            boolean isTask = checkExecution(acmObjectHistory.getObjectType());

            if (isTask)
            {
                // Converter for JSON string to Object
                AcmUnmarshaller converter = getObjectConverter().getJsonUnmarshaller();

                String jsonUpdatedTask = acmObjectHistory.getObjectString();
                AcmTask updatedTask = converter.unmarshall(jsonUpdatedTask, AcmTask.class);

                AcmAssignment acmAssignment = createAcmAssignment(updatedTask);

                AcmObjectHistory acmObjectHistoryExisting = getAcmObjectHistoryService().getAcmObjectHistory(updatedTask.getId(),
                        TaskConstants.OBJECT_TYPE);

                if (acmObjectHistoryExisting != null)
                {
                    String json = acmObjectHistoryExisting.getObjectString();
                    AcmTask existing = converter.unmarshall(json, AcmTask.class);

                    acmAssignment.setOldAssignee(existing.getAssignee());

                    if (isDetailsChanged(existing, updatedTask))
                    {
                        AcmApplicationTaskEvent taskEvent = new AcmApplicationTaskEvent(updatedTask, "details.changed", event.getUserId(),
                                true, event.getIpAddress());
                        getTaskEventPublisher().publishTaskEvent(taskEvent);
                    }

                    if (isPriorityChanged(existing, updatedTask))
                    {
                        AcmApplicationTaskEvent taskEvent = new AcmApplicationTaskEvent(updatedTask, "priority.changed", event.getUserId(),
                                true, event.getIpAddress());
                        getTaskEventPublisher().publishTaskEvent(taskEvent);
                    }

                    if (isStatusChanged(existing, updatedTask))
                    {
                        AcmApplicationTaskEvent taskEvent = new AcmApplicationTaskEvent(updatedTask, "status.changed", event.getUserId(),
                                true, event.getIpAddress());
                        getTaskEventPublisher().publishTaskEvent(taskEvent, "from " + existing.getStatus() + " to " + updatedTask.getStatus());
                    }

                    if (isReworkDetailsChanged(existing, updatedTask))
                    {
                        AcmApplicationTaskEvent taskEvent = new AcmApplicationTaskEvent(updatedTask, "reworkdetails.changed",
                                event.getUserId(), true, event.getIpAddress());
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

        if (assignment.getNewAssignee() != null && assignment.getOldAssignee() != null)
        {
            if (assignment.getNewAssignee().equals(assignment.getOldAssignee()))
            {
                return false;
            }
        }

        if (assignment.getNewAssignee() == null && assignment.getOldAssignee() == null)
        {
            return false;
        }

        return true;
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

    public boolean isDetailsChanged(AcmTask existing, AcmTask updatedTask)
    {
        String updatedDetails = updatedTask.getDetails();
        String details = existing.getDetails();
        if (updatedDetails != null && details != null)
        {
            return !details.equals(updatedDetails);
        }
        else if (updatedDetails != null)
        {
            return true;
        }
        return false;
    }

    public boolean isReworkDetailsChanged(AcmTask existing, AcmTask updatedTask)
    {
        String updatedReworkDetails = updatedTask.getReworkInstructions();
        String reworkDetails = existing.getReworkInstructions();
        if (updatedReworkDetails != null && reworkDetails != null)
        {
            return !reworkDetails.equals(updatedReworkDetails);
        }
        else if (updatedReworkDetails != null)
        {
            return true;
        }
        return false;
    }

    private boolean isPriorityChanged(AcmTask existing, AcmTask updatedTask)
    {
        String updatedPriority = updatedTask.getPriority();
        String priority = existing.getPriority();
        if (updatedPriority != null && priority != null)
        {
            return !updatedPriority.equals(priority);
        }
        return false;
    }

    private boolean isStatusChanged(AcmTask existing, AcmTask updatedTask)
    {
        String updatedStatus = updatedTask.getStatus();
        String status = existing.getStatus();
        if (updatedStatus != null && status != null)
        {
            updatedStatus = updatedStatus.toUpperCase();
            status = status.toUpperCase();
            return !updatedStatus.equals(status);
        }
        return false;
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

    public ObjectConverter getObjectConverter()
    {
        return objectConverter;
    }

    public void setObjectConverter(ObjectConverter objectConverter)
    {
        this.objectConverter = objectConverter;
    }
}
