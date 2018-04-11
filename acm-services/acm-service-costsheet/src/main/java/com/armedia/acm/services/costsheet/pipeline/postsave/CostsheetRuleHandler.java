package com.armedia.acm.services.costsheet.pipeline.postsave;

import com.armedia.acm.services.costsheet.model.AcmCostsheet;
import com.armedia.acm.services.costsheet.pipeline.CostsheetPipelineContext;
import com.armedia.acm.services.costsheet.service.SaveCostsheetBusinessRule;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;

public class CostsheetRuleHandler implements PipelineHandler<AcmCostsheet, CostsheetPipelineContext>
{

    private SaveCostsheetBusinessRule costsheetBusinessRule;

    @Override
    public void execute(AcmCostsheet entity, CostsheetPipelineContext pipelineContext) throws PipelineProcessException
    {
        // apply costsheet business rules after save
        getCostsheetBusinessRule().applyRules(entity);
    }

    @Override
    public void rollback(AcmCostsheet entity, CostsheetPipelineContext pipelineContext) throws PipelineProcessException
    {
        // nothing to execute on rollback
    }

    public SaveCostsheetBusinessRule getCostsheetBusinessRule()
    {
        return costsheetBusinessRule;
    }

    public void setCostsheetBusinessRule(SaveCostsheetBusinessRule costsheetBusinessRule)
    {
        this.costsheetBusinessRule = costsheetBusinessRule;
    }
}
