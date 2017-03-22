package com.armedia.acm.plugins.task.service;

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.objectonverter.ObjectConverter;
import com.armedia.acm.plugins.task.model.AcmTask;
import com.armedia.acm.services.users.model.AcmUser;
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
        "/spring/spring-library-user-service.xml"
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

    private String processId;

    AcmUser samuel = new AcmUser();
    AcmUser ian = new AcmUser();
    AcmUser ann = new AcmUser();
    AcmUser albert = new AcmUser();

    private transient final Logger LOG = LoggerFactory.getLogger(getClass());

    @Before
    public void setUp() throws Exception
    {
        samuel.setUserId("samuel-acm");
        ian.setUserId("ian-acm");
        ann.setUserId("ann-acm");
        albert.setUserId("albert-acm");

        auditPropertyEntityAdapter.setUserId("TEST");

        deployProcessIfNeeded();
        processId = startBuckslipProcess();
    }

    private void deployProcessIfNeeded()
    {
        List<Deployment> deployments = repo.createDeploymentQuery().processDefinitionKey(processName).list();
        if (deployments == null || deployments.isEmpty())
        {
            repo.createDeployment()
                    .addClasspathResource("activiti/ArkCase Buckslip Process.bpmn20.xml")
                    .deploy();
        }
    }

    private String startBuckslipProcess()
    {

        Long objectId = 500L;
        String objectType = "rockBand";
        String objectNumber = "20170116_101";
        String documentType = "Concert Contract";
        List<String> futureApprovers = new ArrayList<>();
        futureApprovers.add("ann-acm");
        futureApprovers.add("samuel-acm");
        futureApprovers.add("ian-acm");

        Map<String, Object> processVariables = new HashMap<>();
        processVariables.put("OBJECT_ID", objectId);
        processVariables.put("OBJECT_TYPE", objectType);
        processVariables.put("OBJECT_NAME", objectNumber);
        processVariables.put("documentType", documentType);
        // the process should work with either "approvers" or "futureApprovers"
        processVariables.put("approvers", futureApprovers);
        processVariables.put("taskDueDateExpression", "P3D");

        ProcessInstance pi = rt.startProcessInstanceByKey(processName, processVariables);

        return pi.getId();
    }

    private AcmTask findAcmTaskForProcess()
    {
        List<Task> tasks = ts.createTaskQuery().processInstanceId(processId).list();

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
        assertNotNull(processId);

        // should have a task for ann-acm... complete with 'CONCUR' outcome
        AcmTask acmTask = findAcmTaskForProcess();
        assertNotNull(acmTask);
        assertEquals("ann-acm", acmTask.getAssignee());
        assertEquals(2, acmTask.getBuckslipFutureApprovers().size());
        assertTrue(acmTask.getBuckslipFutureApprovers().contains(samuel));
        assertTrue(acmTask.getBuckslipFutureApprovers().contains(ian));
        assertEquals("[]", acmTask.getBuckslipPastApprovers());
        Principal assignee = new UsernamePasswordAuthenticationToken("ann-acm", "ann-acm");
        taskDao.completeTask(assignee, acmTask.getTaskId(), "buckslipOutcome", "CONCUR");

        // should have a task for samuel-acm... complete with 'CONCUR' outcome
        acmTask = findAcmTaskForProcess();
        assertNotNull(acmTask);
        assertEquals("samuel-acm", acmTask.getAssignee());
        assertEquals(1, acmTask.getBuckslipFutureApprovers().size());
        assertTrue(acmTask.getBuckslipFutureApprovers().contains(ian));
        assertTrue(acmTask.getBuckslipPastApprovers().contains("ann-acm"));
        assignee = new UsernamePasswordAuthenticationToken("samuel-acm", "samuel-acm");
        taskDao.completeTask(assignee, acmTask.getTaskId(), "buckslipOutcome", "CONCUR");

        // should have a task for ian-acm... complete with 'NON_CONCUR' outcome
        acmTask = findAcmTaskForProcess();
        assertNotNull(acmTask);
        assertEquals("ian-acm", acmTask.getAssignee());
        assertEquals(0, acmTask.getBuckslipFutureApprovers().size());
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
        assertNotNull(processId);

        // should have a task for ann-acm... complete with 'CONCUR' outcome

        AcmTask acmTask = findAcmTaskForProcess();
        assertNotNull(acmTask);
        assertEquals("ann-acm", acmTask.getAssignee());
        assertEquals(2, acmTask.getBuckslipFutureApprovers().size());
        assertTrue(acmTask.getBuckslipFutureApprovers().contains(ian));
        assertTrue(acmTask.getBuckslipFutureApprovers().contains(samuel));
        assertEquals("[]", acmTask.getBuckslipPastApprovers());
        Principal assignee = new UsernamePasswordAuthenticationToken("ann-acm", "ann-acm");
        taskDao.completeTask(assignee, acmTask.getTaskId(), "buckslipOutcome", "CONCUR");

        // should have a task for samuel-acm... complete with 'CONCUR' outcome
        acmTask = findAcmTaskForProcess();
        assertNotNull(acmTask);
        assertEquals("samuel-acm", acmTask.getAssignee());
        assertEquals(1, acmTask.getBuckslipFutureApprovers().size());
        assertTrue(acmTask.getBuckslipFutureApprovers().contains(ian));
        assertTrue(acmTask.getBuckslipPastApprovers().contains("ann-acm"));

        // add the approver 'albert-acm'
        acmTask.getBuckslipFutureApprovers().add(albert);
        taskDao.save(acmTask);

        assignee = new UsernamePasswordAuthenticationToken("samuel-acm", "samuel-acm");
        taskDao.completeTask(assignee, acmTask.getTaskId(), "buckslipOutcome", "CONCUR");

        // should have a task for ian-acm... complete with 'NON_CONCUR' outcome
        acmTask = findAcmTaskForProcess();
        assertNotNull(acmTask);
        assertEquals("ian-acm", acmTask.getAssignee());
        assertEquals(1, acmTask.getBuckslipFutureApprovers().size());
        assertTrue(acmTask.getBuckslipFutureApprovers().contains(albert));
        assertTrue(acmTask.getBuckslipPastApprovers().contains("ann-acm"));
        assertTrue(acmTask.getBuckslipPastApprovers().contains("samuel-acm"));
        assignee = new UsernamePasswordAuthenticationToken("ian-acm", "ian-acm");
        taskDao.completeTask(assignee, acmTask.getTaskId(), "buckslipOutcome", "NON_CONCUR");

        // should have a task for albert-acm... complete with 'NON_CONCUR' outcome
        acmTask = findAcmTaskForProcess();
        assertNotNull(acmTask);
        assertEquals("albert-acm", acmTask.getAssignee());
        assertEquals(0, acmTask.getBuckslipFutureApprovers().size());
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

        assertNotNull(processId);

        AcmUser samuel = new AcmUser();
        samuel.setUserId("samuel-acm");
        AcmUser ian = new AcmUser();
        ian.setUserId("ian-acm");
        AcmUser ann = new AcmUser();
        ann.setUserId("ann-acm");
        AcmUser albert = new AcmUser();
        albert.setUserId("albert-acm");

        // should have a task for ann-acm... complete with 'CONCUR' outcome
        AcmTask acmTask = findAcmTaskForProcess();
        assertNotNull(acmTask);
        assertEquals("ann-acm", acmTask.getAssignee());
        assertEquals(2, acmTask.getBuckslipFutureApprovers().size());
        assertTrue(acmTask.getBuckslipFutureApprovers().contains(samuel));
        assertTrue(acmTask.getBuckslipFutureApprovers().contains(ian));
        assertEquals("[]", acmTask.getBuckslipPastApprovers());

        // remove ian-acm
        acmTask.getBuckslipFutureApprovers().remove(ian);
        taskDao.save(acmTask);

        Principal assignee = new UsernamePasswordAuthenticationToken("ann-acm", "ann-acm");
        taskDao.completeTask(assignee, acmTask.getTaskId(), "buckslipOutcome", "CONCUR");

        // should have a task for samuel-acm... complete with 'CONCUR' outcome
        acmTask = findAcmTaskForProcess();
        assertNotNull(acmTask);
        assertEquals("samuel-acm", acmTask.getAssignee());
        assertEquals(0, acmTask.getBuckslipFutureApprovers().size());
        assertTrue(acmTask.getBuckslipPastApprovers().contains("ann-acm"));
        assignee = new UsernamePasswordAuthenticationToken("samuel-acm", "samuel-acm");
        taskDao.completeTask(assignee, acmTask.getTaskId(), "buckslipOutcome", "CONCUR");


        // no more tasks
        acmTask = findAcmTaskForProcess();
        assertNull(acmTask);
    }
}
