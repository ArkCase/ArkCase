package com.armedia.acm.services.search.service;

import com.armedia.acm.services.search.model.DatePojo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertNotNull;

/**
 * Created by armdev on 5/5/15.
 */
public class ObjectMapperFactoryTest
{
    @Test
    public void marshalDate() throws Exception
    {
        // ensure that the date format "yyyy-MM-dd" can be parsed by the JSON object mapper used by our
        // Spring message converters.  Each date property that uses this format must be annotated with
        // @JsonFormat(pattern="yyyy-MM-dd").
        ObjectMapper mapper = new ObjectMapperFactory().createObjectMapper();

        String json = "{ \"date\": \"2015-05-05\" }";

        DatePojo marshalled = mapper.readValue(json.getBytes(), DatePojo.class);

        assertNotNull(marshalled);
    }


}
