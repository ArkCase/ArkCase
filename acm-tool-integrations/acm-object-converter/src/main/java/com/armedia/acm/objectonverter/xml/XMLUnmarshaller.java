/**
 * 
 */
package com.armedia.acm.objectonverter.xml;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.armedia.acm.objectonverter.AcmUnmarshaller;
import com.google.common.base.Charsets;

/**
 * @author riste.tutureski
 *
 */
public class XMLUnmarshaller implements AcmUnmarshaller 
{

	private Logger LOG = LoggerFactory.getLogger(getClass());
	
	@Override
	public Object unmarshall(String source, Class<?> c)
	{
		Object obj = null;
		try
		{
			InputStream inputStream = new ByteArrayInputStream(source.getBytes(Charsets.UTF_8));
	        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	        Document document = documentBuilder.parse(inputStream);
	        Element element = document.getDocumentElement();
	        
	        JAXBContext context = JAXBContext.newInstance(c);
	        Unmarshaller unmarshaller = context.createUnmarshaller();
	        JAXBElement<?> jaxbElement = unmarshaller.unmarshal(element, c);
	        obj = jaxbElement.getValue();
		}
        catch(Exception e) 
		{
        	LOG.error("Error while creating Object from XML: " + e.getMessage(), e);
        }
		
		return obj;
	}

}
