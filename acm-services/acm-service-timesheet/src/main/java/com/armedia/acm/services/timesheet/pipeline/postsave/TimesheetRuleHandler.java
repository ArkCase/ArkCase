package com.armedia.acm.services.timesheet.pipeline.postsave;

import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;
import com.armedia.acm.services.timesheet.model.AcmTimesheet;
import com.armedia.acm.services.timesheet.pipeline.TimesheetPipelineContext;
import com.armedia.acm.services.timesheet.service.SaveTimesheetBusinessRule;

public class TimesheetRuleHandler implements PipelineHandler<AcmTimesheet, TimesheetPipelineContext>
{

    private SaveTimesheetBusinessRule timesheetBusinessRule;

    @Override
    public void execute(AcmTimesheet entity, TimesheetPipelineContext pipelineContext) throws PipelineProcessException
    {
        // apply costsheet business rules after save
        getCostsheetBusinessRule().applyRules(entity);
    }

    @Override
    public void rollback(AcmTimesheet entity, TimesheetPipelineContext pipelineContext) throws PipelineProcessException
    {
        // nothing to execute on rollback
    }

    public SaveTimesheetBusinessRule getCostsheetBusinessRule()
    {
        return timesheetBusinessRule;
    }

    public void setCostsheetBusinessRule(SaveTimesheetBusinessRule timesheetBusinessRule)
    {
        this.timesheetBusinessRule = timesheetBusinessRule;
    }

}
