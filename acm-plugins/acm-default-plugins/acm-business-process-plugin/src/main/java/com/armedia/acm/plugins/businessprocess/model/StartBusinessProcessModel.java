package com.armedia.acm.plugins.businessprocess.model;

import com.armedia.acm.services.pipeline.AbstractPipelineContext;

public class StartBusinessProcessModel<T, P extends AbstractPipelineContext>
{

    private T businessObject;

    private P pipelineContext;

    private boolean startProcess;

    private String processName;

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

    public boolean isStartProcess()
    {
        return startProcess;
    }

    public void setStartProcess(boolean startProcess)
    {
        this.startProcess = startProcess;
    }

    public String getProcessName()
    {
        return processName;
    }

    public void setProcessName(String processName)
    {
        this.processName = processName;
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
