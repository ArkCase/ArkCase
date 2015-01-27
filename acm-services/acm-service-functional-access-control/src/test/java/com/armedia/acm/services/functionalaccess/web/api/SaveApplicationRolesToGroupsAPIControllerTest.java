package com.armedia.acm.services.functionalaccess.web.api;

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;

import org.easymock.Capture;
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
import com.armedia.acm.services.users.model.group.AcmGroup;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author riste.tutureski
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/spring/spring-config-functional-access-service-test.xml"
})
public class SaveApplicationRolesToGroupsAPIControllerTest extends EasyMockSupport {

private Logger LOG = LoggerFactory.getLogger(getClass());
	
	private MockMvc mockMvc;
	private SaveApplicationRolesToGroupsAPIController unit;
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
		setUnit(new SaveApplicationRolesToGroupsAPIController());
		setMockMvc(MockMvcBuilders.standaloneSetup(getUnit()).setHandlerExceptionResolvers(getExceptionResolver()).build());
		setMockAuthentication(createMock(Authentication.class));
		setFunctionalAccessServiceTest(createMock(FunctionalAccessService.class));
		
		getUnit().setFunctionalAccessService(getFunctionalAccessServiceTest());
    }
	
	@Test
    public void saveApplicationRolesToGroupsTest() throws Exception {
		
		ObjectMapper objectMapper = new ObjectMapper();
		Map<String, List<String>> input = prepareRoleToGroupsForRetrieving(getApplicationRolesToGroupsTestData());
		String content = objectMapper.writeValueAsString(input);
		
		Capture<Map<String, List<String>>> found = new Capture<Map<String, List<String>>>();
		Capture<Authentication> auth = new Capture<Authentication>();
		
		expect(getFunctionalAccessServiceTest().saveApplicationRolesToGroups(capture(found), capture(auth))).andReturn(true);
		expect(getMockAuthentication().getName()).andReturn("user");
		
		replayAll();
		
		MvcResult result = getMockMvc().perform(
	            post("/api/v1/functionalaccess/rolestogroups")
                    .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .principal(getMockAuthentication())
                    .content(content))
	                .andReturn();
		
		LOG.info("Results: " + result.getResponse().getContentAsString());
		
		assertEquals("true", result.getResponse().getContentAsString());
		assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
		
	}
	
	private Map<String, List<String>> prepareRoleToGroupsForRetrieving(Map<String, String> rolesToGroups)
	{
		Map<String, List<String>> retval = new HashMap<String, List<String>>();
		
		if (rolesToGroups != null && rolesToGroups.size() > 0)
		{
			for (Entry<String, String> entry : rolesToGroups.entrySet())
			{
				retval.put(entry.getKey(), Arrays.asList(entry.getValue().split(",")));
			}
		}
		
		return retval;
	}
	
	public MockMvc getMockMvc() {
		return mockMvc;
	}
	
	public void setMockMvc(MockMvc mockMvc) {
		this.mockMvc = mockMvc;
	}

	public SaveApplicationRolesToGroupsAPIController getUnit() {
		return unit;
	}

	public void setUnit(SaveApplicationRolesToGroupsAPIController unit) {
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
