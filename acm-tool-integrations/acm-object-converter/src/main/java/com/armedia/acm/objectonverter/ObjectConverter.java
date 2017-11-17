/**
 * 
 */
package com.armedia.acm.objectonverter;

import com.armedia.acm.objectonverter.json.JSONMarshaller;
import com.armedia.acm.objectonverter.json.JSONUnmarshaller;
import com.armedia.acm.objectonverter.xml.XMLMarshaller;
import com.armedia.acm.objectonverter.xml.XMLUnmarshaller;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

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

    public JSONMarshaller getIndentedJsonMarshaller()
    {
        if (indentedJsonMarshaller == null)
        {
            synchronized (ObjectConverter.class)
            {
                if (indentedJsonMarshaller == null)
                {
                    indentedJsonMarshaller = new JSONMarshaller();
                    indentedJsonMarshaller.setMapper(getJsonMarshaller().getMapper().copy().enable(SerializationFeature.INDENT_OUTPUT));
                }
            }
        }
        return indentedJsonMarshaller;
    }

    public static AcmMarshaller createJSONMarshallerForTests()
    {
        // Make sure all mapper features are in line with the 'sourceObjectMapper' bean defined in servlet-context.xml
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.setDateFormat(new ISO8601DateFormat());
        mapper.findAndRegisterModules();

        JSONMarshaller jsonMarshaller = new JSONMarshaller();
        jsonMarshaller.setMapper(mapper);

        return jsonMarshaller;
    }

    public static JSONUnmarshaller createJSONUnmarshallerForTests()
    {
        // Make sure all mapper features are in line with the 'sourceObjectMapper' bean defined in servlet-context.xml
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.setDateFormat(new ISO8601DateFormat());
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
}
