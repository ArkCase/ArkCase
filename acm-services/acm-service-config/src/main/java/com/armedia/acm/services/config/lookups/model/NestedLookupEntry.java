package com.armedia.acm.services.config.lookups.model;

/**
 * Created by bojan.milenkoski on 25.8.2017
 */
public class NestedLookupEntry
{
    private String key;
    private String value;
    private StandardLookup subLookup;

    public NestedLookupEntry()
    {
    }

    public NestedLookupEntry(String key, String value, StandardLookup subLookup)
    {
        this.key = key;
        this.value = value;
        this.subLookup = subLookup;
    }

    public StandardLookup getSubLookup()
    {
        return subLookup;
    }

    public void setSubLookup(StandardLookup subLookup)
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
