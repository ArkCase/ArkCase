package com.armedia.acm.plugins.admin.web.api;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Properties;

/**
 * Google Analytics configuration service.
 * Created by Petar Ilin <petar.ilin@armedia.com> on 31.03.2017.
 */
public class GoogleAnalyticsConfigService
{

    /**
     * Logger instance.
     */
    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Configuration file.
     */
    private File configFile;

    /**
     * config.js Freemarker template
     */
    private Template template;

    /**
     * Default constructor, initialize Freemarker configuration and load the template.
     */
    public GoogleAnalyticsConfigService()
    {
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_22);
        configuration.setClassForTemplateLoading(getClass(), "/templates");
        try
        {
            template = configuration.getTemplate("config.js.ftl");
        } catch (IOException e)
        {
            logger.error("Cannot read template [classpath:/templates/config.js.ftl]", e);
        }
    }

    /**
     * Retrieve the Google Analytics configuration as a JavaScript file.
     *
     * @return javascript global variables
     */
    public String getGoogleAnalyticsSettingsJs()
    {
        Properties properties = new Properties();
        Writer stringWriter = new StringWriter();
        try (FileInputStream fis = new FileInputStream(configFile))
        {
            properties.load(fis);
            template.process(properties, stringWriter);
            stringWriter.close();
        } catch (IOException e)
        {
            logger.error("Cannot read configuration file [{}]", configFile.getAbsolutePath(), e);
        } catch (TemplateException e)
        {
            logger.error("Cannot process [config.js.ftl] template", e);
        } catch (NullPointerException e)
        {
            logger.error("Template [config.js.ftl] not loaded", e);
        }

        return stringWriter.toString();
    }

    /**
     * Retrieve Google Analytics configuration as JSON object (used in Admin UI).
     *
     * @return configuration represented as JSON
     */
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
    public String setGoogleAnalyticsSettings(String configuration)
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
