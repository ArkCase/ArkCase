package com.armedia.acm.services.config.lookups.model;

/*-
 * #%L
 * ACM Service: Config
 * %%
 * Copyright (C) 2014 - 2020 ArkCase LLC
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Mario Gjurcheski 7/10/2020
 */
public class AcmLookupTransformer
{
    private List<Map<String, Object>> transformedEntries;

    public AcmLookupTransformer(List<Map<String, Object>> transformedEntries)
    {
        this.transformedEntries = transformedEntries;
    }

    public static Map<String, Object> transformIntoInverseLookupEntry(String standardEntryKey, Map<String, Object> entry)
    {

        Map<String, Object> updatedEntry = new HashMap<>();

        updatedEntry.put("key", standardEntryKey);
        updatedEntry.put("inverseValue", entry.get("inverseValue"));
        updatedEntry.put("inverseKey", entry.get("inverseKey"));
        updatedEntry.put("value", entry.get("value"));
        updatedEntry.put("readonly", entry.get("readonly"));
        updatedEntry.put("description", entry.get("description"));

        return updatedEntry;

    }

    public static Map<String, Object> transformIntoStandardLookupEntry(String standardEntryKey, Map<String, Object> entry)
    {

        Map<String, Object> updatedEntry = new HashMap<>();

        updatedEntry.put("key", standardEntryKey);
        updatedEntry.put("value", entry.get("value"));
        updatedEntry.put("readonly", entry.get("readonly"));
        updatedEntry.put("primary", entry.get("primary"));
        updatedEntry.put("description", entry.get("description"));

        return updatedEntry;

    }

    public static Map<String, Object> transformIntoNestedLookupEntry(String standardEntryKey, Map<String, Object> entry)
    {

        Map<String, Object> updatedEntry = new HashMap<>();
        Map<String, Object> sublookups = (Map<String, Object>) entry.get("subLookup");
        List<Map<String, Object>> sublookup = new ArrayList<>();

        if (sublookups != null)
        {
            for (String subLookupKey : sublookups.keySet())
            {
                Map<String, Object> values = (Map<String, Object>) sublookups.get(subLookupKey);

                Map<String, Object> subLookupEntry = new HashMap<>();
                subLookupEntry.put("key", subLookupKey);
                subLookupEntry.put("value", values.get("value"));
                subLookupEntry.put("description", values.get("description"));
                sublookup.add(subLookupEntry);
            }
            updatedEntry.put("subLookup", sublookup);
        }
        updatedEntry.put("key", standardEntryKey);
        updatedEntry.put("value", entry.get("value"));
        updatedEntry.put("readonly", entry.get("readonly"));
        updatedEntry.put("description", entry.get("description"));

        return updatedEntry;
    }

    public List<Map<String, Object>> getTransformedEntries()
    {
        return transformedEntries;
    }

    public void setTransformedEntries(List<Map<String, Object>> transformedEntries)
    {
        this.transformedEntries = transformedEntries;
    }
}
