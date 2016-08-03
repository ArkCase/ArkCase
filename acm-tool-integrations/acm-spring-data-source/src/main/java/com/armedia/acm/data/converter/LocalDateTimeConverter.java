package com.armedia.acm.data.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;

/**
 *
 * Converts LocalDateTime to Date and vice versa.
 *
 * Created by nebojsha on 27.08.2016.
 */
@Converter
public class LocalDateTimeConverter implements AttributeConverter<LocalDateTime, Date>
{

    @Override
    public Date convertToDatabaseColumn(LocalDateTime localDateTime)
    {
        if (localDateTime == null)
        {
            return null;
        }
        return Timestamp.valueOf(localDateTime);
    }

    @Override
    public LocalDateTime convertToEntityAttribute(Date date)
    {
        if (date == null)
        {
            return null;
        }
        if (date instanceof java.sql.Timestamp)
        {
            return ((java.sql.Timestamp) date).toLocalDateTime();
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}
