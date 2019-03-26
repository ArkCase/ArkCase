package com.armedia.acm.calendar.config.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class PurgeOptionsDeserializer extends JsonDeserializer
{
    @Override
    public PurgeOptions deserialize(JsonParser p, DeserializationContext ctxt)
    {
        try
        {
            if (p.getValueAsString() == null)
            {
                return null;
            }
            return PurgeOptions.valueOf(p.getValueAsString());
        }
        catch (IOException e)
        {
            return null;
        }
    }
}
