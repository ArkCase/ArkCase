/**
 * 
 */
package com.armedia.acm.services.users.web.api.group;

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import org.codehaus.jackson.map.ObjectMapper;
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

import com.armedia.acm.services.users.dao.group.AcmGroupDao;
import com.armedia.acm.services.users.dao.ldap.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.group.AcmGroup;

/**
 * @author riste.tutureski
 *
 */
/**
 * @author riste.tutureski
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/spring/spring-web-acm-web.xml",
        "classpath:/spring/spring-config-user-service-test-dummy-beans.xml"
})
public class SaveSupervisorToGroupAPIControllerTest extends EasyMockSupport {

	private Logger LOG = LoggerFactory.getLogger(getClass());
	
	private MockMvc mockMvc;
	private SaveSupervisorToGroupAPIController unit;
	private Authentication mockAuthentication;
	private AcmGroupDao mockGroupDao;
	private UserDao mockUserDao;
	
	@Autowired
    private ExceptionHandlerExceptionResolver exceptionResolver;
	
	@Before
    public void setUp() throws Exception
    {
		setUnit(new SaveSupervisorToGroupAPIController());
		setMockMvc(MockMvcBuilders.standaloneSetup(getUnit()).setHandlerExceptionResolvers(getExceptionResolver()).build());
		setMockAuthentication(createMock(Authentication.class));	
		setMockGroupDao(createMock(AcmGroupDao.class));
		setMockUserDao(createMock(UserDao.class));
		
		getUnit().setGroupDao(getMockGroupDao());
		getUnit().setUserDao(getMockUserDao());
    }
	
	@Test
    public void saveSupervisorToGroupTest() throws Exception
    {   
		AcmGroup group = new AcmGroup();
		
		group.setName("Group Name");
		group.setDescription("Group Description");
		group.setType("Group Type");
		group.setStatus("Group Status");
		
		AcmUser supervisor = new AcmUser();
		supervisor.setUserId("test-user");
		supervisor.setUserDirectoryName("Test Directory Name");
		supervisor.setUserState("TEST");
		supervisor.setFirstName("First Name");
		supervisor.setLastName("Last Name");
		
		ObjectMapper objectMapper = new ObjectMapper();
		String supervisorAsJson = objectMapper.writeValueAsString(supervisor);
		
		LOG.debug("Input JSON: " + supervisorAsJson);
		
		Capture<AcmGroup> found = new Capture<AcmGroup>();
		Capture<String> userIdCapture = new Capture<String>();

		expect(getMockGroupDao().findByName(group.getName())).andReturn(group);
		expect(getMockUserDao().findByUserId(capture(userIdCapture))).andReturn(supervisor);
		expect(getMockGroupDao().save(capture(found))).andReturn(group);
		expect(getMockAuthentication().getName()).andReturn("user");
		
		replayAll();
		
		MvcResult result = getMockMvc().perform(
	            post("/api/v1/users/group/" + group.getName() + "/supervisor/save/false")
	                    .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
	                    .contentType(MediaType.APPLICATION_JSON)
	                    .principal(getMockAuthentication())
	                    .content(supervisorAsJson))
	                .andReturn();
		
		LOG.info("Results: " + result.getResponse().getContentAsString());
		
		verifyAll();
		
		AcmGroup resultGroup = objectMapper.readValue(result.getResponse().getContentAsString(), AcmGroup.class);
		
		assertEquals(supervisor.getUserId(), resultGroup.getSupervisor().getUserId());
		assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
    }

	public MockMvc getMockMvc() {
		return mockMvc;
	}

	public void setMockMvc(MockMvc mockMvc) {
		this.mockMvc = mockMvc;
	}

	public SaveSupervisorToGroupAPIController getUnit() {
		return unit;
	}

	public void setUnit(SaveSupervisorToGroupAPIController unit) {
		this.unit = unit;
	}

	public Authentication getMockAuthentication() {
		return mockAuthentication;
	}

	public void setMockAuthentication(Authentication mockAuthentication) {
		this.mockAuthentication = mockAuthentication;
	}

	public ExceptionHandlerExceptionResolver getExceptionResolver() {
		return exceptionResolver;
	}

	public void setExceptionResolver(
			ExceptionHandlerExceptionResolver exceptionResolver) {
		this.exceptionResolver = exceptionResolver;
	}

	public AcmGroupDao getMockGroupDao() {
		return mockGroupDao;
	}

	public void setMockGroupDao(AcmGroupDao mockGroupDao) {
		this.mockGroupDao = mockGroupDao;
	}

	public UserDao getMockUserDao() {
		return mockUserDao;
	}

	public void setMockUserDao(UserDao mockUserDao) {
		this.mockUserDao = mockUserDao;
	}
	
}
