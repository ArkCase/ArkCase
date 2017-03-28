package com.armedia.acm.plugins.admin.web.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

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
    Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Configuration file.
     */
    File configFile;

    /**
     * Retrieve the Google Analytics configuration as a JavaScript file.
     *
     * @return javascript global variables
     */
    @RequestMapping(value = "/googleAnalytics/config.js", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getGoogleAnalyticsSettings()
    {

        Properties properties = new Properties();
        try
        {
            properties.load(new FileInputStream(configFile));

        } catch (IOException e)
        {
            logger.error("Cannot read configuration file [{}]", configFile.getAbsolutePath(), e);
        }

        StringBuffer sb = new StringBuffer();

        // Google Analytics enabled flag
        sb.append("var GOOGLE_ANALYTICS_ENABLED = ");
        if (properties.containsKey("ga.enabled"))
        {
            sb.append(properties.getProperty("ga.enabled"));
        } else
        {
            sb.append("false");
        }
        sb.append(";");

        // Google Analytics Tracking ID
        sb.append("var GOOGLE_ANALYTICS_TRACKING_ID = ");
        if (properties.containsKey("ga.trackingId"))
        {
            sb.append("'").append(properties.getProperty("ga.trackingId")).append("'");
        } else
        {
            sb.append("null");
        }
        sb.append(";");

        // Google Analytics debug flag
        sb.append("var GOOGLE_ANALYTICS_DEBUG = ");
        if (properties.containsKey("ga.debug"))
        {
            sb.append(properties.getProperty("ga.debug"));
        } else
        {
            sb.append("false");
        }
        sb.append(";");


        return sb.toString();
    }

    // TODO: add methods for Administration UI (get and set settings)

    public File getConfigFile()
    {
        return configFile;
    }

    public void setConfigFile(File configFile)
    {
        this.configFile = configFile;
    }
}
