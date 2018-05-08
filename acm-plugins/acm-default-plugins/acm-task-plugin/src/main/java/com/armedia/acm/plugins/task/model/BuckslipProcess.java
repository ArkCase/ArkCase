package com.armedia.acm.plugins.task.model;

import com.armedia.acm.data.BuckslipFutureTask;

import java.util.List;

public class BuckslipProcess
{
    private String businessProcessId;
    private String businessProcessName;
    private Boolean nonConcurEndsApprovals;
    private String pastTasks;
    private List<BuckslipFutureTask> futureTasks;
    private String objectType;
    private Long objectId;

    public String getBusinessProcessId()
    {
        return businessProcessId;
    }

    public void setBusinessProcessId(String businessProcessId)
    {
        this.businessProcessId = businessProcessId;
    }

    public String getBusinessProcessName()
    {
        return businessProcessName;
    }

    public void setBusinessProcessName(String businessProcessName)
    {
        this.businessProcessName = businessProcessName;
    }

    public Boolean getNonConcurEndsApprovals()
    {
        return nonConcurEndsApprovals;
    }

    public void setNonConcurEndsApprovals(Boolean nonConcurEndsApprovals)
    {
        this.nonConcurEndsApprovals = nonConcurEndsApprovals;
    }

    public String getPastTasks()
    {
        return pastTasks;
    }

    public void setPastTasks(String pastTasks)
    {
        this.pastTasks = pastTasks;
    }

    public List<BuckslipFutureTask> getFutureTasks()
    {
        return futureTasks;
    }

    public void setFutureTasks(List<BuckslipFutureTask> futureTasks)
    {
        this.futureTasks = futureTasks;
    }

    public String getObjectType()
    {
        return objectType;
    }

    public void setObjectType(String objectType)
    {
        this.objectType = objectType;
    }

    public Long getObjectId()
    {
        return objectId;
    }

    public void setObjectId(Long objectId)
    {
        this.objectId = objectId;
    }
}
