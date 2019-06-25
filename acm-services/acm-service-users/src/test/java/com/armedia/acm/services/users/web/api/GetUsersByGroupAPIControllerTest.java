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

import static junit.framework.Assert.assertEquals;
import static org.easymock.EasyMock.expect;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import com.armedia.acm.services.users.service.group.GroupServiceImpl;

import org.apache.commons.httpclient.HttpStatus;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Base64;
import java.util.Optional;

/**
 * Created by armdev on 5/28/14.
 */
public class GetUsersByGroupAPIControllerTest extends EasyMockSupport
{
    private MockMvc mockMvc;
    private GroupServiceImpl mockGroupService;
    private Authentication mockAuthentication;

    private Logger log = LogManager.getLogger(getClass());

    @Before
    public void setUp() throws Exception
    {
        mockGroupService = createMock(GroupServiceImpl.class);
        mockAuthentication = createMock(Authentication.class);

        GetUsersByGroupAPIController unit = new GetUsersByGroupAPIController();
        unit.setGroupService(mockGroupService);

        mockMvc = MockMvcBuilders.standaloneSetup(unit).build();
    }

    @Test
    public void getValidUserMembersForGroupTest() throws Exception
    {
        String response = "response";
        String group = "Group1";
        String status = "VALID";
        expect(mockAuthentication.getName()).andReturn("USER");
        expect(mockGroupService.getUserMembersForGroup(group, Optional.of(status), mockAuthentication)).andReturn(response);

        replayAll();

        MvcResult result = mockMvc
                .perform(get("/api/latest/users/by-group/{group}", Base64.getUrlEncoder().encodeToString(group.getBytes()))
                        .param("status", "VALID")
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .principal(mockAuthentication)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        log.info("results: [{}]", result.getResponse().getContentAsString());

        verifyAll();

        assertEquals(HttpStatus.SC_OK, result.getResponse().getStatus());
    }

    @Test
    public void getUserMembersForGroupTest() throws Exception
    {
        String response = "response";
        String group = "Group 1";
        expect(mockAuthentication.getName()).andReturn("USER");
        expect(mockGroupService.getUserMembersForGroup(group, Optional.empty(), mockAuthentication)).andReturn(response);

        replayAll();

        MvcResult result = mockMvc
                .perform(get("/api/latest/users/by-group/{group}", Base64.getUrlEncoder().encodeToString(group.getBytes()))
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .principal(mockAuthentication)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        log.info("results: [{}]", result.getResponse().getContentAsString());

        verifyAll();

        assertEquals(HttpStatus.SC_OK, result.getResponse().getStatus());
    }
}
