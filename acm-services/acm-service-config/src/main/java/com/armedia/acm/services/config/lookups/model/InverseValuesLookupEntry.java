package com.armedia.acm.services.config.lookups.model;

/**
 * Created by bojan.milenkoski on 24.8.2017
 */
public class InverseValuesLookupEntry
{
    private String key;
    private String value;
    private String inverseKey;
    private String inverseValue;

    public InverseValuesLookupEntry()
    {
    }

    public InverseValuesLookupEntry(String key, String value, String inverseKey, String inverseValue)
    {
        super();
        this.key = key;
        this.value = value;
        this.inverseKey = inverseKey;
        this.inverseValue = inverseValue;
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

    public String getInverseKey()
    {
        return inverseKey;
    }

    public void setInverseKey(String inverseKey)
    {
        this.inverseKey = inverseKey;
    }

    public String getInverseValue()
    {
        return inverseValue;
    }

    public void setInverseValue(String inverseValue)
    {
        this.inverseValue = inverseValue;
    }
}
