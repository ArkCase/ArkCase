package com.armedia.acm.activiti;

import com.armedia.acm.event.AcmEvent;
import org.activiti.engine.task.Task;

import java.util.Date;

/**
 * Created by armdev on 6/24/14.
 */
public class AcmTaskEvent extends AcmEvent
{
    private String assignee;
    private String taskName;
    private Date taskCreated;
    private String description;
    private Date dueDate;
    private String taskEvent;

    public AcmTaskEvent(Task source, String taskEvent)
    {
        super(source);
        setSucceeded(true);
        setObjectType("TASK");
        setEventType("com.armedia.acm.activiti.task." + taskEvent);
        setObjectId(Long.valueOf(source.getId()));
        setEventDate(new Date());
        setUserId("ACTIVITI_SYSTEM");

        setAssignee(source.getAssignee());
        setTaskName(source.getName());
        setTaskCreated(source.getCreateTime());
        setDescription(source.getDescription());
        setDueDate(source.getDueDate());
        setTaskEvent(taskEvent);
    }

    public String getAssignee()
    {
        return assignee;
    }

    public void setAssignee(String assignee)
    {
        this.assignee = assignee;
    }

    public String getTaskName()
    {
        return taskName;
    }

    public void setTaskName(String taskName)
    {
        this.taskName = taskName;
    }

    public Date getTaskCreated()
    {
        return taskCreated;
    }

    public void setTaskCreated(Date taskCreated)
    {
        this.taskCreated = taskCreated;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public Date getDueDate()
    {
        return dueDate;
    }

    public void setDueDate(Date dueDate)
    {
        this.dueDate = dueDate;
    }

    public String getTaskEvent()
    {
        return taskEvent;
    }

    public void setTaskEvent(String taskEvent)
    {
        this.taskEvent = taskEvent;
    }
}
