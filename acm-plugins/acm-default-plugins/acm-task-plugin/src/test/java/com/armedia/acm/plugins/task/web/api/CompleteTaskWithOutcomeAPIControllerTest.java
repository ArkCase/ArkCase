package com.armedia.acm.plugins.task.web.api;


import com.armedia.acm.plugins.task.model.AcmApplicationTaskEvent;
import com.armedia.acm.plugins.task.model.AcmTask;
import com.armedia.acm.plugins.task.model.TaskOutcome;
import com.armedia.acm.plugins.task.service.TaskDao;
import com.armedia.acm.plugins.task.service.TaskEventPublisher;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.easymock.Capture;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/spring/spring-web-acm-web.xml",
        "classpath:/spring/spring-library-task-plugin-test.xml"
})
public class CompleteTaskWithOutcomeAPIControllerTest extends EasyMockSupport
{
    private MockMvc mockMvc;
    private MockHttpSession mockHttpSession;

    private CompleteTaskWithOutcomeAPIController unit;

    private TaskDao mockTaskDao;
    private TaskEventPublisher mockTaskEventPublisher;
    private Authentication mockAuthentication;

    @Autowired
    private ExceptionHandlerExceptionResolver exceptionResolver;

    private Logger log = LoggerFactory.getLogger(getClass());

    @Before
    public void setUp() throws Exception
    {
        mockTaskDao = createMock(TaskDao.class);
        mockTaskEventPublisher = createMock(TaskEventPublisher.class);
        mockHttpSession = new MockHttpSession();
        mockAuthentication = createMock(Authentication.class);

        unit = new CompleteTaskWithOutcomeAPIController();

        unit.setTaskDao(mockTaskDao);
        unit.setTaskEventPublisher(mockTaskEventPublisher);

        mockMvc = MockMvcBuilders.standaloneSetup(unit).setHandlerExceptionResolvers(exceptionResolver).build();
    }

    @Test
    public void completeTask_noAvailableOutcome_taskShouldBeCompleted() throws Exception
    {
        Long taskId = 500L;
        String ipAddress = "ipAddress";

        AcmTask request = new AcmTask();
        request.setTaskId(taskId);

        Capture<AcmTask> toSave = new Capture<>();

        AcmTask found = new AcmTask();
        found.setTaskId(taskId);

        AcmTask saved = new AcmTask();
        saved.setTaskId(taskId);

        AcmTask completed = new AcmTask();
        completed.setTaskId(taskId);

        Capture<AcmApplicationTaskEvent> capturedEvent = new Capture<>();

        mockHttpSession.setAttribute("acm_ip_address", ipAddress);

        expect(mockTaskDao.findById(taskId)).andReturn(found);
        expect(mockTaskDao.save(capture(toSave))).andReturn(saved);
        expect(mockTaskDao.completeTask(mockAuthentication, taskId, null, null)).andReturn(completed);
        mockTaskEventPublisher.publishTaskEvent(capture(capturedEvent));
        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("user").atLeastOnce();

        ObjectMapper om = new ObjectMapper();
        String requestJson = om.writeValueAsString(request);

        replayAll();

        MvcResult result = mockMvc.perform(
                post("/api/v1/plugin/task/completeTask")
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON)
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
        assertEquals(taskId, completedTask.getTaskId());

        AcmApplicationTaskEvent event = capturedEvent.getValue();
        assertEquals(taskId, event.getObjectId());
        assertEquals("TASK", event.getObjectType());
        assertTrue(event.isSucceeded());

        AcmTask capturedTaskToSave = toSave.getValue();
        assertEquals(taskId, capturedTaskToSave.getTaskId());
    }

    @Test
    public void completeTask_availableOutcomesButNoSelectedOutcome_shouldThrowException() throws Exception
    {
        Long taskId = 500L;
        String ipAddress = "ipAddress";

        AcmTask request = new AcmTask();
        request.setTaskId(taskId);

        AcmTask found = new AcmTask();
        found.setTaskId(taskId);

        TaskOutcome available = new TaskOutcome();
        available.setName("outcome name");
        available.setDescription("outcome description");
        found.getAvailableOutcomes().add(available);

        Capture<AcmApplicationTaskEvent> capturedEvent = new Capture<>();

        mockHttpSession.setAttribute("acm_ip_address", ipAddress);

        expect(mockTaskDao.findById(taskId)).andReturn(found);
        mockTaskEventPublisher.publishTaskEvent(capture(capturedEvent));
        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("user").atLeastOnce();

        ObjectMapper om = new ObjectMapper();
        String requestJson = om.writeValueAsString(request);

        replayAll();

        MvcResult result = mockMvc.perform(
                post("/api/v1/plugin/task/completeTask")
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(mockHttpSession)
                        .principal(mockAuthentication))
                .andReturn();

        verifyAll();

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
        assertTrue(result.getResponse().getContentType().startsWith(MediaType.TEXT_PLAIN_VALUE));

        String returned = result.getResponse().getContentAsString();

        log.info("results: " + returned);

        AcmApplicationTaskEvent event = capturedEvent.getValue();
        assertEquals(taskId, event.getObjectId());
        assertEquals("TASK", event.getObjectType());
        assertFalse(event.isSucceeded());
    }

    @Test
    public void completeTask_taskOutcomeNotInAvailableOutcomes_shouldThrowException() throws Exception
    {
        Long taskId = 500L;
        String ipAddress = "ipAddress";

        AcmTask request = new AcmTask();
        request.setTaskId(taskId);
        TaskOutcome taskOutcome = new TaskOutcome();
        taskOutcome.setName("unknown name");
        taskOutcome.setDescription("unknown description");
        request.setTaskOutcome(taskOutcome);

        AcmTask found = new AcmTask();
        found.setTaskId(taskId);

        TaskOutcome available = new TaskOutcome();
        available.setName("outcome name");
        available.setDescription("outcome description");
        found.getAvailableOutcomes().add(available);

        Capture<AcmApplicationTaskEvent> capturedEvent = new Capture<>();

        mockHttpSession.setAttribute("acm_ip_address", ipAddress);

        expect(mockTaskDao.findById(taskId)).andReturn(found);
        mockTaskEventPublisher.publishTaskEvent(capture(capturedEvent));
        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("user").atLeastOnce();

        ObjectMapper om = new ObjectMapper();
        String requestJson = om.writeValueAsString(request);

        replayAll();

        MvcResult result = mockMvc.perform(
                post("/api/v1/plugin/task/completeTask")
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(mockHttpSession)
                        .principal(mockAuthentication))
                .andReturn();

        verifyAll();

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
        assertTrue(result.getResponse().getContentType().startsWith(MediaType.TEXT_PLAIN_VALUE));

        String returned = result.getResponse().getContentAsString();

        log.info("results: " + returned);

        AcmApplicationTaskEvent event = capturedEvent.getValue();
        assertEquals(taskId, event.getObjectId());
        assertEquals("TASK", event.getObjectType());
        assertFalse(event.isSucceeded());
    }

    @Test
    public void completeTask_taskOutcomeInAvailableOutcomesButRequiredFieldsNotSet_shouldThrowException() throws Exception
    {
        Long taskId = 500L;
        String ipAddress = "ipAddress";

        AcmTask request = new AcmTask();
        request.setTaskId(taskId);
        TaskOutcome taskOutcome = new TaskOutcome();
        taskOutcome.setName("outcome name");
        taskOutcome.setDescription("outcome description");
        request.setTaskOutcome(taskOutcome);

        AcmTask found = new AcmTask();
        found.setTaskId(taskId);

        TaskOutcome available = new TaskOutcome();
        available.setName("outcome name");
        available.setDescription("outcome description");
        available.getFieldsRequiredWhenOutcomeIsChosen().add("reworkInstructions");
        found.getAvailableOutcomes().add(available);


        Capture<AcmApplicationTaskEvent> capturedEvent = new Capture<>();

        mockHttpSession.setAttribute("acm_ip_address", ipAddress);

        expect(mockTaskDao.findById(taskId)).andReturn(found);
        mockTaskEventPublisher.publishTaskEvent(capture(capturedEvent));
        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("user").atLeastOnce();

        ObjectMapper om = new ObjectMapper();
        String requestJson = om.writeValueAsString(request);

        replayAll();

        MvcResult result = mockMvc.perform(
                post("/api/v1/plugin/task/completeTask")
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(mockHttpSession)
                        .principal(mockAuthentication))
                .andReturn();

        verifyAll();

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
        assertTrue(result.getResponse().getContentType().startsWith(MediaType.TEXT_PLAIN_VALUE));

        String returned = result.getResponse().getContentAsString();

        log.info("results: " + returned);

        AcmApplicationTaskEvent event = capturedEvent.getValue();
        assertEquals(taskId, event.getObjectId());
        assertEquals("TASK", event.getObjectType());
        assertFalse(event.isSucceeded());
    }

    @Test
    public void completeTask_taskOutcomeIsValid_taskShouldBeCompleted() throws Exception
    {
        Long taskId = 500L;
        String ipAddress = "ipAddress";

        AcmTask request = new AcmTask();
        request.setTaskId(taskId);
        request.setOutcomeName("outcome property");
        TaskOutcome taskOutcome = new TaskOutcome();
        taskOutcome.setName("outcome name");
        taskOutcome.setDescription("outcome description");
        request.setTaskOutcome(taskOutcome);
        request.setReworkInstructions("rework instructions");

        AcmTask found = new AcmTask();
        found.setTaskId(taskId);

        TaskOutcome available = new TaskOutcome();
        available.setName("outcome name");
        available.setDescription("outcome description");
        available.getFieldsRequiredWhenOutcomeIsChosen().add("reworkInstructions");
        found.getAvailableOutcomes().add(available);

        Capture<AcmTask> toSave = new Capture<>();

        AcmTask saved = new AcmTask();
        saved.setTaskId(taskId);

        AcmTask completed = new AcmTask();
        completed.setTaskId(taskId);

        Capture<AcmApplicationTaskEvent> capturedEvent = new Capture<>();

        mockHttpSession.setAttribute("acm_ip_address", ipAddress);

        expect(mockTaskDao.findById(taskId)).andReturn(found);
        expect(mockTaskDao.save(capture(toSave))).andReturn(saved);
        expect(mockTaskDao.completeTask(
                mockAuthentication,
                taskId,
                request.getOutcomeName(),
                request.getTaskOutcome().getName())).andReturn(completed);
        mockTaskEventPublisher.publishTaskEvent(capture(capturedEvent));
        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("user").atLeastOnce();

        ObjectMapper om = new ObjectMapper();
        String requestJson = om.writeValueAsString(request);

        log.debug("request json: " + requestJson);

        replayAll();

        MvcResult result = mockMvc.perform(
                post("/api/v1/plugin/task/completeTask")
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON)
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
        assertEquals(taskId, completedTask.getTaskId());

        AcmTask capturedTaskToSave = toSave.getValue();
        assertEquals(taskId, capturedTaskToSave.getTaskId());

        AcmApplicationTaskEvent event = capturedEvent.getValue();
        assertEquals(taskId, event.getObjectId());
        assertEquals("TASK", event.getObjectType());
        assertTrue(event.isSucceeded());
    }

}
