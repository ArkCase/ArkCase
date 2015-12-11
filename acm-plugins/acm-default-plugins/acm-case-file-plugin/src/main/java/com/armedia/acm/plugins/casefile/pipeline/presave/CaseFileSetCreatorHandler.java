package com.armedia.acm.plugins.casefile.pipeline.presave;

import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.pipeline.CaseFilePipelineContext;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;

/**
 * Set creator so assignment rules can use it to set participants
 * Created Petar Ilin <petar.ilin@armedia.com> on 16.11.2015.
 */
public class CaseFileSetCreatorHandler implements PipelineHandler<CaseFile, CaseFilePipelineContext>
{
    @Override
    public void execute(CaseFile entity, CaseFilePipelineContext pipelineContext) throws PipelineProcessException
    {
        entity.setCreator(pipelineContext.getAuthentication().getName());
    }

    @Override
    public void rollback(CaseFile entity, CaseFilePipelineContext pipelineContext) throws PipelineProcessException
    {
        // nothing to do here, there is no rollback action to be executed
    }
}
