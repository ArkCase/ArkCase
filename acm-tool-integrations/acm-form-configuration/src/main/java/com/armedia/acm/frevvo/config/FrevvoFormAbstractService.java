/**
 * 
 */
package com.armedia.acm.frevvo.config;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author riste.tutureski
 *
 */
public abstract class FrevvoFormAbstractService {

	private Logger LOG = LoggerFactory.getLogger(FrevvoFormAbstractService.class);
	
	public Object convertFromXMLToObject(String xml, Class<?> c) {
		Object obj = null;
		try{
			InputStream inputStream = new ByteArrayInputStream(xml.getBytes());
	        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	        Document document = documentBuilder.parse(inputStream);
	        Element element = document.getDocumentElement();
	        JAXBContext context = JAXBContext.newInstance(c);
	        Unmarshaller unmarshaller = context.createUnmarshaller();
	        JAXBElement<?> jaxbElement = unmarshaller.unmarshal(element, c);
	        obj = jaxbElement.getValue();
		}
        catch(Exception e) {
        	LOG.error("Error while creating Object from XML. " + e);
        }
		
		return obj;
	}
	
}
