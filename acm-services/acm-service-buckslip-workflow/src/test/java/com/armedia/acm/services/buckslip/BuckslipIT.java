package com.armedia.acm.services.buckslip;

import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
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

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/spring/spring-library-buckslip-workflow-test.xml"})
public class BuckslipIT
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

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @Before
    public void setUp() throws Exception
    {
        // deploy
        repo.createDeployment()
                .addClasspathResource("activiti/ArkCase Buckslip Process.bpmn20.xml")
                .deploy();
    }

    @After
    public void shutDown() throws Exception
    {
        pe.close();
    }

    @Test
    public void basicPath_noApproverChanges() throws Exception
    {
        final String completedApprovalsKey = "pastApprovers";
        Long objectId = 500L;
        String objectType = "rockBand";
        String documentType = "Concert Contract";
        Boolean isBuckslipWorkflow = Boolean.TRUE;
        List<String> futureApprovers = Arrays.asList("bob", "phil");
        String currentApprover = "jerry";
        String completedApprovals = "[]";
        String moreApprovers = "true";

        Map<String, Object> processVariables = new HashMap<>();
        processVariables.put("OBJECT_ID", objectId);
        processVariables.put("OBJECT_TYPE", objectType);
        processVariables.put("isBuckslipWorkflow", isBuckslipWorkflow);
        processVariables.put("documentType", documentType);
        processVariables.put("futureApprovers", futureApprovers);
        processVariables.put(completedApprovalsKey, completedApprovals);
        processVariables.put("currentApprover", currentApprover);
        processVariables.put("moreApprovers", moreApprovers);

        ProcessInstance pi = rt.startProcessInstanceByKey("ArkCaseBuckslipProcess", processVariables);

        List<Task> approvals = getTasks(pi);

        assertEquals(1, approvals.size());
        Task task = approvals.get(0);
        assertEquals("jerry", task.getAssignee());
        String approvalsSoFar = (String) task.getProcessVariables().get(completedApprovalsKey);
        assertEquals("[]", approvalsSoFar);

        String updatedApprovers = addApprover(approvalsSoFar, task.getAssignee());
        log.debug("Approvers after jerry: {}", updatedApprovers);
        ts.setVariable(task.getId(), completedApprovalsKey, updatedApprovers);

        futureApprovers = (List<String>) task.getProcessVariables().get("futureApprovers");
        updateProcessVariables(futureApprovers, task);

        ts.complete(task.getId());

        approvals = getTasks(pi);

        assertEquals(1, approvals.size());
        task = approvals.get(0);
        assertEquals("bob", task.getAssignee());
        approvalsSoFar = (String) task.getProcessVariables().get(completedApprovalsKey);

        assertEquals(updatedApprovers, approvalsSoFar);

        log.debug("Approvers in bob's task: {}", approvalsSoFar);
        updatedApprovers = addApprover(approvalsSoFar, task.getAssignee());
        ts.setVariable(task.getId(), completedApprovalsKey, updatedApprovers);
        futureApprovers = (List<String>) task.getProcessVariables().get("futureApprovers");
        updateProcessVariables(futureApprovers, task);

        ts.complete(task.getId());

        approvals = getTasks(pi);

        assertEquals(1, approvals.size());
        task = approvals.get(0);
        assertEquals("phil", task.getAssignee());
        approvalsSoFar = (String) task.getProcessVariables().get(completedApprovalsKey);
        assertEquals(updatedApprovers, approvalsSoFar);
        log.debug("Approvers in phil's task: {}", approvalsSoFar);
        updatedApprovers = addApprover(approvalsSoFar, task.getAssignee());
        ts.setVariable(task.getId(), completedApprovalsKey, updatedApprovers);

        futureApprovers = (List<String>) task.getProcessVariables().get("futureApprovers");
        updateProcessVariables(futureApprovers, task);

        ts.complete(task.getId());

        List<HistoricProcessInstance> hpiList =
                hs.createHistoricProcessInstanceQuery().processInstanceId(pi.getId()).includeProcessVariables().list();
        assertEquals(1, hpiList.size());
        String allApprovals = (String) hpiList.get(0).getProcessVariables().get(completedApprovalsKey);
        assertEquals(updatedApprovers, allApprovals);

        // should not be a current process any more
        List<ProcessInstance> pis = rt.createProcessInstanceQuery().processInstanceId(pi.getId()).list();
        assertEquals(0, pis.size());
    }

    @Test
    public void removeAnApprover() throws Exception
    {
        final String completedApprovalsKey = "pastApprovers";
        Long objectId = 500L;
        String objectType = "rockBand";
        String documentType = "Concert Contract";
        Boolean isBuckslipWorkflow = Boolean.TRUE;
        List<String> futureApprovers = Arrays.asList("bob", "phil", "bill");
        String currentApprover = "jerry";
        String completedApprovals = "[]";
        String moreApprovers = "true";

        Map<String, Object> processVariables = new HashMap<>();
        processVariables.put("OBJECT_ID", objectId);
        processVariables.put("OBJECT_TYPE", objectType);
        processVariables.put("isBuckslipWorkflow", isBuckslipWorkflow);
        processVariables.put("documentType", documentType);
        processVariables.put("futureApprovers", futureApprovers);
        processVariables.put(completedApprovalsKey, completedApprovals);
        processVariables.put("currentApprover", currentApprover);
        processVariables.put("moreApprovers", moreApprovers);

        ProcessInstance pi = rt.startProcessInstanceByKey("ArkCaseBuckslipProcess", processVariables);

        List<Task> approvals = getTasks(pi);

        assertEquals(1, approvals.size());
        Task task = approvals.get(0);
        assertEquals("jerry", task.getAssignee());
        String approvalsSoFar = (String) task.getProcessVariables().get(completedApprovalsKey);
        assertEquals("[]", approvalsSoFar);

        String updatedApprovers = addApprover(approvalsSoFar, task.getAssignee());
        log.debug("Approvers after jerry: {}", updatedApprovers);
        ts.setVariable(task.getId(), completedApprovalsKey, updatedApprovers);

        futureApprovers = (List<String>) task.getProcessVariables().get("futureApprovers");
        updateProcessVariables(futureApprovers, task);

        ts.complete(task.getId());

        approvals = getTasks(pi);

        assertEquals(1, approvals.size());
        task = approvals.get(0);
        assertEquals("bob", task.getAssignee());
        approvalsSoFar = (String) task.getProcessVariables().get(completedApprovalsKey);

        assertEquals(updatedApprovers, approvalsSoFar);

        log.debug("Approvers in bob's task: {}", approvalsSoFar);
        updatedApprovers = addApprover(approvalsSoFar, task.getAssignee());
        ts.setVariable(task.getId(), completedApprovalsKey, updatedApprovers);
        futureApprovers = (List<String>) task.getProcessVariables().get("futureApprovers");
        updateProcessVariables(futureApprovers, task);

        ts.complete(task.getId());


        approvals = getTasks(pi);

        assertEquals(1, approvals.size());
        task = approvals.get(0);
        assertEquals("phil", task.getAssignee());
        approvalsSoFar = (String) task.getProcessVariables().get(completedApprovalsKey);
        assertEquals(updatedApprovers, approvalsSoFar);
        log.debug("Approvers in phil's task: {}", approvalsSoFar);
        updatedApprovers = addApprover(approvalsSoFar, task.getAssignee());
        ts.setVariable(task.getId(), completedApprovalsKey, updatedApprovers);

        futureApprovers = (List<String>) task.getProcessVariables().get("futureApprovers");

        // here we will remove the last approver, so the process should stop now, intead of going on to bill.
        // we should have one more approver from the original list, but we will remove it, and the proces should end.
        assertEquals(1, futureApprovers.size());
        futureApprovers = new ArrayList<>();

        updateProcessVariables(futureApprovers, task);

        ts.complete(task.getId());

        List<HistoricProcessInstance> hpiList =
                hs.createHistoricProcessInstanceQuery().processInstanceId(pi.getId()).includeProcessVariables().list();
        assertEquals(1, hpiList.size());
        String allApprovals = (String) hpiList.get(0).getProcessVariables().get(completedApprovalsKey);
        assertEquals(updatedApprovers, allApprovals);

        // should not be a current process any more
        List<ProcessInstance> pis = rt.createProcessInstanceQuery().processInstanceId(pi.getId()).list();
        assertEquals(0, pis.size());
    }

    private void updateProcessVariables(List<String> futureApprovers, Task task)
    {
        String moreApprovers;
        String currentApprover;
        if (!futureApprovers.isEmpty())
        {
            moreApprovers = "true";
            currentApprover = futureApprovers.get(0);
            futureApprovers = new ArrayList<>(futureApprovers.subList(1, futureApprovers.size()));

            ts.setVariable(task.getId(), "currentApprover", currentApprover);
            ts.setVariable(task.getId(), "moreApprovers", moreApprovers);
            ts.setVariable(task.getId(), "futureApprovers", futureApprovers);
        } else
        {
            ts.setVariable(task.getId(), "currentApprover", "");
            ts.setVariable(task.getId(), "moreApprovers", "false");
            ts.setVariable(task.getId(), "futureApprovers", new ArrayList<String>());
        }
    }

    @Test
    public void addAnApprover() throws Exception
    {
        final String completedApprovalsKey = "pastApprovers";
        Long objectId = 500L;
        String objectType = "rockBand";
        String documentType = "Concert Contract";
        Boolean isBuckslipWorkflow = Boolean.TRUE;
        List<String> futureApprovers = Arrays.asList("bob");
        String currentApprover = "jerry";
        String completedApprovals = "[]";
        String moreApprovers = "true";

        Map<String, Object> processVariables = new HashMap<>();
        processVariables.put("OBJECT_ID", objectId);
        processVariables.put("OBJECT_TYPE", objectType);
        processVariables.put("isBuckslipWorkflow", isBuckslipWorkflow);
        processVariables.put("documentType", documentType);
        processVariables.put("futureApprovers", futureApprovers);
        processVariables.put(completedApprovalsKey, completedApprovals);
        processVariables.put("currentApprover", currentApprover);
        processVariables.put("moreApprovers", moreApprovers);

        ProcessInstance pi = rt.startProcessInstanceByKey("ArkCaseBuckslipProcess", processVariables);

        List<Task> approvals = getTasks(pi);

        assertEquals(1, approvals.size());
        Task task = approvals.get(0);
        assertEquals("jerry", task.getAssignee());
        String approvalsSoFar = (String) task.getProcessVariables().get(completedApprovalsKey);
        assertEquals("[]", approvalsSoFar);

        String updatedApprovers = addApprover(approvalsSoFar, task.getAssignee());
        log.debug("Approvers after jerry: {}", updatedApprovers);
        ts.setVariable(task.getId(), completedApprovalsKey, updatedApprovers);

        futureApprovers = (List<String>) task.getProcessVariables().get("futureApprovers");
        updateProcessVariables(futureApprovers, task);

        ts.complete(task.getId());

        approvals = getTasks(pi);

        assertEquals(1, approvals.size());
        task = approvals.get(0);
        assertEquals("bob", task.getAssignee());
        approvalsSoFar = (String) task.getProcessVariables().get(completedApprovalsKey);

        assertEquals(updatedApprovers, approvalsSoFar);

        log.debug("Approvers in bob's task: {}", approvalsSoFar);
        updatedApprovers = addApprover(approvalsSoFar, task.getAssignee());
        ts.setVariable(task.getId(), completedApprovalsKey, updatedApprovers);
        futureApprovers = (List<String>) task.getProcessVariables().get("futureApprovers");

        // here is where we add approvers that were not there when we started the process.
        futureApprovers.add("phil");
        futureApprovers.add("bill");
        updateProcessVariables(futureApprovers, task);

        ts.complete(task.getId());

        approvals = getTasks(pi);

        assertEquals(1, approvals.size());
        task = approvals.get(0);
        assertEquals("phil", task.getAssignee());
        approvalsSoFar = (String) task.getProcessVariables().get(completedApprovalsKey);
        assertEquals(updatedApprovers, approvalsSoFar);
        log.debug("Approvers in phil's task: {}", approvalsSoFar);
        updatedApprovers = addApprover(approvalsSoFar, task.getAssignee());
        ts.setVariable(task.getId(), completedApprovalsKey, updatedApprovers);

        futureApprovers = (List<String>) task.getProcessVariables().get("futureApprovers");
        updateProcessVariables(futureApprovers, task);

        ts.complete(task.getId());

        approvals = getTasks(pi);

        assertEquals(1, approvals.size());
        task = approvals.get(0);
        assertEquals("bill", task.getAssignee());
        approvalsSoFar = (String) task.getProcessVariables().get(completedApprovalsKey);
        assertEquals(updatedApprovers, approvalsSoFar);
        log.debug("Approvers in bill's task: {}", approvalsSoFar);
        updatedApprovers = addApprover(approvalsSoFar, task.getAssignee());
        ts.setVariable(task.getId(), completedApprovalsKey, updatedApprovers);

        futureApprovers = (List<String>) task.getProcessVariables().get("futureApprovers");
        updateProcessVariables(futureApprovers, task);

        // before completing the last task, should still be a current process
        List<ProcessInstance> pis = rt.createProcessInstanceQuery().processInstanceId(pi.getId()).list();
        assertEquals(1, pis.size());

        ts.complete(task.getId());

        List<HistoricProcessInstance> hpiList =
                hs.createHistoricProcessInstanceQuery().processInstanceId(pi.getId()).includeProcessVariables().list();
        assertEquals(1, hpiList.size());
        String allApprovals = (String) hpiList.get(0).getProcessVariables().get(completedApprovalsKey);
        assertEquals(updatedApprovers, allApprovals);

        // should not be a current process any more
        pis = rt.createProcessInstanceQuery().processInstanceId(pi.getId()).list();
        assertEquals(0, pis.size());
    }

    private String addApprover(String approvalsSoFar, String approverId)
    {
        JSONArray jsonApprovers = new JSONArray(approvalsSoFar);
        JSONObject newApprover = new JSONObject();
        newApprover.put("approverId", approverId);
        ZonedDateTime date = ZonedDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
        String approvalDate = formatter.format(date);
        newApprover.put("approvalDate", approvalDate);
        jsonApprovers.put(newApprover);
        return jsonApprovers.toString();
    }

    private List<Task> getTasks(ProcessInstance pi)
    {
        return ts.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).includeProcessVariables().list();
    }
}
