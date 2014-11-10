package com.armedia.acm.plugins.ecm.service;


import org.activiti.bpmn.BpmnAutoLayout;
import org.activiti.bpmn.model.*;
import org.activiti.bpmn.model.Process;
import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricDetail;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.bpmn.diagram.ProcessDiagramGenerator;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ExecutionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/spring-library-ecm-activiti-test.xml" } )
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

    private Logger log = LoggerFactory.getLogger(getClass());

    private List<String> reviewers;
    private ProcessInstance pi;
    private String documentAuthor;

    @Before
    public void setUp() throws Exception
    {

        // deploy
        repo.createDeployment()
                .addClasspathResource("activiti/acmDocumentWorkflow.bpmn20.xml")
                .deploy();

        Map<String, Object> pvars = new HashMap<>();

        reviewers = Arrays.asList("jerry", "bob", "mickey");
        String taskName = "Request to Close Complaint '20141015_9823'";
        documentAuthor = "phil";

        pvars.put("reviewers", reviewers);
        pvars.put("taskName", taskName);
        pvars.put("documentAuthor", documentAuthor);

        pi = createWorkflowProcess(pvars);

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

        for ( int a = 0; a < reviews.size(); ++a )
        {
            Task task = reviews.get(a);
            ts.setVariable(task.getId(), "reviewOutcome", "APPROVE");
            ts.complete(task.getId());

            ProcessInstance current = rt.createProcessInstanceQuery().processInstanceId(pi.getProcessInstanceId()).singleResult();
            boolean shouldBeComplete = a == reviews.size() - 1;

            // when all reviewers have approved, the process will be complete and the runtime service query
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
        ts.complete(second.getId());

        // should be only the rework task now.
        List<Task> reworkTasks = ts.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).list();
        assertEquals(1, reworkTasks.size());

        Task rework = reworkTasks.get(0);
        assertEquals("authorReworksDocument", rework.getTaskDefinitionKey());
        assertEquals(documentAuthor, rework.getAssignee());

        ts.setVariable(rework.getId(), "reworkOutcome", "CANCEL_DOCUMENT");
        ts.complete(rework.getId());

        ProcessInstance piShouldBeNull =
                rt.createProcessInstanceQuery().processInstanceId(pi.getProcessInstanceId()).singleResult();
        assertNull(piShouldBeNull);
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
    public void documentApproval_generatePng() throws Exception
    {
        assertNotNull(pi);

//        BpmnModel designModel = repo.getBpmnModel(pi.getProcessDefinitionId());

        BpmnModel model = new BpmnModel();
        Process p = new Process();
        model.addProcess(p);
        p.setId("test-runtime-image");

//        Process designProcess = designModel.getProcesses().get(0);
//        p.getFlowElements().addAll(designProcess.getFlowElements());
//
//        Map<String, UserTask> designTasks = new HashMap<>();
//        List<UserTask> toRemove = new ArrayList<>();
//        for ( FlowElement fe : designProcess.getFlowElements() )
//        {
//            if ( fe instanceof UserTask )
//            {
//                UserTask ut = (UserTask) fe;
//                designTasks.put(ut.getId(), ut);
//            }
//        }

//        StartEvent runtimeStart = createStartEvent();
//        EndEvent runtimeEnd = createEndEvent();
//
//        p.addFlowElement(runtimeStart);
//        p.addFlowElement(runtimeEnd);
//
//        List<SequenceFlow> sfRemove = new ArrayList<>();
//        List<Task> reviews = ts.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).list();
//        for ( Task t : reviews )
//        {
//            UserTask tRuntime = createUserTask(t.getId(), t.getName(), t.getAssignee());
//            UserTask tDesign = designTasks.get(t.getTaskDefinitionKey());
//
//
//
//            for ( SequenceFlow sfDesign : tDesign.getOutgoingFlows() )
//            {
//                log.debug("Adding sequence from: " + tRuntime.getId() + " to " + sfDesign.getTargetRef());
//                SequenceFlow sfRuntime = createSequenceFlow(tRuntime.getId(), sfDesign.getTargetRef());
//                tRuntime.getOutgoingFlows().add(sfRuntime);
//                p.addFlowElement(sfRuntime);
//                sfRemove.add(sfDesign);
//            }
//
//            for ( SequenceFlow sfDesign : tDesign.getIncomingFlows() )
//            {
//                log.debug("design sequence: " + sfDesign.getSourceRef() + " -> " + sfDesign.getTargetRef());
//                SequenceFlow sfRuntime = createSequenceFlow(sfDesign.getSourceRef(), tRuntime.getId());
//                tRuntime.getIncomingFlows().add(sfRuntime);
//                p.addFlowElement(sfRuntime);
//                sfRemove.add(sfDesign);
//            }
//
//
//            p.addFlowElement(tRuntime);
//            toRemove.add(tDesign);
//
//
//        }
//
//        List<SequenceFlow> runtimeRemove = new ArrayList<>();
//
//        for ( SequenceFlow removeMe : sfRemove )
//        {
//            for ( FlowElement fe : p.getFlowElements() )
//            {
//                if ( fe instanceof SequenceFlow )
//                {
//                    SequenceFlow sf = (SequenceFlow) fe;
//                    if ( sf.getTargetRef().equals(removeMe.getTargetRef()) && sf.getSourceRef().equals(removeMe.getSourceRef()) )
//                    {
//                        runtimeRemove.add(sf);
//                        FlowElement src = p.getFlowElement(sf.getSourceRef());
//                        if ( src instanceof FlowNode )
//                        {
//                            FlowNode srcFn = (FlowNode) src;
//                            srcFn.getOutgoingFlows().remove(sf);
//                        }
//
//                        FlowElement target = p.getFlowElement(sf.getTargetRef());
//                        if ( target instanceof FlowNode )
//                        {
//                            FlowNode targetFn = (FlowNode) target;
//                            targetFn.getIncomingFlows().remove(sf);
//                        }
//                    }
//                }
//            }
//        }
//
//        log.debug("size before: " + p.getFlowElements().size());
//        p.getFlowElements().removeAll(runtimeRemove);
//        log.debug("size after: " + p.getFlowElements().size());
//
//       p.getFlowElements().removeAll(toRemove);
        List<String> taskIds = new ArrayList<>();
//
        List<Task> reviews = ts.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).list();

        // approve first task
        Task task = reviews.get(0);
        ts.setVariable(task.getId(), "reviewOutcome", "APPROVE");
        ts.setVariableLocal(task.getId(), "outcome", "APPROVE");
        ts.complete(task.getId());

        reviews = ts.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).list();

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

        int maxNameLength = Integer.MIN_VALUE;

        List<HistoricTaskInstance> completed = hs.
                createHistoricTaskInstanceQuery().
                processInstanceId(pi.getProcessInstanceId()).
                includeTaskLocalVariables().
                orderByHistoricTaskInstanceEndTime().asc().
                list();
        for ( HistoricTaskInstance hti : completed )
        {
            if ( hti.getEndTime() != null )
            {
                String taskName = "DONE: " + hti.getName() + " [" + hti.getTaskLocalVariables().get("outcome") +
                        " by " + hti.getAssignee() + " on " + sdf.format(hti.getEndTime()) + "]";
                maxNameLength = taskName.length() > maxNameLength ? taskName.length() : maxNameLength;
                log.debug("task name length: "  + taskName.length());
                UserTask ut = createUserTask(hti.getId(), taskName, hti.getAssignee());
                p.addFlowElement(ut);
            }

        }

        for ( Task t : reviews )
        {
            log.debug("task id: " + t.getId() + "; " + t.getName() + "; " + t.getAssignee() +
                "; task def key: " + t.getTaskDefinitionKey());
            String taskName = t.getName() + " [" + t.getAssignee() + ", due: " + sdf.format(t.getDueDate()) + "]";
            maxNameLength = taskName.length() > maxNameLength ? taskName.length() : maxNameLength;
            UserTask ut = createUserTask(t.getId(), taskName, t.getAssignee());
            p.addFlowElement(ut);

//            SequenceFlow sfStartToStask = createSequenceFlow("start", t.getId());
//            p.addFlowElement(sfStartToStask);
//            runtimeStart.getOutgoingFlows().add(sfStartToStask);
//            ut.getIncomingFlows().add(sfStartToStask);

//            SequenceFlow sfTaskToEnd = createSequenceFlow(t.getId(), "end");
//            p.addFlowElement(sfTaskToEnd);
//            ut.getOutgoingFlows().add(sfTaskToEnd);
//            runtimeEnd.getIncomingFlows().add(sfTaskToEnd);

            taskIds.add(t.getId());
        }

        log.debug("max name length: " + maxNameLength);
//
//        StartEvent designStart = (StartEvent) designModel.getFlowElement("startDocumentApproval");
//


        // 2. Generate graphical information
        BpmnAutoLayout bal = new BpmnAutoLayout(model);
        log.debug("default task width: " + bal.getTaskWidth());
        // need about 8 per character
        bal.setTaskWidth(maxNameLength * 8);
        bal.execute();

        InputStream is = ProcessDiagramGenerator.generateDiagram(model, "png", taskIds);
//        InputStream is = ProcessDiagramGenerator.generatePngDiagram(model);


//        BpmnModel model = repo.getBpmnModel(pi.getProcessDefinitionId());
//        assertNotNull(model);
//
//        List<Task> reviews = ts.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).list();
//
//        List<String> reviewIds = new ArrayList<>();
//        for ( Task t : reviews )
//        {
//            reviewIds.add(t.getId());
//        }
//
////        InputStream is = ProcessDiagramGenerator.generateDiagram(model, "png", rt.getActiveActivityIds(pi.getId()));
//        InputStream is = ProcessDiagramGenerator.generateDiagram(model, "png", reviewIds);



//        ProcessDefinitionEntity pde = (ProcessDefinitionEntity) repo.createProcessDefinitionQuery().
//                processDefinitionId(pi.getProcessDefinitionId())
//                .singleResult();

//        InputStream is = repo.getResourceAsStream(pde.getDeploymentId(), pde.getDiagramResourceName());

        File output = new File(System.getProperty("user.home") + "/model.png");

        log.debug("writing to: " + output.getCanonicalPath());

        FileUtils.copyInputStreamToFile(is, output);

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

        for ( FormProperty fp : formProperties )
        {
            log.debug("fp name: " + fp.getName() + "; id: " + fp.getId());
            for ( FormValue fv : fp.getFormValues() )
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
