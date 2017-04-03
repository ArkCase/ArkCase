package com.armedia.acm.plugins.admin.web.api;

import com.armedia.acm.plugins.admin.service.GoogleAnalyticsConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Google Analytics configuration.
 * Created by Petar Ilin <petar.ilin@armedia.com> on 28.03.2017.
 */
@Controller
@RequestMapping({"/api/v1/plugin/admin", "/api/latest/plugin/admin"})
public class GoogleAnalyticsConfigAPIController
{

    /**
     * Logger instance.
     */
    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Google Analytics configuration instance.
     */
    private GoogleAnalyticsConfigService googleAnalyticsConfigService;

    /**
     * Retrieve the Google Analytics configuration as a JavaScript file.
     *
     * @return javascript global variables
     */
    @RequestMapping(value = "/googleAnalytics/config.js", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getGoogleAnalyticsSettingsJs()
    {
        return googleAnalyticsConfigService.getGoogleAnalyticsSettingsJs();
    }

    /**
     * Retrieve Google Analytics configuration as JSON object (used in Admin UI).
     *
     * @return configuration represented as JSON
     */
    @RequestMapping(value = "/googleAnalytics", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getGoogleAnalyticsSettings()
    {
        return googleAnalyticsConfigService.getGoogleAnalyticsSettings();
    }

    /**
     * Store Google Analytics configuration as key-value properties (used in Admin UI).
     *
     * @param configuration JSON representation of GA settings
     * @return properties
     */
    @RequestMapping(value = "/googleAnalytics", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String setGoogleAnalyticsSettings(@RequestBody String configuration)
    {
        return googleAnalyticsConfigService.setGoogleAnalyticsSettings(configuration);
    }

    public GoogleAnalyticsConfigService getGoogleAnalyticsConfigService()
    {
        return googleAnalyticsConfigService;
    }

    public void setGoogleAnalyticsConfigService(GoogleAnalyticsConfigService googleAnalyticsConfigService)
    {
        this.googleAnalyticsConfigService = googleAnalyticsConfigService;
    }
}
