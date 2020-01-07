package com.armedia.acm.services.timesheet.pipeline.postsave;

/*-
 * #%L
 * ACM Service: Timesheet
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;
import com.armedia.acm.services.timesheet.model.AcmTimesheet;
import com.armedia.acm.services.timesheet.pipeline.TimesheetPipelineContext;
import com.armedia.acm.services.timesheet.service.SaveTimesheetBusinessRule;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class TimesheetRuleHandler implements PipelineHandler<AcmTimesheet, TimesheetPipelineContext>
{
    private final Logger log = LogManager.getLogger(getClass());
    private SaveTimesheetBusinessRule timesheetBusinessRule;

    @Override
    public void execute(AcmTimesheet entity, TimesheetPipelineContext pipelineContext) throws PipelineProcessException
    {
        // apply timesheet business rules after save
        log.info("Timesheet with id [{}] and title [{}] entering TimesheetRuleHandler", entity.getId(), entity.getTitle());

        getTimesheetBusinessRule().applyRules(entity);

        log.info("Timesheet with id [{}] and title [{}] exiting TimesheetRuleHandler", entity.getId(), entity.getTitle());
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
