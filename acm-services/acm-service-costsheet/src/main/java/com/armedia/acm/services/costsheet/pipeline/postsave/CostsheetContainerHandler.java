package com.armedia.acm.services.costsheet.pipeline.postsave;

import com.armedia.acm.services.costsheet.model.AcmCostsheet;
import com.armedia.acm.services.costsheet.pipeline.CostsheetPipelineContext;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;

public class CostsheetContainerHandler implements PipelineHandler<AcmCostsheet, CostsheetPipelineContext>
{

    @Override
    public void execute(AcmCostsheet entity, CostsheetPipelineContext pipelineContext) throws PipelineProcessException
    {

        if (entity.getContainer().getContainerObjectTitle() == null)
        {
            entity.getContainer().setContainerObjectTitle(entity.getCostsheetNumber());
        }
    }

    @Override
    public void rollback(AcmCostsheet entity, CostsheetPipelineContext pipelineContext) throws PipelineProcessException
    {

    }
}
