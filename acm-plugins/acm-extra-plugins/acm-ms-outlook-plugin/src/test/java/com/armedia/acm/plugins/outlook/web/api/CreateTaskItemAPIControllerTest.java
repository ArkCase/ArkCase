package com.armedia.acm.plugins.outlook.web.api;

/*-
 * #%L
 * ACM Extra Plugin: MS Outlook Integration
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

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.armedia.acm.plugins.profile.service.UserOrgService;
import com.armedia.acm.service.outlook.model.AcmOutlookUser;
import com.armedia.acm.service.outlook.model.OutlookDTO;
import com.armedia.acm.service.outlook.model.OutlookTaskItem;
import com.armedia.acm.service.outlook.service.OutlookService;
import com.armedia.acm.services.users.model.AcmUser;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import java.util.Date;

import microsoft.exchange.webservices.data.core.enumeration.property.WellKnownFolderName;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = { "classpath:/spring-web-ms-outlook-plugin-api.xml" })
public class CreateTaskItemAPIControllerTest extends EasyMockSupport
{
    @Autowired
    WebApplicationContext wac;
    @Autowired
    MockHttpSession session;
    @Autowired
    MockHttpServletRequest request;
    @Autowired
    CreateTaskItemAPIController createTaskItemAPIController;

    private Authentication mockAuthentication;

    private MockMvc mockMvc;
    @Autowired
    private ExceptionHandlerExceptionResolver exceptionResolver;

    private OutlookService outlookService;
    private UserOrgService userOrgService;

    @Before
    public void setup()
    {
        outlookService = createMock(OutlookService.class);
        userOrgService = createMock(UserOrgService.class);
        mockAuthentication = createMock(Authentication.class);
        createTaskItemAPIController.setUserOrgService(userOrgService);
        createTaskItemAPIController.setOutlookService(outlookService);
        this.mockMvc = MockMvcBuilders.standaloneSetup(createTaskItemAPIController).setHandlerExceptionResolvers(exceptionResolver).build();
    }

    @Test
    public void testCreateTaskItem() throws Exception
    {
        OutlookTaskItem taskItem = new OutlookTaskItem();
        taskItem.setSubject("Task 1");
        taskItem.setBody("");
        long tomorrow = System.currentTimeMillis() + 1000 * 60 * 60 * 24;// due to tomorrow
        taskItem.setDueDate(new Date(tomorrow));
        taskItem.setPercentComplete(20);
        taskItem.setComplete(false);
        taskItem.setStartDate(new Date(System.currentTimeMillis() + 1000 * 60));// start next minute
        assertNull(taskItem.getId());

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        String content = objectMapper.writeValueAsString(taskItem);

        expect(mockAuthentication.getName()).andReturn("user").times(2);
        OutlookDTO password = new OutlookDTO();
        password.setOutlookPassword("outlookPassword");
        expect(outlookService.retrieveOutlookPassword(mockAuthentication)).andReturn(password);
        AcmUser user = new AcmUser();
        user.setMail("test@armedia.com");
        session.setAttribute("acm_user", user);

        Capture<AcmOutlookUser> outlookUserCapture = EasyMock.newCapture();
        Capture<OutlookTaskItem> taskItemCapture = EasyMock.newCapture();

        taskItem.setId("some_fake_id");
        expect(outlookService.createOutlookTaskItem(capture(outlookUserCapture), eq(WellKnownFolderName.Tasks), capture(taskItemCapture)))
                .andReturn(taskItem);

        replayAll();

        MvcResult result = mockMvc
                .perform(post("/api/latest/plugin/outlook/tasks").session(session).accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON).principal(mockAuthentication).content(content))
                .andExpect(status().isOk()).andReturn();

        OutlookTaskItem item = objectMapper.readValue(result.getResponse().getContentAsString(), OutlookTaskItem.class);
        assertNotNull(item.getId());
    }
}
