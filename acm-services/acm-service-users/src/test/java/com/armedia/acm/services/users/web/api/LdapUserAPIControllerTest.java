package com.armedia.acm.services.users.web.api;

import com.armedia.acm.core.exceptions.AcmAppErrorJsonMsg;
import com.armedia.acm.services.users.dao.group.AcmGroupDao;
import com.armedia.acm.services.users.dao.ldap.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.group.AcmGroup;
import com.armedia.acm.services.users.model.ldap.AcmLdapActionFailedException;
import com.armedia.acm.services.users.service.AcmUserEventPublisher;
import com.armedia.acm.services.users.service.ldap.LdapUserService;
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