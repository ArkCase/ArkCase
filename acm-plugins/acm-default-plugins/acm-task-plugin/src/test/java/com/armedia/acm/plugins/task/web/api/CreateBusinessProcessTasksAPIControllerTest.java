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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.task.exception.AcmTaskException;
import com.armedia.acm.plugins.task.model.AcmApplicationTaskEvent;
import com.armedia.acm.plugins.task.model.AcmTask;
import com.armedia.acm.plugins.task.service.AcmTaskService;
import com.armedia.acm.plugins.task.service.TaskEventPublisher;
import com.fasterxml.jackson.databind.ObjectMapper;

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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by vladimir.radeski on 11/20/2017.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/spring-web-acm-web.xml",
        "classpath:/spring/spring-library-task-plugin-unit-test.xml" })
public class CreateBusinessProcessTasksAPIControllerTest extends EasyMockSupport
{
    private MockMvc mockMvc;
    private MockHttpSession mockHttpSession;

    private CreateBusinessProcessTasksAPIController unit;
    private AcmTaskService mockTaskService;
    private TaskEventPublisher mockTaskEventPublisher;
    private Authentication mockAuthentication;
    private EcmFile mockEcmFile;

    @Autowired
    private ExceptionHandlerExceptionResolver exceptionResolver;

    @Before
    public void setUp() throws Exception
    {
        mockTaskService = createMock(AcmTaskService.class);
        mockTaskEventPublisher = createMock(TaskEventPublisher.class);
        mockHttpSession = new MockHttpSession();
        mockAuthentication = createMock(Authentication.class);
        mockEcmFile = createMock(EcmFile.class);

        unit = new CreateBusinessProcessTasksAPIController();

        unit.setTaskService(mockTaskService);
        unit.setTaskEventPublisher(mockTaskEventPublisher);

        mockMvc = MockMvcBuilders.standaloneSetup(unit).setHandlerExceptionResolvers(exceptionResolver).build();
    }

    @Test
    public void reviewDocuments() throws Exception
    {
        String businessProcessName = "acmDocumentWorkflow";
        String attachedToObjectType = "COMPLAINT";
        String attachedToObjectName = "20140827_202";
        Long attachedToObjectId = 202L;
        Long taskId = 500L;
        String ipAddress = "ipAddress";
        ArrayList<EcmFile> documentsToReview = new ArrayList<>();

        AcmTask reviewTask = new AcmTask();
        reviewTask.setAssignee("assignee");
        reviewTask.setAttachedToObjectType(attachedToObjectType);
        reviewTask.setTaskStartDate(new Date());
        reviewTask.setTitle("title");
        reviewTask.setStatus("ASSIGNED");
        reviewTask.setAttachedToObjectName(attachedToObjectName);
        reviewTask.setAttachedToObjectId(attachedToObjectId);
        reviewTask.setDocumentsToReview(documentsToReview);
        reviewTask.setDueDate(new Date());
        reviewTask.setPercentComplete(0);

        AcmTask createdAcmTask = new AcmTask();
        createdAcmTask.setAssignee(reviewTask.getAssignee());
        createdAcmTask.setTaskId(taskId);
        List<AcmTask> createdAcmTasks = new ArrayList<>();
        createdAcmTasks.add(createdAcmTask);

        ObjectMapper objectMapper = new ObjectMapper();
        String inJson = objectMapper.writeValueAsString(reviewTask);

        mockHttpSession.setAttribute("acm_ip_address", ipAddress);

        Capture<AcmTask> capturedAcmTask = EasyMock.newCapture();

        expect(mockAuthentication.getName()).andReturn("user").atLeastOnce();
        expect(mockTaskService.startReviewDocumentsWorkflow(capture(capturedAcmTask), EasyMock.eq(businessProcessName),
                EasyMock.eq(mockAuthentication))).andReturn(createdAcmTasks);

        replayAll();

        MvcResult result = mockMvc
                .perform(post("/api/v1/plugin/tasks/documents/review?businessProcessName=acmDocumentWorkflow")
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .session(mockHttpSession)
                        .principal(mockAuthentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(inJson))
                .andReturn();

        verifyAll();

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertTrue(result.getResponse().getContentType().startsWith(MediaType.APPLICATION_JSON_VALUE));
    }

    @Test
    public void reviewDocuments_exception() throws Exception
    {
        String businessProcessName = "acmDocumentWorkflow";
        String attachedToObjectType = "COMPLAINT";
        String attachedToObjectName = "20140827_202";
        Long attachedToObjectId = 202L;
        String ipAddress = "ipAddress";
        ArrayList<EcmFile> documentsToReview = new ArrayList<>();

        AcmTask reviewTask = new AcmTask();
        reviewTask.setAssignee("assignee");
        reviewTask.setAttachedToObjectType(attachedToObjectType);
        reviewTask.setTaskStartDate(new Date());
        reviewTask.setTitle("title");
        reviewTask.setStatus("ASSIGNED");
        reviewTask.setAttachedToObjectName(attachedToObjectName);
        reviewTask.setAttachedToObjectId(attachedToObjectId);
        reviewTask.setDocumentsToReview(documentsToReview);
        reviewTask.setDueDate(new Date());
        reviewTask.setPercentComplete(0);

        ObjectMapper objectMapper = new ObjectMapper();
        String inJson = objectMapper.writeValueAsString(reviewTask);

        mockHttpSession.setAttribute("acm_ip_address", ipAddress);

        Capture<AcmTask> capturedAcmTask = EasyMock.newCapture();
        Capture<AcmApplicationTaskEvent> capturedEvent = Capture.newInstance();

        expect(mockAuthentication.getName()).andReturn("user").atLeastOnce();
        expect(mockTaskService.startReviewDocumentsWorkflow(capture(capturedAcmTask), EasyMock.eq(businessProcessName),
                EasyMock.eq(mockAuthentication))).andThrow(new AcmTaskException("Test Exception"));
        mockTaskEventPublisher.publishTaskEvent(capture(capturedEvent));

        replayAll();

        Exception exception;

        MvcResult res = mockMvc.perform(post("/api/v1/plugin/tasks/documents/review?businessProcessName=acmDocumentWorkflow")
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                .session(mockHttpSession)
                .principal(mockAuthentication)
                .contentType(MediaType.APPLICATION_JSON)
                .content(inJson)).andReturn();

        exception = res.getResolvedException();

        verifyAll();

        assertNotNull(exception);
        assertTrue(exception.getCause() instanceof AcmTaskException);

    }
}
