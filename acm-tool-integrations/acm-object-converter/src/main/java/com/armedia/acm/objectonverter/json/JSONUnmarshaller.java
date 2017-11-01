/**
 * 
 */
package com.armedia.acm.objectonverter.json;

import com.armedia.acm.objectonverter.AcmUnmarshaller;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;

/**
 * @author riste.tutureski
 *
 */
public class JSONUnmarshaller implements AcmUnmarshaller
{

    private Logger LOG = LoggerFactory.getLogger(getClass());
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
