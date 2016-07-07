package com.armedia.acm.plugins.businessprocess.model;

import com.armedia.acm.services.pipeline.AbstractPipelineContext;

public class StartBusinessProcessModel<T, P extends AbstractPipelineContext>
{

    private T businessObject;

    private P pipelineContext;

    private boolean startProcess;

    private String processName;

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

}
