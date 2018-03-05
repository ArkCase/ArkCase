package com.armedia.acm.data.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import java.util.UUID;

/**
 * {@link UUID} to {@link String} converter.
 * Postgres has issues with {@link UUID} fileds, so we convert them to {@link String}
 * Created by Bojan Milenkoski on 18.1.2016.
 */
@Converter
public class UUIDToStringConverter implements AttributeConverter<UUID, String>
{
    @Override
    public String convertToDatabaseColumn(UUID attribute)
    {
        if (attribute == null)
        {
            return null;
        }
        return attribute.toString();
    }

    @Override
    public UUID convertToEntityAttribute(String dbData)
    {
        if (dbData == null)
        {
            return null;
        }
        return UUID.fromString(dbData);
    }
}
