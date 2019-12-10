package com.armedia.acm.plugins.task.web.api;

/*-
 * #%L
 * ACM Default Plugin: Tasks
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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.armedia.acm.plugins.task.exception.AcmTaskException;
import com.armedia.acm.plugins.task.model.AcmApplicationTaskEvent;
import com.armedia.acm.plugins.task.model.AcmTask;
import com.armedia.acm.plugins.task.service.TaskDao;
import com.armedia.acm.plugins.task.service.TaskEventPublisher;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/spring/spring-web-acm-web.xml",
        "classpath:/spring/spring-library-task-plugin-unit-test.xml"
})
public class CompleteTaskAPIControllerTest extends EasyMockSupport
{
    private MockMvc mockMvc;
    private MockHttpSession mockHttpSession;

    private CompleteTaskAPIController unit;

    private TaskDao mockTaskDao;
    private TaskEventPublisher mockTaskEventPublisher;
    private Authentication mockAuthentication;

    @Autowired
    private ExceptionHandlerExceptionResolver exceptionResolver;

    private Logger log = LogManager.getLogger(getClass());

    @Before
    public void setUp() throws Exception
    {
        mockTaskDao = createMock(TaskDao.class);
        mockTaskEventPublisher = createMock(TaskEventPublisher.class);
        mockHttpSession = new MockHttpSession();
        mockAuthentication = createMock(Authentication.class);

        unit = new CompleteTaskAPIController();

        unit.setTaskDao(mockTaskDao);
        unit.setTaskEventPublisher(mockTaskEventPublisher);

        mockMvc = MockMvcBuilders.standaloneSetup(unit).setHandlerExceptionResolvers(exceptionResolver).build();
    }

    @Test
    public void completeTask() throws Exception
    {
        Long taskId = 500L;
        String ipAddress = "ipAddress";

        AcmTask found = new AcmTask();
        found.setTaskId(taskId);

        Capture<AcmApplicationTaskEvent> capturedEvent = EasyMock.newCapture();

        mockHttpSession.setAttribute("acm_ip_address", ipAddress);

        expect(mockTaskDao.completeTask(eq(mockAuthentication), eq(taskId))).andReturn(found);
        mockTaskEventPublisher.publishTaskEvent(capture(capturedEvent));
        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("user").atLeastOnce();

        replayAll();

        MvcResult result = mockMvc.perform(
                post("/api/v1/plugin/task/completeTask/{taskId}", taskId)
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .session(mockHttpSession)
                        .principal(mockAuthentication))
                .andReturn();

        verifyAll();

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertTrue(result.getResponse().getContentType().startsWith(MediaType.APPLICATION_JSON_VALUE));

        String returned = result.getResponse().getContentAsString();

        log.info("results: " + returned);

        ObjectMapper objectMapper = new ObjectMapper();

        AcmTask completedTask = objectMapper.readValue(returned, AcmTask.class);

        assertNotNull(completedTask);
        assertEquals(completedTask.getTaskId(), taskId);

        AcmApplicationTaskEvent event = capturedEvent.getValue();
        assertEquals(taskId, event.getObjectId());
        assertEquals("TASK", event.getObjectType());
        assertTrue(event.isSucceeded());
    }

    @Test
    public void completeTask_exception() throws Exception
    {
        Long taskId = 500L;
        String ipAddress = "ipAddress";

        AcmTask found = new AcmTask();
        found.setTaskId(taskId);

        Capture<AcmApplicationTaskEvent> capturedEvent = EasyMock.newCapture();

        mockHttpSession.setAttribute("acm_ip_address", ipAddress);

        expect(mockTaskDao.completeTask(eq(mockAuthentication), eq(taskId))).andThrow(new AcmTaskException("testException"));
        mockTaskEventPublisher.publishTaskEvent(capture(capturedEvent));
        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("user").atLeastOnce();

        replayAll();

        mockMvc.perform(
                post("/api/v1/plugin/task/completeTask/{taskId}", taskId)
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .session(mockHttpSession)
                        .principal(mockAuthentication))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.TEXT_PLAIN));

        verifyAll();

        AcmApplicationTaskEvent event = capturedEvent.getValue();
        assertEquals(taskId, event.getObjectId());
        assertEquals("TASK", event.getObjectType());
        assertFalse(event.isSucceeded());
    }

}
