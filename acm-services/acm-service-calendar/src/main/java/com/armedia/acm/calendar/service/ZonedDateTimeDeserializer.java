package com.armedia.acm.calendar.service;

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

        StringBuilder dateText = new StringBuilder(jp.getText());
        if (dateText.charAt(10) == ' ')
        {
            dateText.replace(10, 11, "T");
        }
        if (dateText.charAt(dateText.length() - 3) != ':')
        {
            dateText.insert(dateText.length() - 2, ":");
        }
        return ZonedDateTime.parse(dateText, DateTimeFormatter.ISO_DATE_TIME);
    }

}
