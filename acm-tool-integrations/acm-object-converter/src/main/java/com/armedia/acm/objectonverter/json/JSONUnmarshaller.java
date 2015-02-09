/**
 * 
 */
package com.armedia.acm.objectonverter.json;

import java.io.IOException;

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
		ObjectMapper mapper = new ObjectMapper();
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
