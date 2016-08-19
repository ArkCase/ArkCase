package com.armedia.acm.plugins.businessprocess.model;

import com.armedia.acm.services.pipeline.AbstractPipelineContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EnterQueueModel<T, P extends AbstractPipelineContext>
{

    private T businessObject;

    private P pipelineContext;

    private List<String> cannotEnterReasons = new ArrayList<>();

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

    public List<String> getCannotEnterReasons()
    {
        return Collections.unmodifiableList(cannotEnterReasons);
    }

    public void setCannotEnterReasons(List<String> cannotEnterReasons)
    {
        List<String> reasons = new ArrayList<>();
        reasons.addAll(cannotEnterReasons);
        this.cannotEnterReasons = reasons;
    }

    public void addCannotEnterReason(String reason)
    {
        cannotEnterReasons.add(reason);
    }

}
