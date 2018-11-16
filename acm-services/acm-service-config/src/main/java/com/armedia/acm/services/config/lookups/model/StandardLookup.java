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
