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
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.group.AcmGroup;
import com.armedia.acm.services.users.service.group.GroupServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.easymock.EasyMockRunner;
import org.easymock.EasyMockSupport;
import org.easymock.Mock;
import org.easymock.TestSubject;
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

import java.util.Base64;

@RunWith(EasyMockRunner.class)
public class SupervisorGroupAPIControllerTest extends EasyMockSupport implements HandlerExceptionResolver
{

    private Logger log = LogManager.getLogger(getClass());

    @TestSubject
    private SupervisorGroupAPIController unit = new SupervisorGroupAPIController();

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
    public void removeSupervisorFromGroupTest() throws Exception
    {
        AcmGroup group = new AcmGroup();
        group.setName("A");

        expect(groupService.removeSupervisor("A", false)).andReturn(group);
        expect(mockAuthentication.getName()).andReturn("ann-acm");
        replayAll();

        MvcResult result = mockMvc.perform(
                delete("/api/latest/users/group/" + Base64.getUrlEncoder().encodeToString(group.getName().getBytes())
                        + "/supervisor/remove/false")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .principal(mockAuthentication))
                .andReturn();

        log.info("Results: {}", result.getResponse().getContentAsString());

        verifyAll();

        ObjectMapper objectMapper = new ObjectMapper();
        AcmGroup resultGroup = objectMapper.readValue(result.getResponse().getContentAsString(), AcmGroup.class);

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertEquals(null, resultGroup.getSupervisor());
    }

    @Test
    public void addSupervisorToGroupTest() throws Exception
    {
        String content = "{\"userId\":\"ann-acm\"}";

        AcmUser supervisor = new AcmUser();
        supervisor.setUserId("ann-acm");

        AcmGroup group = new AcmGroup();
        group.setName("A");
        group.setSupervisor(supervisor);

        expect(groupService.setSupervisor(supervisor, "A", false)).andReturn(group);
        expect(mockAuthentication.getName()).andReturn("ann-acm");
        replayAll();

        MvcResult result = mockMvc.perform(
                post("/api/latest/users/group/{groupId}/supervisor/save/false",
                        Base64.getUrlEncoder().encodeToString(group.getName().getBytes()))
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .principal(mockAuthentication)
                                .content(content))
                .andReturn();

        log.info("Results: {}", result.getResponse().getContentAsString());

        verifyAll();

        ObjectMapper objectMapper = new ObjectMapper();
        AcmGroup resultGroup = objectMapper.readValue(result.getResponse().getContentAsString(), AcmGroup.class);

        assertEquals(supervisor.getUserId(), resultGroup.getSupervisor().getUserId());
        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
    }

    @Override
    public ModelAndView resolveException(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o,
            Exception e)
    {
        log.error("An error occurred", e);
        return null;
    }
}
