package com.armedia.acm.services.config.lookups.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Objects;

/**
 * Created by bojan.milenkoski on 24.8.2017
 */
public class StandardLookup extends AcmLookup<StandardLookupEntry>
{
    @Override
    @JsonIgnore
    public LookupValidationResult validate()
    {
        return validate(null);
    }

    @JsonIgnore
    protected LookupValidationResult validate(AcmLookup<?> parentLookup)
    {
        String lookupName = parentLookup == null ? getName() : parentLookup.getName();

        // Check empty key or value
        for (StandardLookupEntry entry : entries)
        {
            if (entry.getKey() == null || entry.getKey().isEmpty())
            {
                return new LookupValidationResult(false, "Empty key found in '" + lookupName + "' lookup!");
            }
            if (entry.getValue() == null || entry.getValue().isEmpty())
            {
                return new LookupValidationResult(false, "Empty value found in '" + lookupName + "' lookup!");
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
                            "Duplicate key found in '" + lookupName + "' lookup! [key : " + entries.get(i).getKey() + "]");
                }
                if (Objects.equals(entries.get(i).getValue(), entries.get(j).getValue()))
                {
                    return new LookupValidationResult(false,
                            "Duplicate value found in '" + lookupName + "' lookup! [values : " + entries.get(i).getValue() + "]");
                }
            }

        }

        return new LookupValidationResult(true, null);
    }
}
