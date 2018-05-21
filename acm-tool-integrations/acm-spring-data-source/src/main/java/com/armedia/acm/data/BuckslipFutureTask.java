package com.armedia.acm.data;

/*-
 * #%L
 * Tool Integrations: Spring Data Source
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

import java.io.Serializable;

public class BuckslipFutureTask implements Serializable
{
    private String approverId;
    private String approverFullName;
    private String taskName;
    private String groupName;
    private String details;
    private String addedBy;
    private String addedByFullName;
    private int maxTaskDurationInDays = 3;

    public String getApproverId()
    {
        return approverId;
    }

    public void setApproverId(String approverId)
    {
        this.approverId = approverId;
    }

    public String getApproverFullName()
    {
        return approverFullName;
    }

    public void setApproverFullName(String approverFullName)
    {
        this.approverFullName = approverFullName;
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

    public String getAddedByFullName()
    {
        return addedByFullName;
    }

    public void setAddedByFullName(String addedByFullName)
    {
        this.addedByFullName = addedByFullName;
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
