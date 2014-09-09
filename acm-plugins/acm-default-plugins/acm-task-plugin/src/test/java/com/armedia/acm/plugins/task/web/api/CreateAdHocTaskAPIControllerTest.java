package com.armedia.acm.plugins.task.web.api;

import com.armedia.acm.plugins.task.exception.AcmTaskException;
import com.armedia.acm.plugins.task.model.AcmApplicationTaskEvent;
import com.armedia.acm.plugins.task.model.AcmTask;
import com.armedia.acm.plugins.task.service.TaskDao;
import com.armedia.acm.plugins.task.service.TaskEventPublisher;
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

import java.util.Date;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/spring/spring-web-acm-web.xml",
        "classpath:/spring/spring-library-task-plugin-test.xml"
})
public class CreateAdHocTaskAPIControllerTest extends EasyMockSupport
{
    private MockMvc mockMvc;
    private MockHttpSession mockHttpSession;

    private CreateAdHocTaskAPIController unit;

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

        unit = new CreateAdHocTaskAPIController();

        unit.setTaskDao(mockTaskDao);
        unit.setTaskEventPublisher(mockTaskEventPublisher);

        mockMvc = MockMvcBuilders.standaloneSetup(unit).setHandlerExceptionResolvers(exceptionResolver).build();
    }

    @Test
    public void createAdHocTask() throws Exception
    {
        Long taskId = 500L;
        String ipAddress = "ipAddress";

        AcmTask adHoc = new AcmTask();
        adHoc.setAssignee("assignee");
        adHoc.setAttachedToObjectType("COMPLAINT");
        adHoc.setDetails("details");
        adHoc.setPercentComplete(23);
        adHoc.setStatus("ASSIGNED");
        adHoc.setTaskStartDate(new Date());
        adHoc.setTitle("title");

        AcmTask found = new AcmTask();
        found.setAssignee(adHoc.getAssignee());
        found.setTaskId(taskId);

        Capture<AcmTask> taskSentToDao = new Capture<>();
        Capture<AcmApplicationTaskEvent> capturedEvent = new Capture<>();

        org.codehaus.jackson.map.ObjectMapper objectMapper = new org.codehaus.jackson.map.ObjectMapper();
        String inJson = objectMapper.writeValueAsString(adHoc);

        mockHttpSession.setAttribute("acm_ip_address", ipAddress);

        expect(mockTaskDao.createAdHocTask(capture(taskSentToDao))).andReturn(found);
        mockTaskEventPublisher.publishTaskEvent(capture(capturedEvent));

        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("user").atLeastOnce();

        replayAll();

        MvcResult result = mockMvc.perform(
                post("/api/v1/plugin/task/adHocTask")
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
        assertNull(sentToDao.getTaskId());
        assertEquals(adHoc.getAssignee(), sentToDao.getAssignee());

        String returned = result.getResponse().getContentAsString();

        log.info("results: " + returned);

        AcmTask newTask = objectMapper.readValue(returned, AcmTask.class);

        assertNotNull(newTask);
        assertEquals(newTask.getTaskId(), found.getTaskId());

        AcmApplicationTaskEvent event = capturedEvent.getValue();
        assertEquals(taskId, event.getObjectId());
        assertEquals("TASK", event.getObjectType());
        assertTrue(event.isSucceeded());
    }

    @Test
    public void createAdHocTask_exception() throws Exception
    {
        String ipAddress = "ipAddress";

        AcmTask adHoc = new AcmTask();
        adHoc.setAssignee("assignee");

        Capture<AcmTask> taskSentToDao = new Capture<>();
        Capture<AcmApplicationTaskEvent> capturedEvent = new Capture<>();

        org.codehaus.jackson.map.ObjectMapper objectMapper = new org.codehaus.jackson.map.ObjectMapper();
        String inJson = objectMapper.writeValueAsString(adHoc);

        mockHttpSession.setAttribute("acm_ip_address", ipAddress);

        expect(mockTaskDao.createAdHocTask(capture(taskSentToDao))).andThrow(new AcmTaskException("testException"));
        mockTaskEventPublisher.publishTaskEvent(capture(capturedEvent));

        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("user").atLeastOnce();

        replayAll();

        mockMvc.perform(
                post("/api/v1/plugin/task/adHocTask")
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .session(mockHttpSession)
                        .principal(mockAuthentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(inJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.TEXT_PLAIN));

        verifyAll();

        AcmTask sentToDao = taskSentToDao.getValue();
        assertNull(sentToDao.getTaskId());
        assertEquals(adHoc.getAssignee(), sentToDao.getAssignee());

        AcmApplicationTaskEvent event = capturedEvent.getValue();
        assertNull(event.getObjectId());
        assertEquals("TASK", event.getObjectType());
        assertFalse(event.isSucceeded());
    }


}
