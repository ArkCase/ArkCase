package com.armedia.acm.plugins.task.model;

import com.armedia.acm.core.AcmObject;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;

public class AcmTask implements AcmObject
{
    private Long taskId;
    private int priority;
    private String title;
    private Date dueDate;
    private String attachedToObjectType;
    private Long attachedToObjectId;
    private String assignee;
    private String businessProcessName;
    private boolean adhocTask;
    private boolean completed;

    private Date taskStartDate;
    private Date taskFinishedDate;
    private Long taskDurationInMillis;

    public Long getTaskId()
    {
        return taskId;
    }

    public void setTaskId(Long taskId)
    {
        this.taskId = taskId;
    }

    public int getPriority()
    {
        return priority;
    }

    public void setPriority(int priority)
    {
        this.priority = priority;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public Date getDueDate()
    {
        return dueDate;
    }

    public void setDueDate(Date dueDate)
    {
        this.dueDate = dueDate;
    }

    public String getAttachedToObjectType()
    {
        return attachedToObjectType;
    }

    public void setAttachedToObjectType(String attachedToObjectType)
    {
        this.attachedToObjectType = attachedToObjectType;
    }

    public Long getAttachedToObjectId()
    {
        return attachedToObjectId;
    }

    public void setAttachedToObjectId(Long attachedToObjectId)
    {
        this.attachedToObjectId = attachedToObjectId;
    }

    public String getAssignee()
    {
        return assignee;
    }

    public void setAssignee(String assignee)
    {
        this.assignee = assignee;
    }

    public String getBusinessProcessName()
    {
        return businessProcessName;
    }

    public void setBusinessProcessName(String businessProcessName)
    {
        this.businessProcessName = businessProcessName;
    }

    public boolean isAdhocTask()
    {
        return adhocTask;
    }

    public void setAdhocTask(boolean adhocTask)
    {
        this.adhocTask = adhocTask;
    }

    public boolean isCompleted()
    {
        return completed;
    }

    public void setCompleted(boolean completed)
    {
        this.completed = completed;
    }

    public Date getTaskStartDate()
    {
        return taskStartDate;
    }

    public void setTaskStartDate(Date taskStartDate)
    {
        this.taskStartDate = taskStartDate;
    }

    public Date getTaskFinishedDate()
    {
        return taskFinishedDate;
    }

    public void setTaskFinishedDate(Date taskFinishedDate)
    {
        this.taskFinishedDate = taskFinishedDate;
    }

    public Long getTaskDurationInMillis()
    {
        return taskDurationInMillis;
    }

    public void setTaskDurationInMillis(Long taskDurationInMillis)
    {
        this.taskDurationInMillis = taskDurationInMillis;
    }

    @Override
    @JsonIgnore
    public String getObjectType()
    {
        return "Task";
    }
}
