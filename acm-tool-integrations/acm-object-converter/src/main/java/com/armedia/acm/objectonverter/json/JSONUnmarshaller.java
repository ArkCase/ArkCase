/**
 * 
 */
package com.armedia.acm.objectonverter.json;

import java.io.IOException;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.armedia.acm.objectonverter.AcmUnmarshaller;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author riste.tutureski
 *
 */
public class JSONUnmarshaller implements AcmUnmarshaller {

	private Logger LOG = LoggerFactory.getLogger(getClass());
	
	@Override
	public Object unmarshall(String source, Class<?> c)
	{
		Object output = null;
		//FIXME not sure why we are instating new ObjectMapper instead we can use it one which is in ApplicationContext
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule( new JavaTimeModule());
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		
		try 
		{
			output = mapper.readValue(source, c);
		} 
		catch (IOException e) 
		{
			LOG.error("Error while creating Object from XML: " + e.getMessage(), e);
		}
		
		return output;
	}

}
