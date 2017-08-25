package com.armedia.acm.services.config.lookups.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Created by bojan.milenkoski on 24.8.2017
 */
public enum LookupType
{
    STANDARD_LOOKUP("standardLookup", StandardLookup.class),
    INVERSE_VALUES_LOOKUP("inverseValuesLookup", InverseValuesLookup.class),
    SUB_LOOKUP("subLookup", SubLookup.class);

    private String typeName;

    private Class<? extends AcmLookup> lookupClass;

    LookupType(String typeName, Class<? extends AcmLookup> lookupClass)
    {
        this.typeName = typeName;
        this.lookupClass = lookupClass;
    }

    @JsonValue
    public String getTypeName()
    {
        return typeName;
    }

    public Class<? extends AcmLookup> getLookupClass()
    {
        return lookupClass;
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
}
