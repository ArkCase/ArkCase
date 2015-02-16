/**
 * 
 */
package com.armedia.acm.services.users.web.api.group;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

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
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import com.armedia.acm.services.users.dao.group.AcmGroupDao;
import com.armedia.acm.services.users.model.group.AcmGroup;
import com.armedia.acm.services.users.model.group.AcmGroupStatus;

/**
 * @author riste.tutureski
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/spring/spring-web-acm-web.xml",
        "classpath:/spring/spring-config-user-service-test-dummy-beans.xml"
})
public class RemoveGroupAPIControllerTest extends EasyMockSupport {

	private Logger LOG = LoggerFactory.getLogger(getClass());
	
	private MockMvc mockMvc;
	private RemoveGroupAPIController unit;
	private Authentication mockAuthentication;
	private AcmGroupDao mockGroupDao;
	
	@Autowired
    private ExceptionHandlerExceptionResolver exceptionResolver;
	
	@Before
    public void setUp() throws Exception
    {
		setUnit(new RemoveGroupAPIController());
		setMockMvc(MockMvcBuilders.standaloneSetup(getUnit()).setHandlerExceptionResolvers(getExceptionResolver()).build());
		setMockAuthentication(createMock(Authentication.class));	
		setMockGroupDao(createMock(AcmGroupDao.class));
		
		getUnit().setGroupDao(getMockGroupDao());
    }
	
	@Test
    public void removeGroupTest_true() throws Exception
    {   
		AcmGroup group = new AcmGroup();
		
		group.setName("Name");
		group.setStatus(AcmGroupStatus.DELETE);
		
		expect(getMockGroupDao().markGroupDelete(group.getName())).andReturn(group);
		expect(getMockAuthentication().getName()).andReturn("user");
		
		replayAll();
		
		MvcResult result = getMockMvc().perform(
	            delete("/api/v1/users/group/{groupId}/remove", group.getName())
	                    .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
	                    .contentType(MediaType.APPLICATION_JSON)
	                    .principal(getMockAuthentication()))
	                .andReturn();
		
		LOG.info("Results: " + result.getResponse().getContentAsString());
		
		verifyAll();
		
		ObjectMapper objectMapper = new ObjectMapper();
		AcmGroup resultGroup = objectMapper.readValue(result.getResponse().getContentAsString(), AcmGroup.class);
		
		assertEquals(AcmGroupStatus.DELETE, resultGroup.getStatus());
		assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
    }
	
	@Test
    public void removeGroupTest_false() throws Exception
    {   
		AcmGroup group = new AcmGroup();
		
		group.setName("Name");
		
		expect(getMockGroupDao().markGroupDelete((group.getName()))).andReturn(group);
		expect(getMockAuthentication().getName()).andReturn("user");
		
		replayAll();
		
		MvcResult result = getMockMvc().perform(
	            delete("/api/v1/users/group/{groupId}/remove", group.getName())
	                    .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
	                    .contentType(MediaType.APPLICATION_JSON)
	                    .principal(getMockAuthentication()))
	                .andReturn();
		
		LOG.info("Results: " + result.getResponse().getContentAsString());
		
		verifyAll();
		
		ObjectMapper objectMapper = new ObjectMapper();
		AcmGroup resultGroup = objectMapper.readValue(result.getResponse().getContentAsString(), AcmGroup.class);
		
		assertEquals(null, resultGroup.getStatus());
		assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
    }

	public MockMvc getMockMvc() {
		return mockMvc;
	}

	public void setMockMvc(MockMvc mockMvc) {
		this.mockMvc = mockMvc;
	}

	public RemoveGroupAPIController getUnit() {
		return unit;
	}

	public void setUnit(RemoveGroupAPIController unit) {
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
	
}
