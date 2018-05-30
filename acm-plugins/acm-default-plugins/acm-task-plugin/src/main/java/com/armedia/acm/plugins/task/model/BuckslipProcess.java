package com.armedia.acm.plugins.task.model;

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
