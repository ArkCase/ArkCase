package com.armedia.acm.plugins.admin.web.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.armedia.acm.plugins.admin.model.TimesheetConfig;
import com.armedia.acm.plugins.admin.service.TimesheetConfigurationService;

@Controller
@RequestMapping({"/api/v1/service/timesheet/config", "/api/latest/service/timesheet/config"})
public class TimesheetConfigurationAPIController {

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

    public TimesheetConfigurationService getTimesheetConfigurationService() {
        return timesheetConfigurationService;
    }

    public void setTimesheetConfigurationService(TimesheetConfigurationService timesheetConfigurationService) {
        this.timesheetConfigurationService = timesheetConfigurationService;
    }
}
