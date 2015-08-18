package com.armedia.acm.plugins.complaint.pipeline.postsave;

import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.complaint.pipeline.ComplaintPipelineContext;
import com.armedia.acm.plugins.complaint.service.SaveComplaintBusinessRule;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;

/**
 * Apply business rules to a Complaint.
 * Created by Petar Ilin <petar.ilin@armedia.com> on 12.08.2015.
 */
public class ComplaintRulesHandler implements PipelineHandler<Complaint, ComplaintPipelineContext>
{
    /**
     * Business rule manager.
     */
    private SaveComplaintBusinessRule saveRule;

    @Override
    public void execute(Complaint entity, ComplaintPipelineContext pipelineContext) throws PipelineProcessException
    {
        entity = saveRule.applyRules(entity);
    }

    @Override
    public void rollback(Complaint entity, ComplaintPipelineContext pipelineContext) throws PipelineProcessException
    {
        // nothing to do here, there is no rollback action to be executed
    }

    public SaveComplaintBusinessRule getSaveRule()
    {
        return saveRule;
    }

    public void setSaveRule(SaveComplaintBusinessRule saveRule)
    {
        this.saveRule = saveRule;
    }
}
