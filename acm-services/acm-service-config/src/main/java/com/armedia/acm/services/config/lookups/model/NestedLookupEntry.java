package com.armedia.acm.services.config.lookups.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bojan.milenkoski on 25.8.2017
 */
public class NestedLookupEntry
{
    private String key;
    private String value;
    private List<StandardLookupEntry> subLookup = new ArrayList<>();

    public NestedLookupEntry()
    {
    }

    public NestedLookupEntry(String key, String value, List<StandardLookupEntry> subLookup)
    {
        this.key = key;
        this.value = value;
        this.subLookup = subLookup;
    }

    public List<StandardLookupEntry> getSubLookup()
    {
        return subLookup;
    }

    public void setSubLookup(List<StandardLookupEntry> subLookup)
    {
        this.subLookup = subLookup;
    }

    public String getKey()
    {
        return key;
    }

    public void setKey(String key)
    {
        this.key = key;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }
}
