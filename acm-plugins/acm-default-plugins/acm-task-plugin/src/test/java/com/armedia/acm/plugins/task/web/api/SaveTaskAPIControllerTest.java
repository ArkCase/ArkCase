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

import java.util.Date;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/spring/spring-web-acm-web.xml",
        "classpath:/spring/spring-library-task-plugin-unit-test.xml"
})
public class SaveTaskAPIControllerTest extends EasyMockSupport
{
    private MockMvc mockMvc;
    private MockHttpSession mockHttpSession;

    private SaveTaskAPIController unit;

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

        unit = new SaveTaskAPIController();

        unit.setTaskDao(mockTaskDao);
        unit.setTaskEventPublisher(mockTaskEventPublisher);

        mockMvc = MockMvcBuilders.standaloneSetup(unit).setHandlerExceptionResolvers(exceptionResolver).build();
    }

    @Test
    public void saveTask() throws Exception
    {
        Long taskId = 500L;
        String ipAddress = "ipAddress";

        AcmTask in = new AcmTask();
        in.setAssignee("assignee");
        in.setAttachedToObjectType("COMPLAINT");
        in.setDetails("details");
        in.setPercentComplete(23);
        in.setStatus("ASSIGNED");
        in.setTaskStartDate(new Date());
        in.setTitle("title");
        in.setTaskId(taskId);

        AcmTask saved = new AcmTask();
        saved.setAssignee(in.getAssignee());
        saved.setTaskId(taskId);

        Capture<AcmTask> taskSentToDao = EasyMock.newCapture();
        Capture<AcmApplicationTaskEvent> capturedEvent = EasyMock.newCapture();

        ObjectMapper objectMapper = new ObjectMapper();
        String inJson = objectMapper.writeValueAsString(in);

        mockHttpSession.setAttribute("acm_ip_address", ipAddress);

        expect(mockTaskDao.findById(in.getTaskId())).andReturn(in);
        expect(mockTaskDao.save(capture(taskSentToDao))).andReturn(saved);
        mockTaskEventPublisher.publishTaskEvent(capture(capturedEvent));

        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("user").atLeastOnce();

        replayAll();

        MvcResult result = mockMvc.perform(
                post("/api/v1/plugin/task/save/" + taskId)
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .session(mockHttpSession)
                        .principal(mockAuthentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(inJson))
                .andReturn();

        verifyAll();

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertTrue(result.getResponse().getContentType().startsWith(MediaType.APPLICATION_JSON_VALUE));

        AcmTask sentToDao = taskSentToDao.getValue();
        assertEquals(taskId, sentToDao.getTaskId());

        String returned = result.getResponse().getContentAsString();

        log.info("results: " + returned);

        AcmTask updatedTask = objectMapper.readValue(returned, AcmTask.class);

        assertNotNull(updatedTask);
        assertEquals(updatedTask.getTaskId(), in.getTaskId());

        AcmApplicationTaskEvent event = capturedEvent.getValue();
        assertEquals(taskId, event.getObjectId());
        assertEquals("TASK", event.getObjectType());
        assertTrue(event.isSucceeded());
    }

    @Test
    public void saveTask_exception() throws Exception
    {
        String ipAddress = "ipAddress";

        AcmTask in = new AcmTask();
        in.setAssignee("assignee");
        in.setTaskId(500L);

        Capture<AcmTask> taskSentToDao = EasyMock.newCapture();
        Capture<AcmApplicationTaskEvent> capturedEvent = EasyMock.newCapture();

        ObjectMapper objectMapper = new ObjectMapper();
        String inJson = objectMapper.writeValueAsString(in);

        mockHttpSession.setAttribute("acm_ip_address", ipAddress);

        expect(mockTaskDao.findById(in.getTaskId())).andReturn(in);
        expect(mockTaskDao.save(capture(taskSentToDao))).andThrow(new AcmTaskException("testException"));
        mockTaskEventPublisher.publishTaskEvent(capture(capturedEvent));

        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("user").atLeastOnce();

        replayAll();

        mockMvc.perform(
                post("/api/v1/plugin/task/save/" + in.getTaskId())
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .session(mockHttpSession)
                        .principal(mockAuthentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(inJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.TEXT_PLAIN));

        verifyAll();

        AcmTask sentToDao = taskSentToDao.getValue();
        assertEquals(in.getTaskId(), sentToDao.getTaskId());

        AcmApplicationTaskEvent event = capturedEvent.getValue();
        assertEquals(in.getTaskId(), event.getObjectId());
        assertEquals("TASK", event.getObjectType());
        assertFalse(event.isSucceeded());
    }

}
