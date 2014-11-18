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
import org.mule.api.MuleMessage;
import org.mule.api.client.MuleClient;
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
import java.util.HashMap;
import java.util.Map;

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
    private MuleClient mockMuleClient;
    private MuleMessage mockMuleMessage;

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
        mockMuleClient = createMock(MuleClient.class);
        mockMuleMessage = createMock(MuleMessage.class);

        unit = new CreateAdHocTaskAPIController();
        unit.setMuleClient(mockMuleClient);

        unit.setTaskDao(mockTaskDao);
        unit.setTaskEventPublisher(mockTaskEventPublisher);

        mockMvc = MockMvcBuilders.standaloneSetup(unit).setHandlerExceptionResolvers(exceptionResolver).build();
    }

    @Test
    public void createAdHocTask() throws Exception
    {
        String name = "20140827_202";
        String type = "COMPLAINT";
        String query = "name:" + name;
        query += " AND (object_type_s:" + type + ")";

        String solrResponse = "{ \"responseHeader\": { \"status\": 0, \"QTime\": 5, \"params\": { \"indent\": \"true\", \"q\": \"name: 20140827_202,\", \"_\": \"1411491195199\", \"wt\": \"json\" } }, \"response\": { \"numFound\": 1, \"start\": 0, \"docs\": [ { \"status_s\": \"DRAFT\", \"create_dt\": \"2014-08-27T16:04:25Z\", \"title_t\": \"Test complaint for report\", \"object_id_s\": \"202\", \"owner_s\": \"ann-acm\", \"deny_acl_ss\": [ \"TEST-DENY-ACL\" ], \"object_type_s\": \"COMPLAINT\", \"allow_acl_ss\": [ \"TEST-ALLOW-ACL\" ], \"id\": \"202-Complaint\", \"modifier_s\": \"ann-acm\", \"author\": \"ann-acm\", \"author_s\": \"ann-acm\", \"last_modified\": \"2014-08-27T16:04:25Z\", \"name\": \"20140827_202\", \"_version_\": 1477621708197200000 } ] } }  ";

        Map<String, Object> headers = new HashMap<>();
        headers.put("query", query);
        headers.put("firstRow", 0);
        headers.put("maxRows", 10);
        headers.put("sort", "");
    	
        Long taskId = 500L;
        String ipAddress = "ipAddress";

        AcmTask adHoc = new AcmTask();
        adHoc.setAssignee("assignee");
        adHoc.setAttachedToObjectType(type);
        adHoc.setDetails("details");
        adHoc.setPercentComplete(23);
        adHoc.setStatus("ASSIGNED");
        adHoc.setTaskStartDate(new Date());
        adHoc.setTitle("title");
        adHoc.setAttachedToObjectName(name);

        AcmTask found = new AcmTask();
        found.setAssignee(adHoc.getAssignee());
        found.setTaskId(taskId);

        Capture<AcmTask> taskSentToDao = new Capture<>();
        Capture<AcmApplicationTaskEvent> capturedEvent = new Capture<>();

        org.codehaus.jackson.map.ObjectMapper objectMapper = new org.codehaus.jackson.map.ObjectMapper();
        String inJson = objectMapper.writeValueAsString(adHoc);

        mockHttpSession.setAttribute("acm_ip_address", ipAddress);

        expect(mockTaskDao.createAdHocTask(capture(taskSentToDao))).andReturn(found);
        expect(mockMuleClient.send("vm://quickSearchQuery.in", "", headers)).andReturn(mockMuleMessage);
        expect(mockMuleMessage.getPayload()).andReturn(solrResponse).atLeastOnce();
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
        String name = "20140827_202";
        String type = "COMPLAINT";
        String query = "name:" + name;
        query += " AND (object_type_s:" + type + ")";

        String solrResponse = "{ \"responseHeader\": { \"status\": 0, \"QTime\": 5, \"params\": { \"indent\": \"true\", \"q\": \"name: 20140827_202,\", \"_\": \"1411491195199\", \"wt\": \"json\" } }, \"response\": { \"numFound\": 1, \"start\": 0, \"docs\": [ { \"status_s\": \"DRAFT\", \"create_dt\": \"2014-08-27T16:04:25Z\", \"title_t\": \"Test complaint for report\", \"object_id_s\": \"202\", \"owner_s\": \"ann-acm\", \"deny_acl_ss\": [ \"TEST-DENY-ACL\" ], \"object_type_s\": \"COMPLAINT\", \"allow_acl_ss\": [ \"TEST-ALLOW-ACL\" ], \"id\": \"202-Complaint\", \"modifier_s\": \"ann-acm\", \"author\": \"ann-acm\", \"author_s\": \"ann-acm\", \"last_modified\": \"2014-08-27T16:04:25Z\", \"name\": \"20140827_202\", \"_version_\": 1477621708197200000 } ] } }  ";

        Map<String, Object> headers = new HashMap<>();
        headers.put("query", query);
        headers.put("firstRow", 0);
        headers.put("maxRows", 10);
        headers.put("sort", "");
        
        String ipAddress = "ipAddress";

        AcmTask adHoc = new AcmTask();
        adHoc.setAssignee("assignee");
        adHoc.setAttachedToObjectName(name);
        adHoc.setAttachedToObjectType(type);


        Capture<AcmTask> taskSentToDao = new Capture<>();
        Capture<AcmApplicationTaskEvent> capturedEvent = new Capture<>();

        org.codehaus.jackson.map.ObjectMapper objectMapper = new org.codehaus.jackson.map.ObjectMapper();
        String inJson = objectMapper.writeValueAsString(adHoc);

        mockHttpSession.setAttribute("acm_ip_address", ipAddress);

        expect(mockTaskDao.createAdHocTask(capture(taskSentToDao))).andThrow(new AcmTaskException("testException"));
        expect(mockMuleClient.send("vm://quickSearchQuery.in", "", headers)).andReturn(mockMuleMessage);
        expect(mockMuleMessage.getPayload()).andReturn(solrResponse).atLeastOnce();
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
