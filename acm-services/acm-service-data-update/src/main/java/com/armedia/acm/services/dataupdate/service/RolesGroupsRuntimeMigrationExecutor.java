package com.armedia.acm.services.dataupdate.service;

/*-
 * #%L
 * ACM Service: Data Update Service
 * %%
 * Copyright (C) 2014 - 2019 ArkCase LLC
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

import com.armedia.acm.configuration.core.ConfigurationContainer;
import com.armedia.acm.configuration.service.CollectionPropertiesConfigurationService;
import com.armedia.acm.configuration.service.ConfigurationPropertyService;
import com.armedia.acm.configuration.util.MergeFlags;
import com.armedia.acm.objectonverter.json.JSONUnmarshaller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Mario Gjurcheski
 */
public class RolesGroupsRuntimeMigrationExecutor implements AcmDataUpdateExecutor
{

    CollectionPropertiesConfigurationService collectionPropertiesConfigurationService;
    ConfigurationPropertyService configurationPropertyService;
    ConfigurationContainer configurationContainer;
    JSONUnmarshaller jsonUnmarshaller;

    private static final Logger log = LogManager.getLogger(RolesGroupsRuntimeMigrationExecutor.class);

    @Override
    public String getUpdateId()
    {
        return "roles-groups-runtime-migration-v1";
    }

    @Override
    public void execute()
    {
        List<String> propertiesForMigration = new LinkedList(
                Arrays.asList("application.roles", "application.privileges", "application.rolesToGroups", "application.rolesToPrivileges"));

        Map<String, Object> configMap = configurationContainer.getWithoutRuntimeConfigurationMap();

        Map<String, Object> runtimeMap = configurationContainer.getRuntimeConfigurationMap();

        // properties for merging
        for (Map.Entry<String, Object> runtimeEntry : runtimeMap.entrySet())
        {
            String key = runtimeEntry.getKey();
            String value = runtimeEntry.getValue().toString();

            // for instance ROLE_ADMINISTRATOR: value,value
            if (value.startsWith("{") && value.endsWith("}"))
            {
                // application.rolesToPrivileges
                if (propertiesForMigration.contains(key))
                {
                    // ROLE_ADMINISTRATOR: value,value,value
                    // ROLE_ADMINISTRATOR2: value,value,value
                    Map<String, Object> unmarshalledValues = jsonUnmarshaller.unmarshall(new JSONObject(value).toString(), Map.class);

                    for (Map.Entry<String, Object> mapEntry : unmarshalledValues.entrySet())
                    {

                        String combinedKey = key + "." + mapEntry.getKey();
                        // value1, value2, value3
                        List<String> runtimeList = new LinkedList<>(Arrays.asList(mapEntry.getValue().toString().split(",")));

                        removePropertiesIfNotExistInRuntime(configMap, key, mapEntry, combinedKey, runtimeList);
                        mergePropertyIfNotExistInRuntime(configMap, mapEntry.getKey(), combinedKey, key, runtimeList);

                    }

                }
            }

        }

    }

    private void removePropertiesIfNotExistInRuntime(Map<String, Object> configMap, String key, Map.Entry<String, Object> mapEntry,
            String combinedKey, List<String> runtimeList)
    {
        for (Map.Entry<String, Object> configMapEntry : configMap.entrySet())
        {

            String configMapEntryValue = configMapEntry.getValue().toString();
            if (configMapEntry.getKey().startsWith(combinedKey))
            {
                if (!runtimeList.contains(configMapEntryValue))
                {
                    List<Object> list = new ArrayList<>(Arrays.asList(configMapEntryValue));
                    Map<String, Object> config = collectionPropertiesConfigurationService.updateMapProperty(key, mapEntry.getKey(),
                            list,
                            MergeFlags.REMOVE);
                    configurationPropertyService.updateProperties(config);

                    log.info("property key %s with property value %s is updated in runtime", combinedKey,
                            configMapEntryValue);
                }
            }
        }
    }

    private void mergePropertyIfNotExistInRuntime(Map<String, Object> configMap, Object mapEntry, String combinedKey, String key,
            List<String> runtimeList)
    {
        // properties for merging
        for (String propertyValue : runtimeList)
        {
            Map<Object, Object> collectMerging = configMap.entrySet().stream()
                    .filter(x -> x.getKey().startsWith(combinedKey) && x.getValue().toString().startsWith(propertyValue))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            // if the property doesn't exist in combinedMap put in runtime
            if (collectMerging.size() == 0)
            {
                List<Object> list = new ArrayList<>(Arrays.asList(propertyValue));
                Map<String, Object> config = collectionPropertiesConfigurationService.updateMapProperty(key, mapEntry.toString(), list,
                        MergeFlags.MERGE);
                configurationPropertyService.updateProperties(config);

                log.info("property key %s with property value %s is updated in runtime", combinedKey, propertyValue);
            }
        }
    }

    public void setCollectionPropertiesConfigurationService(
            CollectionPropertiesConfigurationService collectionPropertiesConfigurationService)
    {
        this.collectionPropertiesConfigurationService = collectionPropertiesConfigurationService;
    }

    public void setConfigurationContainer(ConfigurationContainer configurationContainer)
    {
        this.configurationContainer = configurationContainer;
    }

    public void setJsonUnmarshaller(JSONUnmarshaller jsonUnmarshaller)
    {
        this.jsonUnmarshaller = jsonUnmarshaller;
    }

    public void setConfigurationPropertyService(ConfigurationPropertyService configurationPropertyService)
    {
        this.configurationPropertyService = configurationPropertyService;
    }
}
