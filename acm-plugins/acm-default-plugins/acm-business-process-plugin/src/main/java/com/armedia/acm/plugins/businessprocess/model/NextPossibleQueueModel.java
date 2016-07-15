package com.armedia.acm.plugins.businessprocess.model;

import com.armedia.acm.services.pipeline.AbstractPipelineContext;

import java.util.List;

public class NextPossibleQueueModel<T, P extends AbstractPipelineContext>
{
    private T businessObject;

    private P pipelineContext;

    private List<String> nextPossibleQueues;

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

    public List<String> getNextPossibleQueues()
    {
        return nextPossibleQueues;
    }

    public void setNextPossibleQueues(List<String> nextPossibleQueues)
    {
        this.nextPossibleQueues = nextPossibleQueues;
    }

}
