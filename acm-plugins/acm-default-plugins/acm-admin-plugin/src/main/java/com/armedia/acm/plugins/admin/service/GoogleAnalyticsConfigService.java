package com.armedia.acm.plugins.admin.service;

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

import com.armedia.acm.configuration.service.ConfigurationPropertyService;
import com.armedia.acm.plugins.admin.model.GoogleAnalyticsConfig;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * Google Analytics configuration service.
 * Created by Petar Ilin <petar.ilin@armedia.com> on 31.03.2017.
 */
public class GoogleAnalyticsConfigService
{
    /**
     * Logger instance.
     */
    private Logger logger = LogManager.getLogger(getClass());

    private GoogleAnalyticsConfig config;

    private ConfigurationPropertyService configurationPropertyService;

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
        }
        catch (IOException e)
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
        try (Writer stringWriter = new StringWriter())
        {
            Map<String, Object> configProperties = configurationPropertyService.getProperties(config);
            template.process(configProperties, stringWriter);
            return stringWriter.toString();
        }
        catch (IOException e)
        {
            logger.error("Cannot write to template", e);
        }
        catch (TemplateException e)
        {
            logger.error("Cannot process [config.js.ftl] template", e);
        }
        catch (NullPointerException e)
        {
            logger.error("Template [config.js.ftl] not loaded", e);
        }

        return "";
    }

    /**
     * Retrieve Google Analytics configuration (used in Admin UI).
     *
     * @return configuration
     */
    public GoogleAnalyticsConfig getGoogleAnalyticsSettings()
    {
        logger.debug("Retrieving Google Analytics configuration");
        return config;
    }

    /**
     * Store Google Analytics configuration as key-value properties (used in Admin UI).
     *
     * @param config
     *            GA settings
     * @return properties
     */
    public GoogleAnalyticsConfig setGoogleAnalyticsSettings(GoogleAnalyticsConfig config)
    {
        logger.debug("Storing Google Analytics properties");
        configurationPropertyService.updateProperties(config);
        return config;
    }

    public GoogleAnalyticsConfig getConfig()
    {
        return config;
    }

    public void setConfig(GoogleAnalyticsConfig config)
    {
        this.config = config;
    }

    public ConfigurationPropertyService getConfigurationPropertyService()
    {
        return configurationPropertyService;
    }

    public void setConfigurationPropertyService(ConfigurationPropertyService configurationPropertyService)
    {
        this.configurationPropertyService = configurationPropertyService;
    }
}
