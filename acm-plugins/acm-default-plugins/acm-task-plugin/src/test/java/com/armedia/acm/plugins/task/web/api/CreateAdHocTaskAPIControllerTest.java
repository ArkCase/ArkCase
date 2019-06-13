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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.armedia.acm.core.exceptions.AcmAppErrorJsonMsg;
import com.armedia.acm.plugins.task.model.AcmApplicationTaskEvent;
import com.armedia.acm.plugins.task.model.AcmTask;
import com.armedia.acm.plugins.task.service.TaskDao;
import com.armedia.acm.plugins.task.service.TaskEventPublisher;
import com.armedia.acm.plugins.task.service.impl.CreateAdHocTaskService;
import com.armedia.acm.services.search.model.SolrCore;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.easymock.Capture;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mule.api.DefaultMuleException;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
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
@ContextConfiguration(locations = { "classpath:/spring/spring-web-acm-web.xml",
        "classpath:/spring/spring-library-task-plugin-unit-test.xml" })
public class CreateAdHocTaskAPIControllerTest extends EasyMockSupport
{
    private MockMvc mockMvc;
    private MockHttpSession mockHttpSession;

    private CreateAdHocTaskAPIController unit;

    private TaskDao mockTaskDao;
    private TaskEventPublisher mockTaskEventPublisher;
    private Authentication mockAuthentication;
    private ExecuteSolrQuery mockExecuteSolrQuery;

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
        mockExecuteSolrQuery = createMock(ExecuteSolrQuery.class);

        unit = new CreateAdHocTaskAPIController();
        CreateAdHocTaskService createAdHocTaskService = new CreateAdHocTaskService();
        createAdHocTaskService.setExecuteSolrQuery(mockExecuteSolrQuery);

        createAdHocTaskService.setTaskDao(mockTaskDao);
        createAdHocTaskService.setTaskEventPublisher(mockTaskEventPublisher);

        unit.setCreateAdHocTaskService(createAdHocTaskService);

        mockMvc = MockMvcBuilders.standaloneSetup(unit).setHandlerExceptionResolvers(exceptionResolver).build();
    }

    @Test
    public void createAdHocTask() throws Exception
    {
        String name = "20140827_202";
        String type = "COMPLAINT";
        String query = "object_type_s:" + type;
        query += " AND name:" + name + " AND -status_s:DELETE";

        String solrResponse = "{ \"responseHeader\": { \"status\": 0, \"QTime\": 5, \"params\": { \"indent\": \"true\", \"q\": \"name: 20140827_202,\", \"_\": \"1411491195199\", \"wt\": \"json\" } }, \"response\": { \"numFound\": 1, \"start\": 0, \"docs\": [ { \"status_s\": \"DRAFT\", \"create_dt\": \"2014-08-27T16:04:25Z\", \"title_t\": \"Test complaint for report\", \"object_id_s\": \"202\", \"owner_s\": \"ann-acm\", \"deny_acl_ss\": [ \"TEST-DENY-ACL\" ], \"object_type_s\": \"COMPLAINT\", \"allow_acl_ss\": [ \"TEST-ALLOW-ACL\" ], \"id\": \"202-Complaint\", \"modifier_s\": \"ann-acm\", \"author\": \"ann-acm\", \"author_s\": \"ann-acm\", \"last_modified\": \"2014-08-27T16:04:25Z\", \"name\": \"20140827_202\", \"_version_\": 1477621708197200000 } ] } }  ";

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
        found.setStatus("ASSIGNED");

        Capture<AcmTask> taskSentToDao = Capture.newInstance();
        Capture<AcmApplicationTaskEvent> capturedEvent = Capture.newInstance();

        ObjectMapper objectMapper = new ObjectMapper();
        String inJson = objectMapper.writeValueAsString(adHoc);

        mockHttpSession.setAttribute("acm_ip_address", ipAddress);

        expect(mockTaskDao.createAdHocTask(capture(taskSentToDao))).andReturn(found);
        expect(mockExecuteSolrQuery.getResultsByPredefinedQuery(mockAuthentication, SolrCore.QUICK_SEARCH, query, 0, 10, ""))
                .andReturn(solrResponse).atLeastOnce();

        mockTaskEventPublisher.publishTaskEvent(capture(capturedEvent));

        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("user").atLeastOnce();

        replayAll();

        MvcResult result = mockMvc.perform(post("/api/v1/plugin/task/adHocTask")
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8")).session(mockHttpSession)
                .principal(mockAuthentication).contentType(MediaType.APPLICATION_JSON).content(inJson)).andReturn();

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
        String query = "object_type_s:" + type;
        query += " AND name:" + name + " AND -status_s:DELETE";

        String ipAddress = "ipAddress";

        AcmTask adHoc = new AcmTask();
        adHoc.setAssignee("assignee");
        adHoc.setAttachedToObjectName(name);
        adHoc.setAttachedToObjectType(type);

        ObjectMapper objectMapper = new ObjectMapper();
        String inJson = objectMapper.writeValueAsString(adHoc);

        mockHttpSession.setAttribute("acm_ip_address", ipAddress);

        expect(mockExecuteSolrQuery
                .getResultsByPredefinedQuery(mockAuthentication, SolrCore.QUICK_SEARCH, query, 0, 10, ""))
                        .andThrow(new DefaultMuleException("test Exception"));

        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("user").atLeastOnce();

        replayAll();

        Exception exception = null;

        try
        {
            mockMvc.perform(post("/api/v1/plugin/task/adHocTask").accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                    .session(mockHttpSession).principal(mockAuthentication)
                    .contentType(MediaType.APPLICATION_JSON).content(inJson)).andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.TEXT_PLAIN));
        }
        catch (Exception e)
        {
            exception = e;
        }

        verifyAll();

        assertNotNull(exception);
        assertTrue(exception.getCause() instanceof AcmAppErrorJsonMsg);
    }

}
