package com.armedia.acm.services.config.lookups.model;

/*-
 * #%L
 * ACM Service: Config
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by bojan.milenkoski on 24.8.2017
 */
public class InverseValuesLookup extends AcmLookup<InverseValuesLookupEntry>
{
    @Override
    @JsonIgnore
    public LookupValidationResult validate()
    {
        // Check empty keys or values
        for (InverseValuesLookupEntry entry : entries)
        {
            if (entry.getKey() == null)
            {
                return new LookupValidationResult(false, "Key not found or have [null] value in '" + getName() + "' lookup!");
            }
            if (entry.getValue() == null || entry.getValue().isEmpty())
            {
                return new LookupValidationResult(false, "Empty value found in '" + getName() + "' lookup!");
            }
            if (entry.getInverseKey() == null)
            {
                return new LookupValidationResult(false, "Inverse key not found or have [null] value in '" + getName() + "' lookup!");
            }
            if (entry.getInverseValue() == null || entry.getInverseValue().isEmpty())
            {
                return new LookupValidationResult(false, "Empty inverse value found in '" + getName() + "' lookup!");
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
                if (Objects.equals(entries.get(i).getInverseKey(), entries.get(j).getInverseKey()))
                {
                    return new LookupValidationResult(false,
                            "Duplicate inverse key found in '" + getName() + "' lookup! [key : " + entries.get(i).getInverseKey() + "]");
                }
                if (Objects.equals(entries.get(i).getInverseValue(), entries.get(j).getInverseValue()))
                {
                    return new LookupValidationResult(false, "Duplicate inverse value found in '" + getName() + "' lookup! [values : "
                            + entries.get(i).getInverseValue() + "]");
                }
            }

        }

        return new LookupValidationResult(true, null);
    }

    @Override
    @JsonIgnore
    public AcmLookupTransformer transformToConfigurationEntries(List<?> entries)
    {

        List<Map<String, Object>> lookupEntries = new ArrayList<>();
        List<InverseValuesLookupEntry> inverseLookupEntries = (List<InverseValuesLookupEntry>) entries;

        inverseLookupEntries.forEach(entry -> {
            Map<String, Object> lookupEntry = new HashMap<>();
            lookupEntry.put("key", entry.getKey());
            lookupEntry.put("inverseValue", entry.getInverseValue());
            lookupEntry.put("inverseKey", entry.getInverseKey());
            lookupEntry.put("value", entry.getValue());
            lookupEntry.put("readonly", entry.isReadonly());
            lookupEntry.put("description", entry.getDescription());
            if (entry.getOrder() != null)
            {
                lookupEntry.put("order", entry.getOrder());

            }
            else
            {
                lookupEntry.put("order", entries.size() + 1);
            }

            lookupEntries.add(lookupEntry);
        });

        return new AcmLookupTransformer(lookupEntries);

    }
}
