package com.armedia.acm.plugins.businessprocess.model;

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
