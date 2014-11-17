package com.armedia.acm.plugins.task.web.api;

import com.armedia.acm.plugins.task.model.WorkflowHistoryInstance;
import com.armedia.acm.plugins.task.service.TaskDao;
import com.fasterxml.jackson.databind.ObjectMapper;

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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/spring/spring-web-acm-web.xml",
        "classpath:/spring/spring-library-task-plugin-test.xml"
})
public class WorkflowHistoryAPIControllerTest extends EasyMockSupport
{
    private MockMvc mockMvc;
    private MockHttpSession mockHttpSession;

    private WorkflowHistoryAPIController unit;

    private TaskDao mockTaskDao;

    @Autowired
    private ExceptionHandlerExceptionResolver exceptionResolver;

    private Logger log = LoggerFactory.getLogger(getClass());

    @Before
    public void setUp() throws Exception
    {
        mockTaskDao = createMock(TaskDao.class);
        mockHttpSession = new MockHttpSession();

        unit = new WorkflowHistoryAPIController();

        unit.setTaskDao(mockTaskDao);

        mockMvc = MockMvcBuilders.standaloneSetup(unit).setHandlerExceptionResolvers(exceptionResolver).build();
    }

    @Test
    public void workflowHistoryNonAdhocTest() throws Exception
    {        
        WorkflowHistoryInstance instance = new WorkflowHistoryInstance();
        instance.setId("id");
        instance.setParticipant("participant");
        instance.setRole("role");
        instance.setStatus("status");
        instance.setStartDate(new Date());
        instance.setEndDate(new Date());
        
        String ipAddress = "ipAddress";
        String businessProecessId = "businessProecessId";

        expect(mockTaskDao.getWorkflowHistory(businessProecessId, false)).andReturn(Arrays.asList(instance));

        mockHttpSession.setAttribute("acm_ip_address", ipAddress);
        mockHttpSession.setAttribute("id", businessProecessId);

        replayAll();

        MvcResult result = mockMvc.perform(
                get("/api/v1/plugin/task/history/{id}/false", businessProecessId)
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
                .andReturn();

        verifyAll();

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertTrue(result.getResponse().getContentType().startsWith(MediaType.APPLICATION_JSON_VALUE));

        String returned = result.getResponse().getContentAsString();

        log.info("results: " + returned);

        ObjectMapper objectMapper = new ObjectMapper();

        List<WorkflowHistoryInstance> foundWorkflowHistory = objectMapper.readValue(returned,
                objectMapper.getTypeFactory().constructParametricType(List.class, WorkflowHistoryInstance.class));

        assertEquals(1, foundWorkflowHistory.size());

        WorkflowHistoryInstance found = foundWorkflowHistory.get(0);
        assertEquals(instance.getId(), found.getId());
    }
    
    @Test
    public void workflowHistoryAdhocTest() throws Exception
    {        
        WorkflowHistoryInstance instance = new WorkflowHistoryInstance();
        instance.setId("id");
        instance.setParticipant("participant");
        instance.setRole("role");
        instance.setStatus("status");
        instance.setStartDate(new Date());
        instance.setEndDate(new Date());
        
        String ipAddress = "ipAddress";
        String taskId = "taskId";

        expect(mockTaskDao.getWorkflowHistory(taskId, true)).andReturn(Arrays.asList(instance));

        mockHttpSession.setAttribute("acm_ip_address", ipAddress);
        mockHttpSession.setAttribute("id", taskId);

        replayAll();

        MvcResult result = mockMvc.perform(
                get("/api/v1/plugin/task/history/{id}/true", taskId)
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
                .andReturn();

        verifyAll();

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertTrue(result.getResponse().getContentType().startsWith(MediaType.APPLICATION_JSON_VALUE));

        String returned = result.getResponse().getContentAsString();

        log.info("results: " + returned);

        ObjectMapper objectMapper = new ObjectMapper();

        List<WorkflowHistoryInstance> foundWorkflowHistory = objectMapper.readValue(returned,
                objectMapper.getTypeFactory().constructParametricType(List.class, WorkflowHistoryInstance.class));

        assertEquals(1, foundWorkflowHistory.size());

        WorkflowHistoryInstance found = foundWorkflowHistory.get(0);
        assertEquals(instance.getId(), found.getId());
    }
}
