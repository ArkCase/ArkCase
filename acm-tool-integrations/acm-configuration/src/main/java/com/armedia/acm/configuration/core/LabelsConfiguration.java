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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jmx.export.annotation.ManagedResource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@ManagedResource(objectName = "configuration:name=labels-service,type=com.armedia.acm.configuration.ConfigurationService,artifactId=labels-service")
public class LabelsConfiguration implements ConfigurationFacade
{
    private static final Logger log = LogManager.getLogger(LabelsConfiguration.class);

    private final static String defaultLocale = "en";
    private volatile static LabelsConfiguration INSTANCE;

    private Map<String, Object> labelsMap = new HashMap<>();
    private Map<String, Object> labelsDefaultMap = new HashMap<>();

    @Autowired
    private ConfigurationServiceBootClient configurationServiceBootClient;

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
        List<String> modulesNames = configurationServiceBootClient.getModulesNames();

        log.info("Loading labels from config server with language: {}", defaultLocale);
        modulesNames.parallelStream().forEach(labelsModule -> {
            String key = String.format("%s-%s", labelsModule, defaultLocale);
            log.trace("Loading {} labels", labelsModule);
            labelsMap.put(key, this.configurationServiceBootClient.loadConfiguration(key, null));
            labelsDefaultMap.put(key, this.configurationServiceBootClient.loadDefaultConfiguration(key, null));
            log.trace("Labels {} loaded", labelsModule);
        });
        log.info("Finished loading labels with language: {}", defaultLocale);
    }

    public void includeOtherLanguageInLabelsMap(String lang)
    {
        List<String> modulesNames = configurationServiceBootClient.getModulesNames();
        modulesNames.parallelStream().forEach(labelsModule -> {
            String key = String.format("%s-%s", labelsModule, lang);
            labelsMap.put(key, this.configurationServiceBootClient.loadLangConfiguration(key,
                    (Map<String, Object>) labelsMap.get(labelsModule + "-" + defaultLocale), null));
            labelsDefaultMap.put(key, this.configurationServiceBootClient.loadDefaultConfiguration(key, null));
        });
    }

    /**
     * Return list of modules configuration
     *
     * @return
     */
    public List<ModuleConfig> getModules()
    {
        List<ModuleConfig> modules = new ArrayList<>();

        List<String> modulesNames = configurationServiceBootClient.getModulesNames();

        for (String moduleName : modulesNames)
        {
            ModuleConfig module = new ModuleConfig();
            module.setId(moduleName);
            module.setName(moduleName);
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

}
