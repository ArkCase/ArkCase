package com.armedia.acm.configuration.service;

import com.armedia.acm.configuration.util.MergeFlags;

import java.util.List;
import java.util.Map;

public interface CollectionPropertiesConfigurationService
{

    /**
     * Updates map properties in runtime which contains list as value
     *
     * @param mapPropertyKey
     * @param mapEntryKey
     * @param propertyValues
     * @param action
     *
     */
    Map<String, Object> updateMapProperty(String mapPropertyKey, String mapEntryKey,
            List<Object> propertyValues, MergeFlags action);

    /**
     * Converts yaml format properties to map and list respectively.
     *
     * @param mapPropertyKey
     * @param mapEntryKeyRemove
     * @param entryKeyMerge
     * @param propertyValuesForRemove
     * @param propertyValuesForMerging
     */
    Map<String, Object> updateAndRemoveMapProperties(String mapPropertyKey, String mapEntryKeyRemove, String entryKeyMerge,
            List<Object> propertyValuesForRemove, List<Object> propertyValuesForMerging);

    /**
     * Updates list property in runtime which contains list as value
     *
     * @param mapPropertyKey
     * @param mapEntryKey
     * @param propertyValues
     * @param action
     *
     */
    Map<String, Object> updateListEntry(String mapPropertyKey, String mapEntryKey,
            List<Object> propertyValues,
            MergeFlags action);

    /**
     * Converts yaml format properties to map and list respectively.
     *
     * @param mapPropertyKey
     * @param mapEntryKey
     * @param propertyValuesForRemove
     * @param propertyValuesForMerging
     */
    Map<String, Object> updateAndRemoveListProperties(String mapPropertyKey, String mapEntryKey,
            List<Object> propertyValuesForRemove, List<Object> propertyValuesForMerging);


    /**
     * Converts yaml format properties to map and list respectively.
     *
     * @param mapPropertyKey
     * @param updatedMap
     * @param convertWithMap
     *
     */
    Map<String, Object> filterAndConvertProperties(String mapPropertyKey, Map<String, Object> updatedMap, boolean convertWithMap);

}
