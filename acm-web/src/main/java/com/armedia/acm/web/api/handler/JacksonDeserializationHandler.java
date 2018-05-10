package com.armedia.acm.web.api.handler;

/*-
 * #%L
 * ACM Shared Web Artifacts
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

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
    public boolean handleUnknownProperty(DeserializationContext ctxt, JsonParser jp, JsonDeserializer<?> deserializer, Object beanOrClass,
            String propertyName) throws IOException, JsonProcessingException
    {
        // if @id or @ref exists that means Bean is not annotated to be processed with Jsog
        if ("@id".equals(propertyName) || "@ref".equals(propertyName))
        {
            return true;
        }
        else
        {
            return super.handleUnknownProperty(ctxt, jp, deserializer, beanOrClass, propertyName);
        }
    }
}
