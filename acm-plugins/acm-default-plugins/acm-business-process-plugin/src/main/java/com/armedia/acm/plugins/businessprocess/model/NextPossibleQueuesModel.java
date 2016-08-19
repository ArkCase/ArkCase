package com.armedia.acm.plugins.businessprocess.model;

import com.armedia.acm.services.pipeline.AbstractPipelineContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NextPossibleQueuesModel<T, P extends AbstractPipelineContext>
{
    private T businessObject;

    private P pipelineContext;

    private List<String> nextPossibleQueues = new ArrayList<>();

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
        return Collections.unmodifiableList(nextPossibleQueues);
    }

    public void setNextPossibleQueues(List<String> nextPossibleQueues)
    {
        List<String> queues = new ArrayList<>();
        queues.addAll(nextPossibleQueues);
        this.nextPossibleQueues = queues;
    }

}
