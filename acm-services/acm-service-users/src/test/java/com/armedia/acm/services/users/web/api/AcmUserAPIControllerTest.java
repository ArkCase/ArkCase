package com.armedia.acm.services.users.web.api;

/*-
 * #%L
 * ACM Service: Users
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import com.armedia.acm.core.exceptions.AcmAppErrorJsonMsg;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.dao.group.AcmGroupDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.AcmUserState;
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
import org.mockito.junit.MockitoJUnitRunner;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class AcmUserAPIControllerTest extends EasyMockSupport
{
    @InjectMocks
    @Spy
    AcmUserAPIController acmUserAPIController;
    private Logger LOG = LogManager.getLogger(getClass());
    private MockMvc mockMvc;
    @Mock
    private UserDao mockUserDao;
    @Mock
    private AcmGroupDao mockGroupDao;
    @Mock
    private AcmUserEventPublisher mockUserEventPublisher;
    @Mock
    private LdapUserService mockLdapUserService;

    @Before
    public void setUp()
    {
        mockMvc = MockMvcBuilders.standaloneSetup(acmUserAPIController).build();
        mockLdapUserService.setUserDao(mockUserDao);
    }

    @Test
    public void removeLdapUserTest() throws Exception
    {
        String directory = "armedia";
        AcmUser user = new AcmUser();
        user.setUserId("test-user");
        user.setUserState(AcmUserState.VALID);
        user.setFirstName("First Name");
        user.setLastName("Last Name");

        AcmGroup acmGroup = new AcmGroup();
        List<AcmGroup> groups = new ArrayList<>();
        groups.add(acmGroup);

        mockBehaviour(user, groups);

        MvcResult result = mockMvc
                .perform(delete("/api/v1/ldap/" + directory + "/users/" + user.getUserId())
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8")).contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        LOG.info("Results: " + result.getResponse().getContentAsString());
        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());

        verify(mockLdapUserService, times(1)).deleteAcmUser(anyString(), anyString());
        verifyAll();

    }

    @Test
    public void groupOperationsToUserTest() throws Exception
    {
        String directory = "armedia";
        AcmUser user1 = new AcmUser();

        user1.setUserId("add-user");
        user1.setUserState(AcmUserState.VALID);
        user1.setFirstName("First");
        user1.setLastName("Last");

        AcmUser user2 = new AcmUser();

        user2.setUserId("delete-user");
        user2.setUserState(AcmUserState.VALID);
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

        MvcResult resultAdding = mockMvc.perform(put("/api/v1/ldap/" + directory + "/manage/" + user1.getUserId() + "/groups")
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8")).contentType(MediaType.APPLICATION_JSON)
                .content(content)).andReturn();

        MvcResult resultDeleting = mockMvc
                .perform(delete("/api/v1/ldap/" + directory + "/manage/" + user2.getUserId() + "/groups?groupNames=" + acmGroup.getName())
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8")).contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        LOG.info("Results: " + resultAdding.getResponse().getContentAsString());
        assertEquals(HttpStatus.OK.value(), resultAdding.getResponse().getStatus());

        LOG.info("Results: " + resultDeleting.getResponse().getContentAsString());
        assertEquals(HttpStatus.OK.value(), resultDeleting.getResponse().getStatus());

        verify(mockLdapUserService, times(1)).addUserInGroups(user1.getUserId(), groupsToBeAdded, directory);
        verify(mockLdapUserService, times(1)).removeUserFromGroups(user2.getUserId(), groupsToBeAdded, directory);
        verifyAll();
    }

    private void mockBehaviour(AcmUser user, List<AcmGroup> groups) throws AcmLdapActionFailedException, AcmAppErrorJsonMsg
    {
        when(mockLdapUserService.getUserDao()).thenReturn(mockUserDao);
        when(mockUserDao.findByUserId(anyString())).thenReturn(user);

        when(mockLdapUserService.deleteAcmUser(anyString(), anyString())).thenReturn(user);
        doNothing().when(acmUserAPIController).checkIfLdapManagementIsAllowed(anyString());
    }
}
