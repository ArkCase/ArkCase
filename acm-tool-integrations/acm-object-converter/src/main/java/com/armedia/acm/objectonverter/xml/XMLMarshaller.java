/**
 * 
 */
package com.armedia.acm.objectonverter.xml;

import com.armedia.acm.objectonverter.AcmMarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

/**
 * @author riste.tutureski
 *
 */
public class XMLMarshaller implements AcmMarshaller
{

    private Logger LOG = LoggerFactory.getLogger(getClass());

    @Override
    public String marshal(Object obj)
    {
        String output = null;
        try
        {
            OutputStream outputStream = new ByteArrayOutputStream();

            JAXBContext context = JAXBContext.newInstance(obj.getClass());
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(obj, outputStream);

            output = outputStream.toString().replace("<ns2:", "<p0:").replace("</ns2:", "</p0:").replace(" xmlns:ns2=", " xmlns:p0=");
        }
        catch (Exception e)
        {
            LOG.error("Error while creating XML from Object: " + e.getMessage(), e);
        }

        return output;
    }

}
