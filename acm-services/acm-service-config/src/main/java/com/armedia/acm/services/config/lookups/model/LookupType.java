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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Created by bojan.milenkoski on 24.8.2017
 */
public enum LookupType
{
    STANDARD_LOOKUP("standardLookup", StandardLookup.class),
    INVERSE_VALUES_LOOKUP(
            "inverseValuesLookup",
            InverseValuesLookup.class),
    NESTED_LOOKUP("nestedLookup", NestedLookup.class);

    private String typeName;

    private Class<? extends AcmLookup<?>> lookupClass;

    LookupType(String typeName, Class<? extends AcmLookup<?>> lookupClass)
    {
        this.typeName = typeName;
        this.lookupClass = lookupClass;
    }

    @JsonCreator
    public static LookupType forValue(String typeName)
    {
        LookupType[] lookupTypeEnums = LookupType.values();
        for (LookupType lookupTypeEnum : lookupTypeEnums)
        {
            if (lookupTypeEnum.getTypeName().equals(typeName))
            {
                return lookupTypeEnum;
            }
        }
        throw new RuntimeException("Unknown lookup type: " + typeName + "!");
    }

    @JsonValue
    public String getTypeName()
    {
        return typeName;
    }

    public Class<? extends AcmLookup<?>> getLookupClass()
    {
        return lookupClass;
    }
}
