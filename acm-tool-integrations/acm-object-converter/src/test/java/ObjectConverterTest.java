
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.armedia.acm.objectonverter.ObjectConverter;
import com.armedia.acm.objectonverter.json.JSONMarshaller;
import com.armedia.acm.objectonverter.xml.XMLMarshaller;
import com.armedia.acm.objectonverter.xml.XMLUnmarshaller;

import org.apache.commons.lang3.time.DateUtils;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by bojan.milenkoski on 15.10.2017
 */
public class ObjectConverterTest extends EasyMockSupport
{
    private ObjectConverter objectConverter;

    @Before
    public void setUp()
    {
        // set time zone to UTC, so date.toString(), date.getHour(), etc, show the same zone as JSON serializer
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        objectConverter = new ObjectConverter();
        objectConverter.setJsonMarshaller((JSONMarshaller) ObjectConverter.createJSONMarshallerForTests());
        objectConverter.setJsonUnmarshaller(ObjectConverter.createJSONUnmarshallerForTests());
        objectConverter.setXmlMarshaller(new XMLMarshaller());
        objectConverter.setXmlUnmarshaller(new XMLUnmarshaller());
    }

    @Test
    public void testJSONDateMarshalling()
    {
        // given
        Date date = new Date();
        String year = String.valueOf(date.getYear() + 1900);
        String month = String.valueOf(date.getMonth() + 1);
        String day = String.valueOf(date.getDate());
        String hours = String.valueOf(date.getHours());
        String minutes = String.valueOf(date.getMinutes());
        String seconds = String.valueOf(date.getSeconds());
        int n = (int) (date.getTime() % 1000);
        String millis = String.valueOf(n < 0 ? n + 1000 : n);
        ObjectWithDateField objectWithDateField = new ObjectWithDateField();
        objectWithDateField.theDate = date;

        // when
        String json = objectConverter.getJsonMarshaller().marshal(objectWithDateField);

        // then
        assertTrue(json.equals("{\"theDate\":\"" + year + "-" + prependZeros(month, 2) + "-" + prependZeros(day, 2) + "T"
                + prependZeros(hours, 2) + ":" + prependZeros(minutes, 2) + ":" + prependZeros(seconds, 2) + "." + prependZeros(millis, 3)
                + "Z\"}"));
    }

    private String prependZeros(String text, int len)
    {
        String zeroes = "";
        for (int i = 0; i < len; i++)
        {
            zeroes = zeroes + "0";
        }
        return zeroes.substring(text.length()) + text;
    }

    @Test
    public void testJSONDateMarshallingUnmarshallingProducesSameDate()
    {
        // given
        Date date = new Date();
        ObjectWithDateField objectWithDateField = new ObjectWithDateField();
        objectWithDateField.theDate = date;

        // when
        String json = objectConverter.getJsonMarshaller().marshal(objectWithDateField);
        ObjectWithDateField unmarshalled = objectConverter.getJsonUnmarshaller().unmarshall(json, ObjectWithDateField.class);

        // then
        assertTrue(DateUtils.truncatedEquals(objectWithDateField.theDate, unmarshalled.theDate, Calendar.SECOND));
    }

    @Test
    public void testJSONDateUnmarshallingMarshallingProducesSameDate()
    {
        // given
        String json = "{\"theDate\":\"2017-08-26T10:28:33.000Z\"}";

        // when
        ObjectWithDateField unmarshalled = objectConverter.getJsonUnmarshaller().unmarshall(json, ObjectWithDateField.class);
        String marshalled = objectConverter.getJsonMarshaller().marshal(unmarshalled);

        // then
        assertTrue(json.equals(marshalled));
    }

    @Test
    public void testJSONDateUnmarshallingValueWithZ()
    {
        // given
        String year = "2017";
        String month = "08";
        String day = "26";
        String hours = "10";
        String minutes = "28";
        String seconds = "33";
        String json = "{\"theDate\":\"" + year + "-" + month + "-" + day + "T" + hours + ":" + minutes + ":" + seconds + "Z\"}";

        // when
        ObjectWithDateField unmarshalled = objectConverter.getJsonUnmarshaller().unmarshall(json, ObjectWithDateField.class);

        // then
        assertEquals(year, String.valueOf(unmarshalled.getTheDate().getYear() + 1900));
        assertEquals(month, prependZeros(String.valueOf(unmarshalled.getTheDate().getMonth() + 1), 2));
        assertEquals(day, prependZeros(String.valueOf(unmarshalled.getTheDate().getDate()), 2));
        assertEquals(hours, prependZeros(String.valueOf(unmarshalled.getTheDate().getHours()), 2));
        assertEquals(minutes, prependZeros(String.valueOf(unmarshalled.getTheDate().getMinutes()), 2));
        assertEquals(seconds, prependZeros(String.valueOf(unmarshalled.getTheDate().getSeconds()), 2));
    }

    @Test
    public void testJSONDateUnmarshallingValueWith0000()
    {
        // given
        String year = "2017";
        String month = "02";
        String day = "20";
        String hours = "22";
        String minutes = "08";
        String seconds = "50";
        String json = "{\"theDate\":\"" + year + "-" + month + "-" + day + "T" + hours + ":" + minutes + ":" + seconds + "+00:00\"}";

        // when
        ObjectWithDateField unmarshalled = objectConverter.getJsonUnmarshaller().unmarshall(json, ObjectWithDateField.class);

        // then
        assertEquals(year, String.valueOf(unmarshalled.getTheDate().getYear() + 1900));
        assertEquals(month, prependZeros(String.valueOf(unmarshalled.getTheDate().getMonth() + 1), 2));
        assertEquals(day, prependZeros(String.valueOf(unmarshalled.getTheDate().getDate()), 2));
        assertEquals(hours, prependZeros(String.valueOf(unmarshalled.getTheDate().getHours()), 2));
        assertEquals(minutes, prependZeros(String.valueOf(unmarshalled.getTheDate().getMinutes()), 2));
        assertEquals(seconds, prependZeros(String.valueOf(unmarshalled.getTheDate().getSeconds()), 2));
    }

    static class ObjectWithDateField
    {
        private Date theDate;

        public ObjectWithDateField()
        {
        }

        public Date getTheDate()
        {
            return theDate;
        }

        public void setTheDate(Date theDate)
        {
            this.theDate = theDate;
        }
    }

    @Test
    public void dateTests()
    {
        // ---------------- marshal ------------
        String marshalledLocalDateTime = objectConverter.getJsonMarshaller().marshal(LocalDateTime.now());
        marshalledLocalDateTime = marshalledLocalDateTime.substring(1, marshalledLocalDateTime.length() - 1);
        System.out.println("Serialize java.time.LocalDateTime: " + marshalledLocalDateTime);
        assertTrue(marshalledLocalDateTime.contains("T"));
        assertTrue(marshalledLocalDateTime.endsWith("Z"));

        System.out.println("Serialize java.time.LocalDate: " + objectConverter.getJsonMarshaller().marshal(LocalDate.now()));

        marshalledLocalDateTime = objectConverter.getJsonMarshaller().marshal(ZonedDateTime.now());
        marshalledLocalDateTime = marshalledLocalDateTime.substring(1, marshalledLocalDateTime.length() - 1);
        System.out.println("Serialize java.time.ZonedDateTime: " + marshalledLocalDateTime);
        assertTrue(marshalledLocalDateTime.contains("T"));
        assertTrue(marshalledLocalDateTime.endsWith("Z"));

        marshalledLocalDateTime = objectConverter.getJsonMarshaller().marshal(new Date());
        marshalledLocalDateTime = marshalledLocalDateTime.substring(1, marshalledLocalDateTime.length() - 1);
        System.out.println("Serialize java.util.Date: " + marshalledLocalDateTime);
        assertTrue(marshalledLocalDateTime.contains("T"));
        assertTrue(marshalledLocalDateTime.endsWith("Z"));

        // ---------------- unmarshal ----------
        String dateTime = "\"2019-02-12T12:58:34.576Z\"";
        LocalDateTime unmarshalledLocalDateTime = objectConverter.getJsonUnmarshaller().unmarshall(dateTime, LocalDateTime.class);
        System.out.println("Deserialize Z java.time.LocalDateTime: " + unmarshalledLocalDateTime);
        assertEquals(unmarshalledLocalDateTime.getHour(), 12);

        System.out.println(
                "Deserialize Z java.time.LocalDate: " + objectConverter.getJsonUnmarshaller().unmarshall(dateTime, LocalDate.class));

        ZonedDateTime unmarshalledLocalZonedDateTime = objectConverter.getJsonUnmarshaller().unmarshall(dateTime, ZonedDateTime.class);
        System.out.println("Deserialize Z java.time.ZonedDateTime: " + unmarshalledLocalZonedDateTime);
        assertEquals(unmarshalledLocalZonedDateTime.getHour(), 12);

        System.out.println("Deserialize Z java.util.Date: " + objectConverter.getJsonUnmarshaller().unmarshall(dateTime, Date.class));

        dateTime = "\"2019-02-12T12:58:34.576+00:00\"";
        unmarshalledLocalDateTime = objectConverter.getJsonUnmarshaller().unmarshall(dateTime, LocalDateTime.class);
        System.out.println("Deserialize +00:00 java.time.LocalDateTime: " + unmarshalledLocalDateTime);
        assertEquals(unmarshalledLocalDateTime.getHour(), 12);

        unmarshalledLocalZonedDateTime = objectConverter.getJsonUnmarshaller().unmarshall(dateTime, ZonedDateTime.class);
        System.out.println("Deserialize +00:00 java.time.ZonedDateTime: " + unmarshalledLocalZonedDateTime);
        assertEquals(unmarshalledLocalZonedDateTime.getHour(), 12);

        Date unmarshalledLocalDate = objectConverter.getJsonUnmarshaller().unmarshall(dateTime, Date.class);
        System.out.println("Deserialize +00:00 java.util.Date: " + unmarshalledLocalDate);

        dateTime = "\"2019-02-12T12:58:34.576\"";
        unmarshalledLocalDateTime = objectConverter.getJsonUnmarshaller().unmarshall(dateTime, LocalDateTime.class);
        System.out.println("Deserialize java.time.LocalDateTime: " + unmarshalledLocalDateTime);
        assertEquals(unmarshalledLocalDateTime.getHour(), 12);

        System.out
                .println("Deserialize java.time.LocalDate: " + objectConverter.getJsonUnmarshaller().unmarshall(dateTime, LocalDate.class));

        System.out.println("Deserialize java.util.Date: " + objectConverter.getJsonUnmarshaller().unmarshall(dateTime, Date.class));
    }
}
