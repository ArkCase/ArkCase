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
public class ObjectConverter
{

    private final AcmMarshaller marshaller;
    private final AcmUnmarshaller unmarshaller;

    public ObjectConverter(AcmMarshaller marshaller, AcmUnmarshaller unmarshaller)
    {
        this.marshaller = marshaller;
        this.unmarshaller = unmarshaller;
    }

    public AcmMarshaller getMarshaller()
    {
        return marshaller;
    }

    public AcmUnmarshaller getUnmarshaller()
    {
        return unmarshaller;
    }

    public static ObjectConverter createJSONConverter()
    {
        return new ObjectConverter(new JSONMarshaller(), new JSONUnmarshaller());
    }

    public static ObjectConverter createXMLConverter()
    {
        return new ObjectConverter(new XMLMarshaller(), new XMLUnmarshaller());
    }

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
