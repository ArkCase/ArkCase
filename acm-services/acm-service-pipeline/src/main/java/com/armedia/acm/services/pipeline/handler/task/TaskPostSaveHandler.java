package com.armedia.acm.services.pipeline.handler.task;

import com.armedia.acm.plugins.task.model.AcmTask;
import com.armedia.acm.services.pipeline.PipelineContext;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;

/**
 * Created by Petar Ilin <petar.ilin@armedia.com> on 28.07.2015.
 */
public class TaskPostSaveHandler implements PipelineHandler<AcmTask>
{
    @Override
    public void execute(AcmTask entity, PipelineContext pipelineContext) throws PipelineProcessException
    {

    }

    @Override
    public void rollback(AcmTask entity, PipelineContext pipelineContext) throws PipelineProcessException
    {

    }
}
