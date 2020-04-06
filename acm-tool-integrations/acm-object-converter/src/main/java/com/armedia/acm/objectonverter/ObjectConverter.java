/**
 * 
 */
package com.armedia.acm.objectonverter;

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

import com.armedia.acm.objectonverter.json.JSONMarshaller;
import com.armedia.acm.objectonverter.json.JSONUnmarshaller;
import com.armedia.acm.objectonverter.xml.XMLMarshaller;
import com.armedia.acm.objectonverter.xml.XMLUnmarshaller;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.std.DateSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author riste.tutureski
 *
 */
public class ObjectConverter
{
    private JSONMarshaller jsonMarshaller;
    private JSONMarshaller indentedJsonMarshaller;
    private JSONUnmarshaller jsonUnmarshaller;
    private XMLMarshaller xmlMarshaller;
    private XMLUnmarshaller xmlUnmarshaller;

    public static AcmMarshaller createJSONMarshallerForTests()
    {
        // Make sure all mapper features are in line with the 'sourceObjectMapper' bean defined in
        // spring-library-object-converter.xml
        ObjectMapper mapper = new ObjectMapper();

        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        // Always format dates with Z at the end
        JavaTimeModule javaTimeModule = new JavaTimeModule();

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        LocalDateTimeSerializer localDateTimeSerializer = new LocalDateTimeSerializer(dateTimeFormatter);
        javaTimeModule.addSerializer(LocalDateTime.class, localDateTimeSerializer);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        DateSerializer dateSerializer = new DateSerializer(false, simpleDateFormat);
        javaTimeModule.addSerializer(Date.class, dateSerializer);

        mapper.registerModule(javaTimeModule);
        mapper.findAndRegisterModules();

        JSONMarshaller jsonMarshaller = new JSONMarshaller();
        jsonMarshaller.setMapper(mapper);

        return jsonMarshaller;
    }

    public static JSONUnmarshaller createJSONUnmarshallerForTests()
    {
        // Make sure all mapper features are in line with the 'sourceObjectMapper' bean defined in
        // spring-library-object-converter.xml
        ObjectMapper mapper = new ObjectMapper();

        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        // Hack time module to allow 'Z' at the end of string (i.e. javascript json's)
        JavaTimeModule javaTimeModule = new JavaTimeModule();

        LocalDateTimeDeserializer localDateTimeDeserializer = new LocalDateTimeDeserializer(DateTimeFormatter.ISO_DATE_TIME);
        javaTimeModule.addDeserializer(LocalDateTime.class, localDateTimeDeserializer);

        mapper.registerModule(javaTimeModule);
        mapper.findAndRegisterModules();

        JSONUnmarshaller jsonUnmarshaller = new JSONUnmarshaller();
        jsonUnmarshaller.setMapper(mapper);

        return jsonUnmarshaller;
    }

    public static ObjectConverter createObjectConverterForTests()
    {
        ObjectConverter objectConverter = new ObjectConverter();
        objectConverter.setJsonMarshaller((JSONMarshaller) createJSONMarshallerForTests());
        objectConverter.setJsonUnmarshaller(createJSONUnmarshallerForTests());
        objectConverter.setXmlMarshaller(new XMLMarshaller());
        objectConverter.setXmlUnmarshaller(new XMLUnmarshaller());
        return objectConverter;
    }

    public DateTimeFormatter getISODateTimeFormatter()
    {
        return DateTimeFormatter.ISO_DATE_TIME;
    }

    public JSONMarshaller getJsonMarshaller()
    {
        return jsonMarshaller;
    }

    public void setJsonMarshaller(JSONMarshaller jsonMarshaller)
    {
        this.jsonMarshaller = jsonMarshaller;
    }

    public JSONUnmarshaller getJsonUnmarshaller()
    {
        return jsonUnmarshaller;
    }

    public void setJsonUnmarshaller(JSONUnmarshaller jsonUnmarshaller)
    {
        this.jsonUnmarshaller = jsonUnmarshaller;
    }

    public XMLMarshaller getXmlMarshaller()
    {
        return xmlMarshaller;
    }

    public void setXmlMarshaller(XMLMarshaller xmlMarshaller)
    {
        this.xmlMarshaller = xmlMarshaller;
    }

    public XMLUnmarshaller getXmlUnmarshaller()
    {
        return xmlUnmarshaller;
    }

    public void setXmlUnmarshaller(XMLUnmarshaller xmlUnmarshaller)
    {
        this.xmlUnmarshaller = xmlUnmarshaller;
    }

    public synchronized JSONMarshaller getIndentedJsonMarshaller()
    {
        if (indentedJsonMarshaller == null)
        {
            indentedJsonMarshaller = new JSONMarshaller();
            indentedJsonMarshaller.setMapper(getJsonMarshaller().getMapper().copy().enable(SerializationFeature.INDENT_OUTPUT));
        }
        return indentedJsonMarshaller;
    }
}
