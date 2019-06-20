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

import com.armedia.acm.objectonverter.AcmUnmarshaller;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.util.Collection;

/**
 * @author riste.tutureski
 *
 */
public class JSONUnmarshaller implements AcmUnmarshaller
{

    private Logger LOG = LogManager.getLogger(getClass());
    private ObjectMapper mapper;

    @Override
    public <E> E unmarshall(String source, Class<E> c)
    {
        E output = null;
        try
        {
            output = mapper.readValue(source, c);
        }
        catch (IOException e)
        {
            LOG.error("Error while creating Object from JSON: " + e.getMessage(), e);
        }

        return output;
    }

    @Override
    public <T> T unmarshallCollection(String source, Class<? extends Collection> collectionClass, Class elementClass)
    {
        T output = null;
        try
        {
            CollectionType javaType = mapper.getTypeFactory().constructCollectionType(collectionClass, elementClass);
            output = mapper.readValue(source, javaType);
        }
        catch (IOException e)
        {
            LOG.error("Error while creating Object from JSON: " + e.getMessage(), e);
        }

        return output;
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
