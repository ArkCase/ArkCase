package com.armedia.acm.plugins.admin.web.api;

import com.armedia.acm.plugins.admin.model.HolidayConfiguration;
import com.armedia.acm.plugins.admin.service.HolidayConfigurationService;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping({ "/api/v1/service/holidayConfig", "/api/latest/service/holidayConfig" })
public class HolidayConfigurationAPIController
{
    private HolidayConfigurationService holidayConfigurationService;

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public void saveHolidayConfig(@RequestBody HolidayConfiguration holidayConfiguration)
    {
        holidayConfigurationService.saveHolidayConfig(holidayConfiguration);
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public HolidayConfiguration getHolidayConfig()
    {
        return holidayConfigurationService.getHolidayConfiguration();
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
