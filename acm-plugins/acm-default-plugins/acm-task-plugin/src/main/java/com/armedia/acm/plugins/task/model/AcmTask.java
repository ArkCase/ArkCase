package com.armedia.acm.plugins.task.model;

import java.util.Date;

public class AcmTask
{
    private Long taskId;
    private int priority;
    private String title;
    private Date dueDate;
    private String attachedToObjectType;
    private Long attachedToObjectId;
    private String assignee;

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
}
