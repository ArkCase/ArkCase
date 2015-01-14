package com.armedia.acm.services.functionalaccess.web.api;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import com.armedia.acm.services.functionalaccess.service.FunctionalAccessService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author riste.tutureski
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/spring/spring-config-functional-access-service-test.xml"
})
public class GetApplicationRolesToGroupsAPIControllerTest extends EasyMockSupport {

private Logger LOG = LoggerFactory.getLogger(getClass());
	
	private MockMvc mockMvc;
	private GetApplicationRolesToGroupsAPIController unit;
	private Authentication mockAuthentication;
	
	@Autowired
	private FunctionalAccessService functionalAccessServiceTest;
	
	@Resource(name="applicationRolesToGroupsTestData")
	private Map<String, String> applicationRolesToGroupsTestData;
	
	@Autowired
    private ExceptionHandlerExceptionResolver exceptionResolver;
	
	@Before
    public void setUp() throws Exception
    {
		setUnit(new GetApplicationRolesToGroupsAPIController());
		setMockMvc(MockMvcBuilders.standaloneSetup(getUnit()).setHandlerExceptionResolvers(getExceptionResolver()).build());
		setMockAuthentication(createMock(Authentication.class));
		
		getUnit().setFunctionalAccessService(getFunctionalAccessServiceTest());
    }
	
	@Test
    public void getApplicationRolesToGroupsTest() throws Exception {
		
		expect(getMockAuthentication().getName()).andReturn("user");
		
		replayAll();
		
		MvcResult result = getMockMvc().perform(
	            get("/api/v1/functionalaccess/rolestogroups")
                    .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .principal(getMockAuthentication()))
	                .andReturn();
		
		LOG.info("Results: " + result.getResponse().getContentAsString());
		
		Map<String, List<String>> resultTestData = new HashMap<String, List<String>>();
		ObjectMapper mapper = new ObjectMapper();
		
		try 
		{
			resultTestData = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<HashMap<String, List<String>>>(){});
	    } 
		catch (Exception e) {
	        LOG.error("Cannot create map from source: " + result.getResponse().getContentAsString());
	    }
		
		assertEquals(getApplicationRolesToGroupsTestData().size(), resultTestData.size());
		assertEquals(getApplicationRolesToGroupsTestData().get("ROLE_INVESTIGATOR"), StringUtils.join(resultTestData.get("ROLE_INVESTIGATOR"), ","));
		assertEquals("acm_investigator_dev,acm_administrator_dev", StringUtils.join(resultTestData.get("ROLE_INVESTIGATOR"), ","));
		assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
		
	}
	
	public MockMvc getMockMvc() {
		return mockMvc;
	}
	
	public void setMockMvc(MockMvc mockMvc) {
		this.mockMvc = mockMvc;
	}
	
	public GetApplicationRolesToGroupsAPIController getUnit() {
		return unit;
	}

	public void setUnit(GetApplicationRolesToGroupsAPIController unit) {
		this.unit = unit;
	}

	public Authentication getMockAuthentication() {
		return mockAuthentication;
	}
	
	public void setMockAuthentication(Authentication mockAuthentication) {
		this.mockAuthentication = mockAuthentication;
	}

	public FunctionalAccessService getFunctionalAccessServiceTest() {
		return functionalAccessServiceTest;
	}

	public void setFunctionalAccessServiceTest(
			FunctionalAccessService functionalAccessServiceTest) {
		this.functionalAccessServiceTest = functionalAccessServiceTest;
	}

	public ExceptionHandlerExceptionResolver getExceptionResolver() {
		return exceptionResolver;
	}

	public void setExceptionResolver(
			ExceptionHandlerExceptionResolver exceptionResolver) {
		this.exceptionResolver = exceptionResolver;
	}

	public Map<String, String> getApplicationRolesToGroupsTestData() {
		return applicationRolesToGroupsTestData;
	}

	public void setApplicationRolesToGroupsTestData(
			Map<String, String> applicationRolesToGroupsTestData) {
		this.applicationRolesToGroupsTestData = applicationRolesToGroupsTestData;
	}
	
}
