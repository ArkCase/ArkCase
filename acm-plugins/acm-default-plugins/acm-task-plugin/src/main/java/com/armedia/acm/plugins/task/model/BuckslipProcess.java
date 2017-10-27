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

    public void setBusinessProcessId(String businessProcessId)
    {
        this.businessProcessId = businessProcessId;
    }

    public String getBusinessProcessId()
    {
        return businessProcessId;
    }

    public void setBusinessProcessName(String businessProcessName)
    {
        this.businessProcessName = businessProcessName;
    }

    public String getBusinessProcessName()
    {
        return businessProcessName;
    }

    public void setNonConcurEndsApprovals(Boolean nonConcurEndsApprovals)
    {
        this.nonConcurEndsApprovals = nonConcurEndsApprovals;
    }

    public Boolean getNonConcurEndsApprovals()
    {
        return nonConcurEndsApprovals;
    }

    public void setPastTasks(String pastTasks)
    {
        this.pastTasks = pastTasks;
    }

    public String getPastTasks()
    {
        return pastTasks;
    }

    public void setFutureTasks(List<BuckslipFutureTask> futureTasks)
    {
        this.futureTasks = futureTasks;
    }

    public List<BuckslipFutureTask> getFutureTasks()
    {
        return futureTasks;
    }

    public void setObjectType(String objectType)
    {
        this.objectType = objectType;
    }

    public String getObjectType()
    {
        return objectType;
    }

    public void setObjectId(Long objectId)
    {
        this.objectId = objectId;
    }

    public Long getObjectId()
    {
        return objectId;
    }
}
