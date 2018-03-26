package com.armedia.acm.plugins.admin.web.api;

import com.armedia.acm.plugins.admin.model.HolidayScheduleConfiguration;
import com.armedia.acm.plugins.admin.service.HolidayScheduleConfigurationService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping({ "/api/v1/service/holidaySchedule/config", "/api/latest/service/holidaySchedule/config" })
public class HolidayScheduleConfigurationAPIController
{
    private HolidayScheduleConfigurationService holidayScheduleConfigurationService;

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public void saveHolidaySchedule(@RequestBody HolidayScheduleConfiguration holidayScheduleConf)
    {
        holidayScheduleConfigurationService.saveHolidaySchedule(holidayScheduleConf);
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<HolidayScheduleConfiguration> getHolidaySchedule()
    {
        return new ResponseEntity<>(holidayScheduleConfigurationService.getHolidaySchedule(), HttpStatus.OK);
    }

    public HolidayScheduleConfigurationService getHolidayScheduleConfigurationService()
    {
        return holidayScheduleConfigurationService;
    }

    public void setHolidayScheduleConfigurationService(HolidayScheduleConfigurationService holidayScheduleConfigurationService)
    {
        this.holidayScheduleConfigurationService = holidayScheduleConfigurationService;
    }
}
