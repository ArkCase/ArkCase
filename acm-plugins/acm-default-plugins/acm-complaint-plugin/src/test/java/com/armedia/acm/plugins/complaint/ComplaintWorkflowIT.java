package com.armedia.acm.plugins.complaint;


import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricProcessInstanceQuery;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.history.HistoricVariableInstanceQuery;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ExecutionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.spring.SpringProcessEngineConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

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
                .addClasspathResource("activiti/DefaultComplaintWorkflow.bpmn20.xml")
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

        assertNotNull(rt);

        Execution found = createWorkflowProcess(complaintId, badId);

        Task approval = ts.createTaskQuery().executionId(found.getId()).singleResult();

        assertNotNull(approval);

        log.debug("Found task id '" + approval.getId() + "'");

        ts.claim(approval.getId(), user);

        log.debug("claimed task");

        Task afterClaim = ts.createTaskQuery().executionId(found.getId()).singleResult();

        assertEquals(user, afterClaim.getAssignee());

        completeWorkflow(afterClaim);

        // runtime service should not find the process anymore since it should be ended
        ExecutionQuery eqEnded = rt.createExecutionQuery().variableValueEquals("cmComplaintId", complaintId);
        Execution ended = eqEnded.singleResult();
        assertNull(ended);

        // historical query should find it
        HistoricProcessInstanceQuery hq = hs.createHistoricProcessInstanceQuery().processInstanceId(found.getProcessInstanceId());
        HistoricProcessInstance hpi = hq.singleResult();
        assertNotNull(hpi);
        assertNotNull(hpi.getEndTime());



    }

    private void completeWorkflow(Task afterClaim)
    {
        ExecutionQuery eqInApproval = rt.createExecutionQuery().activityId("approveComplaint");
        List<Execution> approvals = eqInApproval.list();
        assertEquals(1, approvals.size());

        ts.complete(afterClaim.getId());

        eqInApproval = rt.createExecutionQuery().activityId("approveComplaint");
        approvals = eqInApproval.list();
        assertEquals(0, approvals.size());
    }

    private Execution createWorkflowProcess(Long complaintId, Long badId)
    {
        // start a process
        ProcessInstance pi = rt.startProcessInstanceByKey("cmComplaintWorkflow");
        rt.setVariable(pi.getId(), "cmComplaintId", complaintId);

        ExecutionQuery eq = rt.createExecutionQuery().variableValueEquals("cmComplaintId", complaintId);
        ExecutionQuery eqNotFound = rt.createExecutionQuery().variableValueEquals("cmComplaintId", badId);

        Execution found = eq.singleResult();
        Object notFound = eqNotFound.singleResult();

        assertNotNull(found);
        assertNull(notFound);

        log.debug("Found item of type '" + found.getClass().getName() + "'");
        return found;
    }

}
