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
import com.armedia.acm.services.labels.exception.AcmLabelManagementException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

import java.io.File;
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
    private String resourcesLocation;
    private String resourceFile;
    private String settingsFileLocation;
    private String defaultLocale;
    private Map<String, Object> defaultLocales;
    private ApplicationEventPublisher applicationEventPublisher;
    private Map<String, Map<String, JSONObject>> cachedResources = new HashMap<>();

    @Autowired
    private LabelsConfiguration labelsConfiguration;
    @Autowired
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
        String fileName = moduleId + "-" + lang;

        return loadResource(fileName);
    }

    /**
     * Return cached module's resource. Doesn't read from the file if once read
     *
     * @param moduleId
     * @param lang
     * @return
     */
    public JSONObject getCachedResource(String moduleId, String lang) throws AcmLabelManagementException
    {
        Map<String, JSONObject> moduleResource = cachedResources.get(moduleId);

        if (moduleResource == null)
        {
            JSONObject jsonObject = getResource(moduleId, lang);
            moduleResource = new HashMap<>();
            moduleResource.put(lang, jsonObject);
            cachedResources.put(moduleId, moduleResource);
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
        // 1. Load module's resource file
        String fileName = String.format("%s-%s", moduleId, lang);
        JSONObject moduleResource = loadResource(fileName);
        HashMap<String, String> defaultLabels = (HashMap<String, String>) labelsConfiguration.getDefaultProperty(fileName);

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
    public JSONObject getSettings(boolean createIfAbsent) throws AcmLabelManagementException
    {
        JSONObject settings;
        try
        {
            File file = FileUtils.getFile(settingsFileLocation);
            String resource = FileUtils.readFileToString(file, "UTF-8");
            settings = new JSONObject(resource);
        }
        catch (Exception e)
        {
            log.warn(String.format("Can't read resource file %s", settingsFileLocation));
            return null;
        }

        // Try to create resource if required
        if (settings == null && createIfAbsent)
        {
            // Create default settings
            JSONObject defaultSettings = getDefaultSettings();
            settings = updateSettings(defaultSettings);
        }

        return settings;
    }

    /**
     * Create default settings object
     *
     * @return
     */
    public JSONObject getDefaultSettings()
    {
        JSONObject defaultSettings = new JSONObject();
        Map<String, Object> locales = getDefaultLocales();
        String locale = getDefaultLocale();
        defaultSettings.put("locales", locales);
        defaultSettings.put("defaultLocale", locale);
        return defaultSettings;
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
     * Batch refresh of modules
     *
     * @param modules
     * @param langs
     */
    public void refresh(List<String> modules, List<String> langs)
    {
        for (String lang : langs)
        {
            for (String module : modules)
            {
                String fileName = String.format("%s-%s", module, lang);
                loadResource(fileName);
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
        String fileName = String.format("%s-%s-runtime.yaml", moduleId, lang);
        try
        {
            File resourceFile = new File(resourcesLocation + fileName);
            FileUtils.deleteQuietly(resourceFile);
            loadResource(moduleId + "-" + lang);
        }
        catch (Exception e)
        {
            String msg = String.format("Can't reset resource file %s", fileName);
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
     * Update Settings file
     *
     * @param objSettings
     * @return
     */
    public JSONObject updateSettings(JSONObject objSettings) throws AcmLabelManagementException
    {
        try
        {
            File file = FileUtils.getFile(settingsFileLocation);
            FileUtils.writeStringToFile(file, objSettings.toString(), "UTF-8");
        }
        catch (Exception e)
        {
            log.error(String.format("Can't write settings data in to the file %s", settingsFileLocation));
            throw new AcmLabelManagementException("Update settings error", e);
        }
        return objSettings;
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
     * @param fileName
     * @return
     */
    private JSONObject loadResource(String fileName)
    {
        try
        {
            HashMap<String, String> labels = (HashMap<String, String>) labelsConfiguration.getProperty(fileName);
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(labels);
            return new JSONObject(json);

        }
        catch (Exception e)
        {
            log.warn(String.format("Can't read resource file %s", fileName));
            return null;
        }
    }

    public void setResourcesLocation(String resourcesLocation)
    {
        this.resourcesLocation = resourcesLocation;
    }

    public void setResourceFile(String resourceFile)
    {
        this.resourceFile = resourceFile;
    }

    public void setSettingsFileLocation(String settingsFileLocation)
    {
        this.settingsFileLocation = settingsFileLocation;
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

    public ApplicationEventPublisher getApplicationEventPublisher()
    {
        return applicationEventPublisher;
    }

    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        this.applicationEventPublisher = applicationEventPublisher;
    }
}
