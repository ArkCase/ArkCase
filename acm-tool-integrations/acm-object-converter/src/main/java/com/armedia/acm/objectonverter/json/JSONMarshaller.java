/**
 * 
 */
package com.armedia.acm.objectonverter.json;

/*-
 * #%L
 * Tool Integrations: Object Converter
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

import com.armedia.acm.objectonverter.AcmMarshaller;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * @author riste.tutureski
 *
 */
public class JSONMarshaller implements AcmMarshaller
{
    private Logger LOG = LogManager.getLogger(getClass());
    private ObjectMapper mapper;

    @Override
    public String marshal(Object obj)
    {
        String output = null;
        try
        {
            output = mapper.writeValueAsString(obj);
        }
        catch (JsonProcessingException e)
        {
            LOG.error("Error while creating JSON from Object: {}", e.getMessage(), e);
        }

        return output;
    }

    @Override
    public String marshal(Object object, Class clazz)
    {
        try
        {
            return mapper.writerFor(clazz).writeValueAsString(object);
        }
        catch (JsonProcessingException e)
        {
            LOG.error("Error while creating JSON from Object: {}", e.getMessage(), e);
            return null;
        }
    }

    public ObjectMapper getMapper()
    {
        return mapper;
    }

    public void setMapper(ObjectMapper mapper)
    {
        this.mapper = mapper;
    }
}
