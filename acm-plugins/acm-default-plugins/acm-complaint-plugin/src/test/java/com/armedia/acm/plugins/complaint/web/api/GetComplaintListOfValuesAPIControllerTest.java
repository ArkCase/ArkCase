package com.armedia.acm.plugins.complaint.web.api;

import com.armedia.acm.configuration.ListOfValuesService;
import com.armedia.acm.configuration.LookupTableDescriptor;
import org.codehaus.jackson.map.ObjectMapper;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.xml.MarshallingHttpMessageConverter;
import org.springframework.oxm.castor.CastorMarshaller;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;


import java.util.Properties;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/spring/spring-web-acm-web.xml",
        "classpath:/spring/spring-library-complaint-plugin-test.xml"
})
public class GetComplaintListOfValuesAPIControllerTest extends EasyMockSupport
{
    private MockMvc mockMvc;
    private Authentication mockAuthentication;

    @Autowired
    private ExceptionHandlerExceptionResolver exceptionResolver;

    private GetComplaintListOfValuesAPIController unit;

    private Logger log = LoggerFactory.getLogger(getClass());

    private Properties complaintProperties = new Properties();



    @Before
    public void setUp() throws Exception
    {
        unit = new GetComplaintListOfValuesAPIController();

        mockMvc = MockMvcBuilders.standaloneSetup(unit).setHandlerExceptionResolvers(exceptionResolver).build();

        mockAuthentication = createMock(Authentication.class);

        complaintProperties.setProperty("complaint.priorities", "1,2,3,4");
        complaintProperties.setProperty("complaint.complaint-types", "A,B,C,D");

        unit.setComplaintProperties(complaintProperties);

    }

    @Test
    public void getComplaintTypes() throws Exception
    {
        String[] typeList = { "A", "B", "C", "D" };

        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("user");

        replayAll();

        MvcResult result = mockMvc.perform(
                get("/api/latest/plugin/complaint/types")
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                .principal(mockAuthentication))
                .andReturn();

        verifyAll();

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertTrue(result.getResponse().getContentType().startsWith(MediaType.APPLICATION_JSON_VALUE));

        String returned = result.getResponse().getContentAsString();

        log.info("results: " + returned);

        ObjectMapper objectMapper = new ObjectMapper();

        String types[] = objectMapper.readValue(returned, String[].class);

        assertEquals(4, types.length);

        assertArrayEquals(typeList, types);


    }

    @Test
    public void getComplaintPriorities() throws Exception
    {
        String[] priorityList = { "1", "2", "3", "4" };

        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("user");

        replayAll();

        MvcResult result = mockMvc.perform(
                get("/api/latest/plugin/complaint/priorities")
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                .principal(mockAuthentication))
                .andReturn();

        verifyAll();

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertTrue(result.getResponse().getContentType().startsWith(MediaType.APPLICATION_JSON_VALUE));

        String returned = result.getResponse().getContentAsString();

        log.info("results: " + returned);

        ObjectMapper objectMapper = new ObjectMapper();

        String priorities[] = objectMapper.readValue(returned, String[].class);

        assertEquals(4, priorities.length);

        assertArrayEquals(priorityList, priorities);


    }

    @Test
    public void getComplaintPriorities_xml() throws Exception
    {

        MarshallingHttpMessageConverter xmlConverter = new MarshallingHttpMessageConverter();
        CastorMarshaller marshaller = new CastorMarshaller();
        marshaller.afterPropertiesSet();
        marshaller.setValidating(false);
        xmlConverter.setMarshaller(marshaller);
        xmlConverter.setUnmarshaller(marshaller);

        mockMvc = MockMvcBuilders.
                standaloneSetup(unit).
                setHandlerExceptionResolvers(exceptionResolver).
                setMessageConverters(xmlConverter).
                build();

        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("user");

        replayAll();

        MvcResult result = mockMvc.perform(
                get("/api/latest/plugin/complaint/priorities")
                .accept(MediaType.parseMediaType("text/xml"))
                .principal(mockAuthentication))
                .andReturn();

        verifyAll();

        String returned = result.getResponse().getContentAsString();
        log.info("results: " + returned);

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertTrue(result.getResponse().getContentType().startsWith(MediaType.TEXT_XML_VALUE));


    }

}
