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
import com.armedia.acm.services.users.model.group.AcmGroup;


/**
 * @author riste.tutureski
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/spring/spring-web-acm-web.xml",
        "classpath:/spring/spring-config-user-service-test-dummy-beans.xml"
})
public class SaveGroupAPIControllerTest extends EasyMockSupport {

	private Logger LOG = LoggerFactory.getLogger(getClass());
	
	private MockMvc mockMvc;
	private SaveGroupAPIController unit;
	private Authentication mockAuthentication;
	private AcmGroupDao mockGroupDao;
	
	@Autowired
    private ExceptionHandlerExceptionResolver exceptionResolver;
	
	@Before
    public void setUp() throws Exception
    {
		setUnit(new SaveGroupAPIController());
		setMockMvc(MockMvcBuilders.standaloneSetup(getUnit()).setHandlerExceptionResolvers(getExceptionResolver()).build());
		setMockAuthentication(createMock(Authentication.class));	
		setMockGroupDao(createMock(AcmGroupDao.class));
		
		getUnit().setGroupDao(getMockGroupDao());
    }
	
	@Test
    public void saveGroupTest() throws Exception
    {   
		AcmGroup group = new AcmGroup();
		
		group.setName("Group Name");
		group.setDescription("Group Description");
		group.setType("Group Type");
		group.setStatus("Group Status");
		
		ObjectMapper objectMapper = new ObjectMapper();
		String groupAsJson = objectMapper.writeValueAsString(group);
		
		LOG.debug("Input JSON: " + groupAsJson);
		
		Capture<AcmGroup> found = new Capture<AcmGroup>();
		
		expect(getMockGroupDao().findByName(group.getName())).andReturn(null);
		expect(getMockGroupDao().save(capture(found))).andReturn(group);
		expect(getMockAuthentication().getName()).andReturn("user");
		
		replayAll();
		
		MvcResult result = getMockMvc().perform(
	            post("/api/v1/users/group/save")
	                    .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
	                    .contentType(MediaType.APPLICATION_JSON)
	                    .principal(getMockAuthentication())
	                    .content(groupAsJson))
	                .andReturn();
		
		LOG.info("Results: " + result.getResponse().getContentAsString());
		
		verifyAll();
		
		assertEquals(group.getName(), found.getValue().getName());
		assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
    }
	
	@Test
    public void saveSubGroupTest() throws Exception
    {   
		AcmGroup group = new AcmGroup();
		
		group.setName("Group Name");
		group.setDescription("Group Description");
		group.setType("Group Type");
		group.setStatus("Group Status");
		
		AcmGroup subGroup = new AcmGroup();
		
		subGroup.setName("Sub Group Name");
		subGroup.setDescription("Sub Group Description");
		subGroup.setType("Sub Group Type");
		subGroup.setStatus("Sub Group Status");
		
		ObjectMapper objectMapper = new ObjectMapper();
		String groupAsJson = objectMapper.writeValueAsString(subGroup);
		
		LOG.debug("Input JSON: " + groupAsJson);
		
		Capture<AcmGroup> found = new Capture<AcmGroup>();
		
		expect(getMockGroupDao().findByName(group.getName())).andReturn(group);
		expect(getMockGroupDao().findByName(subGroup.getName())).andReturn(null);
		expect(getMockGroupDao().save(capture(found))).andReturn(subGroup);
		expect(getMockAuthentication().getName()).andReturn("user");
		
		replayAll();
		
		MvcResult result = getMockMvc().perform(
	            post("/api/v1/users/group/save/" + group.getName())
	                    .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
	                    .contentType(MediaType.APPLICATION_JSON)
	                    .principal(getMockAuthentication())
	                    .content(groupAsJson))
	                .andReturn();
		
		LOG.info("Results: " + result.getResponse().getContentAsString());
		
		verifyAll();
		
		assertEquals(subGroup.getName(), found.getValue().getName());
		assertEquals(group.getName(), found.getValue().getParentGroup().getName());
		assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
    }

	public MockMvc getMockMvc() {
		return mockMvc;
	}

	public void setMockMvc(MockMvc mockMvc) {
		this.mockMvc = mockMvc;
	}

	public SaveGroupAPIController getUnit() {
		return unit;
	}

	public void setUnit(SaveGroupAPIController unit) {
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
