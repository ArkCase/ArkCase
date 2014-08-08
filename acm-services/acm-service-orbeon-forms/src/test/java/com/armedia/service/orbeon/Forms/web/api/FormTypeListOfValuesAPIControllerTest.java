package com.armedia.service.orbeon.Forms.web.api;

import com.armedia.acm.configuration.ListOfValuesService;
import com.armedia.acm.configuration.LookupTableDescriptor;
import com.armedia.acm.service.orbeon.forms.web.api.FormTypeListOfValuesAPIController;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import org.codehaus.jackson.map.ObjectMapper;

import static org.easymock.EasyMock.*;
import org.easymock.EasyMockSupport;
import static org.junit.Assert.*;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/spring/spring-web-acm-web.xml",
        "classpath:/spring/spring-library-form-service-test.xml"
})
public class FormTypeListOfValuesAPIControllerTest extends EasyMockSupport
{
    private MockMvc mockMvc;
    private Authentication mockAuthentication;

    @Autowired
    private ExceptionHandlerExceptionResolver exceptionResolver;

    private FormTypeListOfValuesAPIController  unit;

    private ListOfValuesService mockListOfValuesService;

    private Logger log = LoggerFactory.getLogger(getClass());

    private LookupTableDescriptor typeDescriptor = new LookupTableDescriptor();

    @Before
    public void setUp() throws Exception
    {
        unit = new FormTypeListOfValuesAPIController ();

        mockMvc = MockMvcBuilders.standaloneSetup(unit).setHandlerExceptionResolvers(exceptionResolver).build();

        mockListOfValuesService = createMock(ListOfValuesService.class);
        mockAuthentication = createMock(Authentication.class);

        typeDescriptor.setTableName("formTypeTable");

        unit.setListOfValuesService(mockListOfValuesService);
        unit.setTypesDescriptor(typeDescriptor);
    }

    @Test
    public void getFormTypes() throws Exception
    {
        List<String> typeList = Arrays.asList("Form Type 1", "Form Type 2", "Form Type 3");

        expect(mockListOfValuesService.lookupListOfStringValues(typeDescriptor)).andReturn(typeList);

        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("user");

        replayAll();

        MvcResult result = mockMvc.perform(
                get("/api/latest/service/form/types")
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
    public void getFormTypes_exception() throws Exception
    {
        expect(mockListOfValuesService.lookupListOfStringValues(typeDescriptor)).andThrow(new CannotGetJdbcConnectionException(
                "testException", new SQLException("testException")));

        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("user");

        replayAll();

        mockMvc.perform(
                get("/api/latest/service/form//types")
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .principal(mockAuthentication))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.TEXT_PLAIN));

        verifyAll();
    }

  
}
