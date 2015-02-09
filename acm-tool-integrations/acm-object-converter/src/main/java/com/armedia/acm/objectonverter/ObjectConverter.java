/**
 * 
 */
package com.armedia.acm.objectonverter;

import com.armedia.acm.objectonverter.json.JSONMarshaller;
import com.armedia.acm.objectonverter.json.JSONUnmarshaller;
import com.armedia.acm.objectonverter.xml.XMLMarshaller;
import com.armedia.acm.objectonverter.xml.XMLUnmarshaller;

/**
 * @author riste.tutureski
 *
 */
public class ObjectConverter {

	public static AcmMarshaller createXMLMarshaller()
	{
		return new XMLMarshaller();
	}
	
	public static AcmUnmarshaller createXMLUnmarshaller()
	{
		return new XMLUnmarshaller();
	}
	
	public static AcmMarshaller createJSONMarshaller()
	{
		return new JSONMarshaller();
	}
	
	public static AcmUnmarshaller createJSONUnmarshaller()
	{
		return new JSONUnmarshaller();
	}
	
}
