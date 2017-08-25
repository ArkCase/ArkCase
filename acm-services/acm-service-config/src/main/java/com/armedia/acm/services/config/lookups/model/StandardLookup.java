package com.armedia.acm.services.config.lookups.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Created by bojan.milenkoski on 24.8.2017
 */
public class StandardLookup extends AcmLookup<StandardLookupEntry>
{
    @Override
    @JsonIgnore
    public LookupValidationResult validate()
    {
        // Check empty key or value
        for (StandardLookupEntry entry : entries)
        {
            if (entry.getKey() == null || entry.getKey().isEmpty())
            {
                return new LookupValidationResult(false, "Empty key found!");
            }
            if (entry.getValue() == null || entry.getValue().isEmpty())
            {
                return new LookupValidationResult(false, "Empty value found!");
            }
        }

        // Check duplicate keys or values
        for (int i = 0; i < entries.size(); i++)
        {
            for (int j = i + 1; j < entries.size(); j++)
            {
                if (entries.get(i).getKey().equals(entries.get(j).getKey()))
                {
                    return new LookupValidationResult(false, "Duplicate key found! [key : " + entries.get(i).getKey() + "]");
                }
                if (entries.get(i).getValue().equals(entries.get(j).getValue()))
                {
                    return new LookupValidationResult(false, "Duplicate value found! [values : " + entries.get(i).getValue() + "]");
                }
            }

        }

        return new LookupValidationResult(true, null);
    }
}
