package com.armedia.acm.plugins.task.service;

import com.armedia.acm.plugins.task.listener.BuckslipTaskCompletedListener;
import com.armedia.acm.plugins.task.model.TaskConstants;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.easymock.EasyMockSupport;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.*;

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
                .addClasspathResource("activiti/ArkCase Buckslip Process v4.bpmn20.xml")
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
    public void basicPath_noApproverChanges_nonConcurContinuesApprovals() throws Exception
    {
        basicPath(false, "NON_CONCUR");
    }

    @Test
    public void basicPath_noApproverChanges_nonConcurEndsApprovals() throws Exception
    {
        basicPath(true, "NON_CONCUR");
    }

    @Test
    public void basicPath_noApproverChanges_withdraw() throws Exception
    {
        basicPath(true, "WITHDRAW");
    }

    public void basicPath(boolean nonConcurEndsApprovals, String lastTaskOutcome)
    {
        expect(userDaoMock.findByUserId("jerry")).andReturn(new AcmUser());
        expect(userDaoMock.findByUserId("bob")).andReturn(new AcmUser());
        if (!"WITHDRAW".equals(lastTaskOutcome)
                && !(nonConcurEndsApprovals && "NON_CONCUR".equals(lastTaskOutcome)))
        {
            expect(userDaoMock.findByUserId("phil")).andReturn(new AcmUser());
        }

        replayAll();

        final String completedTasksKey = "pastTasks";
        Long objectId = 500L;
        String objectType = "rockBand";
        String documentType = "Concert Contract";
        JSONArray futureTasks = new JSONArray();
        makeTask(futureTasks, "jerry", "jerry task", "Grateful Dead", 1);

        makeTask(futureTasks, "bob", "bob task", "Furthur", 2);

        makeTask(futureTasks, "phil", "phil task", "phil lesh and friends", 3);

        String strFutureTasks = futureTasks.toString();

        log.debug("JSON future tasks: {}", strFutureTasks);

        Map<String, Object> processVariables = new HashMap<>();
        processVariables.put(TaskConstants.VARIABLE_NAME_OBJECT_ID, objectId);
        processVariables.put(TaskConstants.VARIABLE_NAME_OBJECT_TYPE, objectType);
        processVariables.put("documentType", documentType);
        processVariables.put(TaskConstants.VARIABLE_NAME_BUCKSLIP_FUTURE_TASKS, strFutureTasks);

        // nonconcur is false by default
        if (nonConcurEndsApprovals)
        {
            processVariables.put(TaskConstants.VARIABLE_NAME_NON_CONCUR_ENDS_APPROVALS, nonConcurEndsApprovals);
        }

        ProcessInstance pi = rt.startProcessInstanceByKey("ArkCaseBuckslipProcess", processVariables);

        signalActivity(pi, TaskConstants.INITIATE_TASK_NAME);

        List<Task> approvals = getTasks(pi);

        assertEquals(1, approvals.size());
        Task task = approvals.get(0);
        assertEquals("jerry", task.getAssignee());
        assertEquals("jerry task", task.getName());
        String approvalsSoFar = (String) task.getProcessVariables().get(completedTasksKey);
        assertEquals("[]", approvalsSoFar);

        completeTask(task, "CONCUR");

        approvals = getTasks(pi);

        assertEquals(1, approvals.size());
        task = approvals.get(0);
        assertEquals("bob", task.getAssignee());
        assertEquals("bob task", task.getName());
        approvalsSoFar = (String) task.getProcessVariables().get(completedTasksKey);

        log.debug("Approvers in bob's task: {}", approvalsSoFar);

        completeTask(task, "CONCUR");

        approvals = getTasks(pi);

        assertEquals(1, approvals.size());
        task = approvals.get(0);
        assertEquals("phil", task.getAssignee());
        assertEquals("phil task", task.getName());
        approvalsSoFar = (String) task.getProcessVariables().get(completedTasksKey);

        log.debug("Approvers in phil's task: {}", approvalsSoFar);

        if ("WITHDRAW".equals(lastTaskOutcome))
        {
            rt.messageEventReceived("Withdraw Message", task.getExecutionId());
        }
        else
        {
            completeTask(task, lastTaskOutcome);
        }
        int expectedCurrentProcessInstances = nonConcurEndsApprovals ? 1 : 0;

        List<HistoricProcessInstance> hpiList =
                hs.createHistoricProcessInstanceQuery().processInstanceId(pi.getId()).includeProcessVariables().list();

        List<ProcessInstance> pis = rt.createProcessInstanceQuery().processInstanceId(pi.getId()).includeProcessVariables().list();

        assertEquals(expectedCurrentProcessInstances, pis.size());
        assertEquals(1, hpiList.size());

        if (nonConcurEndsApprovals || "WITHDRAW".equals(lastTaskOutcome))
        {
            // the historic process instance should still be active
            assertNull(hpiList.get(0).getEndTime());
            // the receive task should be active now
            Execution initiateTask = getReceiveTaskId(pi, TaskConstants.INITIATE_TASK_NAME);
            assertNotNull(initiateTask);

            // future approvers should be restored to the original value
            String afterEndApprovalsFutureTasks = (String) pis.get(0).getProcessVariables().get("futureTasks");
            assertEquals(futureTasks.toString(), afterEndApprovalsFutureTasks);
        }
        else
        {
            assertNotNull(hpiList.get(0).getEndTime());
        }

        verifyAll();


    }

    private void completeTask(Task task, String outcome)
    {
        ts.setVariable(task.getId(), "buckslipOutcome", outcome);
        ts.complete(task.getId());
    }

    @Test
    public void removeAnApprover() throws Exception
    {
        expect(userDaoMock.findByUserId("jerry")).andReturn(new AcmUser());
        expect(userDaoMock.findByUserId("bob")).andReturn(new AcmUser());
        expect(userDaoMock.findByUserId("phil")).andReturn(new AcmUser());

        replayAll();

        final String pastTasksKey = "pastTasks";
        Long objectId = 500L;
        String objectType = "rockBand";
        String documentType = "Concert Contract";

        JSONArray futureTasks = new JSONArray();
        makeTask(futureTasks, "jerry", "jerry task", "Grateful Dead", 1);

        makeTask(futureTasks, "bob", "bob task", "Furthur", 2);

        makeTask(futureTasks, "phil", "phil task", "phil lesh and friends", 3);

        makeTask(futureTasks, "bill", "bill task", "the other ones", 4);

        String strFutureTasks = futureTasks.toString();

        Map<String, Object> processVariables = new HashMap<>();
        processVariables.put(TaskConstants.VARIABLE_NAME_OBJECT_ID, objectId);
        processVariables.put(TaskConstants.VARIABLE_NAME_OBJECT_TYPE, objectType);
        processVariables.put("documentType", documentType);
        processVariables.put(TaskConstants.VARIABLE_NAME_BUCKSLIP_FUTURE_TASKS, strFutureTasks);
        processVariables.put(TaskConstants.VARIABLE_NAME_NON_CONCUR_ENDS_APPROVALS, Boolean.FALSE);

        ProcessInstance pi = rt.startProcessInstanceByKey("ArkCaseBuckslipProcess", processVariables);
        signalActivity(pi, TaskConstants.INITIATE_TASK_NAME);

        List<Task> approvals = getTasks(pi);

        assertEquals(1, approvals.size());
        Task task = approvals.get(0);
        assertEquals("jerry", task.getAssignee());
        assertEquals("jerry task", task.getName());
        String approvalsSoFar = (String) task.getProcessVariables().get(pastTasksKey);
        assertEquals("[]", approvalsSoFar);

        completeTask(task, "CONCUR");

        approvals = getTasks(pi);

        assertEquals(1, approvals.size());
        task = approvals.get(0);
        assertEquals("bob", task.getAssignee());
        assertEquals("bob task", task.getName());
        approvalsSoFar = (String) task.getProcessVariables().get(pastTasksKey);

        log.debug("Approvers in bob's task: {}", approvalsSoFar);

        completeTask(task, "CONCUR");

        approvals = getTasks(pi);

        assertEquals(1, approvals.size());
        task = approvals.get(0);
        assertEquals("phil", task.getAssignee());
        assertEquals("phil task", task.getName());
        approvalsSoFar = (String) task.getProcessVariables().get(pastTasksKey);
        log.debug("Approvers in phil's task: {}", approvalsSoFar);

        // here we will set the future approver list to an empty list, so the process should stop now, intead of going on to bill.
        // we should have one more approver from the original list, but we will remove it, and the proces should end.
        strFutureTasks = "[]";
        ts.setVariable(task.getId(), "futureTasks", strFutureTasks);

        completeTask(task, "CONCUR");

        List<HistoricProcessInstance> hpiList =
                hs.createHistoricProcessInstanceQuery().processInstanceId(pi.getId()).includeProcessVariables().list();
        assertEquals(1, hpiList.size());

        // should not be a current process any more
        List<ProcessInstance> pis = rt.createProcessInstanceQuery().processInstanceId(pi.getId()).list();
        assertEquals(0, pis.size());

        verifyAll();
    }

    protected void makeTask(JSONArray futureTasks, String approverId, String taskName, String groupName, int taskDuration)
    {
        JSONObject task = new JSONObject();
        task.put("approverId", approverId);
        task.put("taskName", taskName);
        task.put("groupName", groupName);
        task.put("maxTaskDurationInDays", taskDuration);
        futureTasks.put(task);
    }


    @Test
    public void addAnApprover() throws Exception
    {

        expect(userDaoMock.findByUserId("jerry")).andReturn(new AcmUser());
        expect(userDaoMock.findByUserId("bob")).andReturn(new AcmUser());
        expect(userDaoMock.findByUserId("phil")).andReturn(new AcmUser());
        expect(userDaoMock.findByUserId("bill")).andReturn(new AcmUser());

        replayAll();

        final String pastTasksKey = "pastTasks";
        Long objectId = 500L;
        String objectType = "rockBand";
        String documentType = "Concert Contract";

        JSONArray futureTasks = new JSONArray();
        makeTask(futureTasks, "jerry", "jerry task", "Grateful Dead", 1);

        makeTask(futureTasks, "bob", "bob task", "Furthur", 2);

        String strFutureTasks = futureTasks.toString();

        Map<String, Object> processVariables = new HashMap<>();
        processVariables.put(TaskConstants.VARIABLE_NAME_OBJECT_ID, objectId);
        processVariables.put(TaskConstants.VARIABLE_NAME_OBJECT_TYPE, objectType);
        processVariables.put("documentType", documentType);
        processVariables.put(TaskConstants.VARIABLE_NAME_BUCKSLIP_FUTURE_TASKS, strFutureTasks);
        processVariables.put(TaskConstants.VARIABLE_NAME_NON_CONCUR_ENDS_APPROVALS, Boolean.FALSE);

        ProcessInstance pi = rt.startProcessInstanceByKey("ArkCaseBuckslipProcess", processVariables);

        signalActivity(pi, TaskConstants.INITIATE_TASK_NAME);

        List<Task> approvals = getTasks(pi);

        assertEquals(1, approvals.size());
        Task task = approvals.get(0);
        assertEquals("jerry", task.getAssignee());
        assertEquals("jerry task", task.getName());
        String approvalsSoFar = (String) task.getProcessVariables().get(pastTasksKey);
        assertEquals("[]", approvalsSoFar);

        completeTask(task, "CONCUR");

        approvals = getTasks(pi);

        assertEquals(1, approvals.size());
        task = approvals.get(0);
        assertEquals("bob", task.getAssignee());
        assertEquals("bob task", task.getName());
        approvalsSoFar = (String) task.getProcessVariables().get(pastTasksKey);

        log.debug("Approvers in bob's task: {}", approvalsSoFar);

        JSONArray newFutureTasks = new JSONArray();
        makeTask(newFutureTasks, "phil", "phil task", "phil lesh and friends", 3);

        makeTask(newFutureTasks, "bill", "bill task", "The Other Ones", 4);

        // here is where we add tasks that were not there when we started the process.
        ts.setVariable(task.getId(), "futureTasks", newFutureTasks.toString());

        completeTask(task, "CONCUR");

        approvals = getTasks(pi);

        assertEquals(1, approvals.size());
        task = approvals.get(0);
        assertEquals("phil", task.getAssignee());
        assertEquals("phil task", task.getName());
        approvalsSoFar = (String) task.getProcessVariables().get(pastTasksKey);
        log.debug("Approvers in phil's task: {}", approvalsSoFar);

        completeTask(task, "CONCUR");

        approvals = getTasks(pi);

        assertEquals(1, approvals.size());
        task = approvals.get(0);
        assertEquals("bill", task.getAssignee());
        assertEquals("bill task", task.getName());
        approvalsSoFar = (String) task.getProcessVariables().get(pastTasksKey);
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

        verifyAll();
    }

    private void signalActivity(ProcessInstance pi, String activityId)
    {
        Execution receiveTask = getReceiveTaskId(pi, activityId);
        assertNotNull(receiveTask);
        rt.signal(receiveTask.getId());
    }

    private Execution getReceiveTaskId(ProcessInstance pi, String receiveTaskId)
    {
        return rt.createExecutionQuery().processInstanceId(pi.getProcessInstanceId()).
                activityId(receiveTaskId).singleResult();
    }


    private List<Task> getTasks(ProcessInstance pi)
    {
        return ts.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).includeProcessVariables().list();
    }
}
