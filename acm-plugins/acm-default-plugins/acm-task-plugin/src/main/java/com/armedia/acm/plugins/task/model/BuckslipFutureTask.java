package com.armedia.acm.plugins.task.model;

import java.util.Date;

public class BuckslipFutureTask
{
    private String approverId;
    private String groupName;
    private String taskName;
    private String details;
    private Date dueDate;
    private String addedBy;
    private int maxTaskDurationInDays = 3;

    public String getApproverId()
    {
        return approverId;
    }

    public void setApproverId(String approverId)
    {
        this.approverId = approverId;
    }

    public String getGroupName()
    {
        return groupName;
    }

    public void setGroupName(String groupName)
    {
        this.groupName = groupName;
    }

    public String getTaskName()
    {
        return taskName;
    }

    public void setTaskName(String taskName)
    {
        this.taskName = taskName;
    }

    public String getDetails()
    {
        return details;
    }

    public void setDetails(String details)
    {
        this.details = details;
    }

    public Date getDueDate()
    {
        return dueDate;
    }

    public void setDueDate(Date dueDate)
    {
        this.dueDate = dueDate;
    }

    public String getAddedBy()
    {
        return addedBy;
    }

    public void setAddedBy(String addedBy)
    {
        this.addedBy = addedBy;
    }

    public int getMaxTaskDurationInDays()
    {
        return maxTaskDurationInDays;
    }

    public void setMaxTaskDurationInDays(int maxTaskDurationInDays)
    {
        // must be non-negative
        if (maxTaskDurationInDays >= 0)
        {
            this.maxTaskDurationInDays = maxTaskDurationInDays;
        }
    }
}
