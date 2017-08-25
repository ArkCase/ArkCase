package com.armedia.acm.services.config.lookups.model;

/**
 * Created by bojan.milenkoski on 24.8.2017
 */
public class LookupDefinition
{
    private LookupType lookupType;

    private String name;

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
}
