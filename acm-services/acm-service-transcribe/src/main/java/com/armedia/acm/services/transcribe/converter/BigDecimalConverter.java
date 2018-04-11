package com.armedia.acm.services.transcribe.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.math.BigDecimal;
import java.time.Duration;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 03/05/2018
 */
@Converter
public class BigDecimalConverter implements AttributeConverter<BigDecimal, String>
{
    @Override
    public String convertToDatabaseColumn(BigDecimal duration)
    {
        return duration == null || "null".equalsIgnoreCase(duration.toString()) ? "0" : duration.toString();
    }

    @Override
    public BigDecimal convertToEntityAttribute(String durationAsString)
    {
        return durationAsString == null || "null".equalsIgnoreCase(durationAsString) ? new BigDecimal("0") : new BigDecimal(durationAsString);
    }
}
