package com.armedia.acm.services.functionalaccess.web.api;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import com.armedia.acm.services.functionalaccess.service.FunctionalAccessService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author riste.tutureski
 */
public class GetApplicationRolesAPIControllerTest extends EasyMockSupport
{

    private Logger LOG = LoggerFactory.getLogger(getClass());

    private MockMvc mockMvc;
    private GetApplicationRolesAPIController unit;
    private Authentication mockAuthentication;

    private FunctionalAccessService mockFunctionalAccessService;

    @Autowired
    private ExceptionHandlerExceptionResolver exceptionResolver;

    @Before
    public void setUp() throws Exception
    {
        setUnit(new GetApplicationRolesAPIController());
        setMockMvc(MockMvcBuilders.standaloneSetup(getUnit()).setHandlerExceptionResolvers(getExceptionResolver()).build());
        setMockAuthentication(createMock(Authentication.class));

        mockFunctionalAccessService = createMock(FunctionalAccessService.class);

        getUnit().setFunctionalAccessService(mockFunctionalAccessService);
    }

    @Test
    public void getApplicationRolesTest() throws Exception
    {

        expect(getMockAuthentication().getName()).andReturn("user");

        List<String> roles = Arrays.asList("role1", "role2");
        expect(mockFunctionalAccessService.getApplicationRoles()).andReturn(roles);

        replayAll();

        MvcResult result = getMockMvc().perform(
                get("/api/v1/functionalaccess/roles")
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(getMockAuthentication()))
                .andReturn();

        LOG.info("Results: " + result.getResponse().getContentAsString());
        List<String> resultTestData = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();

        try
        {
            resultTestData = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<ArrayList<String>>()
            {
            });
        }
        catch (Exception e)
        {
            LOG.error("Cannot create list from source: " + result.getResponse().getContentAsString());
        }

        assertEquals(roles.size(), resultTestData.size());
        assertEquals(roles.get(1), resultTestData.get(1));
        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());

    }

    public MockMvc getMockMvc()
    {
        return mockMvc;
    }

    public void setMockMvc(MockMvc mockMvc)
    {
        this.mockMvc = mockMvc;
    }

    public GetApplicationRolesAPIController getUnit()
    {
        return unit;
    }

    public void setUnit(GetApplicationRolesAPIController unit)
    {
        this.unit = unit;
    }

    public Authentication getMockAuthentication()
    {
        return mockAuthentication;
    }

    public void setMockAuthentication(Authentication mockAuthentication)
    {
        this.mockAuthentication = mockAuthentication;
    }

    public ExceptionHandlerExceptionResolver getExceptionResolver()
    {
        return exceptionResolver;
    }

    public void setExceptionResolver(
            ExceptionHandlerExceptionResolver exceptionResolver)
    {
        this.exceptionResolver = exceptionResolver;
    }

}
