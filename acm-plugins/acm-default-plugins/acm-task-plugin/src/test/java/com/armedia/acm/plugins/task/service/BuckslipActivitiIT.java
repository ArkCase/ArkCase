package com.armedia.acm.plugins.task.service;

import com.armedia.acm.plugins.task.listener.BuckslipTaskCompletedListener;
import com.armedia.acm.services.users.dao.ldap.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.EasyMockSupport;
import org.easymock.IAnswer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/spring/spring-library-task-activiti-test.xml"})
public class BuckslipActivitiIT extends EasyMockSupport
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

    @Autowired
    private BuckslipTaskCompletedListener buckslipTaskCompletedListener;

    private transient final Logger log = LoggerFactory.getLogger(getClass());
    private UserDao userDaoMock;

    @Before
    public void setUp() throws Exception
    {
        // deploy
        repo.createDeployment()
                .addClasspathResource("activiti/ArkCase Buckslip Process.bpmn20.xml")
                .deploy();
        userDaoMock = createMock(UserDao.class);
        buckslipTaskCompletedListener.setUserDao(userDaoMock);
    }

    @After
    public void shutDown() throws Exception
    {
        pe.close();
    }

    @Test
    public void basicPath_noApproverChanges() throws Exception
    {
        Capture<String> userIdCapture = EasyMock.newCapture();
        EasyMock.expect(userDaoMock.findByUserId(EasyMock.capture(userIdCapture))).andAnswer(() ->
        {
            AcmUser user = new AcmUser();
            user.setFullName(userIdCapture.getValue()+" lastName");
            return user;
        }).anyTimes();

        replayAll();

        final String completedApprovalsKey = "pastApprovers";
        Long objectId = 500L;
        String objectType = "rockBand";
        String documentType = "Concert Contract";
        List<String> futureApprovers = Arrays.asList("jerry", "bob", "phil");

        Map<String, Object> processVariables = new HashMap<>();
        processVariables.put("OBJECT_ID", objectId);
        processVariables.put("OBJECT_TYPE", objectType);
        processVariables.put("documentType", documentType);
        // the process should work with either "approvers" or "futureApprovers"
        processVariables.put("approvers", futureApprovers);

        ProcessInstance pi = rt.startProcessInstanceByKey("ArkCaseBuckslipProcess", processVariables);

        List<Task> approvals = getTasks(pi);

        assertEquals(1, approvals.size());
        Task task = approvals.get(0);
        assertEquals("jerry", task.getAssignee());
        String approvalsSoFar = (String) task.getProcessVariables().get(completedApprovalsKey);
        assertEquals("[]", approvalsSoFar);

        completeTask(task, "CONCUR");

        approvals = getTasks(pi);

        assertEquals(1, approvals.size());
        task = approvals.get(0);
        assertEquals("bob", task.getAssignee());
        approvalsSoFar = (String) task.getProcessVariables().get(completedApprovalsKey);

        log.debug("Approvers in bob's task: {}", approvalsSoFar);

        completeTask(task, "CONCUR");

        approvals = getTasks(pi);

        assertEquals(1, approvals.size());
        task = approvals.get(0);
        assertEquals("phil", task.getAssignee());
        approvalsSoFar = (String) task.getProcessVariables().get(completedApprovalsKey);

        log.debug("Approvers in phil's task: {}", approvalsSoFar);

        completeTask(task, "CONCUR");

        List<HistoricProcessInstance> hpiList =
                hs.createHistoricProcessInstanceQuery().processInstanceId(pi.getId()).includeProcessVariables().list();
        assertEquals(1, hpiList.size());

        // should not be a current process any more
        List<ProcessInstance> pis = rt.createProcessInstanceQuery().processInstanceId(pi.getId()).list();
        assertEquals(0, pis.size());


    }

    private void completeTask(Task task, String outcome)
    {
        ts.setVariable(task.getId(), "buckslipOutcome", outcome);
        ts.complete(task.getId());
    }

    @Test
    public void removeAnApprover() throws Exception
    {
        Capture<String> userIdCapture = EasyMock.newCapture();
        EasyMock.expect(userDaoMock.findByUserId(EasyMock.capture(userIdCapture))).andAnswer(() ->
        {
            AcmUser user = new AcmUser();
            user.setFullName(userIdCapture.getValue()+" lastName");
            return user;
        }).anyTimes();

        replayAll();

        final String completedApprovalsKey = "pastApprovers";
        Long objectId = 500L;
        String objectType = "rockBand";
        String documentType = "Concert Contract";
        List<String> futureApprovers = Arrays.asList("jerry", "bob", "phil", "bill");

        Map<String, Object> processVariables = new HashMap<>();
        processVariables.put("OBJECT_ID", objectId);
        processVariables.put("OBJECT_TYPE", objectType);
        processVariables.put("documentType", documentType);
        // the process should work with either "approvers" or "futureApprovers"
        processVariables.put("futureApprovers", futureApprovers);

        ProcessInstance pi = rt.startProcessInstanceByKey("ArkCaseBuckslipProcess", processVariables);

        List<Task> approvals = getTasks(pi);

        assertEquals(1, approvals.size());
        Task task = approvals.get(0);
        assertEquals("jerry", task.getAssignee());
        String approvalsSoFar = (String) task.getProcessVariables().get(completedApprovalsKey);
        assertEquals("[]", approvalsSoFar);

        completeTask(task, "CONCUR");

        approvals = getTasks(pi);

        assertEquals(1, approvals.size());
        task = approvals.get(0);
        assertEquals("bob", task.getAssignee());
        approvalsSoFar = (String) task.getProcessVariables().get(completedApprovalsKey);

        log.debug("Approvers in bob's task: {}", approvalsSoFar);

        completeTask(task, "CONCUR");


        approvals = getTasks(pi);

        assertEquals(1, approvals.size());
        task = approvals.get(0);
        assertEquals("phil", task.getAssignee());
        approvalsSoFar = (String) task.getProcessVariables().get(completedApprovalsKey);
        log.debug("Approvers in phil's task: {}", approvalsSoFar);

        // here we will set the future approver list to an empty list, so the process should stop now, intead of going on to bill.
        // we should have one more approver from the original list, but we will remove it, and the proces should end.
        futureApprovers = new ArrayList<>();
        ts.setVariable(task.getId(), "futureApprovers", futureApprovers);

        completeTask(task, "CONCUR");

        List<HistoricProcessInstance> hpiList =
                hs.createHistoricProcessInstanceQuery().processInstanceId(pi.getId()).includeProcessVariables().list();
        assertEquals(1, hpiList.size());

        // should not be a current process any more
        List<ProcessInstance> pis = rt.createProcessInstanceQuery().processInstanceId(pi.getId()).list();
        assertEquals(0, pis.size());
    }


    @Test
    public void addAnApprover() throws Exception
    {
        Capture<String> userIdCapture = EasyMock.newCapture();
        EasyMock.expect(userDaoMock.findByUserId(EasyMock.capture(userIdCapture))).andAnswer(() ->
        {
            AcmUser user = new AcmUser();
            user.setFullName(userIdCapture.getValue()+" lastName");
            return user;
        }).anyTimes();

        replayAll();

        final String completedApprovalsKey = "pastApprovers";
        Long objectId = 500L;
        String objectType = "rockBand";
        String documentType = "Concert Contract";
        List<String> futureApprovers = Arrays.asList("jerry", "bob");

        Map<String, Object> processVariables = new HashMap<>();
        processVariables.put("OBJECT_ID", objectId);
        processVariables.put("OBJECT_TYPE", objectType);
        processVariables.put("documentType", documentType);
        // the process should work with either "approvers" or "futureApprovers"
        processVariables.put("futureApprovers", futureApprovers);

        ProcessInstance pi = rt.startProcessInstanceByKey("ArkCaseBuckslipProcess", processVariables);

        List<Task> approvals = getTasks(pi);

        assertEquals(1, approvals.size());
        Task task = approvals.get(0);
        assertEquals("jerry", task.getAssignee());
        String approvalsSoFar = (String) task.getProcessVariables().get(completedApprovalsKey);
        assertEquals("[]", approvalsSoFar);

        completeTask(task, "CONCUR");

        approvals = getTasks(pi);

        assertEquals(1, approvals.size());
        task = approvals.get(0);
        assertEquals("bob", task.getAssignee());
        approvalsSoFar = (String) task.getProcessVariables().get(completedApprovalsKey);

        log.debug("Approvers in bob's task: {}", approvalsSoFar);
        futureApprovers = (List<String>) task.getProcessVariables().get("futureApprovers");

        // here is where we add approvers that were not there when we started the process.
        futureApprovers.add("phil");
        futureApprovers.add("bill");
        ts.setVariable(task.getId(), "futureApprovers", futureApprovers);

        completeTask(task, "CONCUR");

        approvals = getTasks(pi);

        assertEquals(1, approvals.size());
        task = approvals.get(0);
        assertEquals("phil", task.getAssignee());
        approvalsSoFar = (String) task.getProcessVariables().get(completedApprovalsKey);
        log.debug("Approvers in phil's task: {}", approvalsSoFar);

        completeTask(task, "CONCUR");

        approvals = getTasks(pi);

        assertEquals(1, approvals.size());
        task = approvals.get(0);
        assertEquals("bill", task.getAssignee());
        approvalsSoFar = (String) task.getProcessVariables().get(completedApprovalsKey);
        log.debug("Approvers in bill's task: {}", approvalsSoFar);

        // before completing the last task, should still be a current process
        List<ProcessInstance> pis = rt.createProcessInstanceQuery().processInstanceId(pi.getId()).list();
        assertEquals(1, pis.size());

        completeTask(task, "CONCUR");

        List<HistoricProcessInstance> hpiList =
                hs.createHistoricProcessInstanceQuery().processInstanceId(pi.getId()).includeProcessVariables().list();
        assertEquals(1, hpiList.size());

        // should not be a current process any more
        pis = rt.createProcessInstanceQuery().processInstanceId(pi.getId()).list();
        assertEquals(0, pis.size());
    }


    private List<Task> getTasks(ProcessInstance pi)
    {
        return ts.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).includeProcessVariables().list();
    }
}
