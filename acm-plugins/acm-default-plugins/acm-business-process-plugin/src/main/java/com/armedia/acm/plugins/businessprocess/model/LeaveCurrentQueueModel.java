package com.armedia.acm.plugins.businessprocess.model;

import com.armedia.acm.services.pipeline.AbstractPipelineContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LeaveCurrentQueueModel<T, P extends AbstractPipelineContext>
{

    private T businessObject;

    private P pipelineContext;

    private List<String> cannotLeaveReasons = new ArrayList<>();

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

    public List<String> getCannotLeaveReasons()
    {
        return Collections.unmodifiableList(cannotLeaveReasons);
    }

    public void setCannotLeaveReasons(List<String> cannotLeaveReasons)
    {
        List<String> reasons = new ArrayList<>();
        reasons.addAll(cannotLeaveReasons);
        this.cannotLeaveReasons = reasons;
    }

    public void addCannotLeaveReasons(String reason)
    {
        cannotLeaveReasons.add(reason);
    }

}
