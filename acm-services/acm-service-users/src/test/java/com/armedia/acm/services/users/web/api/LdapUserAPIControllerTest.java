package com.armedia.acm.services.users.web.api;

import com.armedia.acm.core.exceptions.AcmAppErrorJsonMsg;
import com.armedia.acm.services.users.dao.group.AcmGroupDao;
import com.armedia.acm.services.users.dao.ldap.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.group.AcmGroup;
import com.armedia.acm.services.users.model.ldap.AcmLdapActionFailedException;
import com.armedia.acm.services.users.service.AcmUserEventPublisher;
import com.armedia.acm.services.users.service.ldap.LdapUserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@RunWith(MockitoJUnitRunner.class)
public class LdapUserAPIControllerTest extends EasyMockSupport
{
    private Logger LOG = LoggerFactory.getLogger(getClass());

    private MockMvc mockMvc;

    @Mock
    private UserDao mockUserDao;

    @Mock
    private AcmGroupDao mockGroupDao;

    @Mock
    private AcmUserEventPublisher mockUserEventPublisher;

    @Mock
    private LdapUserService mockLdapUserService;

    @InjectMocks
    @Spy
    LdapUserAPIController ldapUserAPIController;


    @Before
    public void setUp()
    {
        mockMvc = MockMvcBuilders.standaloneSetup(ldapUserAPIController).build();
        mockLdapUserService.setUserDao(mockUserDao);
        mockLdapUserService.setGroupDao(mockGroupDao);
    }

    @Test
    public void removeLdapUserTest() throws Exception
    {
        String directory = "armedia";
        AcmUser user = new AcmUser();

        user.setUserId("test-user");
        user.setUserState("TEST");
        user.setFirstName("First Name");
        user.setLastName("Last Name");

        AcmGroup acmGroup = new AcmGroup();
        List<AcmGroup> groups = new ArrayList<>();
        groups.add(acmGroup);

        mockBehaviour(user, groups);

        MvcResult result = mockMvc.perform(
                delete("/api/v1/ldap/" + directory + "/users/" + user.getUserId())
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .contentType(MediaType.APPLICATION_JSON)).andReturn();


        LOG.info("Results: " + result.getResponse().getContentAsString());
        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());

        verify(mockLdapUserService, times(1)).removeLdapUser(anyString(), anyString());
        verifyAll();

    }

    @Test
    public void groupOperationsToUserTest() throws Exception
    {
        String directory = "armedia";
        AcmUser user1 = new AcmUser();

        user1.setUserId("add-user");
        user1.setUserState("TEST");
        user1.setFirstName("First");
        user1.setLastName("Last");

        AcmUser user2 = new AcmUser();

        user2.setUserId("delete-user");
        user2.setUserState("TEST");
        user2.setFirstName("First");
        user2.setLastName("Last");


        AcmGroup acmGroup = new AcmGroup();
        acmGroup.setName("test-group");
        List<AcmGroup> groups = new ArrayList<>();
        groups.add(acmGroup);

        List<String> groupsToBeAdded = new ArrayList<>();
        groupsToBeAdded.add(groups.get(0).getName());

        ObjectMapper objectMapper = new ObjectMapper();
        String content = objectMapper.writeValueAsString(groupsToBeAdded);

        mockBehaviour(user1, groups);
        mockBehaviour(user2, groups);

        MvcResult resultAdding = mockMvc.perform(
                put("/api/v1/ldap/" + directory + "/manage/" + user1.getUserId() + "/groups")
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .contentType(MediaType.APPLICATION_JSON).content(content)).andReturn();

        MvcResult resultDeleting = mockMvc.perform(
                delete("/api/v1/ldap/" + directory + "/manage/" + user2.getUserId() + "/groups")
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .contentType(MediaType.APPLICATION_JSON).content(content)).andReturn();

        LOG.info("Results: " + resultAdding.getResponse().getContentAsString());
        assertEquals(HttpStatus.OK.value(), resultAdding.getResponse().getStatus());

        LOG.info("Results: " + resultDeleting.getResponse().getContentAsString());
        assertEquals(HttpStatus.OK.value(), resultDeleting.getResponse().getStatus());

        verify(mockLdapUserService, times(1)).addUserMembersInLdapGroup(user1.getUserId(), groupsToBeAdded, directory);
        verify(mockLdapUserService, times(1)).removeUserMembersInLdapGroup(user2.getUserId(), groupsToBeAdded, directory);
        verifyAll();
    }


    private void mockBehaviour(AcmUser user, List<AcmGroup> groups) throws AcmLdapActionFailedException, AcmAppErrorJsonMsg
    {
        when(mockLdapUserService.getUserDao()).thenReturn(mockUserDao);
        when(mockUserDao.findByUserId(anyString())).thenReturn(user);

        when(mockLdapUserService.getGroupDao()).thenReturn(mockGroupDao);
        when(mockGroupDao.findByUserMember(user)).thenReturn(groups);
        when(mockLdapUserService.removeLdapUser(anyString(), anyString())).thenReturn(user);
        doNothing().when(ldapUserAPIController).checkIfLdapManagementIsAllowed(anyString());
    }
}