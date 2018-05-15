package com.armedia.acm.plugins.businessprocess.model;

/*-
 * #%L
 * ACM Default Plugin: Acm Business Process
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

import com.armedia.acm.services.pipeline.AbstractPipelineContext;

public class OnEnterQueueModel<T, P extends AbstractPipelineContext>
{

    private T businessObject;

    private P pipelineContext;

    private String businessProcessName;

    /**
     * Many queue workflows set the business object to a new status. This property allows for a generic
     * Activiti business process that can be used for many such queue workflows.
     */
    private String businessObjectNewStatus;

    /**
     * Many queue workflows send the business object toa new queue. This property allows for a generic
     * Activiti business process that can be used for many such queue workflows.
     */
    private String businessObjectNewQueueName;

    /**
     * Sometimes we want to create a set of users tasks on entering a queue. This property allows for a generic
     * Activiti business process to create a set of tasks.
     */
    private String taskAssignees;

    /**
     * Sometimes we want to create a set of users tasks on entering a queue. This property allows for a generic
     * Activiti business process to create a set of tasks.
     */
    private String taskName;

    private String taskOwningGroup;

    public T getBusinessObject()
    {
        return businessObject;
    }

    public void setBusinessObject(T businessObject)
    {
        this.businessObject = businessObject;
    }

    public P getPipelineContext()
    {
        return pipelineContext;
    }

    public void setPipelineContext(P pipelineContext)
    {
        this.pipelineContext = pipelineContext;
    }

    public String getBusinessProcessName()
    {
        return businessProcessName;
    }

    public void setBusinessProcessName(String businessProcessName)
    {
        this.businessProcessName = businessProcessName;
    }

    public String getBusinessObjectNewStatus()
    {
        return businessObjectNewStatus;
    }

    public void setBusinessObjectNewStatus(String businessObjectNewStatus)
    {
        this.businessObjectNewStatus = businessObjectNewStatus;
    }

    public String getBusinessObjectNewQueueName()
    {
        return businessObjectNewQueueName;
    }

    public void setBusinessObjectNewQueueName(String businessObjectNewQueueName)
    {
        this.businessObjectNewQueueName = businessObjectNewQueueName;
    }

    public String getTaskName()
    {
        return taskName;
    }

    public void setTaskName(String taskName)
    {
        this.taskName = taskName;
    }

    public String getTaskOwningGroup()
    {
        return taskOwningGroup;
    }

    public void setTaskOwningGroup(String taskOwningGroup)
    {
        this.taskOwningGroup = taskOwningGroup;
    }

    public String getTaskAssignees()
    {
        return taskAssignees;
    }

    public void setTaskAssignees(String taskAssignees)
    {
        this.taskAssignees = taskAssignees;
    }
}
