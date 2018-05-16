/**
 * 
 */
package com.armedia.acm.services.timesheet.web;

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

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.timesheet.model.AcmTimesheet;
import com.armedia.acm.services.timesheet.model.TimesheetConstants;
import com.armedia.acm.services.timesheet.service.TimesheetService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author riste.tutureski
 *
 */
@Controller
@RequestMapping({ "/api/v1/service/timesheet", "/api/latest/service/timesheet" })
public class SaveTimesheetAPIController
{

    private Logger LOG = LoggerFactory.getLogger(getClass());
    private TimesheetService timesheetService;

    @RequestMapping(value = "", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AcmTimesheet getTimesheet(@RequestBody AcmTimesheet timesheet,
            Authentication auth) throws AcmCreateObjectFailedException
    {
        if (LOG.isInfoEnabled())
        {
            LOG.info("Saving timesheet " + timesheet);
        }

        try
        {
            AcmTimesheet saved = getTimesheetService().save(timesheet);

            return saved;
        }
        catch (RuntimeException | PipelineProcessException e)
        {
            throw new AcmCreateObjectFailedException(TimesheetConstants.OBJECT_TYPE, e.getMessage(), e);
        }
    }

    public TimesheetService getTimesheetService()
    {
        return timesheetService;
    }

    public void setTimesheetService(TimesheetService timesheetService)
    {
        this.timesheetService = timesheetService;
    }
}
