package com.armedia.acm.services.config.lookups.model;

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
