package com.armedia.acm.plugins.task.service.impl;

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

import com.armedia.acm.activiti.services.AcmBpmnService;
import com.armedia.acm.camelcontext.arkcase.cmis.ArkCaseCMISConstants;
import com.armedia.acm.core.AcmNotifiableEntity;
import com.armedia.acm.core.AcmObject;
import com.armedia.acm.core.exceptions.AcmAccessControlException;
import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.data.AcmNameDao;
import com.armedia.acm.data.AcmNotificationDao;
import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.data.BuckslipFutureTask;
import com.armedia.acm.objectonverter.ObjectConverter;
import com.armedia.acm.plugins.ecm.dao.AcmContainerDao;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.plugins.ecm.service.impl.EcmFileParticipantService;
import com.armedia.acm.plugins.task.exception.AcmTaskException;
import com.armedia.acm.plugins.task.model.AcmApplicationTaskEvent;
import com.armedia.acm.plugins.task.model.AcmTask;
import com.armedia.acm.plugins.task.model.NumberOfDays;
import com.armedia.acm.plugins.task.model.TaskConstants;
import com.armedia.acm.plugins.task.model.TaskOutcome;
import com.armedia.acm.plugins.task.model.WorkflowHistoryInstance;
import com.armedia.acm.plugins.task.service.TaskDao;
import com.armedia.acm.plugins.task.service.TaskEventPublisher;
import com.armedia.acm.services.dataaccess.service.impl.DataAccessPrivilegeListener;
import com.armedia.acm.services.participants.dao.AcmParticipantDao;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.services.participants.model.ParticipantTypes;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.FormProperty;
import org.activiti.bpmn.model.FormValue;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricIdentityLink;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.impl.bpmn.diagram.ProcessDiagramGenerator;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.Task;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TypedQuery;

import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ActivitiTaskDao extends AcmAbstractDao<AcmTask> implements TaskDao, AcmNotificationDao, AcmNameDao
{
    private RuntimeService activitiRuntimeService;
    private TaskService activitiTaskService;
    private RepositoryService activitiRepositoryService;
    private Logger log = LogManager.getLogger(getClass());
    private HistoryService activitiHistoryService;
    private Map<String, Integer> priorityLevelToNumberMap;
    private Map<String, List<String>> requiredFieldsPerOutcomeMap;
    private UserDao userDao;
    private AcmParticipantDao participantDao;
    private DataAccessPrivilegeListener dataAccessPrivilegeListener;
    private TaskBusinessRule taskBusinessRule;
    private EcmFileService fileService;
    private EcmFileDao fileDao;
    private AcmContainerDao containerFolderDao;
    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;
    private TaskEventPublisher taskEventPublisher;
    private EcmFileParticipantService fileParticipantService;
    private EcmFileService ecmFileService;

    private ObjectConverter objectConverter;
    private AcmBpmnService acmBpmnService;

    @Override
    public List<ProcessInstance> findProcessesByProcessVariables(Map<String, Object> matchProcessVariables)
    {
        ProcessInstanceQuery processInstanceQuery = getActivitiRuntimeService().createProcessInstanceQuery().includeProcessVariables()
                .orderByProcessInstanceId().asc();
        matchProcessVariables.entrySet().stream().forEach(e -> processInstanceQuery.variableValueEquals(e.getKey(), e.getValue()));
        List<ProcessInstance> retval = processInstanceQuery.list();
        return retval;
    }

    @Override
    public boolean isProcessActive(String businessProcessId) throws AcmTaskException
    {
        ProcessInstance pi = getActivitiRuntimeService().createProcessInstanceQuery().processInstanceId(businessProcessId).singleResult();
        return pi != null;
    }

    @Override
    public boolean isWaitingOnReceiveTask(String businessProcessId, String receiveTaskId) throws AcmTaskException
    {
        Execution execution = getActivitiRuntimeService().createExecutionQuery().processInstanceId(businessProcessId)
                .activityId(receiveTaskId).singleResult();
        return execution != null;
    }

    @Override
    public <T> T readProcessVariable(String businessProcessId, String processVariableKey, boolean readFromHistory) throws AcmTaskException
    {
        ProcessInstance pi = getActivitiRuntimeService().createProcessInstanceQuery().processInstanceId(businessProcessId)
                .includeProcessVariables().singleResult();
        if (pi == null)
        {
            if (readFromHistory == true)
            {
                return (T) getProcessVariableFromHistory(businessProcessId, processVariableKey);
            }
            else
            {
                throw new AcmTaskException(
                        String.format("Can't get process variable %s for business process id %s", processVariableKey, businessProcessId));
            }
        }
        return (T) pi.getProcessVariables().get(processVariableKey);
    }

    @Override
    public void writeProcessVariable(String businessProcessId, String processVariableKey, Object processVariableValue)
            throws AcmTaskException
    {
        getActivitiRuntimeService().setVariable(businessProcessId, processVariableKey, processVariableValue);
    }

    @Override
    public void signalTask(String processInstanceId, String receiveTaskId) throws AcmTaskException
    {
        Execution execution = getActivitiRuntimeService().createExecutionQuery().processInstanceId(processInstanceId)
                .activityId(receiveTaskId).singleResult();
        if (execution != null)
        {
            getActivitiRuntimeService().signal(execution.getId());
        }
    }

    @Override
    public void messageTask(String taskId, String messageName) throws AcmTaskException
    {
        Task task = getActivitiTaskService().createTaskQuery().taskId(taskId).singleResult();
        if (task != null)
        {
            getActivitiRuntimeService().messageEventReceived(messageName, task.getExecutionId());
        }

    }

    @Override
    @Transactional
    public AcmTask createAdHocTask(AcmTask in) throws AcmTaskException, AcmUserActionFailedException, AcmCreateObjectFailedException
    {
        Task activitiTask = getActivitiTaskService().newTask();

        // This could set assignee and owning group if not specified earlier
        getTaskBusinessRule().applyRules(in);

        AcmTask out = updateExistingActivitiTask(in, activitiTask);
        if (out.getStatus().equalsIgnoreCase(TaskConstants.STATE_CLOSED))
        {
            String taskId = String.valueOf(out.getId());
            getActivitiTaskService().complete(taskId);
        }
        AcmContainer container = getFileService().getOrCreateContainer(out.getObjectType(), out.getId());
        out.setContainer(container);
        return out;
    }

    @Override
    @Transactional
    public AcmTask save(AcmTask in) throws AcmTaskException
    {
        Task activitiTask = getActivitiTaskService().createTaskQuery().taskId(in.getTaskId().toString()).singleResult();
        if (activitiTask != null)
        {
            AcmTask acmTask = updateExistingActivitiTask(in, activitiTask);
            return acmTask;
        }

        // task must have been completed. Try finding the historic task; but historical tasks can't be updated, so
        // even if we find it we have to throw an exception
        {
            HistoricTaskInstance hti = getActivitiHistoryService().createHistoricTaskInstanceQuery().taskId(in.getTaskId().toString())
                    .singleResult();

            if (hti == null)
            {
                // no such task!
                throw new AcmTaskException("No such task with id '" + in.getTaskId() + "'");
            }
            else
            {
                // Update participants and privileges
                getParticipantDao().saveParticipants(in.getParticipants());
                throw new AcmTaskException(
                        "Task with id '" + in.getTaskId() + "' has already been completed and so " + "it cannot be updated.");
            }
        }

    }

    public AcmTask updateExistingActivitiTask(AcmTask in, Task activitiTask) throws AcmTaskException
    {
        activitiTask.setAssignee(in.getAssignee());
        activitiTask.setOwner(in.getOwner());
        Integer activitiPriority = activitiPriorityFromAcmPriority(in.getPriority());
        activitiTask.setPriority(activitiPriority);
        activitiTask.setDueDate(in.getDueDate());
        activitiTask.setName(in.getTitle());

        List<AcmParticipant> originalTaskParticipants = getParticipantDao()
                .findParticipantsForObject("TASK", in.getTaskId());

        try
        {
            getActivitiTaskService().saveTask(activitiTask);

            // If start date is not provided, set start date as creation date
            if (in.getTaskStartDate() == null)
            {
                in.setTaskStartDate(activitiTask.getCreateTime());
            }
            if (in.getDocumentsToReview() != null)
            {
                for (EcmFile file : in.getDocumentsToReview())
                {
                    getActivitiTaskService().setVariableLocal(activitiTask.getId(), TaskConstants.VARIABLE_NAME_PDF_RENDITION_ID,
                            file.getId().toString());
                }

            }
            getActivitiTaskService().setVariableLocal(activitiTask.getId(), TaskConstants.VARIABLE_NAME_OBJECT_TYPE,
                    in.getAttachedToObjectType());
            getActivitiTaskService().setVariableLocal(activitiTask.getId(), TaskConstants.VARIABLE_NAME_OBJECT_ID,
                    in.getAttachedToObjectId());
            if (in.getAttachedToObjectType() != null)
            {
                getActivitiTaskService().setVariableLocal(activitiTask.getId(), in.getAttachedToObjectType(), in.getAttachedToObjectId());
            }

            getActivitiTaskService().setVariableLocal(activitiTask.getId(), TaskConstants.VARIABLE_NAME_OBJECT_NAME,
                    in.getAttachedToObjectName());
            getActivitiTaskService().setVariableLocal(activitiTask.getId(), TaskConstants.VARIABLE_NAME_START_DATE, in.getTaskStartDate());
            getActivitiTaskService().setVariableLocal(activitiTask.getId(), TaskConstants.VARIABLE_NAME_PERCENT_COMPLETE,
                    in.getPercentComplete());
            getActivitiTaskService().setVariableLocal(activitiTask.getId(), TaskConstants.VARIABLE_NAME_DETAILS, in.getDetails());

            getActivitiTaskService().setVariableLocal(activitiTask.getId(), TaskConstants.VARIABLE_NAME_PARENT_OBJECT_ID,
                    in.getParentObjectId());
            getActivitiTaskService().setVariableLocal(activitiTask.getId(), TaskConstants.VARIABLE_NAME_PARENT_OBJECT_TYPE,
                    in.getParentObjectType());
            getActivitiTaskService().setVariableLocal(activitiTask.getId(), TaskConstants.VARIABLE_NAME_PARENT_OBJECT_NAME,
                    in.getParentObjectName());
            getActivitiTaskService().setVariableLocal(activitiTask.getId(), TaskConstants.VARIABLE_NAME_PARENT_OBJECT_TITLE,
                    in.getParentObjectTitle());
            getActivitiTaskService().setVariableLocal(activitiTask.getId(), TaskConstants.VARIABLE_NAME_TASK_TYPE, in.getType());

            getActivitiTaskService().setVariableLocal(activitiTask.getId(), TaskConstants.VARIABLE_NAME_REWORK_INSTRUCTIONS,
                    in.getReworkInstructions());

            getActivitiTaskService().setVariableLocal(activitiTask.getId(), TaskConstants.VARIABLE_NAME_LEGACY_SYSTEM_ID,
                    in.getLegacySystemId());

            if (in.getTaskOutcome() != null)
            {
                if (in.getTaskOutcome().getName() != null && in.getTaskOutcome().getName().equals("SEND_FOR_REWORK"))
                {
                    getActivitiRuntimeService().setVariable(activitiTask.getProcessInstanceId(),
                            TaskConstants.VARIABLE_NAME_REWORK_INSTRUCTIONS, in.getReworkInstructions());
                }
                else if (in.getTaskOutcome().getName() != null && !in.getTaskOutcome().getName().equals("SEND_FOR_REWORK"))
                {
                    getActivitiRuntimeService().setVariable(activitiTask.getProcessInstanceId(),
                            TaskConstants.VARIABLE_NAME_REWORK_INSTRUCTIONS, null);
                }
                getActivitiTaskService().setVariableLocal(activitiTask.getId(), TaskConstants.VARIABLE_NAME_OUTCOME,
                        in.getTaskOutcome().getName());
            }

            getActivitiTaskService().setVariable(activitiTask.getId(), TaskConstants.VARIABLE_NAME_REQUEST_TYPE,
                    in.getWorkflowRequestType());

            if (in.isBuckslipTask())
            {
                getActivitiTaskService().setVariable(
                        activitiTask.getId(),
                        TaskConstants.VARIABLE_NAME_BUCKSLIP_FUTURE_TASKS,
                        getObjectConverter().getJsonMarshaller().marshal(in.getBuckslipFutureTasks()));
            }

            in.setTaskId(Long.valueOf(activitiTask.getId()));
            in.setCreateDate(activitiTask.getCreateTime());

            // AFDP-1876 save the assignee for the next task in the process to process-level variables. The next
            // assignee is to support business processes where the current assignee of a task can select the assignee
            // for the next task. This feature was added originally for the DoD Joint Staff EDTRM project.
            getActivitiTaskService().setVariable(activitiTask.getId(), TaskConstants.VARIABLE_NAME_NEXT_ASSIGNEE, in.getNextAssignee());

            // make sure an assignee participant is there, so the right data access can be set on the assignee...
            // activiti has to control the assignee, not the assignment rules.
            ensureCorrectAssigneeInParticipants(in);

            // to ensure that the participants for new and updated ad-hoc tasks are visible to the client right away, we
            // have to apply the assignment and data access control rules right here, inline with the save operation.
            // Tasks generated or updated by the Activiti engine will have participants set by a specialized
            // Camel flow.

            try
            {
                getDataAccessPrivilegeListener().applyAssignmentAndAccessRules(in);
            }
            catch (AcmAccessControlException e)
            {
                log.error("Failed to apply assignment and access rules while updating task", e);
            }

            // Now we have to check the assignee again, to be sure the Activiti task assignee is the "assignee"
            // participant. I know we're calling the same method twice!, to overwrite any changes the rules make to the
            // assignee... In short, Activiti controls the task assignee, not the assignment rules.
            ensureCorrectAssigneeInParticipants(in); // there's a good reason we call this again, see above

            // the rules (or the user) may have removed some participants. We want to delete all participants other
            // than the ones we just now validated.
            getParticipantDao().removeAllOtherParticipantsForObject(TaskConstants.OBJECT_TYPE, in.getTaskId(), in.getParticipants());
            in.setParticipants(getParticipantDao().saveParticipants(in.getParticipants()));

            // Add any candidate Groups from the adhoc task to the activiti task.
            if (in.getCandidateGroups() != null && !in.getCandidateGroups().isEmpty())
            {
                List<String> candidateGroupList = in.getCandidateGroups();
                for (String group : candidateGroupList)
                {
                    List<String> candidateGroups = findCandidateGroups(activitiTask.getId());
                    if (candidateGroups != null && !candidateGroups.contains(group))
                    {
                        getActivitiTaskService().addCandidateGroup(activitiTask.getId(), group);
                    }
                }
            }
            AcmContainer container = null;
            try
            {
                container = fileService.getOrCreateContainer(TaskConstants.OBJECT_TYPE, in.getId(),
                        ArkCaseCMISConstants.DEFAULT_CMIS_REPOSITORY_ID);

                getFileParticipantService().inheritParticipantsFromAssignedObject(in.getParticipants(),
                        originalTaskParticipants, container, in.getRestricted());
            }
            catch (AcmCreateObjectFailedException | AcmUserActionFailedException e)
            {
                log.error("Can not create container folder for TASK with ID: [{}]", in.getId());
            }
            in.setContainer(container);

            return in;
        }
        catch (ActivitiException e)
        {
            throw new AcmTaskException(e.getMessage(), e);
        }
    }

    @Override
    public void ensureCorrectAssigneeInParticipants(AcmTask in)
    {
        boolean assigneeFound = false;

        if (in.getParticipants() != null)
        {

            List<AcmParticipant> participantsToRemove = new ArrayList<>();

            for (AcmParticipant ap : in.getParticipants())
            {
                if (ParticipantTypes.ASSIGNEE.equals(ap.getParticipantType()))
                {
                    if (in.getAssignee() == null)
                    {
                        // task has no assignee so we need to remove this participant
                        participantsToRemove.add(ap);
                    }
                    else
                    {
                        assigneeFound = true;
                        if (ap.getParticipantLdapId() == null || !ap.getParticipantLdapId().equalsIgnoreCase(in.getAssignee()))
                        {
                            ap.setParticipantLdapId(in.getAssignee());
                            ap.setReplaceChildrenParticipant(true);
                            break;
                        }
                    }

                }
            }

            if (participantsToRemove.size() > 0)
            {
                participantsToRemove.forEach(acmParticipant -> in.getParticipants().remove(acmParticipant));
                participantsToRemove.clear();
            }
        }

        if (in.getParticipants() == null)
        {
            in.setParticipants(new ArrayList<>());
        }

        if (!assigneeFound && in.getAssignee() != null)
        {
            AcmParticipant assignee = new AcmParticipant();
            assignee.setParticipantLdapId(in.getAssignee());
            assignee.setParticipantType(ParticipantTypes.ASSIGNEE);
            assignee.setObjectId(in.getTaskId());
            assignee.setObjectType(TaskConstants.OBJECT_TYPE);
            assignee.setReplaceChildrenParticipant(true);

            in.getParticipants().add(assignee);
        }

        List<String> candidateGroups = in.getCandidateGroups();

        // Add candidate group as collaborators
        for (String group : candidateGroups)
        {
            Boolean found = false;
            for (AcmParticipant participant : in.getParticipants())
            {
                if (participant.getParticipantLdapId().equals(group))
                {
                    found = true;
                    break;
                }
            }
            if (!found)
            {
                AcmParticipant participant = new AcmParticipant();
                participant.setParticipantType("collaborator group");
                participant.setParticipantLdapId(group);
                in.getParticipants().add(participant);
            }
        }
    }

    @Override
    @Transactional
    public AcmTask completeTask(Principal userThatCompletedTheTask, Long taskId) throws AcmTaskException
    {
        return completeTask(userThatCompletedTheTask, taskId, null, null);
    }

    @Override
    public AcmTask completeTask(Principal userThatCompletedTheTask, Long taskId, String outcomePropertyName, String outcomeId)
            throws AcmTaskException
    {

        verifyCompleteTaskArgs(userThatCompletedTheTask, taskId);

        String user = userThatCompletedTheTask.getName();

        log.info("Completing task '{}' for user '{}'", taskId, user);

        String strTaskId = String.valueOf(taskId);

        Task existingTask = getActivitiTaskService().createTaskQuery().includeProcessVariables().includeTaskLocalVariables()
                .taskId(strTaskId).singleResult();

        verifyTaskExists(taskId, existingTask);

        verifyUserIsTheAssignee(taskId, user, existingTask);

        AcmTask retval = acmTaskFromActivitiTask(existingTask);
        retval = completeTask(retval, user, outcomePropertyName, outcomeId);

        // Task participant privileges updated immediately, not to wait for DAC batch update
        try
        {
            getDataAccessPrivilegeListener().applyAssignmentAndAccessRules(retval);
        }
        catch (AcmAccessControlException e)
        {
            log.error("Failed to apply assignment and access rules while completing task", e);
        }

        return retval;
    }

    @Override
    @Transactional
    public AcmTask deleteTask(Principal userThatCompletedTheTask, Long taskId) throws AcmTaskException
    {
        verifyCompleteTaskArgs(userThatCompletedTheTask, taskId);

        String user = userThatCompletedTheTask.getName();

        log.info("Deleting task '{}' for user '{}'", taskId, user);

        String strTaskId = String.valueOf(taskId);

        Task existingTask = getActivitiTaskService().createTaskQuery().includeProcessVariables().includeTaskLocalVariables()
                .taskId(strTaskId).singleResult();

        verifyTaskExists(taskId, existingTask);

        verifyUserIsTheAssignee(taskId, user, existingTask);

        AcmTask retval = acmTaskFromActivitiTask(existingTask);
        retval = deleteTask(retval, user, null);

        // Task participant privileges updated immediately, not to wait for DAC batch update
        try
        {
            getDataAccessPrivilegeListener().applyAssignmentAndAccessRules(retval);
        }
        catch (AcmAccessControlException e)
        {
            log.error("Failed to apply assignment and access rules while deleting task", e);
        }

        return retval;
    }

    protected void verifyCompleteTaskArgs(Principal userThatCompletedTheTask, Long taskId)
    {
        if (userThatCompletedTheTask == null || taskId == null)
        {
            throw new IllegalArgumentException("userThatCompletedTheTask and taskId must be specified");
        }
    }

    protected void verifyUserIsTheAssignee(Long taskId, String user, Task existingTask) throws AcmTaskException
    {
        if (existingTask.getAssignee() == null || !existingTask.getAssignee().equals(user))
        {
            throw new AcmTaskException("Task '" + taskId + "' can only be closed by the assignee.");
        }
    }

    protected void verifyTaskExists(Long taskId, Task existingTask) throws AcmTaskException
    {
        if (existingTask == null)
        {
            throw new AcmTaskException("Task '" + taskId + "' does not exist or has already been completed.");
        }
    }

    @Override
    public List<AcmTask> tasksForUser(String user)
    {
        log.info("Finding all tasks for user '{}'", user);

        List<AcmTask> retval = new ArrayList<>();

        List<Task> activitiTasks = getActivitiTaskService().createTaskQuery().taskAssignee(user).includeProcessVariables()
                .includeTaskLocalVariables().orderByDueDate().desc().list();

        if (activitiTasks != null)
        {
            log.debug("Found '{}' tasks for user '{}'", activitiTasks.size(), user);

            for (Task activitiTask : activitiTasks)
            {
                AcmTask acmTask = acmTaskFromActivitiTask(activitiTask);

                retval.add(acmTask);
            }
        }

        return retval;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<Long> findTasksIdsForParentObjectIdAndParentObjectType(String parentObjectType, Long parentObjectId)
    {
        List<ProcessInstance> processes = getActivitiRuntimeService().createProcessInstanceQuery()
                .variableValueEquals(TaskConstants.VARIABLE_NAME_PARENT_OBJECT_TYPE, parentObjectType)
                .variableValueEquals(TaskConstants.VARIABLE_NAME_PARENT_OBJECT_ID, parentObjectId).list();

        Stream<Task> activitiWorkflowTasksStream = processes.stream()
                .map(it -> getActivitiTaskService().createTaskQuery()
                        .processInstanceId(it.getProcessInstanceId())
                        .singleResult());

        Stream<Task> adhochTasksStream = getActivitiTaskService().createTaskQuery()
                .taskVariableValueEquals(TaskConstants.VARIABLE_NAME_PARENT_OBJECT_TYPE, parentObjectType)
                .taskVariableValueEquals(TaskConstants.VARIABLE_NAME_PARENT_OBJECT_ID, parentObjectId)
                .list().stream();

        List<Long> taskIds = Stream.concat(activitiWorkflowTasksStream, adhochTasksStream)
                .map(it -> Long.valueOf(it.getId()))
                .collect(Collectors.toList());
        log.debug("Found [{}] tasks for object [{}:{}]", taskIds.size(), parentObjectType, parentObjectId);
        return taskIds;
    }

    public List<AcmTask> findByVariableForObjectTypeAndId(String name, String value, String objectType, Long objectId)
    {
        List<Task> activitiTasks = getActivitiTaskService().createTaskQuery()
                .includeProcessVariables()
                .taskVariableValueEquals(TaskConstants.VARIABLE_NAME_PARENT_OBJECT_TYPE, objectType)
                .taskVariableValueEquals(TaskConstants.VARIABLE_NAME_PARENT_OBJECT_ID, objectId)
                .taskVariableValueEquals(name, value).list();

        return activitiTasks.stream()
                .map(it -> acmTaskFromActivitiTask(it))
                .collect(Collectors.toList());
    }

    @Override
    public List<AcmTask> allTasks()
    {
        log.info("Finding all tasks for all users'");

        List<AcmTask> retval = new ArrayList<>();

        List<Task> activitiTasks = getActivitiTaskService().createTaskQuery().includeProcessVariables().includeTaskLocalVariables()
                .orderByDueDate().desc().list();

        if (activitiTasks != null)
        {

            log.debug("Found '{}' tasks for all users", activitiTasks.size());

            for (Task activitiTask : activitiTasks)
            {
                AcmTask acmTask = acmTaskFromActivitiTask(activitiTask);

                retval.add(acmTask);
            }
        }

        return retval;
    }

    @Override
    public List<AcmTask> pastDueTasks()
    {
        log.info("Finding all tasks for all users that due date was before today");

        List<AcmTask> retval = new ArrayList<>();

        List<Task> activitiTasks = getActivitiTaskService().createTaskQuery().includeProcessVariables().includeTaskLocalVariables()
                .dueBefore(new Date()).list();

        if (activitiTasks != null)
        {

            log.debug("Found '{}' tasks for all users with past due date", activitiTasks.size());

            for (Task activitiTask : activitiTasks)
            {
                AcmTask acmTask = acmTaskFromActivitiTask(activitiTask);

                retval.add(acmTask);
            }
        }

        return retval;
    }

    @Override
    @Transactional
    public void claimTask(Long taskId, String userId) throws AcmTaskException
    {
        if (taskId != null && userId != null)
        {
            try
            {
                getActivitiTaskService().claim(String.valueOf(taskId), userId);
            }
            catch (ActivitiException e)
            {
                log.info("Claiming task failed for task with ID: [{}]", taskId);
                throw new AcmTaskException(e.getMessage(), e);
            }
        }
    }

    @Override
    @Transactional
    public void unclaimTask(Long taskId) throws AcmTaskException
    {
        if (taskId != null)
        {
            try
            {
                getActivitiTaskService().unclaim(String.valueOf(taskId));
            }
            catch (ActivitiException e)
            {
                log.info("Unclaiming task failed for task with ID: [{}]", taskId);
                throw new AcmTaskException(e.getMessage(), e);
            }
        }
    }

    @Override
    @Transactional
    public void deleteProcessInstance(String parentId, String processId, String deleteReason, Authentication authentication,
                                      String ipAddress) throws AcmTaskException
    {
        if (processId != null)
        {
            try
            {
                // get the process instance
                ProcessInstance processInstance = getActivitiRuntimeService().createProcessInstanceQuery().processInstanceId(processId)
                        .singleResult();

                if (processInstance != null)
                {
                    // validate against provided parentId
                    Long objectId = (Long) getActivitiRuntimeService().getVariable(processId, TaskConstants.VARIABLE_NAME_OBJECT_ID);
                    if (objectId == null)
                    {
                        throw new AcmTaskException("No parent object ID is associated with the process instance ID " + processId);
                    }
                    if (objectId != null && objectId.toString().equals(parentId))
                    {
                        log.info("provided ID [{}] and object ID from process instance match [{}]", objectId, parentId);

                        // EDTRM-670 - delete the process instance, all tasks should be marked "TERMINATED" instead of
                        // "CLOSED"
                        // set deleteReason to "TERMINATED" so that we can utilize it to set the status of tasks
                        // belonging to a "TERMINATED"
                        // process as "TERMINATED" from historic task instance

                        deleteReason = TaskConstants.STATE_TERMINATED;
                        getActivitiRuntimeService().deleteProcessInstance(processId, deleteReason);

                        // retrieve historic task instances
                        // update the status of completed task to "TERMINATED"
                        List<HistoricTaskInstance> htis = getActivitiHistoryService().createHistoricTaskInstanceQuery()
                                .processInstanceId(processId).includeProcessVariables().includeTaskLocalVariables().list();

                        for (HistoricTaskInstance hti : htis)
                        {
                            AcmTask acmTask = acmTaskFromHistoricActivitiTask(hti);
                            if (acmTask != null)
                            {
                                log.info("Task with id [{}] TERMINATED due to deletion of process instance with ID [{}]", acmTask.getId(),
                                        processId);
                                AcmApplicationTaskEvent event = new AcmApplicationTaskEvent(acmTask, "terminate", authentication.getName(),
                                        true, ipAddress);
                                getTaskEventPublisher().publishTaskEvent(event);
                            }
                        }
                    }
                    else
                    {
                        throw new AcmTaskException("Process cannot be deleted as supplied parentId : " + parentId
                                + "doesn't match with process instance object Id : " + objectId);
                    }
                }
            }
            catch (ActivitiException e)
            {
                log.info("Deleting process instance failed for process ID: [{}]", processId);
                throw new AcmTaskException(e.getMessage(), e);
            }
        }
    }

    @Override
    public List<AcmTask> dueSpecificDateTasks(NumberOfDays numberOfDaysFromToday)
    {
        log.info(String.format("Finding all tasks for all users which due date is until %s from today", numberOfDaysFromToday.getnDays()));

        List<AcmTask> retval = new ArrayList<>();

        List<Task> activitiTasks = getActivitiTaskService().createTaskQuery().includeProcessVariables().includeTaskLocalVariables()
                .dueAfter(new Date()).dueBefore(shiftDateFromToday(numberOfDaysFromToday.getNumOfDays())).list();

        if (activitiTasks != null)
        {
            log.debug("Found '{}' tasks for all users which due date is between today and {} from today", activitiTasks.size(),
                    numberOfDaysFromToday.getnDays());

            for (Task activitiTask : activitiTasks)
            {
                AcmTask acmTask = acmTaskFromActivitiTask(activitiTask);
                retval.add(acmTask);
            }
        }

        return retval;
    }

    @Override
    public AcmTask findById(Long taskId) throws AcmTaskException
    {
        log.info("Finding task with ID '{}'", taskId);
        AcmTask retval;

        Task activitiTask = getActivitiTaskService().createTaskQuery().taskId(String.valueOf(taskId)).includeProcessVariables()
                .includeTaskLocalVariables().singleResult();
        if (activitiTask != null)
        {
            retval = acmTaskFromActivitiTask(activitiTask);
            return retval;
        }
        else
        {
            HistoricTaskInstance hti = getActivitiHistoryService().createHistoricTaskInstanceQuery().taskId(String.valueOf(taskId))
                    .includeProcessVariables().includeTaskLocalVariables().singleResult();

            if (hti != null)
            {
                retval = acmTaskFromHistoricActivitiTask(hti);

                return retval;
            }
        }

        throw new AcmTaskException("Task with ID '" + taskId + "' does not exist.");

    }

    @Override
    public List<WorkflowHistoryInstance> getWorkflowHistory(String id, boolean adhoc)
    {

        List<WorkflowHistoryInstance> retval = new ArrayList<>();

        HistoricTaskInstanceQuery query;

        // due to an Activiti issue, we have to retrieve the task local issues separately for each task instance.
        // if we ask for them at the query level (via "includeTaskLocalVariables") Activiti basically returns one big
        // map, instead of one map per historic task instance. Obviously the one big map will contain correct values
        // only for the last task retrieved.
        if (!adhoc)
        {
            query = getActivitiHistoryService().createHistoricTaskInstanceQuery().processInstanceId(id).includeTaskLocalVariables()
                    .orderByHistoricTaskInstanceEndTime().asc();
        }
        else
        {
            query = getActivitiHistoryService().createHistoricTaskInstanceQuery().taskId(id).includeTaskLocalVariables()
                    .orderByHistoricTaskInstanceEndTime().asc();
        }

        if (null != query)
        {
            List<HistoricTaskInstance> historicTaskInstances = query.list();

            if (null != historicTaskInstances && !historicTaskInstances.isEmpty())
            {
                for (HistoricTaskInstance historicTaskInstance : historicTaskInstances)
                {

                    String taskId = historicTaskInstance.getId();

                    // TODO: For now Role is empty. This is agreed with Dave. Once we have that information, we should
                    // add it here.
                    String role = "";
                    Date startDate = historicTaskInstance.getStartTime();
                    Date endDate = historicTaskInstance.getEndTime();

                    String status = findTaskStatus(historicTaskInstance);

                    // for purposes of the workflow history, the task outcome should override the status.
                    Map<String, Object> localVariables = historicTaskInstance.getTaskLocalVariables();

                    if (null != localVariables && localVariables.containsKey("outcome"))
                    {
                        String outcome = (String) localVariables.get(TaskConstants.VARIABLE_NAME_OUTCOME);
                        status = WordUtils.capitalizeFully(outcome.replaceAll("_", " "));
                    }

                    WorkflowHistoryInstance workflowHistoryInstance = new WorkflowHistoryInstance();

                    workflowHistoryInstance.setId(taskId);

                    workflowHistoryInstance.setRole(role);
                    workflowHistoryInstance.setStatus(status);
                    workflowHistoryInstance.setStartDate(startDate);
                    workflowHistoryInstance.setEndDate(endDate);

                    if (historicTaskInstance.getAssignee() != null)
                    {
                        AcmUser user = getUserDao().findByUserId(historicTaskInstance.getAssignee());
                        if (user != null)
                        {
                            String participant = user.getFullName();
                            workflowHistoryInstance.setParticipant(participant);
                        }
                        else
                        {
                            workflowHistoryInstance.setParticipant("[unknown]");
                        }
                    }

                    retval.add(workflowHistoryInstance);
                }
            }
        }

        return retval;
    }

    protected String findTaskStatus(HistoricTaskInstance historicTaskInstance)
    {
        if (isTaskTerminated(historicTaskInstance))
        {
            return TaskConstants.STATE_TERMINATED;
        }
        else if (isTaskDeleted(historicTaskInstance))
        {
            return TaskConstants.STATE_DELETE;
        }
        if (historicTaskInstance.getEndTime() == null)
        {
            return historicTaskInstance.getAssignee() == null ? TaskConstants.STATE_UNCLAIMED : TaskConstants.STATE_ACTIVE;
        }
        else
        {
            return TaskConstants.STATE_CLOSED;
        }
    }

    private boolean isTaskTerminated(HistoricTaskInstance historicTaskInstance)
    {
        // EDTRM-670 - All tasks should be marked "TERMINATED" instead of "CLOSED"
        // tasks belonging to a "TERMINATED" process will have delete reason set to "TERMINATED"
        if (historicTaskInstance.getDeleteReason() != null && historicTaskInstance.getEndTime() != null
                && historicTaskInstance.getDeleteReason().equals(TaskConstants.STATE_TERMINATED))
        {
            // make a check if the task is ad-hoc or not
            if (historicTaskInstance.getProcessInstanceId() != null)
            {
                HistoricProcessInstance historicProcessInstance = getActivitiHistoryService().createHistoricProcessInstanceQuery()
                        .processInstanceId(historicTaskInstance.getProcessInstanceId()).singleResult();

                // deleted process instance endTime matches terminated tasks endTime to second offset
                if (historicProcessInstance.getEndTime() != null)
                {
                    Date processTerminatedDateTime = DateUtils.round(historicProcessInstance.getEndTime(), Calendar.SECOND);
                    Date taskTerminatedDateTime = DateUtils.round(historicTaskInstance.getEndTime(), Calendar.SECOND);
                    if (processTerminatedDateTime.equals(taskTerminatedDateTime))
                    {
                        return true;
                    }
                }
            }
            return true;
        }
        return false;
    }

    private boolean isTaskDeleted(HistoricTaskInstance historicTaskInstance)
    {
        // EDTRM-763 Tasks related bugs to fix in core V2
        // For adhoc task, the status is DELETE in the tasks grid,
        // but if you click on the task and view it in task module, the state is CLOSED.
        // by default activiti sets deleteReason as "deleted" for deleted tasks
        if (historicTaskInstance.getDeleteReason() != null && historicTaskInstance.getEndTime() != null
                && historicTaskInstance.getDeleteReason().equals(TaskConstants.STATE_DELETED.toLowerCase()))
        {
            return true;
        }
        return false;
    }

    protected String findTaskStatus(HistoricTaskInstance historicTaskInstance, Boolean deleted)
    {
        if (isTaskTerminated(historicTaskInstance))
        {
            return TaskConstants.STATE_TERMINATED;
        }
        if (historicTaskInstance.getEndTime() == null)
        {
            return historicTaskInstance.getAssignee() == null ? TaskConstants.STATE_UNCLAIMED : TaskConstants.STATE_ACTIVE;
        }
        else
        {
            return TaskConstants.STATE_DELETE;
        }
    }

    protected String findTaskStatus(Task task)
    {
        // tasks in ACT_RU_TASK table (where Task objects come from) are active by definition
        // tasks have status unclaimed if assignee is null
        if (task.getAssignee() == null)
        {
            return TaskConstants.STATE_UNCLAIMED;
        }
        return TaskConstants.STATE_ACTIVE;
    }

    @Override
    public List<AcmTask> getTasksModifiedSince(Date lastModified, int start, int pageSize)
    {
        List<AcmTask> retval = new ArrayList<>();

        List<HistoricTaskInstance> tasks = getActivitiHistoryService().createHistoricTaskInstanceQuery().includeProcessVariables()
                .includeTaskLocalVariables().taskCreatedAfter(lastModified).orderByTaskId().asc().listPage(start, pageSize);

        if (tasks != null)
        {
            for (HistoricTaskInstance task : tasks)
            {
                AcmTask active = acmTaskFromHistoricActivitiTask(task);
                retval.add(active);
            }
        }

        return retval;

    }

    protected AcmTask completeTask(AcmTask acmTask, String user, String outcomePropertyName, String outcomeId) throws AcmTaskException
    {
        String strTaskId = String.valueOf(acmTask.getId());
        try
        {
            if (outcomePropertyName != null && outcomeId != null)
            {
                getActivitiTaskService().setVariable(acmTask.getId().toString(), outcomePropertyName, outcomeId);
                getActivitiTaskService().setVariableLocal(strTaskId, TaskConstants.VARIABLE_NAME_OUTCOME, outcomeId);
            }

            getActivitiTaskService().complete(strTaskId);

            HistoricTaskInstance hti = getActivitiHistoryService().createHistoricTaskInstanceQuery().taskId(strTaskId).singleResult();

            // If start date is not provided, set start date as creation date
            if (acmTask.getTaskStartDate() == null)
            {
                acmTask.setTaskStartDate(hti.getStartTime());
            }
            acmTask.setCreateDate(hti.getStartTime());
            acmTask.setTaskFinishedDate(hti.getEndTime());
            acmTask.setTaskDurationInMillis(hti.getDurationInMillis());
            acmTask.setCompleted(true);
            String status = findTaskStatus(hti);
            acmTask.setStatus(status);
            return acmTask;
        }
        catch (ActivitiException e)
        {
            log.error("Could not close task '{}' for user '{}': {}", strTaskId, user, e.getMessage(), e);
            throw new AcmTaskException(e);
        }
    }

    protected AcmTask deleteTask(AcmTask acmTask, String user, String deleteReason) throws AcmTaskException
    {
        String strTaskId = String.valueOf(acmTask.getId());
        try
        {
            getActivitiTaskService().deleteTask(strTaskId, deleteReason);

            HistoricTaskInstance hti = getActivitiHistoryService().createHistoricTaskInstanceQuery().taskId(strTaskId).singleResult();

            // If start date is not provided, set start date as creation date
            if (acmTask.getTaskStartDate() == null)
            {
                acmTask.setTaskStartDate(hti.getStartTime());
            }
            acmTask.setTaskFinishedDate(hti.getEndTime());
            acmTask.setTaskDurationInMillis(hti.getDurationInMillis());
            acmTask.setCompleted(true);
            String status = findTaskStatus(hti, true);
            acmTask.setStatus(status);
            return acmTask;
        }
        catch (ActivitiException e)
        {
            log.error("Could not close task '{}' for user '{}': {}", strTaskId, user, e.getMessage(), e);
            throw new AcmTaskException(e);
        }
    }

    protected AcmTask acmTaskFromHistoricActivitiTask(HistoricTaskInstance hti)
    {
        if (hti == null)
        {
            return null;
        }

        // even active tasks have an entry in the historic task table, so this HistoricTaskInstance may
        // represent an active task
        AcmTask retval;
        retval = new AcmTask();
        // If start date is not provided, set start date as creation date
        if (retval.getTaskStartDate() == null)
        {
            retval.setTaskStartDate(hti.getStartTime());
        }
        retval.setCreateDate(hti.getStartTime());
        retval.setTaskFinishedDate(hti.getEndTime());
        retval.setTaskDurationInMillis(hti.getDurationInMillis());
        retval.setCompleted(hti.getEndTime() != null);

        retval.setTaskId(Long.valueOf(hti.getId()));
        retval.setDueDate(hti.getDueDate());
        String taskPriority = acmPriorityFromActivitiPriority(hti.getPriority());
        retval.setPriority(taskPriority);
        retval.setTitle(hti.getName());
        retval.setAssignee(hti.getAssignee());

        if (retval.isCompleted())
        {
            retval.setContainer(getContainerFolderDao().findByObjectTypeAndIdOrCreate(TaskConstants.OBJECT_TYPE, retval.getTaskId(), null,
                    retval.getTitle()));
        }

        // set Candidate Groups if there are any
        List<String> candidateGroups = findHistoricCandidateGroups(hti.getId());
        retval.setCandidateGroups(candidateGroups);

        if (hti.getProcessVariables() != null)
        {
            retval.setAttachedToObjectId((Long) hti.getProcessVariables().get(TaskConstants.VARIABLE_NAME_OBJECT_ID));
            retval.setAttachedToObjectType((String) hti.getProcessVariables().get(TaskConstants.VARIABLE_NAME_OBJECT_TYPE));
            retval.setAttachedToObjectName((String) hti.getProcessVariables().get(TaskConstants.VARIABLE_NAME_OBJECT_NAME));
            retval.setWorkflowRequestId((Long) hti.getProcessVariables().get(TaskConstants.VARIABLE_NAME_REQUEST_ID));
            retval.setWorkflowRequestType((String) hti.getProcessVariables().get(TaskConstants.VARIABLE_NAME_REQUEST_TYPE));
            if (hti.getProcessVariables().get(TaskConstants.VARIABLE_NAME_PDF_RENDITION_ID) != null)
            {
                if (hti.getProcessVariables().get(TaskConstants.VARIABLE_NAME_PDF_RENDITION_ID).toString().contains(","))
                {
                    retval.setReviewDocumentPdfRenditionId(
                            hti.getProcessVariables().get(TaskConstants.VARIABLE_NAME_PDF_RENDITION_ID).toString());
                }
                else
                {
                    retval.setReviewDocumentPdfRenditionId(getReviewDocumentPdfRenditionIdFromVariables(hti));
                }
            }
            retval.setReviewDocumentFormXmlId((Long) hti.getProcessVariables().get(TaskConstants.VARIABLE_NAME_XML_RENDITION_ID));

            Long parentObjectId = (Long) hti.getProcessVariables().get(TaskConstants.VARIABLE_NAME_PARENT_OBJECT_ID);
            parentObjectId = parentObjectId == null ? retval.getAttachedToObjectId() : parentObjectId;
            retval.setParentObjectId(parentObjectId);

            String parentObjectType = (String) hti.getProcessVariables().get(TaskConstants.VARIABLE_NAME_PARENT_OBJECT_TYPE);
            parentObjectType = parentObjectType == null ? retval.getAttachedToObjectType() : parentObjectType;
            retval.setParentObjectType(parentObjectType);

            String parentObjectName = (String) hti.getProcessVariables().get(TaskConstants.VARIABLE_NAME_PARENT_OBJECT_NAME);
            parentObjectName = parentObjectName == null ? retval.getAttachedToObjectName() : parentObjectName;
            retval.setParentObjectName(parentObjectName);

            retval.setParentObjectTitle((String) hti.getProcessVariables().get(TaskConstants.VARIABLE_NAME_PARENT_OBJECT_TITLE));

            retval.setLegacySystemId((String) hti.getProcessVariables().get(TaskConstants.VARIABLE_NAME_LEGACY_SYSTEM_ID));
        }

        if (hti.getTaskLocalVariables() != null)
        {
            Map<String, Object> taskLocal = hti.getTaskLocalVariables();

            extractTaskLocalVariables(retval, taskLocal);

            // There seems to be an inconsistency when retrieving details from task local variables map
            // especially for tasks inside a subprocces, for e.g. when the subprocess ends, the API seems
            // to retrieve only details from the most recently completed task outside of the subprocess
            // Using HistoricVariableInstance solves this issue and we'll use this until we find any
            // better solution for this issue

            HistoricVariableInstance historicVariableInstance = getActivitiHistoryService().createHistoricVariableInstanceQuery()
                    .taskId(retval.getId().toString()).variableName(TaskConstants.VARIABLE_NAME_DETAILS).singleResult();
            if (historicVariableInstance != null)
            {
                retval.setDetails((String) historicVariableInstance.getValue());
            }

            historicVariableInstance = getActivitiHistoryService().createHistoricVariableInstanceQuery().taskId(retval.getId().toString())
                    .variableName(TaskConstants.VARIABLE_NAME_REWORK_INSTRUCTIONS).singleResult();
            if (historicVariableInstance != null)
            {
                retval.setReworkInstructions((String) historicVariableInstance.getValue());
            }
        }

        String status = findTaskStatus(hti);
        retval.setStatus(status);

        String pid = hti.getProcessDefinitionId();
        String processInstanceId = hti.getProcessInstanceId();
        String taskDefinitionKey = hti.getTaskDefinitionKey();
        if (pid != null)
        {
            findProcessNameAndTaskOutcomes(retval, pid, processInstanceId, taskDefinitionKey);
            findSelectedTaskOutcome(hti, retval);
        }
        else
        {
            retval.setAdhocTask(true);
        }

        List<AcmParticipant> participants = getParticipantDao().findParticipantsForObject("TASK", retval.getTaskId());
        retval.setParticipants(participants);

        log.trace("Activiti task id '{}' for object type '{}', object id '{}' found for user '{}'", retval.getTaskId(),
                retval.getAttachedToObjectType(), retval.getAttachedToObjectId(), retval.getAssignee());

        return retval;
    }

    @Override
    public void createFolderForTaskEvent(AcmTask task) throws AcmTaskException, AcmCreateObjectFailedException
    {
        log.info("Creating folder for task with ID: {}", task.getId());

        if (task.getContainer() != null && task.getContainer().getFolder() != null)
        {
            return;
        }

        task = getTaskBusinessRule().applyRules(task);

        // if the task doesn't have a container folder, the rules will set the EcmFolderPath. If it does have
        // one, the rules will leave EcmFolderPath null. So only create a folder if EcmFolderPath is not null.

        if (task.getEcmFolderPath() != null)
        {
            String folderId = getFileService().createFolder(task.getEcmFolderPath());

            AcmContainer container = new AcmContainer();
            AcmFolder folder = new AcmFolder();
            folder.setCmisFolderId(folderId);
            folder.setName(EcmFileConstants.CONTAINER_FOLDER_NAME);

            folder.setParticipants(getFileParticipantService().getFolderParticipantsFromAssignedObject(task.getParticipants()));

            container.setFolder(folder);
            container.setContainerObjectType(task.getObjectType());
            container.setContainerObjectId(task.getId());
            container.setContainerObjectTitle(task.getTitle());

            container = getContainerFolderDao().save(container);
            task.setContainer(container);

            log.info("Created folder id '{}' for task with ID {}", folderId, task.getTaskId());
        }

    }

    public List<Task> findAllTasksByVariable(String variableName, String variableValue)
    {
        return getActivitiTaskService()
                .createTaskQuery()
                .taskVariableValueEquals(variableName, variableValue)
                .list();
    }

    public void updateVariableForTask(Task activitiTask, String variableName, String variableValue)
    {
        getActivitiTaskService().setVariableLocal(activitiTask.getId(), variableName, variableValue);
    }

    protected void findSelectedTaskOutcome(HistoricTaskInstance hti, AcmTask retval)
    {
        // check for selected task outcome
        String outcomeId = (String) hti.getTaskLocalVariables().get("outcome");
        if (outcomeId != null)
        {
            for (TaskOutcome availableOutcome : retval.getAvailableOutcomes())
            {
                if (outcomeId.equals(availableOutcome.getName()))
                {
                    retval.setTaskOutcome(availableOutcome);
                    break;
                }
            }
        }
    }

    protected void findProcessNameAndTaskOutcomes(AcmTask retval, String processDefinitionId, String processInstanceId,
                                                  String taskDefinitionKey)
    {
        ProcessDefinition pd = getActivitiRepositoryService().createProcessDefinitionQuery().processDefinitionId(processDefinitionId)
                .singleResult();
        retval.setBusinessProcessName(pd.getName());
        retval.setAdhocTask(false);
        retval.setBusinessProcessId(processInstanceId == null ? null : Long.valueOf(processInstanceId));

        List<FormProperty> formProperties = findFormPropertiesForTask(processDefinitionId, taskDefinitionKey);
        if (formProperties != null)
        {
            for (FormProperty fp : formProperties)
            {
                log.debug("form property name: {}; id: {}", fp.getName(), fp.getId());
                if (fp.getId() != null && fp.getId().endsWith("Outcome"))
                {
                    retval.setOutcomeName(fp.getId());
                    for (FormValue fv : fp.getFormValues())
                    {
                        log.debug("{} = {}", fv.getId(), fv.getName());
                        TaskOutcome outcome = new TaskOutcome();
                        outcome.setName(fv.getId());
                        outcome.setDescription(fv.getName());
                        List<String> fieldsRequiredWhenOutcomeIsChosen = getRequiredFieldsPerOutcomeMap().get(fv.getId());
                        if (fieldsRequiredWhenOutcomeIsChosen != null)
                        {
                            outcome.setFieldsRequiredWhenOutcomeIsChosen(fieldsRequiredWhenOutcomeIsChosen);
                        }
                        retval.getAvailableOutcomes().add(outcome);
                    }
                }
            }
        }
    }

    private List<FormProperty> findFormPropertiesForTask(String processDefinitionId, String taskDefinitionKey)
    {
        BpmnModel model = getActivitiRepositoryService().getBpmnModel(processDefinitionId);

        List<Process> processes = model.getProcesses();

        Process p = processes.get(0);

        FlowElement taskFlowElement = p.getFlowElementRecursive(taskDefinitionKey);

        log.debug("task flow type: {}", taskFlowElement.getClass().getName());

        if (taskFlowElement instanceof UserTask)
        {
            UserTask ut = (UserTask) taskFlowElement;

            List<FormProperty> formProperties = ut.getFormProperties();

            return formProperties;
        }
        return Collections.emptyList();

    }

    public void extractTaskLocalVariables(AcmTask acmTask, Map<String, Object> taskLocal)
    {
        if (acmTask.getAttachedToObjectId() == null)
        {
            Long objectId = (Long) taskLocal.get(TaskConstants.VARIABLE_NAME_OBJECT_ID);
            acmTask.setAttachedToObjectId(objectId);
        }
        if (acmTask.getAttachedToObjectType() == null)
        {
            String objectType = (String) taskLocal.get(TaskConstants.VARIABLE_NAME_OBJECT_TYPE);
            acmTask.setAttachedToObjectType(objectType);
        }
        if (acmTask.getAttachedToObjectName() == null)
        {
            String objectName = (String) taskLocal.get(TaskConstants.VARIABLE_NAME_OBJECT_NAME);
            acmTask.setAttachedToObjectName(objectName);
        }
        if (acmTask.getParentObjectId() == null)
        {
            Long parentObjectId = (Long) taskLocal.get(TaskConstants.VARIABLE_NAME_PARENT_OBJECT_ID);
            acmTask.setParentObjectId(parentObjectId);
        }
        if (acmTask.getParentObjectType() == null)
        {
            String parentObjectType = (String) taskLocal.get(TaskConstants.VARIABLE_NAME_PARENT_OBJECT_TYPE);
            acmTask.setParentObjectType(parentObjectType);
        }
        if (acmTask.getParentObjectName() == null)
        {
            String parentObjectName = (String) taskLocal.get(TaskConstants.VARIABLE_NAME_PARENT_OBJECT_NAME);
            acmTask.setParentObjectName(parentObjectName);
        }
        if (acmTask.getParentObjectTitle() == null)
        {
            String parentObjectTitle = (String) taskLocal.get(TaskConstants.VARIABLE_NAME_PARENT_OBJECT_TITLE);
            acmTask.setParentObjectTitle(parentObjectTitle);
        }
        if (acmTask.getLegacySystemId() == null)
        {
            String legacySystemId = (String) taskLocal.get(TaskConstants.VARIABLE_NAME_LEGACY_SYSTEM_ID);
            acmTask.setLegacySystemId(legacySystemId);
        }
        if (acmTask.getWorkflowRequestType() == null)
        {
            String workflowRequestType = (String) taskLocal.get(TaskConstants.VARIABLE_NAME_REQUEST_TYPE);
            acmTask.setWorkflowRequestType(workflowRequestType);
        }
        if (acmTask.getType() == null)
        {
            String taskType = (String) taskLocal.get(TaskConstants.VARIABLE_NAME_TASK_TYPE);
            acmTask.setType(taskType);
        }
        Date startDate = (Date) taskLocal.get(TaskConstants.VARIABLE_NAME_START_DATE);
        acmTask.setTaskStartDate(startDate);

        Integer percentComplete = (Integer) taskLocal.get(TaskConstants.VARIABLE_NAME_PERCENT_COMPLETE);
        acmTask.setPercentComplete(percentComplete);

        // AFDP-1876 Task next assignee field: for ad-hoc tasks (not part of a business process) the next assignee
        // will be stored here in task local variables. It's hard to imagine why a "next assignee" is needed for an
        // ad-hoc task - where you can just change the assignee directly - but we have this code here for
        // consistency and to avoid surprises. This way, every task can have a next assignee.
        //
        // Note, if the task already has nextAssignee set, then the process-level variables had the next assignee,
        // and we don't want to overwrite it here. So only check the task local variables if the nextAssignee is null.
        if (acmTask.getNextAssignee() == null)
        {
            String nextAssignee = (String) taskLocal.get(TaskConstants.VARIABLE_NAME_NEXT_ASSIGNEE);
            acmTask.setNextAssignee(nextAssignee);
        }

    }

    protected String acmPriorityFromActivitiPriority(int priority)
    {
        return getPriorityLevelToNumberMap().entrySet().stream().filter(acmToActiviti -> acmToActiviti.getValue().equals(priority))
                .map(acmToActiviti -> acmToActiviti.getKey()).findFirst().orElse(TaskConstants.DEFAULT_PRIORITY_WORD);
    }

    protected Integer activitiPriorityFromAcmPriority(String acmPriority)
    {
        return getPriorityLevelToNumberMap().entrySet().stream().filter(acmToActiviti -> acmToActiviti.getKey().equals(acmPriority))
                .mapToInt(acmToActiviti -> acmToActiviti.getValue()).findFirst().orElse(TaskConstants.DEFAULT_PRIORITY);
    }

    protected AcmTask createAcmTask(Task activitiTask, Map<String, Object> processVariables, Map<String, Object> localVariables,
                                    String taskEventName)
    {
        if (activitiTask == null)
        {
            return null;
        }

        AcmTask acmTask = new AcmTask();

        acmTask.setTaskId(Long.valueOf(activitiTask.getId()));
        acmTask.setDueDate(activitiTask.getDueDate());
        String taskPriority = acmPriorityFromActivitiPriority(activitiTask.getPriority());
        acmTask.setPriority(taskPriority);
        acmTask.setTitle(activitiTask.getName());
        acmTask.setAssignee(activitiTask.getAssignee());
        acmTask.setCreateDate(activitiTask.getCreateTime());
        acmTask.setOwner(activitiTask.getOwner());
        acmTask.setContainer(getContainerFolderDao().findByObjectTypeAndIdOrCreate(TaskConstants.OBJECT_TYPE, acmTask.getTaskId(), null,
                acmTask.getTitle()));

        extractProcessVariables(processVariables, acmTask);

        if (localVariables != null)
        {
            extractTaskLocalVariables(acmTask, localVariables);

            String details = (String) localVariables.get(TaskConstants.VARIABLE_NAME_DETAILS);
            details = details != null ? details : (String) processVariables.get(TaskConstants.VARIABLE_NAME_DETAILS);
            acmTask.setDetails(details);

            // only on rework task, first time rework instructions will be fetched from process variables
            // otherwise, rework instruction will be fetched via task local variable
            String reworkInstructions = (String) localVariables.get(TaskConstants.VARIABLE_NAME_REWORK_INSTRUCTIONS);
            if (reworkInstructions != null)
            {
                acmTask.setReworkInstructions(reworkInstructions);
            }

            if (localVariables.get(TaskConstants.VARIABLE_NAME_PDF_RENDITION_ID) != null)
            {
                String docUnderReview = null;
                docUnderReview = String.valueOf(localVariables.get(TaskConstants.VARIABLE_NAME_PDF_RENDITION_ID));
                acmTask.setReviewDocumentPdfRenditionId(docUnderReview);
            }
        }

        // If start date is not provided, set start date as creation date
        if (acmTask.getTaskStartDate() == null)
        {
            if (processVariables.get("taskStartDate") == null) {
                acmTask.setTaskStartDate(activitiTask.getCreateTime());
            } else {
                acmTask.setTaskStartDate((Date) processVariables.get("taskStartDate"));
            }
        }

        String status = findTaskStatus(activitiTask);
        acmTask.setStatus(status);

        String pid = activitiTask.getProcessDefinitionId();
        String processInstanceId = activitiTask.getProcessInstanceId();
        String taskDefinitionKey = activitiTask.getTaskDefinitionKey();

        if (pid != null)
        {
            findProcessNameAndTaskOutcomes(acmTask, pid, processInstanceId, taskDefinitionKey);
        }
        else
        {
            acmTask.setAdhocTask(true);
        }

        // if we lookup candidate groups during event processing for complete or delete events, MySQL throws up.
        // Plus, we don't care about candidate groups for delete or complete events anyway.
        boolean skipCandidateGroups = "complete".equals(taskEventName) || "delete".equals(taskEventName);
        if (!skipCandidateGroups)
        {
            List<String> candidateGroups = findCandidateGroups(activitiTask.getId());
            acmTask.setCandidateGroups(candidateGroups);
        }

        log.trace("Activiti task id '{}' for object type '{}', object id '{}', object number '{}' found for user '{}'", acmTask.getTaskId(),
                acmTask.getAttachedToObjectType(), acmTask.getAttachedToObjectId(), acmTask.getAttachedToObjectName(),
                acmTask.getAssignee());

        List<AcmParticipant> participants = getParticipantDao().findParticipantsForObject("TASK", acmTask.getTaskId());
        acmTask.setParticipants(participants);

        ensureCorrectAssigneeInParticipants(acmTask);

        return acmTask;
    }

    @Override
    public AcmTask acmTaskFromActivitiTask(Task activitiTask)
    {
        return createAcmTask(activitiTask, activitiTask.getProcessVariables(), activitiTask.getTaskLocalVariables(), null);
    }

    @Override
    public AcmTask acmTaskFromActivitiTask(Task activitiTask, Map<String, Object> processVariables, Map<String, Object> localVariables)
    {
        return acmTaskFromActivitiTask(activitiTask, processVariables, localVariables, null);
    }

    @Override
    public AcmTask acmTaskFromActivitiTask(Task activitiTask, Map<String, Object> processVariables, Map<String, Object> localVariables,
                                           String taskEventName)
    {
        return createAcmTask(activitiTask, processVariables, localVariables, taskEventName);
    }

    @Override
    public byte[] getDiagram(Long id) throws AcmTaskException
    {
        byte[] diagram = null;
        if (id != null)
        {
            InputStream inputStream = null;
            try
            {
                Task task = getActivitiTaskService().createTaskQuery().taskId(id.toString()).singleResult();
                BpmnModel model = getActivitiRepositoryService().getBpmnModel(task.getProcessDefinitionId());
                List<String> activeActivityIds = getActivitiRuntimeService().getActiveActivityIds(task.getExecutionId());
                inputStream = ProcessDiagramGenerator.generateDiagram(model, "png", activeActivityIds);
                diagram = IOUtils.toByteArray(inputStream);
            }
            catch (Exception e)
            {
                log.warn("Cannot take diagram for task id=[{}]", id);
            }
            finally
            {
                if (inputStream != null)
                {
                    try
                    {
                        inputStream.close();
                    }
                    catch (IOException e)
                    {
                        log.error("Can't close input stream after generating task diagram image.", e);
                    }
                }
            }
        }

        if (diagram == null)
        {
            log.debug("Diagram for task id = [{}] cannot be retrieved", id);
            throw new AcmTaskException("Diagram for task id = [" + id + "] cannot be retrieved");
        }

        return diagram;
    }

    @Override
    public byte[] getDiagram(String processId) throws AcmTaskException
    {
        byte[] diagram = null;
        if (StringUtils.isNotEmpty(processId))
        {
            InputStream inputStream = null;
            try
            {
                ProcessInstance processInstance = getActivitiRuntimeService().createProcessInstanceQuery().processInstanceId(processId)
                        .singleResult();
                BpmnModel model = getActivitiRepositoryService().getBpmnModel(processInstance.getProcessDefinitionId());
                List<String> activeActivityIds = getActivitiRuntimeService().getActiveActivityIds(processId);
                inputStream = ProcessDiagramGenerator.generateDiagram(model, "png", activeActivityIds);
                diagram = IOUtils.toByteArray(inputStream);
            }
            catch (Exception e)
            {
                log.warn("Cannot take diagram for Process ID=[{}]", processId);
            }
            finally
            {
                if (inputStream != null)
                {
                    try
                    {
                        inputStream.close();
                    }
                    catch (IOException e)
                    {
                        log.error("Can't close input stream after generating task diagram image.", e);
                    }
                }
            }
        }

        if (diagram == null)
        {
            log.debug("Diagram for Process ID = [{}] cannot be retrieved", processId);
            throw new AcmTaskException("Diagram for Process ID = [" + processId + "] cannot be retrieved");
        }

        return diagram;
    }

    @Override
    public AcmTask startBusinessProcess(Map<String, Object> pVars, String businessProcessName)
            throws AcmTaskException, AcmUserActionFailedException, AcmCreateObjectFailedException
    {
        ProcessInstance pi = getAcmBpmnService().startBusinessProcess(businessProcessName, pVars);
        Task activitiTask = getActivitiTaskService().createTaskQuery().processInstanceId(pi.getProcessInstanceId()).singleResult();
        AcmTask createdAcmTask = acmTaskFromActivitiTask(activitiTask, activitiTask.getProcessVariables(),
                activitiTask.getTaskLocalVariables());
        createdAcmTask.setParentObjectId((Long) pVars.get("PARENT_OBJECT_ID"));
        createdAcmTask.setParentObjectType((String) pVars.get("PARENT_OBJECT_TYPE"));

        AcmContainer container = getEcmFileService().getOrCreateContainer(createdAcmTask.getObjectType(),
                createdAcmTask.getTaskId());
        createdAcmTask.setContainer(container);
        getFileParticipantService().inheritParticipantsFromAssignedObject(createdAcmTask.getParticipants(),
                container.getFolder().getParticipants(),
                container, createdAcmTask.getRestricted());

        return createdAcmTask;
    }

    protected List<String> findCandidateGroups(String taskId)
    {
        List<IdentityLink> candidates = getActivitiTaskService().getIdentityLinksForTask(taskId);

        if (candidates != null)
        {
            List<String> retval = candidates.stream().filter(il -> TaskConstants.IDENTITY_LINK_TYPE_CANDIDATE.equals(il.getType()))
                    .filter(il -> il.getGroupId() != null).map(IdentityLink::getGroupId).collect(Collectors.toList());
            return retval;
        }

        return null;
    }

    protected List<String> findHistoricCandidateGroups(String taskId)
    {
        List<HistoricIdentityLink> candidates = getActivitiHistoryService().getHistoricIdentityLinksForTask(taskId);
        if (candidates != null)
        {
            List<String> retval = candidates.stream()
                    .filter(il -> TaskConstants.IDENTITY_LINK_TYPE_CANDIDATE.equalsIgnoreCase(il.getType()))
                    .filter(il -> il.getGroupId() != null).map(HistoricIdentityLink::getGroupId).collect(Collectors.toList());
            return retval;
        }

        return null;
    }

    private Date shiftDateFromToday(int daysFromToday)
    {
        Date nextDate;
        Date today = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        cal.add(Calendar.DATE, daysFromToday);
        nextDate = cal.getTime();
        return nextDate;
    }

    protected void extractProcessVariables(Map<String, Object> processVariables, AcmTask acmTask)
    {
        if (processVariables != null)
        {
            acmTask.setAttachedToObjectId((Long) processVariables.get(TaskConstants.VARIABLE_NAME_OBJECT_ID));
            acmTask.setAttachedToObjectType((String) processVariables.get(TaskConstants.VARIABLE_NAME_OBJECT_TYPE));
            acmTask.setAttachedToObjectName((String) processVariables.get(TaskConstants.VARIABLE_NAME_OBJECT_NAME));

            Long parentObjectId = (Long) processVariables.get(TaskConstants.VARIABLE_NAME_PARENT_OBJECT_ID);
            parentObjectId = parentObjectId == null ? acmTask.getAttachedToObjectId() : parentObjectId;
            acmTask.setParentObjectId(parentObjectId);

            String parentObjectType = (String) processVariables.get(TaskConstants.VARIABLE_NAME_PARENT_OBJECT_TYPE);
            parentObjectType = parentObjectType == null ? acmTask.getAttachedToObjectType() : parentObjectType;
            acmTask.setParentObjectType(parentObjectType);

            String parentObjectName = (String) processVariables.get(TaskConstants.VARIABLE_NAME_PARENT_OBJECT_NAME);
            parentObjectName = parentObjectName == null ? acmTask.getAttachedToObjectName() : parentObjectName;
            acmTask.setParentObjectName(parentObjectName);

            acmTask.setParentObjectTitle((String) processVariables.get(TaskConstants.VARIABLE_NAME_PARENT_OBJECT_TITLE));

            acmTask.setWorkflowRequestId((Long) processVariables.get(TaskConstants.VARIABLE_NAME_REQUEST_ID));
            acmTask.setWorkflowRequestType((String) processVariables.get(TaskConstants.VARIABLE_NAME_REQUEST_TYPE));
            String pdfRenditionId = processVariables.get(TaskConstants.VARIABLE_NAME_PDF_RENDITION_ID) == null ? null
                    : processVariables.get(TaskConstants.VARIABLE_NAME_PDF_RENDITION_ID).toString();
            acmTask.setReviewDocumentPdfRenditionId(pdfRenditionId);
            acmTask.setReviewDocumentFormXmlId((Long) processVariables.get(TaskConstants.VARIABLE_NAME_XML_RENDITION_ID));
            acmTask.setReworkInstructions((String) processVariables.get(TaskConstants.VARIABLE_NAME_REWORK_INSTRUCTIONS));
            acmTask.setTaskStartDate((Date) processVariables.get(TaskConstants.VARIABLE_NAME_START_DATE));

            if (acmTask.getReviewDocumentPdfRenditionId() != null)
            {
                List<Long> docsIds = Stream.of(acmTask.getReviewDocumentPdfRenditionId().split(","))
                        .map(Long::parseLong)
                        .collect(Collectors.toList());
                List<EcmFile> docsUnderReview = fileDao.findByIds(docsIds);
                acmTask.setDocumentsToReview(docsUnderReview);
            }

            // AFDP-1876 if the task is part of a business process, the next assignee will be stored in process
            // variables.
            acmTask.setNextAssignee((String) processVariables.get(TaskConstants.VARIABLE_NAME_NEXT_ASSIGNEE));

            acmTask.setLegacySystemId((String) processVariables.get(TaskConstants.VARIABLE_NAME_LEGACY_SYSTEM_ID));

            acmTask.setPendingStatus((String) processVariables.get(TaskConstants.VARIABLE_NAME_PENDING_STATUS));

            if (processVariables.containsKey(TaskConstants.VARIABLE_NAME_IS_BUCKSLIP_WORKFLOW))
            {
                Boolean isBuckslipWorkflow = (Boolean) processVariables.get(TaskConstants.VARIABLE_NAME_IS_BUCKSLIP_WORKFLOW);
                acmTask.setBuckslipTask(isBuckslipWorkflow);

                if (isBuckslipWorkflow != null && isBuckslipWorkflow.booleanValue())
                {
                    try
                    {
                        List<BuckslipFutureTask> buckslipFutureTasks = findBuckslipFutureTasks(processVariables);
                        acmTask.setBuckslipFutureTasks(buckslipFutureTasks);
                    }
                    catch (IOException e)
                    {
                        log.error("Could not set buckslip future tasks: {}", e.getMessage(), e);
                    }

                    String pastTasks = (String) processVariables.get(TaskConstants.VARIABLE_NAME_PAST_TASKS);
                    acmTask.setBuckslipPastApprovers(pastTasks);
                }
            }
        }
    }

    private List<BuckslipFutureTask> findBuckslipFutureTasks(Map<String, Object> processVariables) throws IOException
    {
        String jsonFutureTasks = (String) processVariables.get(TaskConstants.VARIABLE_NAME_BUCKSLIP_FUTURE_TASKS);
        if (jsonFutureTasks != null && !jsonFutureTasks.trim().isEmpty())
        {
            return getObjectConverter().getJsonUnmarshaller().unmarshallCollection(jsonFutureTasks, List.class, BuckslipFutureTask.class);
        }

        return new ArrayList<>();
    }

    private <T> T getProcessVariableFromHistory(String processId, String processVariable) throws AcmTaskException
    {
        List<HistoricTaskInstance> historyTasks = getActivitiHistoryService()
                .createHistoricTaskInstanceQuery()
                .processInstanceId(processId)
                .includeProcessVariables()
                .includeTaskLocalVariables()
                .list();

        if (historyTasks != null && historyTasks.size() > 0)
        {
            return (T) historyTasks.get(0).getProcessVariables().get(processVariable);
        }

        throw new AcmTaskException(
                String.format("Process variable %s does not exist in the process with Id %s", processVariable, processId));
    }

    protected String getReviewDocumentPdfRenditionIdFromVariables(HistoricTaskInstance hti)
    {
        Object renditionId = hti.getProcessVariables().get(TaskConstants.VARIABLE_NAME_PDF_RENDITION_ID);
        if (renditionId instanceof Long)
        {
            return ((Long) renditionId).toString();
        }
        else
        {
            return (String) renditionId;
        }
    }

    public RuntimeService getActivitiRuntimeService()
    {
        return activitiRuntimeService;
    }

    public void setActivitiRuntimeService(RuntimeService activitiRuntimeService)
    {
        this.activitiRuntimeService = activitiRuntimeService;
    }

    public TaskService getActivitiTaskService()
    {
        return activitiTaskService;
    }

    public void setActivitiTaskService(TaskService activitiTaskService)
    {
        this.activitiTaskService = activitiTaskService;
    }

    public RepositoryService getActivitiRepositoryService()
    {
        return activitiRepositoryService;
    }

    public void setActivitiRepositoryService(RepositoryService activitiRepositoryService)
    {
        this.activitiRepositoryService = activitiRepositoryService;
    }

    public HistoryService getActivitiHistoryService()
    {
        return activitiHistoryService;
    }

    public void setActivitiHistoryService(HistoryService activitiHistoryService)
    {
        this.activitiHistoryService = activitiHistoryService;
    }

    public Map<String, Integer> getPriorityLevelToNumberMap()
    {
        return priorityLevelToNumberMap;
    }

    public void setPriorityLevelToNumberMap(Map<String, Integer> priorityLevelToNumberMap)
    {
        this.priorityLevelToNumberMap = priorityLevelToNumberMap;
    }

    public Map<String, List<String>> getRequiredFieldsPerOutcomeMap()
    {
        return requiredFieldsPerOutcomeMap;
    }

    public void setRequiredFieldsPerOutcomeMap(Map<String, List<String>> requiredFieldsPerOutcomeMap)
    {
        this.requiredFieldsPerOutcomeMap = requiredFieldsPerOutcomeMap;
    }

    public UserDao getUserDao()
    {
        return userDao;
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    public AcmParticipantDao getParticipantDao()
    {
        return participantDao;
    }

    public void setParticipantDao(AcmParticipantDao participantDao)
    {
        this.participantDao = participantDao;
    }

    public DataAccessPrivilegeListener getDataAccessPrivilegeListener()
    {
        return dataAccessPrivilegeListener;
    }

    public void setDataAccessPrivilegeListener(DataAccessPrivilegeListener dataAccessPrivilegeListener)
    {
        this.dataAccessPrivilegeListener = dataAccessPrivilegeListener;
    }

    public TaskBusinessRule getTaskBusinessRule()
    {
        return taskBusinessRule;
    }

    public void setTaskBusinessRule(TaskBusinessRule taskBusinessRule)
    {
        this.taskBusinessRule = taskBusinessRule;
    }

    public EcmFileService getFileService()
    {
        return fileService;
    }

    public void setFileService(EcmFileService fileService)
    {
        this.fileService = fileService;
    }

    public AcmContainerDao getContainerFolderDao()
    {
        return containerFolderDao;
    }

    public void setContainerFolderDao(AcmContainerDao containerFolderDao)
    {
        this.containerFolderDao = containerFolderDao;
    }

    public EcmFileDao getFileDao()
    {
        return fileDao;
    }

    public void setFileDao(EcmFileDao fileDao)
    {
        this.fileDao = fileDao;
    }

    public AuditPropertyEntityAdapter getAuditPropertyEntityAdapter()
    {
        return auditPropertyEntityAdapter;
    }

    public void setAuditPropertyEntityAdapter(AuditPropertyEntityAdapter auditPropertyEntityAdapter)
    {
        this.auditPropertyEntityAdapter = auditPropertyEntityAdapter;
    }

    public TaskEventPublisher getTaskEventPublisher()
    {
        return taskEventPublisher;
    }

    public void setTaskEventPublisher(TaskEventPublisher taskEventPublisher)
    {
        this.taskEventPublisher = taskEventPublisher;
    }

    public ObjectConverter getObjectConverter()
    {
        return objectConverter;
    }

    public void setObjectConverter(ObjectConverter objectConverter)
    {
        this.objectConverter = objectConverter;
    }

    public AcmBpmnService getAcmBpmnService()
    {
        return acmBpmnService;
    }

    public void setAcmBpmnService(AcmBpmnService acmBpmnService)
    {
        this.acmBpmnService = acmBpmnService;
    }

    public EcmFileService getEcmFileService()
    {
        return ecmFileService;
    }

    public void setEcmFileService(EcmFileService ecmFileService)
    {
        this.ecmFileService = ecmFileService;
    }

    @Override
    public AcmNotifiableEntity findEntity(Long id)
    {
        try
        {
            return findById(id);
        }
        catch (AcmTaskException e)
        {
            log.error("Task not found:", e);
        }
        return null;
    }

    @Override
    public String getSupportedNotifiableObjectType()
    {
        return TaskConstants.OBJECT_TYPE;
    }

    public EcmFileParticipantService getFileParticipantService()
    {
        return fileParticipantService;
    }

    public void setFileParticipantService(EcmFileParticipantService fileParticipantService)
    {
        this.fileParticipantService = fileParticipantService;
    }

    @Override
    protected Class<AcmTask> getPersistenceClass()
    {
        return AcmTask.class;
    }

    @Override
    public String getSupportedObjectType()
    {
        return "TASK";
    }

    @Override
    public AcmTask find(Long id) throws AcmTaskException
    {
        return findById(id);
    }

    @Override
    public List<AcmTask> findAll()
    {
        return allTasks();
    }

    @Override
    public List<AcmTask> findAllOrderBy(String column)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<AcmTask> findModifiedSince(Date lastModified, int startRow, int pageSize)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public TypedQuery<AcmTask> getSortedQuery(String sort)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public AcmObject findByName(String name)
    {
        return findById(Long.valueOf(name));
    }
}
