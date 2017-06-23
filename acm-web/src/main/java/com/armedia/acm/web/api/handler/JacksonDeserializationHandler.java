package com.armedia.acm.web.api.handler;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;

import java.io.IOException;

/**
 * Created by nebojsha.davidovikj on 5/15/2017.
 */
public class JacksonDeserializationHandler extends DeserializationProblemHandler
{

    @Override
    public boolean handleUnknownProperty(DeserializationContext ctxt, JsonParser jp, JsonDeserializer<?> deserializer, Object beanOrClass, String propertyName) throws IOException, JsonProcessingException
    {
        //if @id or @ref exists that means Bean is not annotated to be processed with Jsog
        if ("@id".equals(propertyName) || "@ref".equals(propertyName))
        {
            return true;
        } else
        {
            return super.handleUnknownProperty(ctxt, jp, deserializer, beanOrClass, propertyName);
        }
    }
}
