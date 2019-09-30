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
    private final String MODULE_CORE_ID = "core";
    private Logger log = LogManager.getLogger(getClass());
    private String customResourcesLocation;
    private String customResourceFile;
    private String moduleConfigLocation;
    private String resourcesLocation;
    private String resourceFile;
    private String settingsFileLocation;
    private String defaultLocale;
    private Map<String, Object> defaultLocales;
    private ApplicationEventPublisher applicationEventPublisher;
    private Map<String, Map<String, JSONObject>> cachedResources = new HashMap<>();

    @Autowired
    private LabelsConfiguration labelsConfiguration;

    public void setLabelsConfiguration(LabelsConfiguration labelsConfiguration)
    {
        this.labelsConfiguration = labelsConfiguration;
    }

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

        // String fileName = String.format(resourcesLocation + resourceFile, moduleId, lang);
        JSONObject resource = loadResource(fileName);

        JSONObject menusInfo = loadMenusResources(lang);
        if (menusInfo != null)
            resource = mergeResources(resource, menusInfo);

        // Try to create resource if required
        if (resource == null)
        {
            resource = updateResource(moduleId, lang);
        }

        return resource;
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
        return getAdminResource(moduleId, lang, true);
    }

    public JSONArray getAdminResource(String moduleId, String lang, boolean loadMenu) throws AcmLabelManagementException
    {
        // 1. Load module's resource file
        // JSONObject moduleResource = normalizeResource(loadModuleResource(moduleId, lang));
        // JSONObject moduleResource = loadModuleResource(moduleId, lang);
        String fileName = String.format("%s-%s", moduleId, lang);
        JSONObject moduleResource = loadResource(fileName);

        if (!defaultLocale.equals(lang))
        {
            // JSONObject baseResource = loadModuleResource(moduleId, defaultLocale);
            String baseFileName = String.format("%s-%s", moduleId, defaultLocale);
            JSONObject baseResource = loadResource(baseFileName);
            moduleResource = mergeResources(baseResource, moduleResource);
        }

        // If module is core, them inject information about menus that are stored in configuration file
        if (loadMenu && MODULE_CORE_ID.equals(moduleId))
        {
            JSONObject menusInfo = loadMenusResources(lang);
            JSONObject coreModuleResource = mergeResources(moduleResource, menusInfo);
            moduleResource = coreModuleResource;
        }

        // TODO: ne treba da se loada od custom, toa kje bide sega po novo vo runtime i treba da si se update-a vo
        // labelite vo memorija i od tamu da si se povleche
        // 2. Load custom resource file
        // JSONObject customResource = loadCustomResource(moduleId, lang);

        // 3. Merge Module's and custom resources
        // JSONObject adminResource = mergeAdminResouces(moduleResource, customResource);

        return convertToObjectForAdminResources(moduleResource);
    }

    /**
     * Return settings
     *
     * @param createIfAbsent
     * @return
     */
    public JSONObject getSettings(boolean createIfAbsent) throws AcmLabelManagementException
    {
        // JSONObject settings = loadResource(settingsFileLocation);
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
     * @throws AcmLabelManagementException
     */
    public void refresh(List<String> modules, List<String> langs) throws AcmLabelManagementException
    {
        for (String lang : langs)
        {
            for (String module : modules)
            {
                updateResource(module, lang);
            }
        }
    }

    /**
     * Remove module's custom resource and update it.
     *
     * @param moduleId
     * @param lang
     */
    public void resetModule(String moduleId, String lang) throws AcmLabelManagementException
    {
        String fileName = String.format(customResourcesLocation + customResourceFile, moduleId, lang);
        try
        {
            File resourceFile = new File(fileName);
            FileUtils.deleteQuietly(resourceFile);
            updateResource(moduleId, lang);
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
     * @param moduleId
     * @param lang
     * @return
     * @throws AcmLabelManagementException
     */
    public JSONObject updateResource(String moduleId, String lang) throws AcmLabelManagementException
    {

        // 1. Load module's resource file
        JSONObject moduleResource = loadModuleResource(moduleId, lang);

        if (!defaultLocale.equals(lang))
        {
            JSONObject baseResource = loadModuleResource(moduleId, defaultLocale);
            moduleResource = mergeResources(baseResource, moduleResource);
        }

        // If module is core, them inject information about menus that are stored in configuration file
        if (MODULE_CORE_ID.equals(moduleId))
        {
            JSONObject menusInfo = loadMenusResources(lang);
            JSONObject coreModuleResource = mergeResources(moduleResource, menusInfo);
            moduleResource = coreModuleResource;
        }

        // 2. Load custom resource file
        JSONObject customResource = loadCustomResource(moduleId, lang);

        // 3. Merge Module's and custom resources
        JSONObject resource = extendResources(moduleResource, customResource);

        // 4. Save updated resource
        String fileName = String.format(resourcesLocation + resourceFile, moduleId, lang);
        try
        {
            for (String moduleName : labelsConfiguration.getModulesNames())
            {
                if (fileName.contains(moduleName))
                {
                    File resourceFile = new File(fileName);
                    FileUtils.writeStringToFile(resourceFile, resource.toString(), "UTF-8");
                    break;
                }
            }
            // configurationPropertyService.updateProperties(map);
        }
        catch (Exception e)
        {
            String msg = String.format("Can't write resource into the file %s", fileName);
            log.error(msg);
            throw new AcmLabelManagementException(msg);
        }
        return resource;
    }

    /**
     * Update custom resource values
     *
     * @param moduleId
     * @param lang
     * @param newValue
     * @return
     * @throws AcmLabelManagementException
     */
    public JSONObject updateCustomResource(String moduleId, String lang, JSONObject newValue) throws AcmLabelManagementException
    {
        if (newValue == null || !(newValue.has("id") && newValue.has("description") && newValue.has("value")))
        {
            throw new AcmLabelManagementException("Wrong format of custom resource");
        }
        else
        {
            String fileName = String.format(customResourcesLocation + customResourceFile, moduleId, lang);
            try
            {
                JSONObject customRes = loadCustomResource(moduleId, lang);
                if (customRes == null)
                {
                    customRes = new JSONObject();
                }
                String id = newValue.getString("id");
                customRes.put(id, newValue);
                File file = new File(fileName);
                FileUtils.writeStringToFile(file, customRes.toString(4), "UTF-8");

                // Update also resource file
                updateResource(moduleId, lang);

                return customRes;
            }
            catch (Exception e)
            {
                String msg = String.format("Can't write resource into the file %s", fileName);
                log.error(msg);
                throw new AcmLabelManagementException(msg);
            }
        }
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
     * Convert resource json properties to next format: "prop1.prop2.prop3": "value"
     *
     * @param resource
     * @return
     */
    private JSONObject normalizeResource(JSONObject resource)
    {
        JSONObject res = new JSONObject();
        if (resource != null)
        {
            normalizeNode(resource, res, "");
        }
        return res;
    }

    /**
     * Recursive method to produce normalized resource string
     *
     * @param source
     * @param normalized
     * @param currentPath
     */
    private void normalizeNode(JSONObject source, JSONObject normalized, String currentPath)
    {
        Iterator<String> keys = source.keys();
        while (keys.hasNext())
        {
            String key = keys.next();
            String propertyPath = (currentPath.isEmpty()) ? key : currentPath + '.' + key;
            Object jsonValue = source.get(key);
            if (jsonValue instanceof JSONObject)
            {
                normalizeNode((JSONObject) jsonValue, normalized, propertyPath);
            }
            else if (jsonValue instanceof String)
            {
                normalized.put(propertyPath, jsonValue);
            }
        }
    }

    /**
     * Extend normalized module and original custom resources to produce new resource
     *
     * @param moduleRes
     * @param customRes
     * @return
     */
    private JSONObject extendResources(JSONObject moduleRes, JSONObject customRes)
    {
        JSONObject res = new JSONObject();
        if (moduleRes != null)
        {
            Iterator<String> keys = moduleRes.keys();
            while (keys.hasNext())
            {
                String key = keys.next();
                String value = moduleRes.getString(key);
                res.put(key, value);
            }
        }

        if (customRes != null)
        {
            Iterator<String> keys = customRes.keys();
            while (keys.hasNext())
            {
                String key = keys.next();
                JSONObject customNode = customRes.getJSONObject(key);
                if (customNode.has("value"))
                {
                    String value = customNode.getString("value");
                    res.put(key, value);
                }
            }
        }

        return res;
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
     * Merge normalized module and original (not normalized) custom resources to produce admin resource
     *
     * @param moduleRes
     * @return
     */
    private JSONArray convertToObjectForAdminResources(JSONObject moduleRes)
    {
        JSONArray jsonResourceArray = new JSONArray();
        // JSONObject adminRes = new JSONObject();
        if (moduleRes != null)
        {
            Iterator<String> keys = moduleRes.keys();
            while (keys.hasNext())
            {
                String key = keys.next();
                String value = moduleRes.getString(key);
                String defaultValue = value;
                String description = "";

                JSONObject node = new JSONObject();
                node.put("id", key);
                node.put("value", value);
                node.put("description", description);
                node.put("defaultValue", defaultValue);

                jsonResourceArray.put(node);
            }
        }

        // if (customRes != null)
        // {
        // Iterator<String> keys = customRes.keys();
        // while (keys.hasNext())
        // {
        // String key = keys.next();
        //
        // if (customRes.has(key))
        // {
        // JSONObject customNode = customRes.getJSONObject(key);
        // String value = customNode.has("value") ? customNode.getString("value") : null;
        // String description = customNode.has("description") ? customNode.getString("description") : null;
        //
        // String defaultValue = null;
        // if (adminRes.has(key))
        // {
        // JSONObject node = adminRes.getJSONObject(key);
        // defaultValue = node.has("defaultValue") ? node.getString("defaultValue") : null;
        // }
        //
        // JSONObject newNode = new JSONObject();
        // newNode.put("value", value);
        // newNode.put("description", description);
        // newNode.put("defaultValue", defaultValue);
        // adminRes.put(key, newNode);
        // }
        // }
        // }

        return jsonResourceArray;
    }

    /**
     * Load custom resource
     *
     * @param moduleId
     * @param lang
     * @return
     */
    private JSONObject loadCustomResource(String moduleId, String lang) throws AcmLabelManagementException
    {
        String fileName = String.format(customResourcesLocation + customResourceFile, moduleId, lang);
        // return loadResource(fileName);

        File file = FileUtils.getFile(fileName);
        try
        {
            String resource = FileUtils.readFileToString(file, "UTF-8");
            return new JSONObject(resource);
        }
        catch (Exception e)
        {
            log.warn(String.format("Can't read resource file %s", fileName));
            return null;
        }

    }

    /**
     * Load module's resource
     *
     * @param moduleId
     * @param lang
     * @return
     */
    private JSONObject loadModuleResource(String moduleId, String lang) throws AcmLabelManagementException
    {
        // TODO: izbrishi ja funkcijava, direktno na loadResource
        // String fileName = String.format(moduleResourcesLocation, moduleId, lang);
        String fileName = String.format("%s-%s", moduleId, lang);
        return loadResource(fileName);
    }

    /**
     * Load JSON resource file
     *
     * @param fileName
     * @return
     * @throws AcmLabelManagementException
     */
    private JSONObject loadResource(String fileName) throws AcmLabelManagementException
    {
        try
        {
            // File file = FileUtils.getFile(fileName);
            // String resource = FileUtils.readFileToString(file, "UTF-8");
            // return new JSONObject(resource);
            // JSONObject jsonObject = new JSONObject();
            // jsonObject.
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

    /**
     * Load information about all modules menus
     *
     * @return
     * @throws AcmLabelManagementException
     */
    private JSONObject loadMenusResources(String lang) throws AcmLabelManagementException
    {
        // 1. Get list of modules
        List<String> modulesNames = labelsConfiguration.getModulesNames();
        JSONObject modulesMenus = new JSONObject();

        // 2. Load modules configuration file and retrieve information about menus
        for (String moduleName : modulesNames)
        {
            String configFileName = String.format(moduleConfigLocation, moduleName);
            JSONObject configResource;// = loadResource(configFileName);
            try
            {
                File file = FileUtils.getFile(configFileName);
                String resource = FileUtils.readFileToString(file, "UTF-8");
                configResource = new JSONObject(resource);
            }
            catch (Exception e)
            {
                log.warn(String.format("Can't read resource file %s", configFileName));
                return null;
            }

            if (configResource != null && configResource.has("menus"))
            {
                JSONArray moduleResourceArray = getAdminResource(moduleName, lang, false);

                JSONArray menus = configResource.getJSONArray("menus");
                for (int i = 0; i < menus.length(); i++)
                {
                    JSONObject menuInfo = menus.getJSONObject(i);
                    // 3. Combine menus information into the one object
                    if (menuInfo.has("menuId") && menuInfo.has("menuItemTitle") && menuInfo.has("menuItemURL"))
                    {
                        String key = MODULE_CORE_ID + ".menus." + menuInfo.getString("menuId") + "." + menuInfo.getString("menuItemURL");
                        String menuItemTitle = (String) menuInfo.get("menuItemTitle");
                        for (int j = 0; j < moduleResourceArray.length(); j++)
                        {
                            if (((JSONObject) moduleResourceArray.get(j)).has(menuItemTitle))
                            {
                                try
                                {
                                    JSONObject titleTranslatedObj = (JSONObject) moduleResourceArray.get(j);
                                    menuItemTitle = (String) titleTranslatedObj.get("value");
                                }
                                catch (Exception e)
                                {
                                }
                            }
                        }

                        modulesMenus.put(key, menuItemTitle);
                    }
                }
            }
        }

        return modulesMenus;
    }

    public void setCustomResourcesLocation(String customResourcesLocation)
    {
        this.customResourcesLocation = customResourcesLocation;
    }

    public void setCustomResourceFile(String customResourceFile)
    {
        this.customResourceFile = customResourceFile;
    }

    public void setModuleConfigLocation(String moduleConfigLocation)
    {
        this.moduleConfigLocation = moduleConfigLocation;
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
