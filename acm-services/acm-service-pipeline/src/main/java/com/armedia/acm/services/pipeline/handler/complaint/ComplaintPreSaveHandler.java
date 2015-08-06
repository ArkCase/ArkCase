package com.armedia.acm.services.pipeline.handler.complaint;

import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.services.pipeline.PipelineContext;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;

/**
 * Created by Petar Ilin <petar.ilin@armedia.com> on 28.07.2015.
 */
public class ComplaintPreSaveHandler implements PipelineHandler<Complaint>
{
    @Override
    public void execute(Complaint entity, PipelineContext pipelineContext) throws PipelineProcessException
    {
        
    }

    @Override
    public void rollback(Complaint entity, PipelineContext pipelineContext) throws PipelineProcessException
    {

    }
}
