package com.armedia.acm.plugins.task.service;

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

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.armedia.acm.plugins.task.listener.BuckslipTaskCompletedListener;
import com.armedia.acm.plugins.task.listener.BuckslipTaskHelper;
import com.armedia.acm.plugins.task.model.BuckslipProcessStateEvent;
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
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/spring-library-task-activiti-test.xml" })
public class BuckslipActivitiIT extends EasyMockSupport
{
    private transient final Logger log = LogManager.getLogger(getClass());
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
    private BuckslipTaskHelper buckslipTaskHelper;
    @Autowired
    private BuckslipTaskCompletedListener buckslipTaskCompletedListener;
    private UserDao userDaoMock;

    private ApplicationEventPublisher mockApplicationEventPublisher;

    @Before
    public void setUp() throws Exception
    {
        // deploy
        repo.createDeployment()
                .addClasspathResource("activiti/ArkCase Buckslip Process v4.bpmn20.xml")
                .deploy();
        userDaoMock = createMock(UserDao.class);
        mockApplicationEventPublisher = createMock(ApplicationEventPublisher.class);
        buckslipTaskCompletedListener.setUserDao(userDaoMock);
        buckslipTaskCompletedListener.setBuckslipTaskHelper(buckslipTaskHelper);
        buckslipTaskCompletedListener.getBuckslipTaskHelper().setApplicationEventPublisher(mockApplicationEventPublisher);
    }

    @After
    public void shutDown() throws Exception
    {
        pe.close();
    }

    @Test
    public void basicPath_noApproverChanges_nonConcurContinuesApprovals() throws Exception
    {
        mockApplicationEventPublisher.publishEvent(anyObject(BuckslipProcessStateEvent.class));
        basicPath(false, "NON_CONCUR");
    }

    @Test
    public void basicPath_noApproverChanges_nonConcurEndsApprovals() throws Exception
    {
        mockApplicationEventPublisher.publishEvent(anyObject(BuckslipProcessStateEvent.class));
        expectLastCall().times(2);
        basicPath(true, "NON_CONCUR");
    }

    @Test
    public void basicPath_noApproverChanges_withdraw() throws Exception
    {
        mockApplicationEventPublisher.publishEvent(anyObject(BuckslipProcessStateEvent.class));
        expectLastCall().times(2);
        basicPath(true, "WITHDRAW");
    }

    public void basicPath(boolean nonConcurEndsApprovals, String lastTaskOutcome)
    {
        AcmUser addedByUser = new AcmUser();
        addedByUser.setFullName("Added By User");

        AcmUser jerry = new AcmUser();
        jerry.setFullName("jerry Full");

        AcmUser bob = new AcmUser();
        bob.setFullName("bob Full");
        expect(userDaoMock.findByUserId("jerry")).andReturn(jerry);
        expect(userDaoMock.findByUserId("bob")).andReturn(bob);
        expect(userDaoMock.findByUserId("addedByUser")).andReturn(addedByUser).atLeastOnce();
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
        processVariables.put("approverFullName", "Bill Graham");

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

        List<HistoricProcessInstance> hpiList = hs.createHistoricProcessInstanceQuery().processInstanceId(pi.getId())
                .includeProcessVariables().list();

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
        mockApplicationEventPublisher.publishEvent(anyObject(BuckslipProcessStateEvent.class));

        expect(userDaoMock.findByUserId("jerry")).andReturn(new AcmUser());
        expect(userDaoMock.findByUserId("bob")).andReturn(new AcmUser());
        expect(userDaoMock.findByUserId("phil")).andReturn(new AcmUser());
        expect(userDaoMock.findByUserId("addedByUser")).andReturn(new AcmUser()).atLeastOnce();

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

        // here we will set the future approver list to an empty list, so the process should stop now, intead of going
        // on to bill.
        // we should have one more approver from the original list, but we will remove it, and the proces should end.
        strFutureTasks = "[]";
        ts.setVariable(task.getId(), "futureTasks", strFutureTasks);

        completeTask(task, "CONCUR");

        List<HistoricProcessInstance> hpiList = hs.createHistoricProcessInstanceQuery().processInstanceId(pi.getId())
                .includeProcessVariables().list();
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
        task.put("details", "Task Details");
        task.put("addedBy", "addedByUser");
        task.put("maxTaskDurationInDays", taskDuration);
        task.put("approverFullName", approverId + " Full");
        task.put("addedByFullName", "Added By User");
        futureTasks.put(task);
    }

    @Test
    public void addAnApprover() throws Exception
    {
        mockApplicationEventPublisher.publishEvent(anyObject(BuckslipProcessStateEvent.class));

        expect(userDaoMock.findByUserId("jerry")).andReturn(new AcmUser());
        expect(userDaoMock.findByUserId("bob")).andReturn(new AcmUser());
        expect(userDaoMock.findByUserId("phil")).andReturn(new AcmUser());
        expect(userDaoMock.findByUserId("bill")).andReturn(new AcmUser());
        expect(userDaoMock.findByUserId("addedByUser")).andReturn(new AcmUser()).atLeastOnce();

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

        List<HistoricProcessInstance> hpiList = hs.createHistoricProcessInstanceQuery().processInstanceId(pi.getId())
                .includeProcessVariables().list();
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
        return rt.createExecutionQuery().processInstanceId(pi.getProcessInstanceId()).activityId(receiveTaskId).singleResult();
    }

    private List<Task> getTasks(ProcessInstance pi)
    {
        return ts.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).includeProcessVariables().list();
    }
}
