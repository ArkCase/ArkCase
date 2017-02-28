package com.armedia.acm.plugins.businessprocess.model;

import com.armedia.acm.services.pipeline.AbstractPipelineContext;

public class OnEnterQueueModel<T, P extends AbstractPipelineContext>
{

    private T businessObject;

    private P pipelineContext;

    private String businessProcessName;

    /**
     * Many queue workflows set the business object to a new status.  This property allows for a generic
     * Activiti business process that can be used for many such queue workflows.
     */
    private String businessObjectNewStatus;

    /**
     * Many queue workflows send the business object toa  new queue.  This property allows for a generic
     * Activiti business process that can be used for many such queue workflows.
     */
    private String businessObjectNewQueueName;

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
}
