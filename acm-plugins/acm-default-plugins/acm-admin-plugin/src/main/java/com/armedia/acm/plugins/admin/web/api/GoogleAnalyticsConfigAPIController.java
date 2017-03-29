package com.armedia.acm.plugins.admin.web.api;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
    public String getGoogleAnalyticsSettingsJs()
    {

        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream(configFile))
        {
            properties.load(fis);

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

    /**
     * Retrieve Google Analytics configuration as JSON object (used in Admin UI).
     *
     * @return configuration represented as JSON
     */
    @RequestMapping(value = "/googleAnalytics", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getGoogleAnalyticsSettings()
    {
        // TODO: this method probably needs improvement/adaptation once Admin UI is developed
        logger.debug("Retrieving Google Analytics configuration");
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream(configFile))
        {
            properties.load(fis);

        } catch (IOException e)
        {
            logger.error("Cannot read configuration file [{}]", configFile.getAbsolutePath(), e);
        }
        JSONObject jsonObject = new JSONObject();
        for (Object key : properties.keySet())
        {
            jsonObject.put((String) key, properties.get(key));
        }
        return jsonObject.toString();
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
        // TODO: this method probably needs improvement/adaptation once Admin UI is developed
        logger.debug("Storing Google Analytics properties");
        JSONObject jsonConfiguration = new JSONObject(configuration);
        Properties properties = new Properties();
        for (Object key : jsonConfiguration.keySet())
        {
            // FIXME: validate keys!
            properties.put(key, jsonConfiguration.get((String) key));
        }
        try (FileOutputStream fos = new FileOutputStream(configFile))
        {
            properties.store(fos, "Google Analytics configuration");
            logger.debug("Google Analytics configuration stored");
        } catch (IOException e)
        {
            logger.error("Cannot write configuration file [{}]", configFile.getAbsolutePath(), e);
        }
        return properties.toString();
    }

    public File getConfigFile()
    {
        return configFile;
    }

    public void setConfigFile(File configFile)
    {
        this.configFile = configFile;
    }
}
