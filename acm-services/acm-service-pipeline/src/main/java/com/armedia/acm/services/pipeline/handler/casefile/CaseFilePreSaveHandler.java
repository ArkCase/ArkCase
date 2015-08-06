package com.armedia.acm.services.pipeline.handler.casefile;

import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.services.pipeline.PipelineContext;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;

/**
 * Created by Petar Ilin <petar.ilin@armedia.com> on 28.07.2015.
 */
public class CaseFilePreSaveHandler implements PipelineHandler<CaseFile>
{
    @Override
    public void execute(CaseFile entity, PipelineContext pipelineContext) throws PipelineProcessException
    {
        
    }

    @Override
    public void rollback(CaseFile entity, PipelineContext pipelineContext) throws PipelineProcessException
    {

    }
}
