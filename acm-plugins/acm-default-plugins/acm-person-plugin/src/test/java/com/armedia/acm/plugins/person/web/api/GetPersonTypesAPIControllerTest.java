package com.armedia.acm.plugins.person.web.api;

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
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/spring/spring-web-acm-web.xml",
        "classpath:/spring/spring-library-person-plugin-test.xml"
})
public class GetPersonTypesAPIControllerTest extends EasyMockSupport
{
    private MockMvc mockMvc;
    private Authentication mockAuthentication;

    @Autowired
    private ExceptionHandlerExceptionResolver exceptionResolver;

    private GetPersonTypesAPIController unit;

    private ListOfValuesService mockListOfValuesService;

    private Logger log = LoggerFactory.getLogger(getClass());

    private LookupTableDescriptor typeDescriptor = new LookupTableDescriptor();

    @Before
    public void setUp() throws Exception
    {
        unit = new GetPersonTypesAPIController();

        mockMvc = MockMvcBuilders.standaloneSetup(unit).setHandlerExceptionResolvers(exceptionResolver).build();

        mockListOfValuesService = createMock(ListOfValuesService.class);
        mockAuthentication = createMock(Authentication.class);

        typeDescriptor.setTableName("typeTable");

        unit.setListOfValuesService(mockListOfValuesService);
        unit.setTypesDescriptor(typeDescriptor);
    }

    @Test
    public void getPersonTypes() throws Exception
    {
        List<String> typeList = Arrays.asList("Type 1", "Type 2", "Type 3");

        expect(mockListOfValuesService.lookupListOfStringValues(typeDescriptor)).andReturn(typeList);

        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("user");

        replayAll();

        MvcResult result = mockMvc.perform(
                get("/api/latest/plugin/person/types")
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

        assertEquals(3, types.length);


    }

    @Test
    public void getPersonTypes_exception() throws Exception
    {
        expect(mockListOfValuesService.lookupListOfStringValues(typeDescriptor)).andThrow(new CannotGetJdbcConnectionException(
                "testException", new SQLException("testException")));

        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("user");

        replayAll();

        mockMvc.perform(
                get("/api/latest/plugin/person/types")
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .principal(mockAuthentication))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.TEXT_PLAIN));

        verifyAll();
    }

  
}
