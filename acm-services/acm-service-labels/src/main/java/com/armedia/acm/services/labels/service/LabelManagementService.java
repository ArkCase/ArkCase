package com.armedia.acm.services.labels.service;

/*-
 * #%L
 * ACM Service: Labels Service
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

import com.armedia.acm.configuration.core.LabelsConfiguration;
import com.armedia.acm.configuration.service.ConfigurationPropertyService;
import com.armedia.acm.core.LanguageSettingsConfig;
import com.armedia.acm.services.labels.exception.AcmLabelManagementException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by sergey on 2/12/16.
 */
public class LabelManagementService
{
    private static final String labelsDescriptionKeyEnd = ".desc?";
    private Logger log = LogManager.getLogger(getClass());
    private String defaultLocale;
    private Map<String, Object> defaultLocales;
    private Map<String, Map<String, JSONObject>> labelResourcesForFrevvo = new HashMap<>();

    private LanguageSettingsConfig languageSettingsConfig;
    private LabelsConfiguration labelsConfiguration;
    private ConfigurationPropertyService configurationPropertyService;

    /**
     * Return module's resource
     *
     * @param moduleId
     * @param lang
     * @return
     */
    public JSONObject getResource(String moduleId, String lang) throws AcmLabelManagementException
    {
        String labelsResource = moduleId + "-" + lang;
        return loadResource(labelsResource);
    }

    /**
     * Return module's resources for translation in frevvo forms
     *
     * @param moduleId
     * @param lang
     * @return
     */
    public JSONObject getLabelResources(String moduleId, String lang) throws AcmLabelManagementException
    {
        Map<String, JSONObject> moduleResource = labelResourcesForFrevvo.get(moduleId);

        if (moduleResource == null)
        {
            JSONObject jsonObject = getResource(moduleId, lang);
            moduleResource = new HashMap<>();
            moduleResource.put(lang, jsonObject);
            labelResourcesForFrevvo.put(moduleId, moduleResource);
        }

        JSONObject json = moduleResource.get(lang);

        if (json == null)
        {
            json = getResource(moduleId, lang);
            moduleResource.put(lang, json);
        }

        return json;
    }

    /**
     * Return module's resource string that contains current value, default value and description
     *
     * @param moduleId
     * @param lang
     * @return
     */
    public JSONArray getAdminResource(String moduleId, String lang) throws AcmLabelManagementException
    {
        String labelsResource = String.format("%s-%s", moduleId, lang);
        HashMap<String, String> defaultLabels = (HashMap<String, String>) labelsConfiguration.getDefaultProperty(labelsResource);
        JSONObject moduleResource = loadResource(labelsResource);

        if (!defaultLocale.equals(lang))
        {
            String baseFileName = String.format("%s-%s", moduleId, defaultLocale);
            JSONObject baseResource = loadResource(baseFileName);
            moduleResource = mergeResources(baseResource, moduleResource);
        }

        return convertToObjectForAdminResources(moduleResource, defaultLabels);
    }

    /**
     * Return settings
     *
     * @param createIfAbsent
     * @return
     */
    public LanguageSettingsConfig getSettings(boolean createIfAbsent) throws AcmLabelManagementException
    {
        LanguageSettingsConfig settings = null;

        if ((languageSettingsConfig.getDefaultLocale().isEmpty() || languageSettingsConfig.getLocaleCode().isEmpty()) && createIfAbsent)
        {
            settings = new LanguageSettingsConfig();
            settings.setDefaultLocale(defaultLocale);
            settings.setLocaleCode(defaultLocale);

            updateLanguageSettings(settings);
        }
        else
        {
            settings = languageSettingsConfig;
        }

        return settings;
    }

    /**
     * Update Language Settings
     *
     * @param languageSettingsConfig
     * @return
     */
    public LanguageSettingsConfig updateLanguageSettings(LanguageSettingsConfig languageSettingsConfig) throws AcmLabelManagementException
    {
        configurationPropertyService.updateProperties(languageSettingsConfig);
        return languageSettingsConfig;
    }

    /**
     * Batch reset of modules
     *
     * @param modules
     * @param langs
     * @throws AcmLabelManagementException
     */
    public void reset(List<String> modules, List<String> langs) throws AcmLabelManagementException
    {
        for (String lang : langs)
        {
            for (String module : modules)
            {
                resetModule(module, lang);
            }
        }
    }

    /**
     * Remove module's runtime resource and reload it.
     *
     * @param moduleId
     * @param lang
     */
    public void resetModule(String moduleId, String lang) throws AcmLabelManagementException
    {
        String labelsResource = String.format("%s-%s", moduleId, lang);
        try
        {
            configurationPropertyService.resetFilePropertiesToDefault(labelsResource);
            loadResource(labelsResource);
        }
        catch (Exception e)
        {
            String msg = String.format("Can't reset resource file %s", labelsResource);
            log.error(msg);
            throw new AcmLabelManagementException(msg);
        }
    }

    /**
     * Update resource file
     *
     * @param resource
     * @param applicationName
     * @return
     */
    public Map<String, Object> updateResource(String resource, String applicationName)
    {
        JSONObject value = new JSONObject(resource);
        Map<String, Object> properties = new HashMap<>();

        if (!value.getString("description").isEmpty())
        {
            properties.put(value.getString("id") + labelsDescriptionKeyEnd, value.getString("description"));
        }
        else
        {
            HashMap<String, String> defaultLabels = (HashMap<String, String>) labelsConfiguration
                    .getDefaultProperty(applicationName);
            if (defaultLabels.containsKey(value.getString("id") + labelsDescriptionKeyEnd))
            {
                properties.put(value.getString("id") + labelsDescriptionKeyEnd, "");
            }
        }

        properties.put(value.getString("id"), value.getString("value"));

        configurationPropertyService.updateProperties(properties, applicationName);
        return properties;
    }

    /**
     * Merge normalized resources
     *
     * @param baseRes
     * @param extRes
     * @return
     */
    private JSONObject mergeResources(JSONObject baseRes, JSONObject extRes)
    {
        JSONObject res;
        // If Base resource is not null then use it as base for merged resources
        if (baseRes != null)
        {
            res = new JSONObject(baseRes.toString());
        }
        else
        {
            res = new JSONObject();
        }

        if (extRes != null)
        {
            Iterator<String> keys = extRes.keys();
            while (keys.hasNext())
            {
                String key = keys.next();
                res.put(key, extRes.getString(key));
            }
        }
        return res;
    }

    /**
     * Convert labels data 'moduleRes' from yaml file to produce admin resource
     *
     * @param moduleRes
     *            - merged labels for a specific module
     * @param defaultLabels
     *            - only the default labels without the changes from runtime
     * @return
     */
    private JSONArray convertToObjectForAdminResources(JSONObject moduleRes, Map<String, String> defaultLabels)
    {
        JSONArray jsonResourceArray = new JSONArray();
        if (moduleRes != null)
        {
            Iterator<String> keys = moduleRes.keys();
            while (keys.hasNext())
            {
                String key = keys.next();
                if (!key.endsWith(labelsDescriptionKeyEnd))
                {
                    String value = moduleRes.getString(key);
                    String defaultValue = defaultLabels.get(key);

                    String description = "";
                    if (moduleRes.has(key + labelsDescriptionKeyEnd))
                    {
                        description = moduleRes.getString(key + labelsDescriptionKeyEnd);
                    }

                    JSONObject node = new JSONObject();
                    node.put("id", key);
                    node.put("value", value);
                    node.put("description", description);
                    node.put("defaultValue", defaultValue);

                    jsonResourceArray.put(node);
                }
            }
        }

        return jsonResourceArray;
    }

    /**
     * Load JSON resource file
     *
     * @param labelsResource
     * @return
     */
    private JSONObject loadResource(String labelsResource)
    {
        try
        {
            HashMap<String, String> labels = (HashMap<String, String>) labelsConfiguration.getProperty(labelsResource);
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(labels);
            return new JSONObject(json);

        }
        catch (Exception e)
        {
            log.warn(String.format("Can't read resource file %s", labelsResource));
            return null;
        }
    }

    public void setLanguageSettingsConfig(LanguageSettingsConfig languageSettingsConfig)
    {
        this.languageSettingsConfig = languageSettingsConfig;
    }

    public String getDefaultLocale()
    {
        return defaultLocale;
    }

    public void setDefaultLocale(String defaultLocale)
    {
        this.defaultLocale = defaultLocale;
    }

    public Map<String, Object> getDefaultLocales()
    {
        return defaultLocales;
    }

    public void setDefaultLocales(Map<String, Object> defaultLocales)
    {
        this.defaultLocales = defaultLocales;
    }

    public LabelsConfiguration getLabelsConfiguration()
    {
        return labelsConfiguration;
    }

    public void setLabelsConfiguration(LabelsConfiguration labelsConfiguration)
    {
        this.labelsConfiguration = labelsConfiguration;
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
