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

import com.armedia.acm.plugins.admin.model.GoogleAnalyticsConfig;
import com.armedia.acm.plugins.admin.service.GoogleAnalyticsConfigService;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
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
@RequestMapping({ "/api/v1/plugin/admin", "/api/latest/plugin/admin" })
public class GoogleAnalyticsConfigAPIController
{

    /**
     * Logger instance.
     */
    private Logger logger = LogManager.getLogger(getClass());

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
    public GoogleAnalyticsConfig getGoogleAnalyticsSettings()
    {
        return googleAnalyticsConfigService.getGoogleAnalyticsSettings();
    }

    /**
     * Store Google Analytics configuration as key-value properties (used in Admin UI).
     *
     * @param configuration
     *            JSON representation of GA settings
     * @return properties
     */
    @RequestMapping(value = "/googleAnalytics", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public GoogleAnalyticsConfig setGoogleAnalyticsSettings(@RequestBody GoogleAnalyticsConfig configuration)
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
