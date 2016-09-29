/**
 * 
 */
package com.armedia.acm.objectonverter.json;

import com.armedia.acm.objectonverter.AcmUnmarshaller;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author riste.tutureski
 *
 */
public class JSONUnmarshaller implements AcmUnmarshaller
{

    private Logger LOG = LoggerFactory.getLogger(getClass());

    // FIXME not sure why we are instating new ObjectMapper instead we can use it one which is in ApplicationContext
    private static ObjectMapper mapper = new ObjectMapper();
    static
    {
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public <E> E unmarshall(String source, Class<E> c)
    {
        E output = null;

        try
        {
            output = mapper.readValue(source, c);
        } catch (IOException e)
        {
            LOG.error("Error while creating Object from XML: " + e.getMessage(), e);
        }

        return output;
    }

}
