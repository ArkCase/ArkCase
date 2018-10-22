package gov.foia.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;

/**
 * Json Date Serializer
 *
 * @author dame.gjorgjievski
 */
@Component
public class JsonDateSerializer extends JsonSerializer<Date>
{

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm");

    @Override
    public void serialize(Date date, JsonGenerator gen, SerializerProvider provider) throws IOException, JsonProcessingException
    {
        if (date != null)
        {
            String formattedDate = DATE_FORMATTER.print(date.getTime());
            gen.writeString(formattedDate);
        }
    }
}