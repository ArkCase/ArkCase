package com.armedia.acm.services.config.lookups.model;

import java.util.List;

/**
 * Created by bojan.milenkoski on 24.8.2017
 */
abstract public class AcmLookup<Entry>
{
    protected String name;

    protected List<Entry> entries;

    abstract public LookupValidationResult validate();

    public List<Entry> getEntries()
    {
        return entries;
    }

    public void setEntries(List<Entry> entries)
    {
        this.entries = entries;
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
