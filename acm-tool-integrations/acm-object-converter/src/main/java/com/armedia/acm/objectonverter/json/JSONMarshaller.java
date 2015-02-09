/**
 * 
 */
package com.armedia.acm.objectonverter.json;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.armedia.acm.objectonverter.AcmMarshaller;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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
		
		ObjectMapper mapper = new ObjectMapper();
		
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
