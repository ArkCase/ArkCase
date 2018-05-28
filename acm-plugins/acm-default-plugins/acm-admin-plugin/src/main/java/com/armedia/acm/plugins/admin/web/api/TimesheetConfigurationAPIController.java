package com.armedia.acm.plugins.admin.web.api;

/*-
 * #%L
 * ACM Default Plugin: admin
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

import com.armedia.acm.plugins.admin.model.TimesheetConfig;
import com.armedia.acm.plugins.admin.service.TimesheetConfigurationService;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping({ "/api/v1/service/timesheet/config", "/api/latest/service/timesheet/config" })
public class TimesheetConfigurationAPIController
{

    private TimesheetConfigurationService timesheetConfigurationService;

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<TimesheetConfig> getTimesheetConfig()
    {
        return new ResponseEntity<>(getTimesheetConfigurationService().getConfig(), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void updateTimesheetConfig(@RequestBody TimesheetConfig timesheetConfig)
    {
        getTimesheetConfigurationService().saveConfig(timesheetConfig);
    }

    public TimesheetConfigurationService getTimesheetConfigurationService()
    {
        return timesheetConfigurationService;
    }

    public void setTimesheetConfigurationService(TimesheetConfigurationService timesheetConfigurationService)
    {
        this.timesheetConfigurationService = timesheetConfigurationService;
    }
}
