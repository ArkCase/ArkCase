package com.armedia.acm.services.timesheet.pipeline.postsave;

import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;
import com.armedia.acm.services.timesheet.model.AcmTimesheet;
import com.armedia.acm.services.timesheet.pipeline.TimesheetPipelineContext;

public class timesheetContainerHandler implements PipelineHandler<AcmTimesheet, TimesheetPipelineContext>
{
    @Override
    public void execute(AcmTimesheet entity, TimesheetPipelineContext pipelineContext) throws PipelineProcessException
    {
        if (entity.getContainer().getContainerObjectTitle() == null)
        {
            entity.getContainer().setContainerObjectTitle(entity.getTimesheetNumber());
        }
    }

    @Override
    public void rollback(AcmTimesheet entity, TimesheetPipelineContext pipelineContext) throws PipelineProcessException
    {

    }
}
