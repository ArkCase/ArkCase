package com.armedia.acm.services.functionalaccess.web.api;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

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
public class GetApplicationRolesAPIControllerTest extends EasyMockSupport {

private Logger LOG = LoggerFactory.getLogger(getClass());
	
	private MockMvc mockMvc;
	private GetApplicationRolesAPIController unit;
	private Authentication mockAuthentication;
	
	@Autowired
	private FunctionalAccessService functionalAccessServiceTest;
	
	@Resource(name="applicationRolesTestData")
	private Map<String, String> applicationRolesTestData;
	
	@Autowired
    private ExceptionHandlerExceptionResolver exceptionResolver;
	
	@Before
    public void setUp() throws Exception
    {
		setUnit(new GetApplicationRolesAPIController());
		setMockMvc(MockMvcBuilders.standaloneSetup(getUnit()).setHandlerExceptionResolvers(getExceptionResolver()).build());
		setMockAuthentication(createMock(Authentication.class));
		
		getUnit().setFunctionalAccessService(getFunctionalAccessServiceTest());
    }
	
	@Test
    public void getApplicationRolesTest() throws Exception {
		
		expect(getMockAuthentication().getName()).andReturn("user");
		
		replayAll();
		
		MvcResult result = getMockMvc().perform(
	            get("/api/v1/functionalaccess/roles")
                    .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .principal(getMockAuthentication()))
	                .andReturn();
		
		LOG.info("Results: " + result.getResponse().getContentAsString());
		
		List<String> expectedTestData = new ArrayList<String>();
		List<String> resultTestData = new ArrayList<String>();
		ObjectMapper mapper = new ObjectMapper();
		
		try 
		{
			expectedTestData = Arrays.asList(getApplicationRolesTestData().get("application.roles").toString().split(","));
			resultTestData = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<ArrayList<String>>(){});
	    } 
		catch (Exception e) {
	        LOG.error("Cannot create list from source: " + result.getResponse().getContentAsString());
	    }
		
		assertEquals(expectedTestData.size(), resultTestData.size());
		assertEquals(expectedTestData.get(2), resultTestData.get(2));
		assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
		
	}
	
	public MockMvc getMockMvc() {
		return mockMvc;
	}
	
	public void setMockMvc(MockMvc mockMvc) {
		this.mockMvc = mockMvc;
	}
	
	public GetApplicationRolesAPIController getUnit() {
		return unit;
	}
	
	public void setUnit(GetApplicationRolesAPIController unit) {
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

	public Map<String, String> getApplicationRolesTestData() {
		return applicationRolesTestData;
	}

	public void setApplicationRolesTestData(
			Map<String, String> applicationRolesTestData) {
		this.applicationRolesTestData = applicationRolesTestData;
	}
	
}
