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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.data.BuckslipFutureTask;
import com.armedia.acm.objectonverter.ObjectConverter;
import com.armedia.acm.plugins.task.model.AcmTask;
import com.armedia.acm.plugins.task.model.BuckslipProcess;
import com.armedia.acm.plugins.task.model.TaskConstants;
import com.armedia.acm.web.api.MDCConstants;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "/spring/spring-library-acm-encryption.xml",
        "/spring/spring-library-activiti-configuration.xml",
        "/spring/spring-library-authentication-token.xml",
        "/spring/spring-library-context-holder.xml",
        "/spring/spring-library-data-access-control.xml",
        "/spring/spring-library-data-source.xml",
        "/spring/spring-library-ecm-file.xml",
        "/spring/spring-library-ecm-tika.xml",
        "/spring/spring-library-folder-watcher.xml",
        "/spring/spring-library-ms-outlook-integration.xml",
        "/spring/spring-library-note.xml",
        "/spring/spring-library-notification.xml",
        "/spring/spring-library-object-association-plugin.xml",
        "/spring/spring-library-object-history.xml",
        "/spring/spring-library-particpants.xml",
        "/spring/spring-library-property-file-manager.xml",
        "/spring/spring-library-search.xml",
        "/spring/spring-library-service-data.xml",
        "/spring/spring-library-task.xml",
        "/spring/spring-library-task-buckslip-test.xml",
        "/spring/spring-library-user-login.xml",
        "/spring/spring-library-plugin-manager.xml",
        "/spring/spring-library-audit-service.xml",
        "/spring/spring-library-user-service.xml",
        "/spring/spring-library-drools-rule-monitor.xml",
        "/spring/spring-library-object-lock.xml",
        "/spring/spring-library-email.xml",
        "/spring/spring-library-email-smtp.xml",
        "/spring/spring-library-calendar-config-service.xml",
        "/spring/spring-library-calendar-integration-exchange-service.xml",
        "/spring/spring-library-object-converter.xml",
        "/spring/spring-library-ecm-file-lock.xml",
        "/spring/spring-library-core-api.xml",
        "/spring/spring-library-configuration.xml",
        "/spring/spring-library-task-plugin-test.xml",
        "/spring/spring-library-convert-folder-service.xml",
        "/spring/spring-test-quartz-scheduler.xml",
        "/spring/spring-library-activemq.xml",
        "/spring/spring-library-websockets.xml",
        "/spring/spring-library-camel-context.xml",
        "/spring/spring-library-ecm-file-sync.xml",
        "/spring/spring-library-object-title.xml",
        "/spring/spring-library-labels-service.xml",
        "/spring/spring-library-business-process.xml"
})
@TransactionConfiguration(defaultRollback = true)
public class BuckslipArkcaseIT
{
    static
    {
        String userHomePath = System.getProperty("user.home");
        System.setProperty("acm.configurationserver.propertyfile", userHomePath + "/.arkcase/acm/conf.yml");
        System.setProperty("configuration.server.url", "http://localhost:9999");
    }

    final String processName = "ArkCaseBuckslipProcess";
    private transient final Logger LOG = LogManager.getLogger(getClass());
    @Autowired
    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;
    @Autowired
    private RuntimeService rt;
    @Autowired
    private RepositoryService repo;
    @Autowired
    private TaskService ts;
    @Autowired
    private AcmTaskService acmTaskService;
    @Autowired
    private TaskDao taskDao;
    private BuckslipProcess buckslipProcess;

    @Before
    public void setUp() throws Exception
    {
        MDC.put(MDCConstants.EVENT_MDC_REQUEST_ALFRESCO_USER_ID_KEY, "admin");
        MDC.put(MDCConstants.EVENT_MDC_REQUEST_ID_KEY, UUID.randomUUID().toString());

        auditPropertyEntityAdapter.setUserId("TEST");

        buckslipProcess = startBuckslipProcess();
    }

    private BuckslipProcess startBuckslipProcess() throws Exception
    {

        Long objectId = 500L;
        String objectType = UUID.randomUUID().toString();

        Long parentObjectId = 501L;
        String parentObjectType = UUID.randomUUID().toString();

        String documentType = "Concert Contract";

        List<BuckslipFutureTask> futureTasks = new ArrayList<>(3);

        BuckslipFutureTask task1 = new BuckslipFutureTask();
        task1.setApproverId("***REMOVED***");
        task1.setApproverFullName("Ann Administrator");
        task1.setTaskName("ann-acm task");
        task1.setGroupName("ann group");
        task1.setDetails("ann details");
        task1.setAddedBy("bthomas@armedia.com");
        task1.setAddedByFullName("Bill Thomas");
        futureTasks.add(task1);

        BuckslipFutureTask task2 = new BuckslipFutureTask();
        task2.setApproverId("samuel-acm@armedia.com");
        task2.setApproverFullName("Samuel Supervisor");
        task2.setTaskName("samuel-acm task");
        task2.setGroupName("samuel group");
        task2.setDetails("samuel details");
        task2.setAddedBy("bthomas@armedia.com");
        task2.setAddedByFullName("Bill Thomas");
        futureTasks.add(task2);

        BuckslipFutureTask task3 = new BuckslipFutureTask();
        task3.setApproverId("***REMOVED***");
        task3.setApproverFullName("Ian Investigator");
        task3.setTaskName("ian-acm task");
        task3.setGroupName("ian group");
        task3.setDetails("ian details");
        task3.setAddedBy("bthomas@armedia.com");
        task3.setAddedByFullName("Bill Thomas");
        futureTasks.add(task3);

        Map<String, Object> processVariables = new HashMap<>();
        processVariables.put(TaskConstants.VARIABLE_NAME_OBJECT_ID, objectId);
        processVariables.put(TaskConstants.VARIABLE_NAME_OBJECT_TYPE, objectType);
        processVariables.put(TaskConstants.VARIABLE_NAME_PARENT_OBJECT_ID, parentObjectId);
        processVariables.put(TaskConstants.VARIABLE_NAME_PARENT_OBJECT_TYPE, parentObjectType);
        processVariables.put("documentType", documentType);
        processVariables.put("taskDueDateExpression", "P3D");
        processVariables.put(TaskConstants.VARIABLE_NAME_NON_CONCUR_ENDS_APPROVALS, Boolean.FALSE);

        ProcessInstance pi = rt.startProcessInstanceByKey(processName, processVariables);

        // we might have to wait a bit while Activiti does its thing to start the process
        Thread.sleep(500L);

        List<BuckslipProcess> processes = acmTaskService.getBuckslipProcessesForObject(objectType, objectId);
        assertEquals(1, processes.size());

        List<BuckslipProcess> processesForParent = acmTaskService.getBuckslipProcessesForChildren(parentObjectType, parentObjectId);
        assertEquals(1, processesForParent.size());

        processesForParent.get(0).setFutureTasks(futureTasks);
        BuckslipProcess updated = acmTaskService.updateBuckslipProcess(processesForParent.get(0));
        assertNotNull(updated);
        assertEquals(pi.getProcessInstanceId(), updated.getBusinessProcessId());

        return updated;
    }

    private AcmTask findAcmTaskForProcess()
    {
        List<Task> tasks = ts.createTaskQuery().processInstanceId(buckslipProcess.getBusinessProcessId()).list();

        if (tasks == null || tasks.isEmpty())
        {
            return null;
        }

        String taskId = tasks.get(0).getId();
        AcmTask acmTask = acmTaskService.retrieveTask(Long.valueOf(taskId));
        assertTrue(acmTask.isBuckslipTask());
        return acmTask;
    }

    @Test
    public void buckslipNoApproverChanges() throws Exception
    {
        assertNotNull(buckslipProcess.getBusinessProcessId());

        // first we have to initiate the process by signaling the initiate task
        acmTaskService.signalTask(buckslipProcess.getBusinessProcessId(), TaskConstants.INITIATE_TASK_NAME);

        // should have a task for ann-acm... complete with 'CONCUR' outcome
        AcmTask acmTask = findAcmTaskForProcess();
        assertNotNull(acmTask);
        assertEquals("***REMOVED***", acmTask.getAssignee());
        assertEquals(2, acmTask.getBuckslipFutureTasks().size());
        assertEquals("samuel-acm@armedia.com", acmTask.getBuckslipFutureTasks().get(0).getApproverId());
        assertEquals("***REMOVED***", acmTask.getBuckslipFutureTasks().get(1).getApproverId());
        assertEquals("[]", acmTask.getBuckslipPastApprovers());
        assertEquals("ann group", acmTask.getCandidateGroups().get(0));
        assertEquals("ann details", acmTask.getDetails());
        // String owningGroup = ParticipantUtils.getOwningGroupIdFromParticipants(acmTask.getParticipants());
        // assertNotNull(owningGroup);
        // assertEquals("ann group", owningGroup);
        Principal assignee = new UsernamePasswordAuthenticationToken("***REMOVED***", "ann-acm");
        taskDao.completeTask(assignee, acmTask.getTaskId(), "buckslipOutcome", "CONCUR");

        // should have a task for samuel-acm... complete with 'CONCUR' outcome
        acmTask = findAcmTaskForProcess();
        assertNotNull(acmTask);
        assertEquals("samuel-acm@armedia.com", acmTask.getAssignee());
        assertEquals(1, acmTask.getBuckslipFutureTasks().size());
        assertEquals("***REMOVED***", acmTask.getBuckslipFutureTasks().get(0).getApproverId());
        assertTrue(acmTask.getBuckslipPastApprovers().contains("***REMOVED***"));

        JSONArray pastTasks = new JSONArray(acmTask.getBuckslipPastApprovers());
        assertEquals(1, pastTasks.length());
        JSONObject firstPastTask = pastTasks.getJSONObject(0);
        assertEquals("ann details", firstPastTask.getString("details"));

        assignee = new UsernamePasswordAuthenticationToken("samuel-acm@armedia.com", "samuel-acm");
        taskDao.completeTask(assignee, acmTask.getTaskId(), "buckslipOutcome", "CONCUR");

        // should have a task for ian-acm... complete with 'NON_CONCUR' outcome
        acmTask = findAcmTaskForProcess();
        assertNotNull(acmTask);
        assertEquals("***REMOVED***", acmTask.getAssignee());
        assertEquals(0, acmTask.getBuckslipFutureTasks().size());
        assertTrue(acmTask.getBuckslipPastApprovers().contains("***REMOVED***"));
        assertTrue(acmTask.getBuckslipPastApprovers().contains("samuel-acm@armedia.com"));
        assignee = new UsernamePasswordAuthenticationToken("***REMOVED***", "ian-acm");

        String jsonTask = ObjectConverter.createJSONMarshallerForTests().marshal(acmTask);
        LOG.debug("json task: {}", jsonTask);

        taskDao.completeTask(assignee, acmTask.getTaskId(), "buckslipOutcome", "NON_CONCUR");

        // no more tasks
        acmTask = findAcmTaskForProcess();
        assertNull(acmTask);
    }

    /**
     * Same as buckslipNoApproverChanges except we add a new approver while samuel-acm is the current approver
     *
     * @throws Exception
     */
    @Test
    public void buckslipAddApprover() throws Exception
    {
        assertNotNull(buckslipProcess.getBusinessProcessId());

        // first we have to initiate the process by signaling the initiate task
        acmTaskService.signalTask(buckslipProcess.getBusinessProcessId(), TaskConstants.INITIATE_TASK_NAME);

        // should have a task for ann-acm... complete with 'CONCUR' outcome

        AcmTask acmTask = findAcmTaskForProcess();
        assertNotNull(acmTask);
        assertEquals("***REMOVED***", acmTask.getAssignee());
        assertEquals(2, acmTask.getBuckslipFutureTasks().size());
        assertEquals("samuel-acm@armedia.com", acmTask.getBuckslipFutureTasks().get(0).getApproverId());
        assertEquals("***REMOVED***", acmTask.getBuckslipFutureTasks().get(1).getApproverId());
        assertEquals("[]", acmTask.getBuckslipPastApprovers());
        Principal assignee = new UsernamePasswordAuthenticationToken("***REMOVED***", "ann-acm");
        taskDao.completeTask(assignee, acmTask.getTaskId(), "buckslipOutcome", "CONCUR");

        // should have a task for samuel-acm... complete with 'CONCUR' outcome
        acmTask = findAcmTaskForProcess();
        assertNotNull(acmTask);
        assertEquals("samuel-acm@armedia.com", acmTask.getAssignee());
        assertEquals(1, acmTask.getBuckslipFutureTasks().size());
        assertEquals("***REMOVED***", acmTask.getBuckslipFutureTasks().get(0).getApproverId());
        assertTrue(acmTask.getBuckslipPastApprovers().contains("ann-acm"));

        // add the approver 'albert-acm'
        BuckslipFutureTask forAlbert = new BuckslipFutureTask();
        forAlbert.setApproverId("albert-acm@armedia.com");
        forAlbert.setTaskName("Albert task");
        acmTask.getBuckslipFutureTasks().add(forAlbert);
        taskDao.save(acmTask);

        assignee = new UsernamePasswordAuthenticationToken("samuel-acm@armedia.com", "samuel-acm");
        taskDao.completeTask(assignee, acmTask.getTaskId(), "buckslipOutcome", "CONCUR");

        // should have a task for ian-acm... complete with 'NON_CONCUR' outcome
        acmTask = findAcmTaskForProcess();
        assertNotNull(acmTask);
        assertEquals("***REMOVED***", acmTask.getAssignee());
        assertEquals(1, acmTask.getBuckslipFutureTasks().size());
        assertEquals("albert-acm@armedia.com", acmTask.getBuckslipFutureTasks().get(0).getApproverId());
        assertTrue(acmTask.getBuckslipPastApprovers().contains("***REMOVED***"));
        assertTrue(acmTask.getBuckslipPastApprovers().contains("samuel-acm@armedia.com"));
        assignee = new UsernamePasswordAuthenticationToken("***REMOVED***", "ian-acm");
        taskDao.completeTask(assignee, acmTask.getTaskId(), "buckslipOutcome", "NON_CONCUR");

        // should have a task for albert-acm... complete with 'NON_CONCUR' outcome
        acmTask = findAcmTaskForProcess();
        assertNotNull(acmTask);
        assertEquals("albert-acm@armedia.com", acmTask.getAssignee());
        assertEquals(0, acmTask.getBuckslipFutureTasks().size());
        assertTrue(acmTask.getBuckslipPastApprovers().contains("***REMOVED***"));
        assertTrue(acmTask.getBuckslipPastApprovers().contains("samuel-acm@armedia.com"));
        assertTrue(acmTask.getBuckslipPastApprovers().contains("***REMOVED***"));
        assignee = new UsernamePasswordAuthenticationToken("albert-acm@armedia.com", "albert-acm");
        taskDao.completeTask(assignee, acmTask.getTaskId(), "buckslipOutcome", "NON_CONCUR");

        // no more tasks
        acmTask = findAcmTaskForProcess();
        assertNull(acmTask);
    }

    /**
     * Same as buckslipNoApproverChanges except we remove ian-acm while ann-acm is the current approver
     *
     * @throws Exception
     */
    @Test
    public void buckslipRemoveApprover() throws Exception
    {

        assertNotNull(buckslipProcess.getBusinessProcessId());

        // first we have to initiate the process by signaling the initiate task
        acmTaskService.signalTask(buckslipProcess.getBusinessProcessId(), TaskConstants.INITIATE_TASK_NAME);

        // should have a task for ann-acm... complete with 'CONCUR' outcome
        AcmTask acmTask = findAcmTaskForProcess();
        assertNotNull(acmTask);
        assertEquals("***REMOVED***", acmTask.getAssignee());
        assertEquals(2, acmTask.getBuckslipFutureTasks().size());
        assertEquals("samuel-acm@armedia.com", acmTask.getBuckslipFutureTasks().get(0).getApproverId());
        assertEquals("***REMOVED***", acmTask.getBuckslipFutureTasks().get(1).getApproverId());
        assertEquals("[]", acmTask.getBuckslipPastApprovers());

        // remove ian-acm
        acmTask.getBuckslipFutureTasks().remove(1);
        taskDao.save(acmTask);

        Principal assignee = new UsernamePasswordAuthenticationToken("***REMOVED***", "ann-acm");
        taskDao.completeTask(assignee, acmTask.getTaskId(), "buckslipOutcome", "CONCUR");

        // should have a task for samuel-acm... complete with 'CONCUR' outcome
        acmTask = findAcmTaskForProcess();
        assertNotNull(acmTask);
        assertEquals("samuel-acm@armedia.com", acmTask.getAssignee());
        assertEquals(0, acmTask.getBuckslipFutureTasks().size());
        assertTrue(acmTask.getBuckslipPastApprovers().contains("***REMOVED***"));
        assignee = new UsernamePasswordAuthenticationToken("samuel-acm@armedia.com", "samuel-acm");
        taskDao.completeTask(assignee, acmTask.getTaskId(), "buckslipOutcome", "CONCUR");

        // no more tasks
        acmTask = findAcmTaskForProcess();
        assertNull(acmTask);
    }

    @Test
    public void withdrawApprovals() throws Exception
    {

        assertNotNull(buckslipProcess.getBusinessProcessId());

        // first we have to initiate the process by signaling the initiate task
        acmTaskService.signalTask(buckslipProcess.getBusinessProcessId(), TaskConstants.INITIATE_TASK_NAME);

        // should have a task for ann-acm... complete with 'CONCUR' outcome
        AcmTask acmTask = findAcmTaskForProcess();
        assertNotNull(acmTask);
        assertEquals("***REMOVED***", acmTask.getAssignee());
        assertEquals(2, acmTask.getBuckslipFutureTasks().size());
        assertEquals("samuel-acm@armedia.com", acmTask.getBuckslipFutureTasks().get(0).getApproverId());
        assertEquals("***REMOVED***", acmTask.getBuckslipFutureTasks().get(1).getApproverId());
        assertEquals("[]", acmTask.getBuckslipPastApprovers());

        Principal assignee = new UsernamePasswordAuthenticationToken("***REMOVED***", "ann-acm");
        taskDao.completeTask(assignee, acmTask.getTaskId(), "buckslipOutcome", "CONCUR");

        // find the next task before we can withdraw
        acmTask = findAcmTaskForProcess();

        // we have a current task so the process should be withdrawable, but not initiatable
        assertFalse(acmTaskService.isInitiatable(buckslipProcess.getBusinessProcessId()));
        assertTrue(acmTaskService.isWithdrawable(buckslipProcess.getBusinessProcessId()));

        // withdraw
        acmTaskService.messageTask(acmTask.getTaskId(), "Withdraw Message");

        // no more tasks
        acmTask = findAcmTaskForProcess();
        assertNull(acmTask);

        // business-process-level information
        assertTrue(acmTaskService.isInitiatable(buckslipProcess.getBusinessProcessId()));
        assertFalse(acmTaskService.isWithdrawable(buckslipProcess.getBusinessProcessId()));
        List<BuckslipFutureTask> buckslipFutureTasks = acmTaskService.getBuckslipFutureTasks(buckslipProcess.getBusinessProcessId());
        assertEquals(3, buckslipFutureTasks.size());
        assertTrue(acmTaskService.getBuckslipPastTasks(buckslipProcess.getBusinessProcessId(), false).contains("ann-acm"));

        // add the approver 'albert-acm'
        BuckslipFutureTask forAlbert = new BuckslipFutureTask();
        forAlbert.setApproverId("albert-acm");
        forAlbert.setTaskName("Albert task");
        buckslipFutureTasks.add(forAlbert);

        // call the service to set the new list of future tasks
        acmTaskService.setBuckslipFutureTasks(buckslipProcess.getBusinessProcessId(), buckslipFutureTasks);

        // now we should have 4... the original 3, plus albert
        buckslipFutureTasks = acmTaskService.getBuckslipFutureTasks(buckslipProcess.getBusinessProcessId());
        assertEquals(4, buckslipFutureTasks.size());
    }
}
