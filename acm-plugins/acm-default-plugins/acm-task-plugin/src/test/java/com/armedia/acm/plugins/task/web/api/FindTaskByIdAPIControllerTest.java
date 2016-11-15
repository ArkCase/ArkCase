package com.armedia.acm.plugins.task.web.api;

import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.task.exception.AcmTaskException;
import com.armedia.acm.plugins.task.model.AcmApplicationTaskEvent;
import com.armedia.acm.plugins.task.model.AcmTask;
import com.armedia.acm.plugins.task.service.AcmTaskService;
import com.armedia.acm.plugins.task.service.TaskEventPublisher;
import org.codehaus.jackson.map.ObjectMapper;
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

import java.util.ArrayList;

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/spring/spring-web-acm-web.xml",
        "classpath:/spring/spring-library-task-plugin-test.xml"
})
public class FindTaskByIdAPIControllerTest extends EasyMockSupport
{
    private MockMvc mockMvc;
    private MockHttpSession mockHttpSession;
    private AcmTaskService mockTaskService;
    private TaskEventPublisher mockTaskEventPublisher;
    private Authentication mockAuthentication;
    private FindTaskByIdAPIController unit;

    @Autowired
    private ExceptionHandlerExceptionResolver exceptionResolver;

    private Logger log = LoggerFactory.getLogger(getClass());

    @Before
    public void setUp() throws Exception
    {
        mockTaskService = createMock(AcmTaskService.class);
        mockTaskEventPublisher = createMock(TaskEventPublisher.class);
        mockHttpSession = new MockHttpSession();
        mockAuthentication = createMock(Authentication.class);

        unit = new FindTaskByIdAPIController();
        unit.setTaskEventPublisher(mockTaskEventPublisher);
        unit.setTaskService(mockTaskService);

        mockMvc = MockMvcBuilders.standaloneSetup(unit).setHandlerExceptionResolvers(exceptionResolver).build();
    }

    @Test
    public void findTaskById() throws Exception
    {
        String ipAddress = "ipAddress";
        String title = "The Test Title";
        Long taskId = 500L;
        Long docUnderReviewId = 250L;

        AcmTask returned = new AcmTask();
        returned.setTaskId(taskId);
        returned.setTitle(title);
        returned.setReviewDocumentPdfRenditionId(docUnderReviewId);
        returned.setChildObjects(new ArrayList<>());
        returned.setDocumentUnderReview(new EcmFile());
        mockHttpSession.setAttribute("acm_ip_address", ipAddress);

        expect(mockTaskService.retrieveTask(taskId)).andReturn(returned);

        Capture<AcmApplicationTaskEvent> eventRaised = Capture.newInstance();
        mockTaskEventPublisher.publishTaskEvent(capture(eventRaised));

        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("user").atLeastOnce();

        replayAll();

        MvcResult result = mockMvc.perform(
                get("/api/v1/plugin/task/byId/{taskId}", taskId)
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .session(mockHttpSession)
                        .principal(mockAuthentication))
                .andReturn();

        verifyAll();

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertTrue(result.getResponse().getContentType().startsWith(MediaType.APPLICATION_JSON_VALUE));

        String json = result.getResponse().getContentAsString();
        log.info("results: {}", json);

        AcmTask fromJson = new ObjectMapper().readValue(json, AcmTask.class);

        assertNotNull(fromJson);
        assertEquals(returned.getTitle(), fromJson.getTitle());

        AcmApplicationTaskEvent event = eventRaised.getValue();
        assertTrue(event.isSucceeded());
        assertEquals(taskId, event.getObjectId());

        assertTrue(fromJson.getChildObjects().isEmpty());
        assertNotNull(fromJson.getDocumentUnderReview());
    }

    @Test
    public void findTaskById_exception() throws Exception
    {
        Long taskId = 500L;
        String ipAddress = "ipAddress";

        mockHttpSession.setAttribute("acm_ip_address", ipAddress);

        expect(mockTaskService.retrieveTask(taskId)).andReturn(null);

        Capture<AcmApplicationTaskEvent> eventRaised = Capture.newInstance();
        mockTaskEventPublisher.publishTaskEvent(capture(eventRaised));

        expect(mockAuthentication.getName()).andReturn("user").atLeastOnce();

        replayAll();

        mockMvc.perform(
                get("/api/v1/plugin/task/byId/{taskId}", taskId)
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .principal(mockAuthentication)
                        .session(mockHttpSession))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.TEXT_PLAIN));

        verifyAll();

        AcmApplicationTaskEvent event = eventRaised.getValue();
        assertFalse(event.isSucceeded());
        assertEquals(taskId, event.getObjectId());
    }
}
