/**
 * 
 */
package com.armedia.acm.objectonverter.json;

import com.armedia.acm.objectonverter.AcmMarshaller;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author riste.tutureski
 *
 */
public class JSONMarshaller implements AcmMarshaller {

	private Logger LOG = LoggerFactory.getLogger(getClass());
	
	@Override
	public String marshal(Object obj)
	{
		String output = null;

		//FIXME not sure why we are instating new ObjectMapper instead we can use it one which is in ApplicationContext
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule( new JavaTimeModule());
		
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

}
