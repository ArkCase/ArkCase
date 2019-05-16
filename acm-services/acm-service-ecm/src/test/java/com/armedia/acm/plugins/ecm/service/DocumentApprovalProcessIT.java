package com.armedia.acm.plugins.ecm.service;

/*-
 * #%L
 * ACM Service: Enterprise Content Management
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.armedia.acm.plugins.ecm.service.impl.MockChangeObjectStatusService;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.EndEvent;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.FormProperty;
import org.activiti.bpmn.model.FormValue;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.bpmn.model.StartEvent;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.Task;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/spring-library-ecm-activiti-test.xml" })
public class DocumentApprovalProcessIT
{
    private final String processId = "acmDocumentWorkflow";

    @Autowired
    private ProcessEngine pe;

    @Autowired
    private RepositoryService repo;

    @Autowired
    private RuntimeService rt;

    @Autowired
    private TaskService ts;

    @Autowired
    private HistoryService hs;

    @Autowired
    @Qualifier(value = "changeObjectStatusService")
    private MockChangeObjectStatusService changeObjectStatusService;

    private Logger log = LoggerFactory.getLogger(getClass());

    private List<String> reviewers;
    private ProcessInstance pi;
    private ProcessInstance piCandidateGroups;
    private String documentAuthor;

    @Before
    public void setUp() throws Exception
    {

        // deploy
        repo.createDeployment()
                .addClasspathResource("activiti/acmDocumentWorkflow_v5.bpmn20.xml")
                .deploy();

        Map<String, Object> pvars = new HashMap<>();

        reviewers = Arrays.asList("jerry", "bob", "mickey");
        String taskName = "Request to Close Complaint '20141015_9823'";
        documentAuthor = "phil";

        pvars.put("reviewers", reviewers);
        pvars.put("taskName", taskName);
        pvars.put("documentAuthor", documentAuthor);
        pvars.put("OBJECT_ID", 500L);
        pvars.put("OBJECT_TYPE", "OBJECT_TYPE");

        pi = createWorkflowProcess(pvars);

        changeObjectStatusService.setTimesCalled(0);

        pvars.put("candidateGroups", "TEST_GROUP");

        piCandidateGroups = createWorkflowProcess(pvars);

    }

    @After
    public void shutDown() throws Exception
    {
        pe.close();
    }

    @Test
    public void documentApproval_happyPath() throws Exception
    {
        assertNotNull(pi);

        List<Task> reviews = ts.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).list();

        log.debug("Found " + reviews.size() + " review tasks.");

        assertEquals(reviewers.size(), reviews.size());

        for (int a = 0; a < reviews.size(); ++a)
        {
            Task task = reviews.get(a);
            ts.setVariable(task.getId(), "reviewOutcome", "APPROVE");
            ts.complete(task.getId());

            ProcessInstance current = rt.createProcessInstanceQuery().processInstanceId(pi.getProcessInstanceId()).singleResult();
            boolean shouldBeComplete = a == reviews.size() - 1;

            // when all reviewers have approved, the process will be complete and the runtime service query
            // will not find it.
            if (shouldBeComplete)
            {
                assertNull(current);
            }
            else
            {
                assertFalse(current.isEnded());
            }
        }

        assertEquals(1, changeObjectStatusService.getTimesCalled());
    }

    @Test
    public void documentApproval_happyPath_with_candidate_groups() throws Exception
    {
        assertNotNull(piCandidateGroups);

        List<Task> reviews = ts.createTaskQuery().processInstanceId(piCandidateGroups.getProcessInstanceId()).list();

        log.debug("Found " + reviews.size() + " review tasks.");

        assertEquals(reviewers.size(), reviews.size());

        for (int a = 0; a < reviews.size(); ++a)
        {
            Task task = reviews.get(a);
            assertEquals(Arrays.asList("TEST_GROUP"), findCandidateGroups(task.getId()));
            ts.setVariable(task.getId(), "reviewOutcome", "APPROVE");
            ts.complete(task.getId());

            ProcessInstance current = rt.createProcessInstanceQuery().processInstanceId(piCandidateGroups.getProcessInstanceId())
                    .singleResult();
            boolean shouldBeComplete = a == reviews.size() - 1;

            // when all reviewers have approved, the process will be complete and the runtime service query
            // will not find it.
            if (shouldBeComplete)
            {
                assertNull(current);
            }
            else
            {
                assertFalse(current.isEnded());
            }
        }

        assertEquals(1, changeObjectStatusService.getTimesCalled());
    }

    private List<String> findCandidateGroups(String taskId)
    {
        List<IdentityLink> candidates = ts.getIdentityLinksForTask(taskId);

        if (candidates != null)
        {
            List<String> retval = candidates.stream().filter(il -> "candidate".equals(il.getType()))
                    .filter(il -> il.getGroupId() != null).map(IdentityLink::getGroupId).collect(Collectors.toList());
            return retval;
        }

        return null;
    }

    @Test
    public void documentApproval_firstReworkClosesOtherReviewTasks_cancelReworkEndsProcess() throws Exception
    {
        assertNotNull(pi);

        List<Task> reviews = ts.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).list();

        log.debug("Found " + reviews.size() + " review tasks.");

        assertEquals(reviewers.size(), reviews.size());

        // approve first task
        Task task = reviews.get(0);
        ts.setVariable(task.getId(), "reviewOutcome", "APPROVE");
        ts.complete(task.getId());

        // send for rework second task
        Task second = reviews.get(1);
        ts.setVariable(second.getId(), "reviewOutcome", "SEND_FOR_REWORK");
        ts.setVariable(second.getId(), "REWORK_INSTRUCTIONS", "rework instructions");
        ts.complete(second.getId());

        // should be only the rework task now.
        List<Task> reworkTasks = ts.createTaskQuery().includeProcessVariables().processInstanceId(pi.getProcessInstanceId()).list();
        assertEquals(1, reworkTasks.size());

        Task rework = reworkTasks.get(0);
        assertEquals("authorReworksDocument", rework.getTaskDefinitionKey());
        assertEquals(documentAuthor, rework.getAssignee());
        assertEquals("rework instructions", rework.getProcessVariables().get("REWORK_INSTRUCTIONS"));

        ts.setVariable(rework.getId(), "reworkOutcome", "CANCEL_DOCUMENT");
        ts.complete(rework.getId());

        ProcessInstance piShouldBeNull = rt.createProcessInstanceQuery().processInstanceId(pi.getProcessInstanceId()).singleResult();
        assertNull(piShouldBeNull);

        assertEquals(1, changeObjectStatusService.getTimesCalled());
    }

    @Test
    public void documentApproval_firstReworkClosesOtherReviewTasks_resubmitGoesBackToReviewers() throws Exception
    {
        assertNotNull(pi);

        List<Task> reviews = ts.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).list();

        log.debug("Found " + reviews.size() + " review tasks.");

        assertEquals(reviewers.size(), reviews.size());

        // approve first task
        Task task = reviews.get(0);
        ts.setVariable(task.getId(), "reviewOutcome", "APPROVE");
        ts.complete(task.getId());

        // send for rework second task
        Task second = reviews.get(1);
        ts.setVariable(second.getId(), "reviewOutcome", "SEND_FOR_REWORK");
        ts.complete(second.getId());

        // should be only the rework task now.
        List<Task> reworkTasks = ts.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).list();
        assertEquals(1, reworkTasks.size());

        Task rework = reworkTasks.get(0);
        assertEquals("authorReworksDocument", rework.getTaskDefinitionKey());
        assertEquals(documentAuthor, rework.getAssignee());
        ts.setVariable(rework.getId(), "reworkOutcome", "RESUBMIT");
        ts.complete(rework.getId());

        List<Task> postReworkReviewTasks = ts.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).list();
        assertEquals(reviewers.size(), postReworkReviewTasks.size());

        assertEquals(0, changeObjectStatusService.getTimesCalled());

    }

    protected StartEvent createStartEvent()
    {
        StartEvent startEvent = new StartEvent();
        startEvent.setId("start");
        return startEvent;
    }

    protected UserTask createUserTask(String id, String name, String assignee)
    {
        UserTask userTask = new UserTask();
        userTask.setName(name);
        userTask.setId(id);
        userTask.setAssignee(assignee);
        return userTask;
    }

    protected SequenceFlow createSequenceFlow(String from, String to)
    {
        SequenceFlow flow = new SequenceFlow();
        flow.setSourceRef(from);
        flow.setTargetRef(to);
        return flow;
    }

    protected EndEvent createEndEvent()
    {
        EndEvent endEvent = new EndEvent();
        endEvent.setId("end");
        return endEvent;
    }

    @Test
    public void documentApproval_findOutcomeValues() throws Exception
    {
        List<Task> reviews = ts.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).list();

        log.debug("Found " + reviews.size() + " review tasks.");

        assertEquals(reviewers.size(), reviews.size());

        // let's look for the possible outcomes for the first task
        BpmnModel model = repo.getBpmnModel(reviews.get(0).getProcessDefinitionId());
        assertNotNull(model);

        List<Process> processes = model.getProcesses();
        assertEquals(1, processes.size());

        Process p = processes.get(0);

        FlowElement taskFlowElement = p.getFlowElement(reviews.get(0).getTaskDefinitionKey());
        assertNotNull(taskFlowElement);
        log.debug("task flow type: " + taskFlowElement.getClass().getName());

        UserTask ut = (UserTask) taskFlowElement;

        List<FormProperty> formProperties = ut.getFormProperties();

        for (FormProperty fp : formProperties)
        {
            log.debug("fp name: " + fp.getName() + "; id: " + fp.getId());
            for (FormValue fv : fp.getFormValues())
            {
                log.debug(fv.getId() + " = " + fv.getName());
            }
        }
    }

    private ProcessInstance createWorkflowProcess(Map<String, Object> processVariables)
    {
        // start a process
        ProcessInstance pi = rt.startProcessInstanceByKey(processId, processVariables);
        return pi;
    }

}
