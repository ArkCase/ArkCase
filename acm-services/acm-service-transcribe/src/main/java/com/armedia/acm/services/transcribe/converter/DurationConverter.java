package com.armedia.acm.services.transcribe.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.time.Duration;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 03/05/2018
 */
@Converter
public class DurationConverter implements AttributeConverter<Duration, String>
{
    @Override
    public String convertToDatabaseColumn(Duration duration)
    {
        return duration == null ? null : duration.toString();
    }

    @Override
    public Duration convertToEntityAttribute(String durationAsString)
    {
        return durationAsString == null ? null : Duration.parse(durationAsString);
    }
}
