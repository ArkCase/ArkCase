package com.armedia.acm.calendar.service;

import static com.armedia.acm.calendar.DateTimeAdjuster.adjustDateTimeString;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity May 4, 2017
 *
 */
public class ZonedDateTimeDeserializer extends JsonDeserializer<ZonedDateTime>
{

    /*
     * (non-Javadoc)
     *
     * @see com.fasterxml.jackson.databind.JsonDeserializer#deserialize(com.fasterxml.jackson.core.JsonParser,
     * com.fasterxml.jackson.databind.DeserializationContext)
     */
    @Override
    public ZonedDateTime deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException
    {
        return ZonedDateTime.parse(adjustDateTimeString(jp.getText()), DateTimeFormatter.ISO_DATE_TIME);
    }

}
