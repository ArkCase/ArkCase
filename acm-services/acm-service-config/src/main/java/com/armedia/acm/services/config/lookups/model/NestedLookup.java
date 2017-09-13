package com.armedia.acm.services.config.lookups.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Objects;

/**
 * Created by bojan.milenkoski on 24.8.2017
 */
public class NestedLookup extends AcmLookup<NestedLookupEntry>
{
    @Override
    @JsonIgnore
    public LookupValidationResult validate()
    {
        // Check empty key or value
        for (NestedLookupEntry entry : entries)
        {
            if (entry.getKey() == null || entry.getKey().isEmpty())
            {
                return new LookupValidationResult(false, "Empty key found in '" + getName() + "' lookup!");
            }
            if (entry.getValue() == null || entry.getValue().isEmpty())
            {
                return new LookupValidationResult(false, "Empty value found in '" + getName() + "' lookup!");
            }
        }

        // Check duplicate keys or values
        for (int i = 0; i < entries.size(); i++)
        {
            for (int j = i + 1; j < entries.size(); j++)
            {
                if (Objects.equals(entries.get(i).getKey(), entries.get(j).getKey()))
                {
                    return new LookupValidationResult(false,
                            "Duplicate key found in '" + getName() + "' lookup! [key : " + entries.get(i).getKey() + "]");
                }
                if (Objects.equals(entries.get(i).getValue(), entries.get(j).getValue()))
                {
                    return new LookupValidationResult(false,
                            "Duplicate value found in '" + getName() + "' lookup! [values : " + entries.get(i).getValue() + "]");
                }
            }

        }

        LookupValidationResult lookupValidationResult = null;
        for (NestedLookupEntry nestedLookupEntry : entries)
        {
            lookupValidationResult = nestedLookupEntry.getSubLookup().validate(this);
            if (!lookupValidationResult.isValid())
            {
                return lookupValidationResult;
            }
        }

        return new LookupValidationResult(true, null);
    }
}
