package com.armedia.acm.services.functionalaccess.web.api;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

import com.armedia.acm.pluginmanager.service.AcmPluginManager;
import com.armedia.acm.services.functionalaccess.service.FunctionalAccessServiceImpl;
import com.armedia.acm.services.users.dao.group.AcmGroupDao;
import com.armedia.acm.services.users.dao.ldap.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.group.AcmGroup;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author riste.tutureski
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/spring/spring-config-functional-access-service-test.xml"
})
public class GetUsersByPrivilegeAndGroupAPIControllerTest extends EasyMockSupport {

private Logger LOG = LoggerFactory.getLogger(getClass());
	
	private MockMvc mockMvc;
	private GetUsersByPrivilegeAndGroupAPIController unit;
	private Authentication mockAuthentication;
	private FunctionalAccessServiceImpl functionalAccessService;
	private AcmPluginManager mockPluginManager;
	private UserDao mockUserDao;
	private AcmGroupDao mockAcmGroupDao;
	
	@Resource(name="applicationRolesToGroupsTestData")
	private Map<String, String> applicationRolesToGroupsTestData;
	
	@Autowired
    private ExceptionHandlerExceptionResolver exceptionResolver;
	
	@Before
    public void setUp() throws Exception
    {
		unit = new GetUsersByPrivilegeAndGroupAPIController();
		mockMvc = MockMvcBuilders.standaloneSetup(unit).setHandlerExceptionResolvers(exceptionResolver).build();
		mockAuthentication = createMock(Authentication.class);
		mockPluginManager = createMock(AcmPluginManager.class);
		mockUserDao = createMock(UserDao.class);
		mockAcmGroupDao = createMock(AcmGroupDao.class);
		
		functionalAccessService = new FunctionalAccessServiceImpl();
		unit.setFunctionalAccessService(functionalAccessService);
		unit.setPluginManager(mockPluginManager);
		functionalAccessService.setUserDao(mockUserDao);
		functionalAccessService.setAcmGroupDao(mockAcmGroupDao);
		functionalAccessService.setApplicationRolesToGroupsProperties(applicationRolesToGroupsTestData);
    }
	
	@Test
    public void usersByPrivilegeTest() throws Exception {
		
		String privilege = "acm-privilege";
		String role1 = "ROLE_ADMINISTRATOR";
		String role2 = "ROLE_INVESTIGATOR_SUPERVISOR";
		
		// Group 1
		Set<AcmUser> membersGroup1 = new HashSet<>();
		
		AcmUser user1 = new AcmUser();
		user1.setUserId("user1");
		
		AcmUser user2 = new AcmUser();
		user2.setUserId("user2");
		
		membersGroup1.addAll(Arrays.asList(user1, user2));
		
		AcmGroup group1 = new AcmGroup();
		group1.setName("acm_administrator_dev");
		group1.setMembers(membersGroup1);
		
		// Group 2
		Set<AcmUser> membersGroup2 = new HashSet<>();
		
		AcmUser user3 = new AcmUser();
		user3.setUserId("user3");
		
		AcmUser user4 = new AcmUser();
		user4.setUserId("user4");
		
		membersGroup2.addAll(Arrays.asList(user3, user4));
		
		AcmGroup group2 = new AcmGroup();
		group2.setName("acm_supervisor_dev");
		group2.setMembers(membersGroup2);
		
		List<AcmUser> expectedUserList = Arrays.asList(user1, user2, user3, user4);

		List<String> rolesForPrivilege = Arrays.asList(role1, role2);
		Map<String, List<String>> rolesToGroups = new HashMap<>();
		rolesToGroups.put(role1, Arrays.asList(group1.getName()));
		rolesToGroups.put(role2, Arrays.asList(group2.getName()));
		
		expect(mockAuthentication.getName()).andReturn("user");
		expect(mockPluginManager.getRolesForPrivilege(privilege)).andReturn(rolesForPrivilege);
		expect(mockAcmGroupDao.findByName(group1.getName())).andReturn(group1);
		expect(mockAcmGroupDao.findByName(group2.getName())).andReturn(group2);
		
		replayAll();
		
		MvcResult result = mockMvc.perform(
	            get("/api/latest/service/functionalaccess/users/{privilege}", privilege)
                    .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .principal(mockAuthentication))
	                .andReturn();
		
		verifyAll();
		
		LOG.info("Results: " + result.getResponse().getContentAsString());
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		List<AcmUser> resultUserList =  mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<ArrayList<AcmUser>>(){});
		
		assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
		assertEquals(expectedUserList.size(), resultUserList.size());
		for ( AcmUser expected : expectedUserList )
		{
			assertTrue(resultUserList.contains(expected));
		}

	}
	
	@Test
    public void usersByPrivilegeAndGroupTest() throws Exception {
		
		String privilege = "acm-privilege";
		String role1 = "ROLE_ADMINISTRATOR";
		String role2 = "ROLE_INVESTIGATOR_SUPERVISOR";
		
		
		
		
		// Group 1
		Set<AcmUser> membersGroup1 = new HashSet<>();
		
		AcmUser user1 = new AcmUser();
		user1.setUserId("user1");
		
		AcmUser user2 = new AcmUser();
		user2.setUserId("user2");
		
		membersGroup1.addAll(Arrays.asList(user1, user2));
		
		AcmGroup group1 = new AcmGroup();
		group1.setName("acm_administrator_dev");
		group1.setMembers(membersGroup1);
		
		
		
		
		// Group 2
		Set<AcmUser> membersGroup2 = new HashSet<>();
		
		AcmUser user3 = new AcmUser();
		user3.setUserId("user3");
		
		AcmUser user4 = new AcmUser();
		user4.setUserId("user4");
		
		membersGroup2.addAll(Arrays.asList(user3, user4));
		
		AcmGroup group2 = new AcmGroup();
		group2.setName("acm_supervisor_dev");
		group2.setMembers(membersGroup2);
		
		
		
		
		List<AcmUser> expectedUserList = Arrays.asList(user1, user2);
		
		
		
		
		List<String> rolesForPrivilege = Arrays.asList(role1, role2);
		Map<String, List<String>> rolesToGroups = new HashMap<>();
		rolesToGroups.put(role1, Arrays.asList(group1.getName()));
		rolesToGroups.put(role2, Arrays.asList(group2.getName()));
		
		
		
		
		
		expect(mockAuthentication.getName()).andReturn("user");
		expect(mockPluginManager.getRolesForPrivilege(privilege)).andReturn(rolesForPrivilege);
		expect(mockAcmGroupDao.findByName(group1.getName())).andReturn(group1);
		
		replayAll();
		
		MvcResult result = mockMvc.perform(
	            get("/api/latest/service/functionalaccess/users/{privilege}/{group}", privilege, group1.getName())
                    .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .principal(mockAuthentication))
	                .andReturn();
		
		verifyAll();
		
		LOG.info("Results: " + result.getResponse().getContentAsString());
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		List<AcmUser> resultUserList =  mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<ArrayList<AcmUser>>(){});
		
		assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
		assertEquals(expectedUserList.size(), resultUserList.size());
		for ( AcmUser expected : expectedUserList )
		{
			assertTrue(resultUserList.contains(expected));
		}

	}
	
	@Test
    public void usersByPrivilegeAndGroupPlusCurrentAssigneeTest() throws Exception {
		
		String privilege = "acm-privilege";
		String role1 = "ROLE_ADMINISTRATOR";
		String role2 = "ROLE_INVESTIGATOR_SUPERVISOR";
		
		
		// Current assignee
		AcmUser currentAssignee = new AcmUser();
		currentAssignee.setUserId("currentAssignee");
		
		
		// Group 1
		Set<AcmUser> membersGroup1 = new HashSet<>();
		
		AcmUser user1 = new AcmUser();
		user1.setUserId("user1");
		
		AcmUser user2 = new AcmUser();
		user2.setUserId("user2");
		
		membersGroup1.addAll(Arrays.asList(user1, user2));
		
		AcmGroup group1 = new AcmGroup();
		group1.setName("acm_administrator_dev");
		group1.setMembers(membersGroup1);
		
		
		
		
		// Group 2
		Set<AcmUser> membersGroup2 = new HashSet<>();
		
		AcmUser user3 = new AcmUser();
		user3.setUserId("user3");
		
		AcmUser user4 = new AcmUser();
		user4.setUserId("user4");
		
		membersGroup2.addAll(Arrays.asList(user3, user4));
		
		AcmGroup group2 = new AcmGroup();
		group2.setName("acm_supervisor_dev");
		group2.setMembers(membersGroup2);
		
		
		
		
		List<AcmUser> expectedUserList = Arrays.asList(user1, user2, currentAssignee);
		
		
		
		
		List<String> rolesForPrivilege = Arrays.asList(role1, role2);
		Map<String, List<String>> rolesToGroups = new HashMap<>();
		rolesToGroups.put(role1, Arrays.asList(group1.getName()));
		rolesToGroups.put(role2, Arrays.asList(group2.getName()));
		
		
		
		
		
		expect(mockAuthentication.getName()).andReturn("user");
		expect(mockPluginManager.getRolesForPrivilege(privilege)).andReturn(rolesForPrivilege);
		expect(mockAcmGroupDao.findByName(group1.getName())).andReturn(group1);
		expect(mockUserDao.findByUserId(currentAssignee.getUserId())).andReturn(currentAssignee);
		
		replayAll();
		
		MvcResult result = mockMvc.perform(
	            get("/api/latest/service/functionalaccess/users/{privilege}/{group}/{currenAssignee}", privilege, group1.getName(), currentAssignee.getUserId())
                    .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .principal(mockAuthentication))
	                .andReturn();
		
		verifyAll();
		
		LOG.info("Results: " + result.getResponse().getContentAsString());
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		List<AcmUser> resultUserList =  mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<ArrayList<AcmUser>>(){});
		
		assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
		assertEquals(expectedUserList.size(), resultUserList.size());
		for ( AcmUser expected : expectedUserList )
		{
			assertTrue(resultUserList.contains(expected));
		}
		
	}
	
	@Test
    public void usersByPrivilegeAndAllGroupsPlusCurrentAssigneeTest() throws Exception {
		
		String privilege = "acm-privilege";
		String role1 = "ROLE_ADMINISTRATOR";
		String role2 = "ROLE_INVESTIGATOR_SUPERVISOR";
		
		
		// Current assignee
		AcmUser currentAssignee = new AcmUser();
		currentAssignee.setUserId("currentAssignee");
		
		
		// Group 1
		Set<AcmUser> membersGroup1 = new HashSet<>();
		
		AcmUser user1 = new AcmUser();
		user1.setUserId("user1");
		
		AcmUser user2 = new AcmUser();
		user2.setUserId("user2");
		
		membersGroup1.addAll(Arrays.asList(user1, user2));
		
		AcmGroup group1 = new AcmGroup();
		group1.setName("acm_administrator_dev");
		group1.setMembers(membersGroup1);
		
		
		
		
		// Group 2
		Set<AcmUser> membersGroup2 = new HashSet<>();
		
		AcmUser user3 = new AcmUser();
		user3.setUserId("user3");
		
		AcmUser user4 = new AcmUser();
		user4.setUserId("user4");
		
		membersGroup2.addAll(Arrays.asList(user3, user4));
		
		AcmGroup group2 = new AcmGroup();
		group2.setName("acm_supervisor_dev");
		group2.setMembers(membersGroup2);
		
		
		
		
		List<AcmUser> expectedUserList = Arrays.asList(user1, user2, currentAssignee, user3, user4 );
		
		
		
		
		List<String> rolesForPrivilege = Arrays.asList(role1, role2);
		Map<String, List<String>> rolesToGroups = new HashMap<>();
		rolesToGroups.put(role1, Arrays.asList(group1.getName()));
		rolesToGroups.put(role2, Arrays.asList(group2.getName()));
		
		
		
		
		
		expect(mockAuthentication.getName()).andReturn("user");
		expect(mockPluginManager.getRolesForPrivilege(privilege)).andReturn(rolesForPrivilege);
		expect(mockAcmGroupDao.findByName(group1.getName())).andReturn(group1);
		expect(mockAcmGroupDao.findByName(group2.getName())).andReturn(group2);
		expect(mockUserDao.findByUserId(currentAssignee.getUserId())).andReturn(currentAssignee);
		
		replayAll();
		
		MvcResult result = mockMvc.perform(
	            get("/api/latest/service/functionalaccess/users/{privilege}/{group}/{currenAssignee}", privilege, "*", currentAssignee.getUserId())
                    .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .principal(mockAuthentication))
	                .andReturn();
		
		verifyAll();
		
		LOG.info("Results: " + result.getResponse().getContentAsString());
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		List<AcmUser> resultUserList =  mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<ArrayList<AcmUser>>(){});
		
		assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
		assertEquals(expectedUserList.size(), resultUserList.size());
		for ( AcmUser expected : expectedUserList )
		{
			assertTrue(resultUserList.contains(expected));
		}
		
	}
	
}
