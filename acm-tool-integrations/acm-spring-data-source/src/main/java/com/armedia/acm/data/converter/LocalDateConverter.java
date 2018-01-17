package com.armedia.acm.data.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * Created by nebojsha on 20.04.2016.
 */
@Converter
public class LocalDateConverter implements AttributeConverter<LocalDate, Date>
{

    @Override
    public Date convertToDatabaseColumn(LocalDate localDate)
    {
        if (localDate == null)
        {
            return null;
        }
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    @Override
    public LocalDate convertToEntityAttribute(Date date)
    {
        if (date == null)
        {
            return null;
        }
        if (date instanceof java.sql.Date)
        {
            return ((java.sql.Date) date).toLocalDate();
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
}
