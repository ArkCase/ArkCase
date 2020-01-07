package com.armedia.acm.services.users.web.api.group;

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

import static org.easymock.EasyMock.expect;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.isIn;
import static org.hamcrest.core.Every.everyItem;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.group.AcmGroup;
import com.armedia.acm.services.users.model.group.AcmGroupStatus;
import com.armedia.acm.services.users.model.group.AcmGroupType;
import com.armedia.acm.services.users.service.group.GroupServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.easymock.EasyMockRunner;
import org.easymock.EasyMockSupport;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.json.JSONArray;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Arrays;
import java.util.Base64;
import java.util.stream.Collectors;

@RunWith(EasyMockRunner.class)
public class AdHocGroupMembersAPIControllerTest extends EasyMockSupport implements HandlerExceptionResolver
{
    private Logger log = LogManager.getLogger(getClass());

    @TestSubject
    private AdHocGroupMembersAPIController unit = new AdHocGroupMembersAPIController();

    private MockMvc mockMvc;

    @Mock
    private Authentication mockAuthentication;

    @Mock
    private GroupServiceImpl groupService;

    @Before
    public void setUp()
    {
        mockMvc = MockMvcBuilders.standaloneSetup(unit).setHandlerExceptionResolvers(this).build();
    }

    @Test
    public void saveMembersToAdHocGroupTest() throws Exception
    {
        AcmGroup group = new AcmGroup();
        group.setName("A");
        group.setType(AcmGroupType.ADHOC_GROUP);
        group.setStatus(AcmGroupStatus.ACTIVE);

        AcmUser user = new AcmUser();
        user.setUserId("ann-acm");

        AcmUser user1 = new AcmUser();
        user1.setUserId("sally-acm");

        group.addUserMember(user);
        group.addUserMember(user1);

        expect(groupService.addUserMembersToGroup(Arrays.asList("ann-acm", "sally-acm"), "A"))
                .andReturn(group);
        expect(mockAuthentication.getName()).andReturn("ann-acm");
        replayAll();

        MvcResult result = mockMvc.perform(
                post("/api/latest/users/group/{groupId}/members/save", Base64.getUrlEncoder().encodeToString(group.getName().getBytes()))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new JSONArray(Arrays.asList("ann-acm", "sally-acm")).toString())
                        .principal(mockAuthentication))
                .andExpect(status().isOk())
                .andReturn();

        verifyAll();

        log.info("Results: {}", result.getResponse().getContentAsString());
        ObjectMapper om = new ObjectMapper();
        AcmGroup acmGroup = om.readValue(result.getResponse().getContentAsString(), AcmGroup.class);

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());

        assertThat(acmGroup.getUserMemberIds().collect(Collectors.toList()),
                everyItem(isIn(Arrays.asList("ann-acm", "sally-acm"))));
    }

    @Test
    public void removeMembersFromAdHocGroupTest() throws Exception
    {
        AcmGroup group = new AcmGroup();
        group.setName("A");
        group.setType(AcmGroupType.ADHOC_GROUP);
        group.setStatus(AcmGroupStatus.ACTIVE);

        AcmUser user1 = new AcmUser();
        user1.setUserId("ann-acm");

        AcmUser user2 = new AcmUser();
        user2.setUserId("sally-acm");

        group.addUserMember(user2);

        expect(mockAuthentication.getName()).andReturn("ann-acm");
        expect(groupService.removeUserMembersFromGroup(Arrays.asList(user1.getUserId()), "A"))
                .andReturn(group);

        replayAll();

        MvcResult result = mockMvc.perform(
                post("/api/latest/users/group/{groupId}/members/remove/", Base64.getUrlEncoder().encodeToString(group.getName().getBytes()))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(mockAuthentication)
                        .content(new JSONArray(Arrays.asList("ann-acm")).toString()))
                .andReturn();

        verifyAll();

        log.info("Results: {}", result.getResponse().getContentAsString());
        ObjectMapper om = new ObjectMapper();
        AcmGroup acmGroup = om.readValue(result.getResponse().getContentAsString(), AcmGroup.class);

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        String[] members = { "sally-acm" };
        assertArrayEquals(acmGroup.getUserMemberIds().toArray(), members);
    }

    @Override
    public ModelAndView resolveException(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o,
            Exception e)
    {
        log.error("An error occurred", e);
        return null;
    }
}
