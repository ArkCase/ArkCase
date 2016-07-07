package com.armedia.acm.plugins.casefile.pipeline.postsave;

import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.model.CaseFileStartBusinessProcessModel;
import com.armedia.acm.plugins.casefile.pipeline.CaseFilePipelineContext;
import com.armedia.acm.plugins.casefile.service.CaseFileStartBusinessProcessBusinessRule;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;

public class CaseFileStartBusinessProcessIfNeededHandler implements PipelineHandler<CaseFile, CaseFilePipelineContext>
{

    private CaseFileStartBusinessProcessModel startBusinessProcessModel;

    private CaseFileStartBusinessProcessBusinessRule businessProcessRule;

    @Override
    public void execute(CaseFile entity, CaseFilePipelineContext pipelineContext) throws PipelineProcessException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void rollback(CaseFile entity, CaseFilePipelineContext pipelineContext) throws PipelineProcessException
    {
        // TODO Auto-generated method stub

    }

    public CaseFileStartBusinessProcessModel getStartBusinessProcessModel()
    {
        return startBusinessProcessModel;
    }

    public void setStartBusinessProcessModel(CaseFileStartBusinessProcessModel startBusinessProcessModel)
    {
        this.startBusinessProcessModel = startBusinessProcessModel;
    }

    public CaseFileStartBusinessProcessBusinessRule getBusinessProcessRule()
    {
        return businessProcessRule;
    }

    public void setBusinessProcessRule(CaseFileStartBusinessProcessBusinessRule businessProcessRule)
    {
        this.businessProcessRule = businessProcessRule;
    }

}
