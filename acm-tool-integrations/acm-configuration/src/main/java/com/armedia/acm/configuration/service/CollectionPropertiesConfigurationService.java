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
