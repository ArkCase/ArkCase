package com.armedia.acm.plugins.dashboard.site.converters;

import org.postgresql.util.PGobject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.sql.SQLException;

/**
 * Created by maksud.sharif on 3/26/2017.
 */
@Converter
public class JsonConverter implements AttributeConverter<String, PGobject>
{
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonConverter.class);

    @Override
    public PGobject convertToDatabaseColumn(String attribute)
    {
        try
        {
            PGobject po = new PGobject();
            // here we tell Postgres to use JSON as type to treat our json
            po.setType("json");
            po.setValue(attribute);
            return po;
        } catch (SQLException e)
        {
            LOGGER.error("Failed to convert column to json", e);
            return null;
        }
    }

    @Override
    public String convertToEntityAttribute(PGobject dbData)
    {
        return dbData != null ? dbData.getValue() : null;
    }
}
