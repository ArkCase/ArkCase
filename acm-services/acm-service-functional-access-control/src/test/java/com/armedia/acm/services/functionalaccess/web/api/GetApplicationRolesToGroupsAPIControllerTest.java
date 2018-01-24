package com.armedia.acm.services.functionalaccess.web.api;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import com.armedia.acm.services.functionalaccess.service.FunctionalAccessService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.lang.StringUtils;
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author riste.tutureski
 */
public class GetApplicationRolesToGroupsAPIControllerTest extends EasyMockSupport
{

    private Logger LOG = LoggerFactory.getLogger(getClass());

    private MockMvc mockMvc;
    private GetApplicationRolesToGroupsAPIController unit;
    private Authentication mockAuthentication;

    private FunctionalAccessService mockFunctionalAccessService;

    @Autowired
    private ExceptionHandlerExceptionResolver exceptionResolver;

    @Before
    public void setUp() throws Exception
    {
        setUnit(new GetApplicationRolesToGroupsAPIController());

        mockFunctionalAccessService = createMock(FunctionalAccessService.class);
        setMockMvc(MockMvcBuilders.standaloneSetup(getUnit()).setHandlerExceptionResolvers(getExceptionResolver()).build());
        setMockAuthentication(createMock(Authentication.class));

        getUnit().setFunctionalAccessService(mockFunctionalAccessService);
    }

    @Test
    public void getApplicationRolesToGroupsTest() throws Exception
    {

        expect(getMockAuthentication().getName()).andReturn("user");

        Map<String, List<String>> rolesToGroups = new HashMap<>();

        rolesToGroups.put("ROLE_INVESTIGATOR", Arrays.asList("acm_investigator_dev", "acm_administrator_dev"));

        expect(mockFunctionalAccessService.getApplicationRolesToGroups()).andReturn(rolesToGroups);

        replayAll();

        MvcResult result = getMockMvc().perform(
                get("/api/v1/functionalaccess/rolestogroups")
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(getMockAuthentication()))
                .andReturn();

        LOG.info("Results: " + result.getResponse().getContentAsString());

        Map<String, List<String>> resultTestData = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();

        try
        {
            resultTestData = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<HashMap<String, List<String>>>()
            {
            });
        }
        catch (Exception e)
        {
            LOG.error("Cannot create map from source: " + result.getResponse().getContentAsString());
        }

        assertEquals(rolesToGroups.size(), resultTestData.size());
        assertEquals(rolesToGroups.get("ROLE_INVESTIGATOR"), resultTestData.get("ROLE_INVESTIGATOR"));
        assertEquals("acm_investigator_dev,acm_administrator_dev", StringUtils.join(resultTestData.get("ROLE_INVESTIGATOR"), ","));
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

    public GetApplicationRolesToGroupsAPIController getUnit()
    {
        return unit;
    }

    public void setUnit(GetApplicationRolesToGroupsAPIController unit)
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
