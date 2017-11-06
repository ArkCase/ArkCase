/**
 * 
 */
package com.armedia.acm.objectonverter.json;

import com.armedia.acm.objectonverter.AcmMarshaller;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author riste.tutureski
 *
 */
public class JSONMarshaller implements AcmMarshaller
{
    private Logger LOG = LoggerFactory.getLogger(getClass());
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
            LOG.error("Error while creating JSON from Object: " + e.getMessage(), e);
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
