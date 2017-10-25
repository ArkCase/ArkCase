package com.armedia.acm.plugins.task.service;

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.objectonverter.ObjectConverter;
import com.armedia.acm.plugins.task.model.AcmTask;
import com.armedia.acm.plugins.task.model.BuckslipFutureTask;
import com.armedia.acm.plugins.task.model.BuckslipProcess;
import com.armedia.acm.plugins.task.model.TaskConstants;
import com.armedia.acm.web.api.MDCConstants;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import static org.junit.Assert.*;

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
        "/spring/spring-library-user-service.xml",
        "/spring/spring-library-drools-rule-monitor.xml",
        "/spring/spring-library-object-lock.xml",
        "/spring/spring-library-email.xml",
        "/spring/spring-library-email-smtp.xml",
        "/spring/spring-library-calendar-config-service.xml",
        "/spring/spring-library-calendar-integration-exchange-service.xml"
})
@TransactionConfiguration(defaultRollback = true)
public class BuckslipArkcaseIT
{
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

    final String processName = "ArkCaseBuckslipProcess";

    private BuckslipProcess buckslipProcess;

    private transient final Logger LOG = LoggerFactory.getLogger(getClass());

    @Before
    public void setUp() throws Exception
    {
        MDC.put(MDCConstants.EVENT_MDC_REQUEST_ALFRESCO_USER_ID_KEY, "admin");
        MDC.put(MDCConstants.EVENT_MDC_REQUEST_ID_KEY, UUID.randomUUID().toString());

        auditPropertyEntityAdapter.setUserId("TEST");

        deployProcessIfNeeded();
        buckslipProcess = startBuckslipProcess();
    }

    private void deployProcessIfNeeded()
    {
        List<Deployment> deployments = repo.createDeploymentQuery().processDefinitionKey(processName).list();
        if (deployments == null || deployments.isEmpty())
        {
            repo.createDeployment()
                    .addClasspathResource("activiti/ArkCase Buckslip Process v4.bpmn20.xml")
                    .deploy();
        }
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
        task1.setApproverId("ann-acm");
        task1.setTaskName("ann-acm task");
        task1.setGroupName("ann group");
        futureTasks.add(task1);

        BuckslipFutureTask task2 = new BuckslipFutureTask();
        task2.setApproverId("samuel-acm");
        task2.setTaskName("samuel-acm task");
        task2.setGroupName("samuel group");
        futureTasks.add(task2);

        BuckslipFutureTask task3 = new BuckslipFutureTask();
        task3.setApproverId("ian-acm");
        task3.setTaskName("ian-acm task");
        task3.setGroupName("ian group");
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
        assertEquals("ann-acm", acmTask.getAssignee());
        assertEquals(2, acmTask.getBuckslipFutureTasks().size());
        assertEquals("samuel-acm", acmTask.getBuckslipFutureTasks().get(0).getApproverId());
        assertEquals("ian-acm", acmTask.getBuckslipFutureTasks().get(1).getApproverId());
        assertEquals("[]", acmTask.getBuckslipPastApprovers());
        assertEquals("ann group", acmTask.getCandidateGroups().get(0));
//        String owningGroup = ParticipantUtils.getOwningGroupIdFromParticipants(acmTask.getParticipants());
//        assertNotNull(owningGroup);
//        assertEquals("ann group", owningGroup);
        Principal assignee = new UsernamePasswordAuthenticationToken("ann-acm", "ann-acm");
        taskDao.completeTask(assignee, acmTask.getTaskId(), "buckslipOutcome", "CONCUR");

        // should have a task for samuel-acm... complete with 'CONCUR' outcome
        acmTask = findAcmTaskForProcess();
        assertNotNull(acmTask);
        assertEquals("samuel-acm", acmTask.getAssignee());
        assertEquals(1, acmTask.getBuckslipFutureTasks().size());
        assertEquals("ian-acm", acmTask.getBuckslipFutureTasks().get(0).getApproverId());
        assertTrue(acmTask.getBuckslipPastApprovers().contains("ann-acm"));
        assignee = new UsernamePasswordAuthenticationToken("samuel-acm", "samuel-acm");
        taskDao.completeTask(assignee, acmTask.getTaskId(), "buckslipOutcome", "CONCUR");

        // should have a task for ian-acm... complete with 'NON_CONCUR' outcome
        acmTask = findAcmTaskForProcess();
        assertNotNull(acmTask);
        assertEquals("ian-acm", acmTask.getAssignee());
        assertEquals(0, acmTask.getBuckslipFutureTasks().size());
        assertTrue(acmTask.getBuckslipPastApprovers().contains("ann-acm"));
        assertTrue(acmTask.getBuckslipPastApprovers().contains("samuel-acm"));
        assignee = new UsernamePasswordAuthenticationToken("ian-acm", "ian-acm");

        ObjectConverter converter = ObjectConverter.createJSONConverter();
        String jsonTask = converter.getMarshaller().marshal(acmTask);
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
        assertEquals("ann-acm", acmTask.getAssignee());
        assertEquals(2, acmTask.getBuckslipFutureTasks().size());
        assertEquals("samuel-acm", acmTask.getBuckslipFutureTasks().get(0).getApproverId());
        assertEquals("ian-acm", acmTask.getBuckslipFutureTasks().get(1).getApproverId());
        assertEquals("[]", acmTask.getBuckslipPastApprovers());
        Principal assignee = new UsernamePasswordAuthenticationToken("ann-acm", "ann-acm");
        taskDao.completeTask(assignee, acmTask.getTaskId(), "buckslipOutcome", "CONCUR");

        // should have a task for samuel-acm... complete with 'CONCUR' outcome
        acmTask = findAcmTaskForProcess();
        assertNotNull(acmTask);
        assertEquals("samuel-acm", acmTask.getAssignee());
        assertEquals(1, acmTask.getBuckslipFutureTasks().size());
        assertEquals("ian-acm", acmTask.getBuckslipFutureTasks().get(0).getApproverId());
        assertTrue(acmTask.getBuckslipPastApprovers().contains("ann-acm"));

        // add the approver 'albert-acm'
        BuckslipFutureTask forAlbert = new BuckslipFutureTask();
        forAlbert.setApproverId("albert-acm");
        forAlbert.setTaskName("Albert task");
        acmTask.getBuckslipFutureTasks().add(forAlbert);
        taskDao.save(acmTask);

        assignee = new UsernamePasswordAuthenticationToken("samuel-acm", "samuel-acm");
        taskDao.completeTask(assignee, acmTask.getTaskId(), "buckslipOutcome", "CONCUR");

        // should have a task for ian-acm... complete with 'NON_CONCUR' outcome
        acmTask = findAcmTaskForProcess();
        assertNotNull(acmTask);
        assertEquals("ian-acm", acmTask.getAssignee());
        assertEquals(1, acmTask.getBuckslipFutureTasks().size());
        assertEquals("albert-acm", acmTask.getBuckslipFutureTasks().get(0).getApproverId());
        assertTrue(acmTask.getBuckslipPastApprovers().contains("ann-acm"));
        assertTrue(acmTask.getBuckslipPastApprovers().contains("samuel-acm"));
        assignee = new UsernamePasswordAuthenticationToken("ian-acm", "ian-acm");
        taskDao.completeTask(assignee, acmTask.getTaskId(), "buckslipOutcome", "NON_CONCUR");

        // should have a task for albert-acm... complete with 'NON_CONCUR' outcome
        acmTask = findAcmTaskForProcess();
        assertNotNull(acmTask);
        assertEquals("albert-acm", acmTask.getAssignee());
        assertEquals(0, acmTask.getBuckslipFutureTasks().size());
        assertTrue(acmTask.getBuckslipPastApprovers().contains("ann-acm"));
        assertTrue(acmTask.getBuckslipPastApprovers().contains("samuel-acm"));
        assertTrue(acmTask.getBuckslipPastApprovers().contains("ian-acm"));
        assignee = new UsernamePasswordAuthenticationToken("albert-acm", "albert-acm");
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
        assertEquals("ann-acm", acmTask.getAssignee());
        assertEquals(2, acmTask.getBuckslipFutureTasks().size());
        assertEquals("samuel-acm", acmTask.getBuckslipFutureTasks().get(0).getApproverId());
        assertEquals("ian-acm", acmTask.getBuckslipFutureTasks().get(1).getApproverId());
        assertEquals("[]", acmTask.getBuckslipPastApprovers());

        // remove ian-acm
        acmTask.getBuckslipFutureTasks().remove(1);
        taskDao.save(acmTask);

        Principal assignee = new UsernamePasswordAuthenticationToken("ann-acm", "ann-acm");
        taskDao.completeTask(assignee, acmTask.getTaskId(), "buckslipOutcome", "CONCUR");

        // should have a task for samuel-acm... complete with 'CONCUR' outcome
        acmTask = findAcmTaskForProcess();
        assertNotNull(acmTask);
        assertEquals("samuel-acm", acmTask.getAssignee());
        assertEquals(0, acmTask.getBuckslipFutureTasks().size());
        assertTrue(acmTask.getBuckslipPastApprovers().contains("ann-acm"));
        assignee = new UsernamePasswordAuthenticationToken("samuel-acm", "samuel-acm");
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
        assertEquals("ann-acm", acmTask.getAssignee());
        assertEquals(2, acmTask.getBuckslipFutureTasks().size());
        assertEquals("samuel-acm", acmTask.getBuckslipFutureTasks().get(0).getApproverId());
        assertEquals("ian-acm", acmTask.getBuckslipFutureTasks().get(1).getApproverId());
        assertEquals("[]", acmTask.getBuckslipPastApprovers());

        Principal assignee = new UsernamePasswordAuthenticationToken("ann-acm", "ann-acm");
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
        assertTrue(acmTaskService.getBuckslipPastTasks(buckslipProcess.getBusinessProcessId()).contains("ann-acm"));

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
