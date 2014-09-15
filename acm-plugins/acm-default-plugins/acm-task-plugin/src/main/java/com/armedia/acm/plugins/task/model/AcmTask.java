package com.armedia.acm.plugins.task.model;

import com.armedia.acm.core.AcmObject;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;

public class AcmTask implements AcmObject
{
    private Long taskId;
    private String priority;
    private String title;
    private Date dueDate;
    private String attachedToObjectType;
    private Long attachedToObjectId;
    private String assignee;
    private String owner;					//creator
    private String businessProcessName;
    private boolean adhocTask;
    private boolean completed;
    private String status;
    private Integer percentComplete;
    private String details;
    private Date createDate;
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

    public String getPriority()
    {
        return priority;
    }

    public void setPriority(String priority)
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

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public Integer getPercentComplete()
    {
        return percentComplete;
    }

    public void setPercentComplete(Integer percentComplete)
    {
        this.percentComplete = percentComplete;
    }

    public String getDetails()
    {
        return details;
    }

    public void setDetails(String details)
    {
        this.details = details;
    }

    @Override
    @JsonIgnore
    public String getObjectType()
    {
        return "Task";
    }

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
}
