package com.armedia.acm.services.config.lookups.model;

/**
 * Created by bojan.milenkoski on 24.8.2017
 */
public class StandardLookupEntry
{
    private String key;
    private String value;

    public StandardLookupEntry()
    {
    }

    public StandardLookupEntry(String key, String value)
    {
        this.key = key;
        this.value = value;
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
