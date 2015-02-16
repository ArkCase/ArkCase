package com.armedia.acm.data.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * Created by armdev on 2/4/15.
 */
@Converter
public class BooleanToStringConverter implements AttributeConverter<Boolean, String>
{

    @Override
    public String convertToDatabaseColumn(Boolean attribute)
    {
        return attribute == null ? "false" : attribute.toString();
    }

    @Override
    public Boolean convertToEntityAttribute(String dbData)
    {
        return dbData == null ? Boolean.FALSE : Boolean.valueOf(dbData);
    }
}
