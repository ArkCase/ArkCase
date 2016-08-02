package com.armedia.acm.plugins.task.service.impl;

import com.armedia.acm.plugins.task.exception.AcmTaskException;
import com.armedia.acm.plugins.task.model.AcmApplicationTaskEvent;
import com.armedia.acm.plugins.task.model.AcmTask;
import com.armedia.acm.plugins.task.model.TaskConstants;
import com.armedia.acm.plugins.task.model.TaskOutcome;
import com.armedia.acm.plugins.task.service.TaskEventPublisher;
import com.armedia.acm.services.dataaccess.service.impl.DataAccessPrivilegeListener;
import com.armedia.acm.services.participants.dao.AcmParticipantDao;
import com.armedia.acm.services.participants.model.AcmParticipant;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FormProperty;
import org.activiti.bpmn.model.FormValue;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricProcessInstanceQuery;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.history.HistoricVariableInstanceQuery;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.easymock.Capture;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.Authentication;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

/**
 * Created by armdev on 6/2/14.
 */
public class ActivitiTaskDaoTest extends EasyMockSupport
{
    private TaskService mockTaskService;
    private ProcessInstanceQuery mockProcessInstanceQuery;
    private ProcessInstance mockProcessInstance;
    private RuntimeService mockRuntimeService;
    private RepositoryService mockRepositoryService;
    private Task mockTask;
    private TaskQuery mockTaskQuery;
    private ProcessDefinitionQuery mockProcessDefinitionQuery;
    private ProcessDefinition mockProcessDefinition;
    private Authentication mockAuthentication;
    private HistoryService mockHistoryService;
    private HistoricTaskInstance mockHistoricTaskInstance;
    private HistoricProcessInstance mockHistoricProcessInstance;
    private HistoricProcessInstanceQuery mockHistoricProcessInstanceQuery;
    private HistoricTaskInstanceQuery mockHistoricTaskInstanceQuery;
    private HistoricVariableInstanceQuery mockHistoricVariableInstanceQuery;
    private HistoricVariableInstance mockHistoricVariableInstance;
    private AcmParticipantDao mockParticipantDao;
    private DataAccessPrivilegeListener mockDataAccessPrivilegeListener;
    private IdentityLink mockCandidateGroup;

    private ActivitiTaskDao unit;
    private BpmnModel mockBpmnModel;
    private Process mockProcess;
    private UserTask mockFlowElement;
    private FormProperty mockFormProperty;
    private FormValue mockFormValue;
    private TaskEventPublisher mockTaskEventPublisher;

    @Before
    public void setUp() throws Exception
    {
        mockTaskService = createMock(TaskService.class);
        mockProcessInstance = createMock(ProcessInstance.class);
        mockProcessInstanceQuery = createMock(ProcessInstanceQuery.class);
        mockRuntimeService = createMock(RuntimeService.class);
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
        mockParticipantDao = createMock(AcmParticipantDao.class);
        mockDataAccessPrivilegeListener = createMock(DataAccessPrivilegeListener.class);
        mockCandidateGroup = createMock(IdentityLink.class);
        mockTaskEventPublisher = createMock(TaskEventPublisher.class);
        mockHistoricProcessInstance = createMock(HistoricProcessInstance.class);
        mockHistoricProcessInstanceQuery = createMock(HistoricProcessInstanceQuery.class);
        mockHistoricVariableInstanceQuery = createMock(HistoricVariableInstanceQuery.class);
        mockHistoricVariableInstance = createMock(HistoricVariableInstance.class);
        unit = new ActivitiTaskDao();

        Map<String, Integer> acmPriorityToActivitiPriority = new HashMap<>();
        acmPriorityToActivitiPriority.put("Medium", 50);
        acmPriorityToActivitiPriority.put("Low", 30);

        unit.setActivitiTaskService(mockTaskService);
        unit.setActivitiRuntimeService(mockRuntimeService);
        unit.setActivitiRepositoryService(mockRepositoryService);
        unit.setActivitiHistoryService(mockHistoryService);
        unit.setParticipantDao(mockParticipantDao);
        unit.setDataAccessPrivilegeListener(mockDataAccessPrivilegeListener);
        unit.setPriorityLevelToNumberMap(acmPriorityToActivitiPriority);
        unit.setRequiredFieldsPerOutcomeMap(new HashMap<>());
        unit.setTaskEventPublisher(mockTaskEventPublisher);
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
        String candidateGroup = "candidateGroup";
        String nextAssignee = "nextAssignee";
        String processId = "500";
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
        in.setParentObjectId(2500L);
        in.setParentObjectType("parent object type");
        in.setNextAssignee(nextAssignee);

        // candidate group should not be saved... it is read-only, we read it from Activiti, but don't save it.
        in.setCandidateGroups(Arrays.asList(candidateGroup));

        TaskOutcome selected = new TaskOutcome();
        selected.setName("outcome name");
        in.setTaskOutcome(selected);

        expect(mockTaskService.createTaskQuery()).andReturn(mockTaskQuery);
        expect(mockTaskQuery.taskId(taskId.toString())).andReturn(mockTaskQuery);
        expect(mockTaskQuery.singleResult()).andReturn(mockTask);

        // find the candidate group
        expect(mockTaskService.getIdentityLinksForTask(String.valueOf(taskId))).andReturn(Arrays.asList(mockCandidateGroup));
        expect(mockCandidateGroup.getType()).andReturn(TaskConstants.IDENTITY_LINK_TYPE_CANDIDATE);
        expect(mockCandidateGroup.getGroupId()).andReturn(candidateGroup).atLeastOnce();

        mockTask.setAssignee(assignee);
        mockTask.setPriority(30);
        mockTask.setDueDate(due);
        mockTask.setName(title);
        mockTask.setOwner(owner);
        expect(mockTask.getProcessInstanceId()).andReturn(processId);
        expect(mockTask.getCreateTime()).andReturn(start);

        mockTaskService.saveTask(mockTask);
        mockRuntimeService.setVariable(processId, "REWORK_INSTRUCTIONS", null);

        expect(mockTask.getId()).andReturn(taskId.toString()).atLeastOnce();
        mockTaskService.setVariableLocal(taskId.toString(), "OBJECT_TYPE", objectType);
        mockTaskService.setVariableLocal(taskId.toString(), "OBJECT_ID", objectId);
        mockTaskService.setVariableLocal(taskId.toString(), objectType, objectId);
        mockTaskService.setVariableLocal(taskId.toString(), "OBJECT_NAME", objectName);
        mockTaskService.setVariableLocal(taskId.toString(), "START_DATE", start);
        mockTaskService.setVariableLocal(taskId.toString(), "PERCENT_COMPLETE", percentComplete);
        mockTaskService.setVariableLocal(taskId.toString(), "DETAILS", details);
        mockTaskService.setVariableLocal(taskId.toString(), "REWORK_INSTRUCTIONS", in.getReworkInstructions());
        mockTaskService.setVariableLocal(taskId.toString(), "outcome", in.getTaskOutcome().getName());
        mockTaskService.setVariableLocal(taskId.toString(), "PARENT_OBJECT_ID", 2500L);
        mockTaskService.setVariableLocal(taskId.toString(), "PARENT_OBJECT_TYPE", "parent object type");
        mockTaskService.setVariable(taskId.toString(), TaskConstants.VARIABLE_NAME_NEXT_ASSIGNEE, in.getNextAssignee());
        mockTaskService.setVariableLocal(taskId.toString(), TaskConstants.VARIABLE_NAME_PARENT_OBJECT_TITLE, null);
        mockTaskService.setVariableLocal(taskId.toString(), TaskConstants.VARIABLE_NAME_LEGACY_SYSTEM_ID, null);
        // data access and assignment rules
        mockDataAccessPrivilegeListener.applyAssignmentAndAccessRules(in);

        Capture<List<AcmParticipant>> keepThese = new Capture<>();
        Capture<List<AcmParticipant>> saved = new Capture<>();

        List<AcmParticipant> merged = new ArrayList<>();
        merged.add(new AcmParticipant());

        expect(mockParticipantDao.removeAllOtherParticipantsForObject(eq("TASK"), eq(in.getTaskId()), capture(keepThese))).andReturn(0);
        expect(mockParticipantDao.saveParticipants(capture(saved))).andReturn(merged);

        replayAll();

        unit.save(in);

        verifyAll();

        // should have been one participant sent in the "keep" list (the assignee)
        assertEquals(1, keepThese.getValue().size());
        // should have been one participant saved
        assertEquals(1, saved.getValue().size());
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
        String processId = "500";
        String processName = "processName";
        Long objectId = 250L;
        String objectType = "objectType";
        String objectName = "objectName";
        String deleteReason = "CLOSED";

        Map<String, Object> pvars = new HashMap<>();
        pvars.put("OBJECT_ID", objectId);
        pvars.put("OBJECT_TYPE", objectType);
        pvars.put("OBJECT_NAME", objectName);

        Map<String, Object> taskLocalVars = new HashMap<>();
        taskLocalVars.put("START_DATE", new Date());
        taskLocalVars.put("PERCENT_COMPLETE", 75);
        taskLocalVars.put("DETAILS", "task details");

        List<AcmParticipant> partList = new ArrayList<>();
        partList.add(new AcmParticipant());

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

        expect(mockHistoricTaskInstance.getStartTime()).andReturn(started).times(2);
        expect(mockHistoricTaskInstance.getEndTime()).andReturn(ended).times(4);
        expect(mockHistoricTaskInstance.getDeleteReason()).andReturn(deleteReason).times(4);

        expect(mockHistoricTaskInstance.getDurationInMillis()).andReturn(taskDuration);

        expect(mockTask.getId()).andReturn(taskId.toString()).atLeastOnce();
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
        expect(mockProcess.getFlowElementRecursive(taskDefKey)).andReturn(mockFlowElement);
        expect(mockFlowElement.getFormProperties()).andReturn(Arrays.asList(mockFormProperty));
        expect(mockFormProperty.getName()).andReturn("Test Outcome").atLeastOnce();
        expect(mockFormProperty.getId()).andReturn("TestOutcome").atLeastOnce();
        expect(mockFormProperty.getFormValues()).andReturn(Arrays.asList(mockFormValue));
        expect(mockFormValue.getId()).andReturn("formValueId").atLeastOnce();
        expect(mockFormValue.getName()).andReturn("formValueName").atLeastOnce();
        expect(mockParticipantDao.findParticipantsForObject("TASK", taskId)).andReturn(partList);

        replayAll();

        AcmTask completed = unit.completeTask(mockAuthentication, taskId);

        verifyAll();

        assertNotNull(completed);
        assertEquals(taskId, completed.getTaskId());
        assertTrue(completed.isCompleted());
        assertEquals(acmPriority, completed.getPriority());

        assertNotNull(completed.getTaskStartDate());
        assertEquals(TaskConstants.STATE_CLOSED, completed.getStatus());
        assertEquals("task details", completed.getDetails());
        assertEquals(Integer.valueOf(75), completed.getPercentComplete());

        assertEquals(partList, completed.getParticipants());
    }

    @Test
    public void deleteTask() throws Exception
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
        String deleteReason = null;

        Map<String, Object> pvars = new HashMap<>();
        pvars.put("OBJECT_ID", objectId);
        pvars.put("OBJECT_TYPE", objectType);

        Map<String, Object> taskLocalVars = new HashMap<>();
        taskLocalVars.put("START_DATE", new Date());
        taskLocalVars.put("PERCENT_COMPLETE", 75);
        taskLocalVars.put("DETAILS", "task details");

        List<AcmParticipant> partList = new ArrayList<>();
        partList.add(new AcmParticipant());

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

        mockTaskService.deleteTask(String.valueOf(taskId), deleteReason);

        expect(mockHistoryService.createHistoricTaskInstanceQuery()).andReturn(mockHistoricTaskInstanceQuery);
        expect(mockHistoricTaskInstanceQuery.taskId(String.valueOf(taskId))).andReturn(mockHistoricTaskInstanceQuery);
        expect(mockHistoricTaskInstanceQuery.singleResult()).andReturn(mockHistoricTaskInstance);

        expect(mockHistoricTaskInstance.getStartTime()).andReturn(started);
        expect(mockHistoricTaskInstance.getEndTime()).andReturn(ended).times(2);
        expect(mockHistoricTaskInstance.getDurationInMillis()).andReturn(taskDuration);

        expect(mockHistoricTaskInstance.getDeleteReason()).andReturn(deleteReason).times(1);

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
        expect(mockProcess.getFlowElementRecursive(taskDefKey)).andReturn(mockFlowElement);
        expect(mockFlowElement.getFormProperties()).andReturn(Arrays.asList(mockFormProperty));
        expect(mockFormProperty.getName()).andReturn("Test Outcome").atLeastOnce();
        expect(mockFormProperty.getId()).andReturn("TestOutcome").atLeastOnce();
        expect(mockFormProperty.getFormValues()).andReturn(Arrays.asList(mockFormValue));
        expect(mockFormValue.getId()).andReturn("formValueId").atLeastOnce();
        expect(mockFormValue.getName()).andReturn("formValueName").atLeastOnce();
        expect(mockParticipantDao.findParticipantsForObject("TASK", taskId)).andReturn(partList);

        replayAll();
        AcmTask deleted = unit.deleteTask(mockAuthentication, taskId);

        verifyAll();

        assertNotNull(deleted);
        assertEquals(taskId, deleted.getTaskId());
        assertTrue(deleted.isCompleted());
        assertEquals(acmPriority, deleted.getPriority());

        assertNotNull(deleted.getTaskStartDate());
        assertEquals(TaskConstants.STATE_DELETE, deleted.getStatus());
        assertEquals("task details", deleted.getDetails());
        assertEquals(Integer.valueOf(75), deleted.getPercentComplete());

        assertEquals(partList, deleted.getParticipants());
    }

    @Test
    public void findById_taskWithAssigneeAndCandidateGroup_acmTaskShouldNotHaveCandidateGroup() throws Exception
    {
        String user = "user";
        Long taskId = 500L;
        Date dueDate = new Date();
        String acmPriority = "Medium";
        int activitiPriority = 50;
        String title = "task Title";
        String processId = "processId";
        String processName = "processName";

        String nextAssignee = "nextAssignee";

        Long objectId = 250L;
        String objectType = "objectType";
        String objectName = "objectName";

        Map<String, Object> pvars = new HashMap<>();
        pvars.put("OBJECT_ID", objectId);
        pvars.put("OBJECT_TYPE", objectType);
        pvars.put("OBJECT_NAME", objectName);

        pvars.put(TaskConstants.VARIABLE_NAME_NEXT_ASSIGNEE, nextAssignee);


        Map<String, Object> taskLocalVars = new HashMap<>();
        taskLocalVars.put("START_DATE", new Date());
        taskLocalVars.put("PERCENT_COMPLETE", 50);
        taskLocalVars.put("DETAILS", "task details");

        List<AcmParticipant> partList = new ArrayList<>();
        partList.add(new AcmParticipant());

        expect(mockTaskService.createTaskQuery()).andReturn(mockTaskQuery);
        expect(mockTaskQuery.taskId(String.valueOf(taskId))).andReturn(mockTaskQuery);
        expect(mockTaskQuery.includeProcessVariables()).andReturn(mockTaskQuery);
        expect(mockTaskQuery.includeTaskLocalVariables()).andReturn(mockTaskQuery);
        expect(mockTaskQuery.singleResult()).andReturn(mockTask);

        expect(mockTask.getId()).andReturn(taskId.toString()).atLeastOnce();
        expect(mockTask.getDueDate()).andReturn(dueDate);
        expect(mockTask.getPriority()).andReturn(activitiPriority);
        expect(mockTask.getName()).andReturn(title);
        expect(mockTask.getProcessVariables()).andReturn(pvars).atLeastOnce();
        expect(mockTask.getTaskLocalVariables()).andReturn(taskLocalVars).atLeastOnce();
        expect(mockTask.getAssignee()).andReturn(user).times(2);
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
        expect(mockProcess.getFlowElementRecursive(taskDefKey)).andReturn(mockFlowElement);
        expect(mockFlowElement.getFormProperties()).andReturn(Arrays.asList(mockFormProperty));
        expect(mockFormProperty.getName()).andReturn("Test Outcome").atLeastOnce();
        expect(mockFormProperty.getId()).andReturn("TestOutcome").atLeastOnce();
        expect(mockFormProperty.getFormValues()).andReturn(Arrays.asList(mockFormValue));
        expect(mockFormValue.getId()).andReturn("formValueId").atLeastOnce();
        expect(mockFormValue.getName()).andReturn("formValueName").atLeastOnce();
        expect(mockParticipantDao.findParticipantsForObject("TASK", taskId)).andReturn(partList);

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
        assertEquals(nextAssignee, task.getNextAssignee());

        assertNotNull(task.getTaskStartDate());
        assertEquals(TaskConstants.STATE_ACTIVE, task.getStatus());
        assertEquals("task details", task.getDetails());
        assertEquals(Integer.valueOf(50), task.getPercentComplete());

        assertEquals(partList, task.getParticipants());

        assertEquals(0, task.getCandidateGroups().size());

    }

    @Test
    public void findById_taskWithNoAssigneeAndCandidateGroup_acmTaskShouldHaveCandidateGroup() throws Exception
    {
        String user = "user";
        Long taskId = 500L;
        Date dueDate = new Date();
        String acmPriority = "Medium";
        int activitiPriority = 50;
        String title = "task Title";
        String processId = "processId";
        String processName = "processName";
        String candidateGroup = "grateful dead";

        Long objectId = 250L;
        String objectType = "objectType";
        String objectName = "objectName";

        Map<String, Object> pvars = new HashMap<>();
        pvars.put("OBJECT_ID", objectId);
        pvars.put("OBJECT_TYPE", objectType);
        pvars.put("OBJECT_NAME", objectName);


        Map<String, Object> taskLocalVars = new HashMap<>();
        taskLocalVars.put("START_DATE", new Date());
        taskLocalVars.put("PERCENT_COMPLETE", 50);
        taskLocalVars.put("DETAILS", "task details");

        List<AcmParticipant> partList = new ArrayList<>();
        partList.add(new AcmParticipant());

        expect(mockTaskService.createTaskQuery()).andReturn(mockTaskQuery);
        expect(mockTaskQuery.taskId(String.valueOf(taskId))).andReturn(mockTaskQuery);
        expect(mockTaskQuery.includeProcessVariables()).andReturn(mockTaskQuery);
        expect(mockTaskQuery.includeTaskLocalVariables()).andReturn(mockTaskQuery);
        expect(mockTaskQuery.singleResult()).andReturn(mockTask);

        // find the candidate group
        expect(mockTaskService.getIdentityLinksForTask(String.valueOf(taskId))).andReturn(Arrays.asList(mockCandidateGroup));
        expect(mockCandidateGroup.getType()).andReturn(TaskConstants.IDENTITY_LINK_TYPE_CANDIDATE);
        expect(mockCandidateGroup.getGroupId()).andReturn(candidateGroup).atLeastOnce();

        expect(mockTask.getId()).andReturn(taskId.toString()).atLeastOnce();
        expect(mockTask.getDueDate()).andReturn(dueDate);
        expect(mockTask.getPriority()).andReturn(activitiPriority);
        expect(mockTask.getName()).andReturn(title);
        expect(mockTask.getProcessVariables()).andReturn(pvars).atLeastOnce();
        expect(mockTask.getTaskLocalVariables()).andReturn(taskLocalVars).atLeastOnce();
        expect(mockTask.getAssignee()).andReturn(null).times(2);
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
        expect(mockProcess.getFlowElementRecursive(taskDefKey)).andReturn(mockFlowElement);
        expect(mockFlowElement.getFormProperties()).andReturn(Arrays.asList(mockFormProperty));
        expect(mockFormProperty.getName()).andReturn("Test Outcome").atLeastOnce();
        expect(mockFormProperty.getId()).andReturn("TestOutcome").atLeastOnce();
        expect(mockFormProperty.getFormValues()).andReturn(Arrays.asList(mockFormValue));
        expect(mockFormValue.getId()).andReturn("formValueId").atLeastOnce();
        expect(mockFormValue.getName()).andReturn("formValueName").atLeastOnce();
        expect(mockParticipantDao.findParticipantsForObject("TASK", taskId)).andReturn(partList);

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
        assertNull(task.getAssignee());
        assertEquals(processName, task.getBusinessProcessName());
        assertFalse(task.isAdhocTask());
        assertFalse(task.isCompleted());

        assertNotNull(task.getTaskStartDate());
        assertEquals(TaskConstants.STATE_UNCLAIMED, task.getStatus());
        assertEquals("task details", task.getDetails());
        assertEquals(Integer.valueOf(50), task.getPercentComplete());

        assertEquals(partList, task.getParticipants());

        assertEquals(1, task.getCandidateGroups().size());

        assertEquals(candidateGroup, task.getCandidateGroups().get(0));
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
        String processId = "500";
        String processName = "processName";
        String deleteReason = "TERMINATION";

        Long objectId = 250L;
        String objectType = "objectType";
        String objectName = "objectName";

        Map<String, Object> pvars = new HashMap<>();
        pvars.put("OBJECT_ID", objectId);
        pvars.put("OBJECT_TYPE", objectType);
        pvars.put("OBJECT_NAME", objectName);

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

        expect(mockHistoricTaskInstance.getStartTime()).andReturn(started).times(2);
        expect(mockHistoricTaskInstance.getEndTime()).andReturn(ended).atLeastOnce();
        expect(mockHistoricTaskInstance.getDurationInMillis()).andReturn(taskDuration);
        expect(mockHistoricTaskInstance.getDeleteReason()).andReturn(deleteReason).times(4);

        Map<String, Object> taskLocalVars = new HashMap<>();
        taskLocalVars.put("START_DATE", new Date());
        taskLocalVars.put("PERCENT_COMPLETE", 50);
        taskLocalVars.put("DETAILS", "details");
        taskLocalVars.put("outcome", "formValueId");

        List<AcmParticipant> partList = new ArrayList<>();
        partList.add(new AcmParticipant());

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

        expect(mockHistoryService.createHistoricVariableInstanceQuery()).andReturn(mockHistoricVariableInstanceQuery);
        expect(mockHistoricVariableInstanceQuery.taskId(taskId.toString())).andReturn(mockHistoricVariableInstanceQuery);
        expect(mockHistoricVariableInstanceQuery.variableName("DETAILS")).andReturn(mockHistoricVariableInstanceQuery);
        expect(mockHistoricVariableInstanceQuery.singleResult()).andReturn(mockHistoricVariableInstance);
        expect(mockHistoricVariableInstance.getValue()).andReturn("details");

        expect(mockHistoryService.createHistoricVariableInstanceQuery()).andReturn(mockHistoricVariableInstanceQuery);
        expect(mockHistoricVariableInstanceQuery.taskId(taskId.toString())).andReturn(mockHistoricVariableInstanceQuery);
        expect(mockHistoricVariableInstanceQuery.variableName("REWORK_INSTRUCTIONS")).andReturn(mockHistoricVariableInstanceQuery);
        expect(mockHistoricVariableInstanceQuery.singleResult()).andReturn(mockHistoricVariableInstance);
        expect(mockHistoricVariableInstance.getValue()).andReturn(null);

        expect(mockProcessDefinition.getName()).andReturn(processName);

        String taskDefKey = "taskDefinitionKey";
        expect(mockHistoricTaskInstance.getTaskDefinitionKey()).andReturn(taskDefKey);
        expect(mockRepositoryService.getBpmnModel(processId)).andReturn(mockBpmnModel);
        expect(mockBpmnModel.getProcesses()).andReturn(Arrays.asList(mockProcess));
        expect(mockProcess.getFlowElementRecursive(taskDefKey)).andReturn(mockFlowElement);
        expect(mockFlowElement.getFormProperties()).andReturn(Arrays.asList(mockFormProperty));
        expect(mockFormProperty.getName()).andReturn("Test Outcome").atLeastOnce();
        expect(mockFormProperty.getId()).andReturn("TestOutcome").atLeastOnce();
        expect(mockFormProperty.getFormValues()).andReturn(Arrays.asList(mockFormValue));
        expect(mockFormValue.getId()).andReturn("formValueId").atLeastOnce();
        expect(mockFormValue.getName()).andReturn("formValueName").atLeastOnce();
        expect(mockParticipantDao.findParticipantsForObject("TASK", taskId)).andReturn(partList);

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

        assertEquals(TaskConstants.STATE_CLOSED, task.getStatus());
        assertEquals("details", task.getDetails());
        assertEquals(Integer.valueOf(50), task.getPercentComplete());

        assertNotNull(task.getTaskOutcome());
        assertEquals("formValueId", task.getTaskOutcome().getName());

        assertEquals(partList, task.getParticipants());
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
        } catch (AcmTaskException ate)
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
        taskLocalVars.put("PERCENT_COMPLETE", 25);
        taskLocalVars.put("DETAILS", "details");

        List<AcmParticipant> partList = new ArrayList<>();
        partList.add(new AcmParticipant());

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
        expect(mockTask.getAssignee()).andReturn(user).times(2);
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
        expect(mockProcess.getFlowElementRecursive(taskDefKey)).andReturn(mockFlowElement);
        expect(mockFlowElement.getFormProperties()).andReturn(Arrays.asList(mockFormProperty));
        expect(mockFormProperty.getName()).andReturn("Test Outcome").atLeastOnce();
        expect(mockFormProperty.getId()).andReturn("TestOutcome").atLeastOnce();
        expect(mockFormProperty.getFormValues()).andReturn(Arrays.asList(mockFormValue));
        expect(mockFormValue.getId()).andReturn("formValueId").atLeastOnce();
        expect(mockFormValue.getName()).andReturn("formValueName").atLeastOnce();
        expect(mockParticipantDao.findParticipantsForObject("TASK", taskId)).andReturn(partList);

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
        assertEquals(TaskConstants.STATE_ACTIVE, found.getStatus());
        assertEquals("details", found.getDetails());
        assertEquals(Integer.valueOf(25), found.getPercentComplete());
        assertEquals(1, found.getAvailableOutcomes().size());
        assertEquals("TestOutcome", found.getOutcomeName());

        TaskOutcome taskOutcome = found.getAvailableOutcomes().get(0);

        assertEquals("formValueId", taskOutcome.getName());
        assertEquals("formValueName", taskOutcome.getDescription());

        assertEquals(partList, found.getParticipants());


    }

    @Test
    public void claimTask() throws Exception
    {
        String user = "user";
        Long taskId = 500L;

        // claim task
        mockTaskService.claim(String.valueOf(taskId), user);

        replayAll();

        try
        {
            unit.claimTask(taskId, user);
        } catch (Exception e)
        {
            //expected so pass
        }

        verifyAll();
    }

    @Test
    public void unclaimTask() throws Exception
    {
        Long taskId = 500L;

        // unclaim task
        mockTaskService.unclaim(String.valueOf(taskId));

        replayAll();

        try
        {
            unit.unclaimTask(taskId);
        } catch (Exception e)
        {
            //expected so pass
        }

        verifyAll();
    }

    @Test
    public void deleteProcessInstance() throws Exception
    {
        Long taskId = 500L;
        Date dueDate = new Date();
        Date started = new Date();
        Date ended = new Date();
        long taskDuration = 9876543L;
        String deleteReason = "TERMINATED";
        String assignee = "assignee";
        int activitiPriority = 50;
        String title = "task Title";
        String userId = "userId";
        String processId = "500";
        String processName = "processName";
        Long objectId = 250L;
        String objectType = "objectType";
        String objectName = "objectName";
        String taskDefKey = "taskDefinitionKey";
        Map<String, Object> pvars = new HashMap<>();
        pvars.put("OBJECT_ID", objectId);
        pvars.put("OBJECT_TYPE", objectType);
        pvars.put("OBJECT_NAME", objectName);

        Map<String, Object> taskLocalVars = new HashMap<>();
        taskLocalVars.put("START_DATE", new Date());
        taskLocalVars.put("PERCENT_COMPLETE", 75);
        taskLocalVars.put("DETAILS", "task details");

        String ipAddress = "ipAddress";

        List<AcmParticipant> partList = new ArrayList<>();
        partList.add(new AcmParticipant());

        Capture<AcmApplicationTaskEvent> capturedEvent = new Capture<>();

        mockTaskEventPublisher.publishTaskEvent(capture(capturedEvent));

        expect(mockRuntimeService.createProcessInstanceQuery()).andReturn(mockProcessInstanceQuery);
        expect(mockProcessInstanceQuery.processInstanceId(processId)).andReturn(mockProcessInstanceQuery);
        expect(mockProcessInstanceQuery.singleResult()).andReturn(mockProcessInstance);
        expect(mockAuthentication.getName()).andReturn(userId).atLeastOnce();

        expect(mockRuntimeService.getVariable(processId, TaskConstants.VARIABLE_NAME_OBJECT_ID)).andReturn(objectId);

        mockRuntimeService.deleteProcessInstance(processId, deleteReason);

        expect(mockHistoryService.createHistoricTaskInstanceQuery()).andReturn(mockHistoricTaskInstanceQuery);

        expect(mockHistoricTaskInstanceQuery.processInstanceId(processId)).andReturn(mockHistoricTaskInstanceQuery);
        expect(mockHistoricTaskInstanceQuery.includeProcessVariables()).andReturn(mockHistoricTaskInstanceQuery);
        expect(mockHistoricTaskInstanceQuery.includeTaskLocalVariables()).andReturn(mockHistoricTaskInstanceQuery);
        expect(mockHistoricTaskInstanceQuery.list()).andReturn(Arrays.asList(mockHistoricTaskInstance));

        expect(mockHistoricTaskInstance.getDeleteReason()).andReturn(deleteReason).times(2);

        expect(mockHistoryService.createHistoricProcessInstanceQuery()).andReturn(mockHistoricProcessInstanceQuery);
        expect(mockHistoricProcessInstanceQuery.processInstanceId(processId)).andReturn(mockHistoricProcessInstanceQuery);
        expect(mockHistoricProcessInstanceQuery.singleResult()).andReturn(mockHistoricProcessInstance);
        expect(mockHistoricProcessInstance.getEndTime()).andReturn(ended).times(2);

        expect(mockHistoricTaskInstance.getStartTime()).andReturn(started).times(2);
        expect(mockHistoricTaskInstance.getEndTime()).andReturn(ended).times(4);
        expect(mockHistoricTaskInstance.getId()).andReturn(taskId.toString());
        expect(mockHistoricTaskInstance.getDurationInMillis()).andReturn(taskDuration);
        expect(mockHistoricTaskInstance.getDueDate()).andReturn(dueDate);
        expect(mockHistoricTaskInstance.getPriority()).andReturn(activitiPriority);
        expect(mockHistoricTaskInstance.getName()).andReturn(title);
        expect(mockHistoricTaskInstance.getAssignee()).andReturn(assignee);
        expect(mockHistoricTaskInstance.getTaskLocalVariables()).andReturn(pvars).atLeastOnce();
        expect(mockHistoricTaskInstance.getProcessVariables()).andReturn(pvars).atLeastOnce();
        expect(mockHistoricTaskInstance.getProcessInstanceId()).andReturn(processId).times(3);
        expect(mockHistoricTaskInstance.getProcessDefinitionId()).andReturn(processId);
        expect(mockHistoricTaskInstance.getTaskDefinitionKey()).andReturn(taskDefKey);

        expect(mockRepositoryService.createProcessDefinitionQuery()).andReturn(mockProcessDefinitionQuery);
        expect(mockProcessDefinitionQuery.processDefinitionId(processId)).andReturn(mockProcessDefinitionQuery);
        expect(mockProcessDefinitionQuery.singleResult()).andReturn(mockProcessDefinition);

        expect(mockHistoryService.createHistoricVariableInstanceQuery()).andReturn(mockHistoricVariableInstanceQuery);
        expect(mockHistoricVariableInstanceQuery.taskId(taskId.toString())).andReturn(mockHistoricVariableInstanceQuery);
        expect(mockHistoricVariableInstanceQuery.variableName("DETAILS")).andReturn(mockHistoricVariableInstanceQuery);
        expect(mockHistoricVariableInstanceQuery.singleResult()).andReturn(mockHistoricVariableInstance);
        expect(mockHistoricVariableInstance.getValue()).andReturn("details");

        expect(mockHistoryService.createHistoricVariableInstanceQuery()).andReturn(mockHistoricVariableInstanceQuery);
        expect(mockHistoricVariableInstanceQuery.taskId(taskId.toString())).andReturn(mockHistoricVariableInstanceQuery);
        expect(mockHistoricVariableInstanceQuery.variableName("REWORK_INSTRUCTIONS")).andReturn(mockHistoricVariableInstanceQuery);
        expect(mockHistoricVariableInstanceQuery.singleResult()).andReturn(mockHistoricVariableInstance);
        expect(mockHistoricVariableInstance.getValue()).andReturn(null);

        expect(mockProcessDefinition.getName()).andReturn(processName);


        expect(mockRepositoryService.getBpmnModel(processId)).andReturn(mockBpmnModel);
        expect(mockBpmnModel.getProcesses()).andReturn(Arrays.asList(mockProcess));
        expect(mockProcess.getFlowElementRecursive(taskDefKey)).andReturn(mockFlowElement);
        expect(mockFlowElement.getFormProperties()).andReturn(Arrays.asList(mockFormProperty));
        expect(mockFormProperty.getName()).andReturn("Test Outcome").atLeastOnce();
        expect(mockFormProperty.getId()).andReturn("TestOutcome").atLeastOnce();
        expect(mockFormProperty.getFormValues()).andReturn(Arrays.asList(mockFormValue));
        expect(mockFormValue.getId()).andReturn("formValueId").atLeastOnce();
        expect(mockFormValue.getName()).andReturn("formValueName").atLeastOnce();
        expect(mockParticipantDao.findParticipantsForObject("TASK", taskId)).andReturn(partList);

        replayAll();


        unit.deleteProcessInstance(objectId.toString(), processId, deleteReason, mockAuthentication, ipAddress);

        verifyAll();
    }
}
