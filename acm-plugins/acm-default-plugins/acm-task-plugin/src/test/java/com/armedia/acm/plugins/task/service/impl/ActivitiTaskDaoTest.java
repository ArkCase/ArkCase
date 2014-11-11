package com.armedia.acm.plugins.task.service.impl;

import com.armedia.acm.plugins.task.exception.AcmTaskException;
import com.armedia.acm.plugins.task.model.AcmTask;
import com.armedia.acm.plugins.task.model.TaskOutcome;
import org.activiti.bpmn.model.*;
import org.activiti.bpmn.model.Process;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.Authentication;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

/**
 * Created by armdev on 6/2/14.
 */
public class ActivitiTaskDaoTest extends EasyMockSupport
{
    private TaskService mockTaskService;
    private RepositoryService mockRepositoryService;
    private Task mockTask;
    private TaskQuery mockTaskQuery;
    private ProcessDefinitionQuery mockProcessDefinitionQuery;
    private ProcessDefinition mockProcessDefinition;
    private Authentication mockAuthentication;
    private HistoryService mockHistoryService;
    private HistoricTaskInstance mockHistoricTaskInstance;
    private HistoricTaskInstanceQuery mockHistoricTaskInstanceQuery;

    private ActivitiTaskDao unit;
    private BpmnModel mockBpmnModel;
    private Process mockProcess;
    private UserTask mockFlowElement;
    private FormProperty mockFormProperty;
    private FormValue mockFormValue;

    @Before
    public void setUp() throws Exception
    {
        mockTaskService = createMock(TaskService.class);
        mockTask = createMock(Task.class);
        mockTaskQuery = createMock(TaskQuery.class);
        mockRepositoryService = createMock(RepositoryService.class);
        mockProcessDefinitionQuery = createMock(ProcessDefinitionQuery.class);
        mockProcessDefinition = createMock(ProcessDefinition.class);
        mockAuthentication = createMock(Authentication.class);
        mockHistoricTaskInstance = createMock(HistoricTaskInstance.class);
        mockHistoryService = createMock(HistoryService.class);
        mockHistoricTaskInstanceQuery = createMock(HistoricTaskInstanceQuery.class);
        mockBpmnModel = createMock(BpmnModel.class);
        mockProcess = createMock(Process.class);
        mockFlowElement = createMock(UserTask.class);
        mockFormProperty = createMock(FormProperty.class);
        mockFormValue = createMock(FormValue.class);


        unit = new ActivitiTaskDao();

        Map<String, Integer> acmPriorityToActivitiPriority = new HashMap<>();
        acmPriorityToActivitiPriority.put("Medium", 50);
        acmPriorityToActivitiPriority.put("Low", 30);

        unit.setActivitiTaskService(mockTaskService);
        unit.setActivitiRepositoryService(mockRepositoryService);
        unit.setActivitiHistoryService(mockHistoryService);
        unit.setPriorityLevelToNumberMap(acmPriorityToActivitiPriority);
        unit.setRequiredFieldsPerOutcomeMap(new HashMap<String, List<String>>());
    }

    @Test
    public void save() throws Exception
    {
        Long taskId = 500L;
        String assignee = "assignee";
        String priority = "Low";
        Date due = new Date();
        String title = "title";
        String objectType = "objectType";
        String objectName = "objectName";
        Long objectId = 400L;
        Date start = new Date();
        String status = "status";
        Integer percentComplete = 25;
        String details = "details";
        String owner = "owner";

        AcmTask in = new AcmTask();
        in.setTaskId(taskId);
        in.setAssignee(assignee);
        in.setTaskStartDate(start);
        in.setStatus(status);
        in.setDetails(details);
        in.setDueDate(due);
        in.setPriority(priority);
        in.setTitle(title);
        in.setAttachedToObjectId(objectId);
        in.setAttachedToObjectType(objectType);
        in.setAttachedToObjectName(objectName);
        in.setPercentComplete(percentComplete);
        in.setOwner(owner);
        in.setCreateDate(start);
        in.setReworkInstructions("rework instructions");

        TaskOutcome selected = new TaskOutcome();
        selected.setName("outcome name");
        in.setTaskOutcome(selected);

        expect(mockTaskService.createTaskQuery()).andReturn(mockTaskQuery);
        expect(mockTaskQuery.taskId(taskId.toString())).andReturn(mockTaskQuery);
        expect(mockTaskQuery.singleResult()).andReturn(mockTask);

        mockTask.setAssignee(assignee);
        mockTask.setPriority(30);
        mockTask.setDueDate(due);
        mockTask.setName(title);
        mockTask.setOwner(owner);
        expect(mockTask.getCreateTime()).andReturn(start);

        mockTaskService.saveTask(mockTask);

        expect(mockTask.getId()).andReturn(taskId.toString()).atLeastOnce();
        mockTaskService.setVariableLocal(taskId.toString(), "OBJECT_TYPE", objectType);
        mockTaskService.setVariableLocal(taskId.toString(), "OBJECT_ID", objectId);
        mockTaskService.setVariableLocal(taskId.toString(), objectType, objectId);
        mockTaskService.setVariableLocal(taskId.toString(), "OBJECT_NAME", objectName);
        mockTaskService.setVariableLocal(taskId.toString(), "START_DATE", start);
        mockTaskService.setVariableLocal(taskId.toString(), "TASK_STATUS", status);
        mockTaskService.setVariableLocal(taskId.toString(), "PERCENT_COMPLETE", percentComplete);
        mockTaskService.setVariableLocal(taskId.toString(), "DETAILS", details);
        mockTaskService.setVariable(taskId.toString(), "REWORK_INSTRUCTIONS", in.getReworkInstructions());
        mockTaskService.setVariableLocal(taskId.toString(), "outcome", in.getTaskOutcome().getName());

        replayAll();

        unit.save(in);

        verifyAll();
    }

    @Test
    public void completeTask() throws Exception
    {
        Long taskId = 500L;
        String user = "user";
        Date dueDate = new Date();
        Date started = new Date();
        Date ended = new Date();
        long taskDuration = 9876543L;
        String acmPriority = "Medium";
        int activitiPriority = 50;
        String title = "task Title";
        String processId = "processId";
        String processName = "processName";
        Long objectId = 250L;
        String objectType = "objectType";
        String objectName = "objectName";

        Map<String, Object> pvars = new HashMap<>();
        pvars.put("OBJECT_ID", objectId);
        pvars.put("OBJECT_TYPE", objectType);
        pvars.put("OBJECT_NAME", objectName);

        Map<String, Object> taskLocalVars = new HashMap<>();
        taskLocalVars.put("START_DATE", new Date());
        taskLocalVars.put("TASK_STATUS", "taskStatus");
        taskLocalVars.put("PERCENT_COMPLETE", 75);
        taskLocalVars.put("DETAILS", "task details");

        expect(mockAuthentication.getName()).andReturn(user);

        expect(mockTaskService.createTaskQuery()).andReturn(mockTaskQuery);
        expect(mockTaskQuery.includeTaskLocalVariables()).andReturn(mockTaskQuery);
        expect(mockTaskQuery.includeProcessVariables()).andReturn(mockTaskQuery);
        expect(mockTaskQuery.taskId(String.valueOf(taskId))).andReturn(mockTaskQuery);
        expect(mockTaskQuery.singleResult()).andReturn(mockTask);

        expect(mockTask.getAssignee()).andReturn(user).atLeastOnce();
        expect(mockTask.getTaskLocalVariables()).andReturn(taskLocalVars).atLeastOnce();
        expect(mockTask.getCreateTime()).andReturn(null);
        expect(mockTask.getOwner()).andReturn(user);
        expect(mockTask.getProcessInstanceId()).andReturn(null);

        mockTaskService.complete(String.valueOf(taskId));

        expect(mockHistoryService.createHistoricTaskInstanceQuery()).andReturn(mockHistoricTaskInstanceQuery);
        expect(mockHistoricTaskInstanceQuery.taskId(String.valueOf(taskId))).andReturn(mockHistoricTaskInstanceQuery);
        expect(mockHistoricTaskInstanceQuery.singleResult()).andReturn(mockHistoricTaskInstance);

        expect(mockHistoricTaskInstance.getStartTime()).andReturn(started);
        expect(mockHistoricTaskInstance.getEndTime()).andReturn(ended);
        expect(mockHistoricTaskInstance.getDurationInMillis()).andReturn(taskDuration);

        expect(mockTask.getId()).andReturn(taskId.toString());
        expect(mockTask.getDueDate()).andReturn(dueDate);
        expect(mockTask.getPriority()).andReturn(activitiPriority);
        expect(mockTask.getName()).andReturn(title);
        expect(mockTask.getProcessVariables()).andReturn(pvars).atLeastOnce();
        expect(mockTask.getProcessDefinitionId()).andReturn(processId);

        expect(mockRepositoryService.createProcessDefinitionQuery()).andReturn(mockProcessDefinitionQuery);
        expect(mockProcessDefinitionQuery.processDefinitionId(processId)).andReturn(mockProcessDefinitionQuery);
        expect(mockProcessDefinitionQuery.singleResult()).andReturn(mockProcessDefinition);

        expect(mockProcessDefinition.getName()).andReturn(processName);

        String taskDefKey = "taskDefinitionKey";
        expect(mockTask.getTaskDefinitionKey()).andReturn(taskDefKey);
        expect(mockRepositoryService.getBpmnModel(processId)).andReturn(mockBpmnModel);
        expect(mockBpmnModel.getProcesses()).andReturn(Arrays.asList(mockProcess));
        expect(mockProcess.getFlowElement(taskDefKey)).andReturn(mockFlowElement);
        expect(mockFlowElement.getFormProperties()).andReturn(Arrays.asList(mockFormProperty));
        expect(mockFormProperty.getName()).andReturn("Test Outcome").atLeastOnce();
        expect(mockFormProperty.getId()).andReturn("TestOutcome").atLeastOnce();
        expect(mockFormProperty.getFormValues()).andReturn(Arrays.asList(mockFormValue));
        expect(mockFormValue.getId()).andReturn("formValueId").atLeastOnce();
        expect(mockFormValue.getName()).andReturn("formValueName").atLeastOnce();

        replayAll();

        AcmTask completed = unit.completeTask(mockAuthentication, taskId);

        verifyAll();

        assertNotNull(completed);
        assertEquals(taskId, completed.getTaskId());
        assertTrue(completed.isCompleted());
        assertEquals(acmPriority, completed.getPriority());

        assertNotNull(completed.getTaskStartDate());
        assertEquals("taskStatus", completed.getStatus());
        assertEquals("task details", completed.getDetails());
        assertEquals(Integer.valueOf(75), completed.getPercentComplete());
    }

    @Test
    public void findById() throws Exception
    {
        String user = "user";
        Long taskId = 500L;
        Date dueDate = new Date();
        String acmPriority = "Medium";
        int activitiPriority = 50;
        String title = "task Title";
        String processId = "processId";
        String processName = "processName";

        Long objectId = 250L;
        String objectType = "objectType";
        String objectName = "objectName";

        Map<String, Object> pvars = new HashMap<>();
        pvars.put("OBJECT_ID", objectId);
        pvars.put("OBJECT_TYPE", objectType);
        pvars.put("OBJECT_NAME", objectName);


        Map<String, Object> taskLocalVars = new HashMap<>();
        taskLocalVars.put("START_DATE", new Date());
        taskLocalVars.put("TASK_STATUS", "taskStatus");
        taskLocalVars.put("PERCENT_COMPLETE", 50);
        taskLocalVars.put("DETAILS", "task details");

        expect(mockTaskService.createTaskQuery()).andReturn(mockTaskQuery);
        expect(mockTaskQuery.taskId(String.valueOf(taskId))).andReturn(mockTaskQuery);
        expect(mockTaskQuery.includeProcessVariables()).andReturn(mockTaskQuery);
        expect(mockTaskQuery.includeTaskLocalVariables()).andReturn(mockTaskQuery);
        expect(mockTaskQuery.singleResult()).andReturn(mockTask);

        expect(mockTask.getId()).andReturn(taskId.toString());
        expect(mockTask.getDueDate()).andReturn(dueDate);
        expect(mockTask.getPriority()).andReturn(activitiPriority);
        expect(mockTask.getName()).andReturn(title);
        expect(mockTask.getProcessVariables()).andReturn(pvars).atLeastOnce();
        expect(mockTask.getTaskLocalVariables()).andReturn(taskLocalVars).atLeastOnce();
        expect(mockTask.getAssignee()).andReturn(user);
        expect(mockTask.getProcessDefinitionId()).andReturn(processId);
        expect(mockTask.getCreateTime()).andReturn(null);
        expect(mockTask.getOwner()).andReturn(user);
        expect(mockTask.getProcessInstanceId()).andReturn("250").atLeastOnce();

        expect(mockRepositoryService.createProcessDefinitionQuery()).andReturn(mockProcessDefinitionQuery);
        expect(mockProcessDefinitionQuery.processDefinitionId(processId)).andReturn(mockProcessDefinitionQuery);
        expect(mockProcessDefinitionQuery.singleResult()).andReturn(mockProcessDefinition);

        expect(mockProcessDefinition.getName()).andReturn(processName);

        String taskDefKey = "taskDefinitionKey";
        expect(mockTask.getTaskDefinitionKey()).andReturn(taskDefKey);
        expect(mockRepositoryService.getBpmnModel(processId)).andReturn(mockBpmnModel);
        expect(mockBpmnModel.getProcesses()).andReturn(Arrays.asList(mockProcess));
        expect(mockProcess.getFlowElement(taskDefKey)).andReturn(mockFlowElement);
        expect(mockFlowElement.getFormProperties()).andReturn(Arrays.asList(mockFormProperty));
        expect(mockFormProperty.getName()).andReturn("Test Outcome").atLeastOnce();
        expect(mockFormProperty.getId()).andReturn("TestOutcome").atLeastOnce();
        expect(mockFormProperty.getFormValues()).andReturn(Arrays.asList(mockFormValue));
        expect(mockFormValue.getId()).andReturn("formValueId").atLeastOnce();
        expect(mockFormValue.getName()).andReturn("formValueName").atLeastOnce();

        replayAll();

        AcmTask task = unit.findById(taskId);

        verifyAll();

        assertEquals(taskId, task.getTaskId());
        assertEquals(dueDate, task.getDueDate());
        assertEquals(acmPriority, task.getPriority());
        assertEquals(title, task.getTitle());
        assertEquals(objectId, task.getAttachedToObjectId());
        assertEquals(objectType, task.getAttachedToObjectType());
        assertEquals(objectName, task.getAttachedToObjectName());
        assertEquals(user, task.getAssignee());
        assertEquals(processName, task.getBusinessProcessName());
        assertFalse(task.isAdhocTask());
        assertFalse(task.isCompleted());

        assertNotNull(task.getTaskStartDate());
        assertEquals("taskStatus", task.getStatus());
        assertEquals("task details", task.getDetails());
        assertEquals(Integer.valueOf(50), task.getPercentComplete());
    }

    @Test
    public void findById_completedTask() throws Exception
    {
        String user = "user";
        Long taskId = 500L;
        Date dueDate = new Date();
        String acmPriority = "Medium";
        int activitiPriority = 50;
        Date started = new Date();
        Date ended = new Date();
        long taskDuration = 9876543L;
        String title = "task Title";
        String processId = "processId";
        String processName = "processName";

        Long objectId = 250L;
        String objectType = "objectType";
        String objectName = "objectName";

        Map<String, Object> pvars = new HashMap<>();
        pvars.put("OBJECT_ID", objectId);
        pvars.put("OBJECT_TYPE", objectType);
        pvars.put("OBJECT_NAME",objectName);

        expect(mockTaskService.createTaskQuery()).andReturn(mockTaskQuery);
        expect(mockTaskQuery.taskId(String.valueOf(taskId))).andReturn(mockTaskQuery);
        expect(mockTaskQuery.includeProcessVariables()).andReturn(mockTaskQuery);
        expect(mockTaskQuery.includeTaskLocalVariables()).andReturn(mockTaskQuery);
        expect(mockTaskQuery.singleResult()).andReturn(null);

        expect(mockHistoryService.createHistoricTaskInstanceQuery()).andReturn(mockHistoricTaskInstanceQuery);
        expect(mockHistoricTaskInstanceQuery.taskId(String.valueOf(taskId))).andReturn(mockHistoricTaskInstanceQuery);
        expect(mockHistoricTaskInstanceQuery.includeProcessVariables()).andReturn(mockHistoricTaskInstanceQuery);
        expect(mockHistoricTaskInstanceQuery.includeTaskLocalVariables()).andReturn(mockHistoricTaskInstanceQuery);
        expect(mockHistoricTaskInstanceQuery.singleResult()).andReturn(mockHistoricTaskInstance);

        expect(mockHistoricTaskInstance.getStartTime()).andReturn(started);
        expect(mockHistoricTaskInstance.getEndTime()).andReturn(ended);
        expect(mockHistoricTaskInstance.getDurationInMillis()).andReturn(taskDuration);

        Map<String, Object> taskLocalVars = new HashMap<>();
        taskLocalVars.put("START_DATE", new Date());
        taskLocalVars.put("TASK_STATUS", "taskStatus");
        taskLocalVars.put("PERCENT_COMPLETE", 50);
        taskLocalVars.put("DETAILS", "details");
        taskLocalVars.put("outcome", "formValueId");

        expect(mockHistoricTaskInstance.getId()).andReturn(taskId.toString());
        expect(mockHistoricTaskInstance.getDueDate()).andReturn(dueDate);
        expect(mockHistoricTaskInstance.getPriority()).andReturn(activitiPriority);
        expect(mockHistoricTaskInstance.getName()).andReturn(title);
        expect(mockHistoricTaskInstance.getProcessVariables()).andReturn(pvars).atLeastOnce();
        expect(mockHistoricTaskInstance.getTaskLocalVariables()).andReturn(taskLocalVars).atLeastOnce();
        expect(mockHistoricTaskInstance.getAssignee()).andReturn(user);
        expect(mockHistoricTaskInstance.getProcessDefinitionId()).andReturn(processId);
        expect(mockHistoricTaskInstance.getProcessInstanceId()).andReturn("250").atLeastOnce();


        expect(mockRepositoryService.createProcessDefinitionQuery()).andReturn(mockProcessDefinitionQuery);
        expect(mockProcessDefinitionQuery.processDefinitionId(processId)).andReturn(mockProcessDefinitionQuery);
        expect(mockProcessDefinitionQuery.singleResult()).andReturn(mockProcessDefinition);

        expect(mockProcessDefinition.getName()).andReturn(processName);

        String taskDefKey = "taskDefinitionKey";
        expect(mockHistoricTaskInstance.getTaskDefinitionKey()).andReturn(taskDefKey);
        expect(mockRepositoryService.getBpmnModel(processId)).andReturn(mockBpmnModel);
        expect(mockBpmnModel.getProcesses()).andReturn(Arrays.asList(mockProcess));
        expect(mockProcess.getFlowElement(taskDefKey)).andReturn(mockFlowElement);
        expect(mockFlowElement.getFormProperties()).andReturn(Arrays.asList(mockFormProperty));
        expect(mockFormProperty.getName()).andReturn("Test Outcome").atLeastOnce();
        expect(mockFormProperty.getId()).andReturn("TestOutcome").atLeastOnce();
        expect(mockFormProperty.getFormValues()).andReturn(Arrays.asList(mockFormValue));
        expect(mockFormValue.getId()).andReturn("formValueId").atLeastOnce();
        expect(mockFormValue.getName()).andReturn("formValueName").atLeastOnce();

        replayAll();

        AcmTask task = unit.findById(taskId);

        verifyAll();

        assertEquals(taskId, task.getTaskId());
        assertEquals(dueDate, task.getDueDate());
        assertEquals(acmPriority, task.getPriority());
        assertEquals(title, task.getTitle());
        assertEquals(objectId, task.getAttachedToObjectId());
        assertEquals(objectType, task.getAttachedToObjectType());
        assertEquals(objectName, task.getAttachedToObjectName());
        assertEquals(user, task.getAssignee());
        assertEquals(processName, task.getBusinessProcessName());
        assertFalse(task.isAdhocTask());
        assertTrue(task.isCompleted());

        assertNotNull(task.getTaskStartDate());
        assertEquals("taskStatus", task.getStatus());
        assertEquals("details", task.getDetails());
        assertEquals(Integer.valueOf(50), task.getPercentComplete());

        assertNotNull(task.getTaskOutcome());
        assertEquals("formValueId", task.getTaskOutcome().getName());
    }

    @Test
    public void findById_noSuchTask() throws Exception
    {
        Long taskId = 500L;

        expect(mockTaskService.createTaskQuery()).andReturn(mockTaskQuery);
        expect(mockTaskQuery.taskId(String.valueOf(taskId))).andReturn(mockTaskQuery);
        expect(mockTaskQuery.includeProcessVariables()).andReturn(mockTaskQuery);
        expect(mockTaskQuery.includeTaskLocalVariables()).andReturn(mockTaskQuery);
        expect(mockTaskQuery.singleResult()).andReturn(null);

        expect(mockHistoryService.createHistoricTaskInstanceQuery()).andReturn(mockHistoricTaskInstanceQuery);
        expect(mockHistoricTaskInstanceQuery.taskId(String.valueOf(taskId))).andReturn(mockHistoricTaskInstanceQuery);
        expect(mockHistoricTaskInstanceQuery.includeProcessVariables()).andReturn(mockHistoricTaskInstanceQuery);
        expect(mockHistoricTaskInstanceQuery.includeTaskLocalVariables()).andReturn(mockHistoricTaskInstanceQuery);
        expect(mockHistoricTaskInstanceQuery.singleResult()).andReturn(null);

        replayAll();

        try
        {
            unit.findById(taskId);
            fail("Should have exception since task was not found");
        }
        catch (AcmTaskException ate)
        {
            // expected, so test passes
        }

        verifyAll();

    }

    @Test
    public void tasksForUser() throws Exception
    {
        String user = "user";

        Long taskId = 500L;
        Date dueDate = new Date();
        String acmPriority = "Medium";
        int activitiPriority = 50;
        String title = "task Title";
        String processId = "processId";
        String processName = "processName";

        Long objectId = 250L;
        String objectType = "objectType";
        String objectName = "objectName";


        Map<String, Object> pvars = new HashMap<>();
        pvars.put("OBJECT_ID", objectId);
        pvars.put("OBJECT_TYPE", objectType);
        pvars.put("OBJECT_NAME", objectName);

        Map<String, Object> taskLocalVars = new HashMap<>();
        taskLocalVars.put("START_DATE", new Date());
        taskLocalVars.put("TASK_STATUS", "taskStatus");
        taskLocalVars.put("PERCENT_COMPLETE", 25);
        taskLocalVars.put("DETAILS", "details");

        expect(mockTaskService.createTaskQuery()).andReturn(mockTaskQuery);
        expect(mockTaskQuery.taskAssignee(user)).andReturn(mockTaskQuery);
        expect(mockTaskQuery.includeProcessVariables()).andReturn(mockTaskQuery);
        expect(mockTaskQuery.includeTaskLocalVariables()).andReturn(mockTaskQuery);
        expect(mockTaskQuery.orderByDueDate()).andReturn(mockTaskQuery);
        expect(mockTaskQuery.desc()).andReturn(mockTaskQuery);
        expect(mockTaskQuery.list()).andReturn(Arrays.asList(mockTask));

        expect(mockTask.getId()).andReturn(taskId.toString());
        expect(mockTask.getDueDate()).andReturn(dueDate);
        expect(mockTask.getPriority()).andReturn(activitiPriority);
        expect(mockTask.getName()).andReturn(title);
        expect(mockTask.getProcessVariables()).andReturn(pvars).atLeastOnce();
        expect(mockTask.getTaskLocalVariables()).andReturn(taskLocalVars).atLeastOnce();
        expect(mockTask.getAssignee()).andReturn(user);
        expect(mockTask.getProcessDefinitionId()).andReturn(processId);
        expect(mockTask.getCreateTime()).andReturn(null);
        expect(mockTask.getOwner()).andReturn(user);
        expect(mockTask.getProcessInstanceId()).andReturn("250").atLeastOnce();

        expect(mockRepositoryService.createProcessDefinitionQuery()).andReturn(mockProcessDefinitionQuery);
        expect(mockProcessDefinitionQuery.processDefinitionId(processId)).andReturn(mockProcessDefinitionQuery);
        expect(mockProcessDefinitionQuery.singleResult()).andReturn(mockProcessDefinition);

        expect(mockProcessDefinition.getName()).andReturn(processName);

        String taskDefKey = "taskDefinitionKey";
        expect(mockTask.getTaskDefinitionKey()).andReturn(taskDefKey);
        expect(mockRepositoryService.getBpmnModel(processId)).andReturn(mockBpmnModel);
        expect(mockBpmnModel.getProcesses()).andReturn(Arrays.asList(mockProcess));
        expect(mockProcess.getFlowElement(taskDefKey)).andReturn(mockFlowElement);
        expect(mockFlowElement.getFormProperties()).andReturn(Arrays.asList(mockFormProperty));
        expect(mockFormProperty.getName()).andReturn("Test Outcome").atLeastOnce();
        expect(mockFormProperty.getId()).andReturn("TestOutcome").atLeastOnce();
        expect(mockFormProperty.getFormValues()).andReturn(Arrays.asList(mockFormValue));
        expect(mockFormValue.getId()).andReturn("formValueId").atLeastOnce();
        expect(mockFormValue.getName()).andReturn("formValueName").atLeastOnce();

        replayAll();

        List<AcmTask> tasks = unit.tasksForUser(user);

        verifyAll();

        assertEquals(1, tasks.size());

        AcmTask found = tasks.get(0);

        assertEquals(taskId, found.getTaskId());
        assertEquals(dueDate, found.getDueDate());
        assertEquals(acmPriority, found.getPriority());
        assertEquals(title, found.getTitle());
        assertEquals(objectId, found.getAttachedToObjectId());
        assertEquals(objectType, found.getAttachedToObjectType());
        assertEquals(objectName, found.getAttachedToObjectName());
        assertEquals(user, found.getAssignee());
        assertEquals(processName, found.getBusinessProcessName());
        assertFalse(found.isAdhocTask());
        assertFalse(found.isCompleted());
        assertNotNull(found.getTaskStartDate());
        assertEquals("taskStatus", found.getStatus());
        assertEquals("details", found.getDetails());
        assertEquals(Integer.valueOf(25), found.getPercentComplete());
        assertEquals(1, found.getAvailableOutcomes().size());
        assertEquals("TestOutcome", found.getOutcomeName());

        TaskOutcome taskOutcome = found.getAvailableOutcomes().get(0);

        assertEquals("formValueId", taskOutcome.getName());
        assertEquals("formValueName", taskOutcome.getDescription());


    }
}
