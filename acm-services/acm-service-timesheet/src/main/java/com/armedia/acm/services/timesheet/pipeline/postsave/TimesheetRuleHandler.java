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
        // apply timesheet business rules after save
        getTimesheetBusinessRule().applyRules(entity);
    }

    @Override
    public void rollback(AcmTimesheet entity, TimesheetPipelineContext pipelineContext) throws PipelineProcessException
    {
        // nothing to execute on rollback
    }

    public SaveTimesheetBusinessRule getTimesheetBusinessRule()
    {
        return timesheetBusinessRule;
    }

    public void setTimesheetBusinessRule(SaveTimesheetBusinessRule timesheetBusinessRule)
    {
        this.timesheetBusinessRule = timesheetBusinessRule;
    }
}
