package com.armedia.acm.plugins.task.web.api;

import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.task.exception.AcmTaskException;
import com.armedia.acm.plugins.task.model.AcmTask;
import com.armedia.acm.plugins.task.model.AcmTaskCompletedEvent;
import com.armedia.acm.plugins.task.service.TaskDao;
import com.armedia.acm.plugins.task.service.TaskEventPublisher;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.easymock.Capture;
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
import org.springframework.web.util.NestedServletException;

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
    private Authentication mockAuthentication;

    private Logger log = LoggerFactory.getLogger(getClass());

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

        mockMvc = MockMvcBuilders.standaloneSetup(unit).build();
    }

    @Test
    public void completeTask() throws Exception
    {
        Long taskId = 500L;
        String ipAddress = "ipAddress";

        AcmTask found = new AcmTask();
        found.setTaskId(taskId);

        Capture<AcmTaskCompletedEvent> capturedEvent = new Capture<>();

        mockHttpSession.setAttribute("acm_ip_address", ipAddress);

        expect(mockTaskDao.completeTask(eq(mockAuthentication), eq(taskId))).andReturn(found);
        mockTaskEventPublisher.publishTaskEvent(
                capture(capturedEvent),
                eq(mockAuthentication),
                eq(ipAddress));
        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("user");

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

        AcmTaskCompletedEvent event = capturedEvent.getValue();
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

        Capture<AcmTaskCompletedEvent> capturedEvent = new Capture<>();

        mockHttpSession.setAttribute("acm_ip_address", ipAddress);

        expect(mockTaskDao.completeTask(eq(mockAuthentication), eq(taskId))).andThrow(new AcmTaskException("testException"));
        mockTaskEventPublisher.publishTaskEvent(
                capture(capturedEvent),
                eq(mockAuthentication),
                eq(ipAddress));
        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("user");

        replayAll();

        // Our controller should throw an exception. When the full dispatcher servlet is running, an
        // @ExceptionHandler will send the right HTTP response code to the browser.  In this test, we just have to
        // make sure the right exception is thrown.
        try
        {
            mockMvc.perform(
                    post("/api/v1/plugin/task/completeTask/{taskId}", taskId)
                            .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                            .session(mockHttpSession)
                            .principal(mockAuthentication));
        }
        catch (NestedServletException e)
        {
            // Spring MVC wraps the real exception with a NestedServletException
            assertNotNull(e.getCause());
            assertEquals(AcmUserActionFailedException.class, e.getCause().getClass());
        }
        catch (Exception e)
        {
            fail("Threw the wrong exception! " + e.getClass().getName());
        }


    verifyAll();

        AcmTaskCompletedEvent event = capturedEvent.getValue();
        assertEquals(taskId, event.getObjectId());
        assertEquals("TASK", event.getObjectType());
        assertFalse(event.isSucceeded());
    }

}
