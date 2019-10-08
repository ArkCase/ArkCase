package com.armedia.acm.configuration.core;

/*-
 * #%L
 * ACM Service: Configuration Library
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

import com.armedia.acm.configuration.api.ConfigurationFacade;
import com.armedia.acm.configuration.client.ConfigurationServiceBootClient;
import com.armedia.acm.configuration.model.ModuleConfig;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jmx.export.annotation.ManagedResource;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@ManagedResource(objectName = "configuration:name=labels-service,type=com.armedia.acm.configuration.ConfigurationService,artifactId=labels-service")
public class LabelsConfiguration implements ConfigurationFacade
{
    private static final Logger log = LogManager.getLogger(LabelsConfiguration.class);

    private final static String CONFIGURATION_SERVER_URL = "configuration.server.url";
    private final static String MODULE_CORE_ID = "core";
    private final static String defaultLocale = "en";
    private volatile static LabelsConfiguration INSTANCE;

    private Map<String, Object> labelsMap = new HashMap<>();
    private Map<String, Object> labelsDefaultMap = new HashMap<>();

    private String modulesLocation = System.getProperty("user.home") + "/.arkcase/custom/modules/";
    private String moduleConfigLocation = System.getProperty("user.home") + "/.arkcase/custom/modules/%s/module_config/config.json";

    @Autowired
    private ConfigurationServiceBootClient configurationServiceBootClient;

    @Autowired
    private Environment environment;

    @Bean
    public static LabelsConfiguration labelsConfiguration()
    {
        if (INSTANCE == null)
        {
            initialize();
        }
        return INSTANCE;
    }

    private static synchronized void initialize()
    {
        if (INSTANCE == null)
        {
            INSTANCE = new LabelsConfiguration();
        }
    }

    private synchronized void initializeLabelsMap()
    {
        String serverUrl = this.environment.getProperty(CONFIGURATION_SERVER_URL);
        List<String> allModules = getModulesNames();
        for (String labelsModule : allModules)
        {
            String key = String.format("%s-%s", labelsModule, defaultLocale);
            labelsMap.put(key, this.configurationServiceBootClient.loadConfiguration(serverUrl, key));
            labelsDefaultMap.put(key, this.configurationServiceBootClient.loadDefaultConfiguration(serverUrl, key));
        }
        Map<String, Object> menusCore = loadMenusResources(defaultLocale, labelsMap);
        ((HashMap) labelsMap.get(MODULE_CORE_ID + "-" + defaultLocale)).putAll(menusCore);
    }

    private Map<String, Object> loadMenusResources(String lang, Map<String, Object> labelsMap)
    {
        // 1. Get list of modules
        List<String> modulesNames = getModulesNames();
        Map<String, Object> modulesMenus = new HashMap<>();

        // 2. Load modules configuration file and retrieve information about menus
        for (String moduleName : modulesNames)
        {
            String configFileName = String.format(moduleConfigLocation, moduleName);
            JSONObject configResource = null;
            try
            {
                File file = FileUtils.getFile(configFileName);
                String resource = FileUtils.readFileToString(file, "UTF-8");
                configResource = new JSONObject(resource);
            }
            catch (Exception e)
            {
                log.warn(String.format("Can't read resource file %s", configFileName));
            }

            if (configResource != null && configResource.has("menus"))
            {
                JSONArray menus = configResource.getJSONArray("menus");
                for (int i = 0; i < menus.length(); i++)
                {
                    JSONObject menuInfo = menus.getJSONObject(i);
                    // 3. Combine menus information into the one object
                    if (menuInfo.has("menuId") && menuInfo.has("menuItemTitle") && menuInfo.has("menuItemURL"))
                    {
                        String key = MODULE_CORE_ID + ".menus." + menuInfo.getString("menuId") + "." + menuInfo.getString("menuItemURL");
                        String menuItemTitle = (String) menuInfo.get("menuItemTitle");

                        if (((HashMap) labelsMap.get(configResource.getString("id") + "-" + lang)).containsKey(menuItemTitle))
                        {
                            menuItemTitle = ((HashMap) labelsMap.get(configResource.getString("id") + "-" + lang))
                                    .get(menuItemTitle).toString();
                        }

                        modulesMenus.put(key, menuItemTitle);
                    }
                }
            }
        }

        return modulesMenus;
    }

    /**
     * Return modules
     */
    public List<String> getModulesNames()
    {
        List<String> modulesNames = new ArrayList<>();
        List<ModuleConfig> modules = getModules();
        for (ModuleConfig moduleIter : modules)
        {
            modulesNames.add(moduleIter.getId());
        }

        return modulesNames;
    }

    /**
     * Return list of modules configuration
     *
     * @return
     */
    public List<ModuleConfig> getModules()
    {
        File modulesDir = new File(modulesLocation);

        File[] dirs = modulesDir.listFiles(File::isDirectory);

        List<ModuleConfig> modules = new ArrayList<>();

        for (File dirIter : dirs)
        {
            ModuleConfig module = new ModuleConfig();
            module.setId(dirIter.getName());
            module.setName(dirIter.getName());
            modules.add(module);
        }

        return modules;
    }

    @Override
    public Object getProperty(String name)
    {
        return this.labelsMap.get(name);
    }

    public Object getDefaultProperty(String name)
    {
        return this.labelsDefaultMap.get(name);
    }

    @Override
    public Object getProperty(String channel, String stage, String instance, String name)
    {
        String propertyName = String.format("%s.%s.%s.%s", channel, stage, name, instance);
        return getProperty(propertyName);
    }

    @Override
    public void refresh()
    {
        initializeLabelsMap();
    }

    public Map<String, Object> getLabelsMap()
    {
        return this.labelsMap;
    }

    public void setLabelsMap(Map<String, Object> labelsMap)
    {
        this.labelsMap = labelsMap;
    }

    public String getModulesLocation()
    {
        return modulesLocation;
    }

    public void setModulesLocation(String modulesLocation)
    {
        this.modulesLocation = modulesLocation;
    }

    public Environment getEnvironment()
    {
        return environment;
    }

    public void setEnvironment(Environment environment)
    {
        this.environment = environment;
    }
}
