package com.armedia.acm.services.config.lookups.model;

/**
 * Created by bojan.milenkoski on 24.8.2017
 */
public class LookupDefinition
{
    private LookupType lookupType;

    private String name;

    private String lookupEntriesAsJson;

    public LookupDefinition()
    {
    }

    public LookupDefinition(LookupType lookupType, String name, String lookupEntriesAsJson)
    {
        super();
        this.lookupType = lookupType;
        this.name = name;
        this.lookupEntriesAsJson = lookupEntriesAsJson;
    }

    public LookupType getLookupType()
    {
        return lookupType;
    }

    public void setLookupType(LookupType lookupType)
    {
        this.lookupType = lookupType;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getLookupEntriesAsJson()
    {
        return lookupEntriesAsJson;
    }

    public void setLookupEntriesAsJson(String lookupEntriesAsJson)
    {
        this.lookupEntriesAsJson = lookupEntriesAsJson;
    }
}
