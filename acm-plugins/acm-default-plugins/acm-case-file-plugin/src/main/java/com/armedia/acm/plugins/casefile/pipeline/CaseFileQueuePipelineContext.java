package com.armedia.acm.plugins.casefile.pipeline;

import com.armedia.acm.services.pipeline.PipelineContext;

/**
 * Created by armdev on 8/26/15.
 */
public class CaseFileQueuePipelineContext extends CaseFilePipelineContext implements PipelineContext
{
    private String enqueueName;

    public String getEnqueueName()
    {
        return enqueueName;
    }

    public void setEnqueueName(String enqueueName)
    {
        this.enqueueName = enqueueName;
    }
}
