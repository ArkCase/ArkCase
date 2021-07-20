package com.armedia.acm.services.timesheet.web;

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

import com.armedia.acm.configuration.service.ConfigurationPropertyException;
import com.armedia.acm.services.timesheet.model.TimesheetConfig;
import com.armedia.acm.services.timesheet.model.TimesheetConfigDTO;
import com.armedia.acm.services.timesheet.service.TimesheetConfigurationService;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping({ "/api/v1/service/timesheet", "/api/latest/service/timesheet" })
public class TimesheetConfigurationAPIController
{
    private Logger log = LogManager.getLogger(getClass());

    private TimesheetConfigurationService timesheetConfigurationService;

    @RequestMapping(value = "/config", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void saveTimesheetConfiguration(@RequestBody TimesheetConfigDTO timesheetConfig)
    {
        getTimesheetConfigurationService().saveTimesheetChargeRolesConfig(timesheetConfig);
    }

    @RequestMapping(value = "/config", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<TimesheetConfigDTO> loadTimesheetConfiguration()
    {
        return new ResponseEntity<>(getTimesheetConfigurationService().loadTimesheetChargeRolesConfig(), HttpStatus.OK);
    }

    @RequestMapping(value = "/properties", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void saveTimesheetProperties(@RequestBody TimesheetConfig timesheetProperties) throws ConfigurationPropertyException
    {
        getTimesheetConfigurationService().saveProperties(timesheetProperties);
    }

    @RequestMapping(value = "/properties", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<TimesheetConfig> loadTimesheetProperties()
    {
        return ResponseEntity.ok(getTimesheetConfigurationService().loadProperties());
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
