package com.armedia.acm.data;

import java.util.Date;

public class BuckslipFutureTask
{
    private String approverId;
    private String taskName;
    private String groupName;
    private String details;
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

    public String getTaskName()
    {
        return taskName;
    }

    public void setTaskName(String taskName)
    {
        this.taskName = taskName;
    }

    public String getGroupName()
    {
        return groupName;
    }

    public void setGroupName(String groupName)
    {
        this.groupName = groupName;
    }

    public String getDetails()
    {
        return details;
    }

    public void setDetails(String details)
    {
        this.details = details;
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
