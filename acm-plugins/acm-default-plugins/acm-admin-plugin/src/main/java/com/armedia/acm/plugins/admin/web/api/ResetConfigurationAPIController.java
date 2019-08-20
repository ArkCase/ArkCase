package com.armedia.acm.plugins.admin.web.api;

import com.armedia.acm.configuration.service.ConfigurationPropertyService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author aleksandar.bujaroski
 */

@Controller
@RequestMapping({ "/api/v1/plugin/admin/configuration/reset", "/api/latest/plugin/admin/configuration/reset" })
public class ResetConfigurationAPIController
{


    private ConfigurationPropertyService configurationPropertyService;

    @RequestMapping(method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void resetPropertiesToDefault()
    {
        getConfigurationPropertyService().resetPropertiesToDefault();
    }

    public ConfigurationPropertyService getConfigurationPropertyService() {
        return configurationPropertyService;

    }

    public void setConfigurationPropertyService(ConfigurationPropertyService configurationPropertyService) {
        this.configurationPropertyService = configurationPropertyService;
    }
}
