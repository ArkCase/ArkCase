package com.armedia.acm.plugins.complaint;


import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ExecutionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/spring-library-complaint-activiti-test.xml" } )
public class ComplaintWorkflowIT
{
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

    private Logger log = LoggerFactory.getLogger(getClass());

    @Before
    public void setUp() throws Exception
    {

        // deploy
        repo.createDeployment()
                .addClasspathResource("activiti/DefaultComplaintWorkflow_v4.bpmn20.xml")
                .deploy();

    }

    @After
    public void shutDown() throws Exception
    {
        pe.close();
    }

    /**
     * Associate a workflow with a business object by setting a process variable.  Find workflows for that
     * business object and complete tasks associated with it.
     * @throws Exception
     */
    @Test
    public void startAndCompleteTask() throws Exception
    {
        Long complaintId = 12345L;
        Long badId = 54321L;
        String user = "Test User";
        String user2 = "Another User";
        String complaintNumber = "20140530_001";

        List<String> approvers = Arrays.asList(user, user2);
        Map<String, Object> processVariables = new HashMap<>();
        processVariables.put("approvers", approvers);
        processVariables.put("complaintNumber", complaintNumber);

        assertNotNull(rt);

        ProcessInstance found = createWorkflowProcess(complaintId, badId, processVariables);
        assertFalse(found.isEnded());

        List<Task> approvals = ts.createTaskQuery().processInstanceId(found.getProcessInstanceId()).list();

        log.debug("Found " + approvals.size() + " approval tasks.");

        assertEquals(approvers.size(), approvals.size());

        for ( int a = 0; a < approvals.size(); ++a )
        {
            Task task = approvals.get(a);
            ts.complete(task.getId());

            ProcessInstance current = rt.createProcessInstanceQuery().processInstanceId(found.getProcessInstanceId()).singleResult();
            boolean shouldBeComplete = a == approvals.size() - 1;

            // when all approvers have finished the process will be complete and the runtime service query
            // will not find it.
            if ( shouldBeComplete )
            {
                assertNull(current);
            }
            else
            {
                assertFalse(current.isEnded());
            }

        }

    }


    private ProcessInstance createWorkflowProcess(Long complaintId, Long badId, Map<String, Object> processVariables)
    {
        // start a process
        ProcessInstance pi = rt.startProcessInstanceByKey("cmComplaintWorkflow", processVariables);
        rt.setVariable(pi.getId(), "cmComplaintId", complaintId);

        ExecutionQuery eq = rt.createExecutionQuery().variableValueEquals("cmComplaintId", complaintId);
        ExecutionQuery eqNotFound = rt.createExecutionQuery().variableValueEquals("cmComplaintId", badId);

        Execution found = eq.singleResult();
        Object notFound = eqNotFound.singleResult();

        assertNotNull(found);
        assertNull(notFound);

        return pi;
    }

}
