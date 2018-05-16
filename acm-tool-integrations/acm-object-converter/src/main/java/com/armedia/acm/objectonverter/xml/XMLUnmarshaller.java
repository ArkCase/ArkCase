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

import com.armedia.acm.objectonverter.AcmUnmarshaller;
import com.google.common.base.Charsets;

import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collection;

/**
 * @author riste.tutureski
 *
 */
public class XMLUnmarshaller implements AcmUnmarshaller
{

    private Logger LOG = LoggerFactory.getLogger(getClass());

    @Override
    public <E> E unmarshall(String source, Class<E> c)
    {
        E obj = null;
        try
        {
            InputStream inputStream = new ByteArrayInputStream(source.getBytes(Charsets.UTF_8));
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = documentBuilder.parse(inputStream);
            Element element = document.getDocumentElement();

            JAXBContext context = JAXBContext.newInstance(c);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            JAXBElement<E> jaxbElement = unmarshaller.unmarshal(element, c);
            obj = jaxbElement.getValue();

        }
        catch (Exception e)
        {
            LOG.error("Error while creating Object from XML: " + e.getMessage(), e);
        }

        return obj;
    }

    @Override
    public <T> T unmarshallCollection(String source, Class<? extends Collection> collectionClass, Class elementClass)
    {
        throw new NotImplementedException("Method unmarshalCollection not implemented for XMLUnmarshaller!");
    }

}
