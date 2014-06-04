package com.armedia.acm.plugins.task.web.api;

import com.armedia.acm.plugins.task.model.AcmTask;
import com.armedia.acm.plugins.task.model.AcmTaskCompletedEvent;
import com.armedia.acm.plugins.task.service.TaskDao;
import com.armedia.acm.plugins.task.service.TaskEventPublisher;
import com.armedia.acm.plugins.task.web.api.CompleteTaskAPIController;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

/**
 * Created by armdev on 6/3/14.
 */
public class CompleteTaskAPIControllerTest extends EasyMockSupport
{
    private MockMvc mockMvc;
    private MockHttpSession mockHttpSession;

    private CompleteTaskAPIController unit;

    private TaskDao mockTaskDao;
    private TaskEventPublisher mockTaskEventPublisher;

    private Logger log = LoggerFactory.getLogger(getClass());

    @Before
    public void setUp() throws Exception
    {
        mockTaskDao = createMock(TaskDao.class);
        mockTaskEventPublisher = createMock(TaskEventPublisher.class);
        mockHttpSession = new MockHttpSession();

        unit = new CompleteTaskAPIController();

        unit.setTaskDao(mockTaskDao);
        unit.setTaskEventPublisher(mockTaskEventPublisher);

        mockMvc = MockMvcBuilders.standaloneSetup(unit).build();
    }

    @Test
    public void completeTask() throws Exception
    {
        Long taskId = 500L;
        String ipAddress = "ipAddress";

        AcmTask found = new AcmTask();
        found.setTaskId(taskId);

        mockHttpSession.setAttribute("acm_ip_address", ipAddress);

        expect(mockTaskDao.completeTask(isNull(Authentication.class), eq(taskId))).andReturn(found);
        mockTaskEventPublisher.publishTaskEvent(
                anyObject(AcmTaskCompletedEvent.class),
                isNull(Authentication.class),
                eq(ipAddress));

        replayAll();

        MvcResult result = mockMvc.perform(
                post("/api/v1/plugin/task/completeTask/{taskId}", taskId)
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .session(mockHttpSession))
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
    }

}
