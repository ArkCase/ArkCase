/**
 * 
 */
package com.armedia.acm.objectonverter.xml;

/*-
 * #%L
 * Tool Integrations: Object Converter
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import com.armedia.acm.objectonverter.AcmMarshaller;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

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

    private Logger LOG = LogManager.getLogger(getClass());

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
