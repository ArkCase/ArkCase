package com.armedia.acm.plugins.admin.web.api;

import com.armedia.acm.plugins.admin.model.HolidayItem;
import com.armedia.acm.plugins.admin.service.HolidayConfigurationService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping({ "/api/v1/service/holidayConfig", "/api/latest/service/holidayConfig" })
public class HolidayConfigurationAPIController
{
    private HolidayConfigurationService holidayConfigurationService;

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public void saveHolidayConfig(@RequestBody List<HolidayItem> holidays)
    {
        holidayConfigurationService.saveHolidayConfig(holidays);
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<HolidayItem>> getHolidayConfig()
    {
        return new ResponseEntity<>(holidayConfigurationService.getHolidayConfiguration(), HttpStatus.OK);
    }

    public HolidayConfigurationService getHolidayConfigurationService()
    {
        return holidayConfigurationService;
    }

    public void setHolidayConfigurationService(HolidayConfigurationService holidayConfigurationService)
    {
        this.holidayConfigurationService = holidayConfigurationService;
    }
}
