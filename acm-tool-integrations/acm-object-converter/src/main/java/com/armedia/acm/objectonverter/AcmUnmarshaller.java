package com.armedia.acm.objectonverter;

/**
 * @author riste.tutureski
 *
 */
public interface AcmUnmarshaller {
	
	public Object unmarshall(String source, Class<?> c);
	
}
