package com.armedia.acm.configuration.service;

/*-
 * #%L
 * ACM Tool Integrations: Configuration Library
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

import com.armedia.acm.configuration.annotations.MapValue;
import com.armedia.acm.configuration.core.ConfigurationContainer;
import com.armedia.acm.configuration.core.LookupsConfigurationContainer;
import com.armedia.acm.configuration.util.MergeFlags;
import com.armedia.acm.configuration.util.MergePropertiesUtil;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * *
 * Created by mario.gjurcheski on 09/30/2019.
 */
@Service("collectionPropertiesConfigurationService")
public class CollectionPropertiesConfigurationServiceImpl implements CollectionPropertiesConfigurationService
{

    @Autowired
    private ConfigurationContainer configurationContainer;

    @Autowired
    private LookupsConfigurationContainer lookupsConfigurationContainer;

    private static final Logger log = LogManager.getLogger(CollectionPropertiesConfigurationServiceImpl.class);

    @Override
    public Map<String, Object> updateMapProperty(String mapPropertyKey, String mapEntryKey,
            List<Object> propertyValues, MergeFlags action)
    {
        Map<String, Object> runtimeMapWithRootKey = new HashMap<>();

        Map<String, Object> configurationMap = configurationContainer.getRuntimeConfigurationMap();

        Map<String, Object> runtimeMap = filterPropertiesFromRuntimeConfiguration(mapPropertyKey, configurationMap);

        if (propertyValues != null)
        {
            mapEntryKey = MergeFlags.MERGE.getSymbol() + mapEntryKey;

            removeRuntimePropertiesIfExistsInInput(mapPropertyKey, mapEntryKey, propertyValues, runtimeMap);

            if (action.equals(MergeFlags.REMOVE))
            {
                propertyValues = addActionFlagToValue(propertyValues, action);
            }

            if (runtimeMap.isEmpty())
            {
                runtimeMap.put(mapEntryKey, propertyValues);
            }
            else
            {
                runtimeMap = combineMapPropertiesWithRuntimeIfExist(mapPropertyKey, mapEntryKey, propertyValues, runtimeMap, false);
            }

            runtimeMapWithRootKey.put(mapPropertyKey, runtimeMap);

            return runtimeMapWithRootKey;
        }

        return runtimeMapWithRootKey;
    }

    @Override
    public Map<String, Object> updateAndRemoveMapProperties(String mapPropertyKey, String entryKeyRemove, String entryKeyMerge,
            List<Object> propertyValuesForRemove,
            List<Object> propertyValuesForMerging)
    {
        Map<String, Object> runtimeMapWithRootKey = new HashMap<>();

        Map<String, Object> configurationMap = configurationContainer.getRuntimeConfigurationMap();

        Map<String, Object> runtimeMap = filterPropertiesFromRuntimeConfiguration(mapPropertyKey, configurationMap);

        if (propertyValuesForRemove != null && propertyValuesForMerging != null)
        {
            entryKeyRemove = MergeFlags.MERGE.getSymbol() + entryKeyRemove;
            entryKeyMerge = MergeFlags.MERGE.getSymbol() + entryKeyMerge;

            removeRuntimePropertiesIfExistsInInput(mapPropertyKey, entryKeyRemove, propertyValuesForRemove, runtimeMap);
            removeRuntimePropertiesIfExistsInInput(mapPropertyKey, entryKeyMerge, propertyValuesForMerging, runtimeMap);

            propertyValuesForRemove = addActionFlagToValue(propertyValuesForRemove, MergeFlags.REMOVE);

            if (runtimeMap.isEmpty())
            {
                runtimeMap.put(entryKeyRemove, propertyValuesForRemove);
                runtimeMap.put(entryKeyMerge, propertyValuesForMerging);
            }
            else
            {
                runtimeMap = combineMapPropertiesWithRuntimeIfExist(mapPropertyKey, entryKeyRemove, propertyValuesForRemove, runtimeMap,
                        false);
                runtimeMap.put(entryKeyMerge, propertyValuesForMerging);
            }

            runtimeMapWithRootKey.put(mapPropertyKey, runtimeMap);

            return runtimeMapWithRootKey;
        }
        return runtimeMapWithRootKey;
    }

    @Override
    public Map<String, Object> updateListEntry(String mapPropertyKey, String mapEntryKey,
            List<Object> propertyValues,
            MergeFlags action)
    {
        Map<String, Object> runtimeMapWithRootKey = new HashMap<>();

        Map<String, Object> configurationMap = configurationContainer.getRuntimeConfigurationMap();

        if (action.equals(MergeFlags.REMOVE))
        {
            propertyValues = addActionFlagToValue(propertyValues, action);
        }

        Map<String, Object> runtimeMap = filterPropertiesFromRuntimeConfiguration(mapPropertyKey + "[",
                configurationMap);

        removeRuntimePropertiesIfExistsInInput(mapPropertyKey, "", propertyValues, runtimeMap);

        if (runtimeMap.isEmpty())
        {
            runtimeMapWithRootKey.put(mapPropertyKey, propertyValues);
        }
        else
        {
            runtimeMap = combineMapPropertiesWithRuntimeIfExist(mapPropertyKey, mapPropertyKey, propertyValues, runtimeMap, true);
            runtimeMapWithRootKey.put(mapPropertyKey, runtimeMap.get(mapPropertyKey));
        }

        return runtimeMapWithRootKey;
    }


    @Override
    public Map<String, Object> addEmptyListCollection(String mapPropertyKey, String mapEntryKey, String action,
            Map<String, Object> runtimeMapWithRootKey)
    {
        Map<String, Object> configurationMap = configurationContainer.getRuntimeConfigurationMap();

        Map<String, Object> runtimeMap = filterPropertiesFromRuntimeConfiguration(mapPropertyKey, configurationMap);

        mapEntryKey = MergeFlags.MERGE.getSymbol() + mapEntryKey;

        removeRuntimePropertiesIfExistsInInput(mapPropertyKey, mapEntryKey, new ArrayList<>(), runtimeMap);

        if (runtimeMap.isEmpty())
        {
            List<String> emptyList = new LinkedList<>();
            emptyList.add("");
            runtimeMap.put(mapEntryKey, emptyList);
        }
        else
        {
            List<Object> emptyList = new LinkedList<>();
            emptyList.add("");
            runtimeMap = combineMapPropertiesWithRuntimeIfExist(mapPropertyKey, mapEntryKey, emptyList, runtimeMap, false);
        }

        runtimeMapWithRootKey.putAll(runtimeMap);

        return runtimeMapWithRootKey;
    }

    @Override
    public Map<String, Object> updateAndRemoveListProperties(String mapPropertyKey, String mapEntryKey,
            List<Object> propertyValuesForRemove, List<Object> propertyValuesForMerging)
    {
        Map<String, Object> runtimeMapWithRootKey = new HashMap<>();

        Map<String, Object> configurationMap = configurationContainer.getRuntimeConfigurationMap();

        propertyValuesForRemove = addActionFlagToValue(propertyValuesForRemove, MergeFlags.REMOVE);

        Map<String, Object> runtimeMap = filterPropertiesFromRuntimeConfiguration(mapPropertyKey + "[",
                configurationMap);

        removeRuntimePropertiesIfExistsInInput(mapPropertyKey, "", propertyValuesForRemove, runtimeMap);
        removeRuntimePropertiesIfExistsInInput(mapPropertyKey, "", propertyValuesForMerging, runtimeMap);

        if (runtimeMap.isEmpty())
        {
            propertyValuesForMerging.addAll(propertyValuesForRemove);
            runtimeMapWithRootKey.put(mapPropertyKey, propertyValuesForMerging);
        }
        else
        {
            runtimeMap = combineMapPropertiesWithRuntimeIfExist(mapPropertyKey, mapEntryKey, propertyValuesForRemove, runtimeMap,
                    false);
            runtimeMap = combineMapPropertiesWithRuntimeIfExist(mapPropertyKey, mapEntryKey, propertyValuesForMerging, runtimeMap,
                    false);

            runtimeMapWithRootKey.put(mapPropertyKey, runtimeMap.get(mapPropertyKey));
        }

        return runtimeMapWithRootKey;
    }

    @Override
    public Map<String, Object> deleteMapProperty(String mapPropertyKey, String mapEntryKey, String mapEntryPrefix)
    {
        Map<String, Object> configurationMap = configurationContainer.getConfigurationMap();

        Map<String, Object> filteredProperties = filterPropertiesFromRuntimeConfiguration(mapPropertyKey + "." + mapEntryKey,
                configurationMap);

        Map<String, Object> modifiedProperties = new HashMap<>();

        for (Map.Entry<String, Object> entry : filteredProperties.entrySet())
        {
            if (entry.getKey().startsWith(mapPropertyKey + "." + mapEntryKey))
            {
                modifiedProperties.put(mapEntryPrefix + mapEntryKey, "");
            }
            else
            {
                modifiedProperties.put(entry.getKey(), entry.getValue());
            }
        }

        return modifiedProperties;
    }

    @Override
    public Map<String, Object> getLookupConfiguration(String mapPropertyKey, String mapEntryKey)
    {
        Map<String, Object> configurationMap = lookupsConfigurationContainer.getRuntimeMap();

        Map<String, Object> filteredProperties = filterAndConvertProperties(mapPropertyKey + "." + mapEntryKey,
                configurationMap, false, true);

        return filteredProperties;
    }

    /**
     * Removes the old state of the properties from the runtime file if they exist.
     *
     * @param mapPropertyKey
     * @param mapEntryKey
     * @param propertyValues
     * @param runtimeMap
     *
     */
    private void removeRuntimePropertiesIfExistsInInput(String mapPropertyKey, String mapEntryKey, List<Object> propertyValues,
            Map<String, Object> runtimeMap)
    {
        if (propertyValues != null)
        {
            for (Object property : propertyValues)
            {
                for (Iterator<Map.Entry<String, Object>> it = runtimeMap.entrySet().iterator(); it.hasNext();)
                {
                    Map.Entry<String, Object> mergedEntry = it.next();

                    // when the map property key is also an entry key
                    if (mapEntryKey.equals(""))
                    {
                        removePropertyFromRuntimeMapIfMapEntryKeyIsSameAsMapPropertyKey(mapPropertyKey, property, it, mergedEntry);
                    }
                    else
                    {
                        if (mergedEntry.getKey().contains(mapPropertyKey + "." + mapEntryKey)
                                && mergedEntry.getValue().toString().endsWith(property.toString()))
                        {
                            it.remove();
                        }
                    }
                }
            }
        }
    }

    private void removePropertyFromRuntimeMapIfMapEntryKeyIsSameAsMapPropertyKey(String mapPropertyKey, Object property,
            Iterator<Map.Entry<String, Object>> it, Map.Entry<String, Object> mergedEntry)
    {
        if (mergedEntry.getKey().contains(mapPropertyKey)
                && property.toString().endsWith(mergedEntry.getValue().toString()))
        {
            it.remove();
        }
    }

    /**
     * Filter And convert properties in map or list respectively
     *
     * @param mapPropertyKey
     * @param updatedMap
     *
     * @return updatedMap
     *
     */
    @Override
    public Map<String, Object> filterAndConvertProperties(String mapPropertyKey, Map<String, Object> updatedMap, boolean convertWithMap,
            boolean convertFromTheRootKey)
    {
        // filter the nested proprty key to avoid map conversion
        Function<Map.Entry<String, Object>, Map.Entry<String, Object>> transform = entry -> {

            if (convertFromTheRootKey)
            {
                String withTheRootKey = entry.getKey().substring(entry.getKey().indexOf(".") + 1);
                return new AbstractMap.SimpleEntry<>(withTheRootKey, entry.getValue());
            }
            else
            {
                String withoutRootKey = MergePropertiesUtil.getLastKey(entry.getKey());
                return new AbstractMap.SimpleEntry<>(withoutRootKey, entry.getValue());
            }
        };

        // filter all the properties from configuration that contains the propertyKey from the annotation
        updatedMap = updatedMap.entrySet()
                .stream()
                .filter(s -> s.getKey().startsWith(mapPropertyKey))
                .map(transform)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return configurationContainer.convertMap(updatedMap, convertWithMap);
    }

    /**
     * Add "~" or "^" to the property value according to the action(update/remove)
     *
     * @param propertyValues
     * @param action
     *
     * @return newList
     *
     */
    private List<Object> addActionFlagToValue(List<Object> propertyValues, MergeFlags action)
    {
        if (action.equals(MergeFlags.REMOVE))
        {
            return propertyValues.stream()
                    .map(s -> MergeFlags.REMOVE.getSymbol() + s.toString().replace(MergeFlags.MERGE.getSymbol(), ""))
                    .collect(Collectors.toList());
        }
        else
        {
            return propertyValues.stream()
                    .map(s -> MergeFlags.MERGE.getSymbol() + s.toString().replace(MergeFlags.REMOVE.getSymbol(), ""))
                    .collect(Collectors.toList());
        }
    }

    /**
     * Filter properties from runtime based on the property rootKey
     *
     * @param mapPropertyKey
     * @param configurationMap
     *
     * @return filteredConfig
     *
     */
    private Map<String, Object> filterPropertiesFromRuntimeConfiguration(String mapPropertyKey,
            Map<String, Object> configurationMap)
    {
        return configurationMap.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(mapPropertyKey))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * Combine properties from input and from runtime in one map.
     *
     * @param mapPropertyKey
     * @param mapEntryKey
     * @param updatedMap
     * @param combineList
     *
     * @return filteredConfig
     *
     */
    private Map<String, Object> combineMapPropertiesWithRuntimeIfExist(String mapPropertyKey, String mapEntryKey,
            List<Object> propertyValues,
            Map<String, Object> updatedMap, boolean combineList)
    {
        updatedMap = filterAndConvertProperties(mapPropertyKey, updatedMap, true, false);

        return combineProperties(mapEntryKey, propertyValues, updatedMap, combineList);

    }

    private Map<String, Object> combineProperties(String mapEntryKey, List<Object> propertyValues, Map<String, Object> updatedMap,
            boolean combineList)
    {
        Map<String, Object> orderedMap = new HashMap<>();

        if (combineList)
        {
            List<Object> mergedValues = new LinkedList<>();

            for (Map.Entry<String, Object> entry : updatedMap.entrySet())
            {
                List<String> propValues = (List<String>) entry.getValue();
                orderedMap.put(mapEntryKey, propValues);
                mergedValues.addAll(propValues);
            }

            if (!mergedValues.isEmpty())
            {
                mergedValues.addAll(propertyValues);
                orderedMap.put(mapEntryKey, mergedValues);
            }
            else
            {
                orderedMap.put(mapEntryKey, propertyValues);
            }
        }
        else
        {
            // combine the old runtime values with the new ones
            for (Map.Entry<String, Object> entry : updatedMap.entrySet())
            {
                if (entry.getValue() instanceof List)
                {
                    List<String> propValues = (List<String>) entry.getValue();
                    List<String> listWithoutNulls = propValues.stream()
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());

                    orderedMap.put(entry.getKey(), listWithoutNulls);
                }
            }

            List mergedValues = (List) orderedMap.get(mapEntryKey);
            if (mergedValues != null)
            {
                if (mergedValues.get(0).equals(""))
                {
                    mergedValues.remove(0);
                }

                mergedValues.addAll(propertyValues);
                orderedMap.put(mapEntryKey, mergedValues);
            }
            else
            {
                orderedMap.put(mapEntryKey, propertyValues);
            }
        }

        return orderedMap;
    }

    @Override
    public List<String> getMapValueAnnotatedKeys(String packages)
    {
        List<String> annotatedProperties = new LinkedList<>();
        Object[] packagesToScan;

        if (!packages.contains(","))
        {
            packages = StringUtils.substringBeforeLast(packages, ".*");

            packagesToScan = new Object[] { packages };
        }
        else
        {
            List<String> packagesArray = Arrays.asList(packages.split(","));

            packagesToScan = packagesArray.stream()
                    .map(it -> StringUtils.substringBeforeLast(it, ".*"))
                    .toArray();
        }

        Set<Method> methodsAnnotatedWith = new Reflections(packagesToScan, new MethodAnnotationsScanner())
                .getMethodsAnnotatedWith(MapValue.class);

        for (final Method method : methodsAnnotatedWith)
        {
            if (method.isAnnotationPresent(MapValue.class))
            {
                MapValue annotatedInstance = method.getAnnotation(MapValue.class);
                annotatedProperties.add(annotatedInstance.value());
            }
        }

        return annotatedProperties;
    }

    public void setConfigurationContainer(ConfigurationContainer configurationContainer)
    {
        this.configurationContainer = configurationContainer;
    }

}
