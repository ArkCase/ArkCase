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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import com.armedia.acm.muletools.mulecontextmanager.MuleContextManager;
import com.armedia.acm.services.users.model.group.AcmGroup;
import com.armedia.acm.services.users.model.group.AcmGroupStatus;
import com.armedia.acm.services.users.service.group.GroupServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.easymock.EasyMockRunner;
import org.easymock.EasyMockSupport;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mule.api.MuleMessage;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Base64;

@RunWith(EasyMockRunner.class)
public class AcmGroupAPIControllerTest extends EasyMockSupport implements HandlerExceptionResolver
{
    @Mock
    MuleMessage muleMessage;
    private Logger log = LogManager.getLogger(getClass());
    private MockMvc mockMvc;
    @Mock
    private GroupServiceImpl groupService;
    @Mock
    private MuleContextManager muleContextManager;

    @TestSubject
    private AcmGroupAPIController unit = new AcmGroupAPIController();

    @Mock
    private Authentication mockAuthentication;

    @Autowired
    private ExceptionHandlerExceptionResolver exceptionResolver;

    @Before
    public void setUp()
    {
        mockMvc = MockMvcBuilders.standaloneSetup(unit).setHandlerExceptionResolvers(this).build();
    }

    @Test
    public void removeGroupTestTrue() throws Exception
    {
        AcmGroup group = new AcmGroup();
        group.setName("Name");
        group.setStatus(AcmGroupStatus.DELETE);

        expect(groupService.markGroupDeleted(group.getName())).andReturn(group);
        expect(mockAuthentication.getName()).andReturn("user");

        replayAll();

        MvcResult result = mockMvc.perform(
                delete("/api/v1/users/group/{groupId}/remove", Base64.getUrlEncoder().encodeToString(group.getName().getBytes()))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(mockAuthentication))
                .andReturn();

        log.info("Results: {}", result.getResponse().getContentAsString());

        verifyAll();

        ObjectMapper objectMapper = new ObjectMapper();
        AcmGroup resultGroup = objectMapper.readValue(result.getResponse().getContentAsString(), AcmGroup.class);

        assertEquals(AcmGroupStatus.DELETE, resultGroup.getStatus());
        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
    }

    @Test
    public void getSubGroupsTestTrue() throws Exception
    {
        String groupId = "some_group////_name";
        expect(mockAuthentication.getName()).andReturn("user").anyTimes();
        expect(groupService.getGroupsByParent(groupId, 0, 10, "", mockAuthentication)).andReturn("[]");
        replayAll();

        String encodedGroupId = Base64.getUrlEncoder().encodeToString(groupId.getBytes());

        MvcResult result = mockMvc.perform(
                get("/api/v1/users/group/" + encodedGroupId + "/get/subgroups")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(mockAuthentication))
                .andReturn();

        log.info("Results: {}", result.getResponse().getContentAsString());

        verifyAll();

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
