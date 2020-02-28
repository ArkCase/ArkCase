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

import com.armedia.acm.auth.AcmAuthentication;
import com.armedia.acm.auth.AcmAuthenticationManager;
import com.armedia.acm.core.AcmNotifiableEntity;
import com.armedia.acm.core.AcmObject;
import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmListObjectsFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.data.BuckslipFutureTask;
import com.armedia.acm.data.service.AcmDataService;
import com.armedia.acm.objectonverter.ObjectConverter;
import com.armedia.acm.plugins.businessprocess.model.BusinessProcess;
import com.armedia.acm.plugins.businessprocess.service.SaveBusinessProcess;
import com.armedia.acm.plugins.ecm.dao.AcmContainerDao;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.exception.AcmFolderException;
import com.armedia.acm.plugins.ecm.exception.LinkAlreadyExistException;
import com.armedia.acm.plugins.ecm.model.*;
import com.armedia.acm.plugins.ecm.service.AcmFolderService;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.plugins.ecm.service.impl.FileWorkflowBusinessRule;
import com.armedia.acm.plugins.ecm.workflow.EcmFileWorkflowConfiguration;
import com.armedia.acm.plugins.objectassociation.model.ObjectAssociation;
import com.armedia.acm.plugins.objectassociation.service.ObjectAssociationService;
import com.armedia.acm.plugins.task.exception.AcmTaskException;
import com.armedia.acm.plugins.task.model.AcmApplicationTaskEvent;
import com.armedia.acm.plugins.task.model.AcmTask;
import com.armedia.acm.plugins.task.model.BuckslipProcess;
import com.armedia.acm.plugins.task.model.TaskConstants;
import com.armedia.acm.plugins.task.service.AcmTaskService;
import com.armedia.acm.plugins.task.service.TaskDao;
import com.armedia.acm.plugins.task.service.TaskEventPublisher;
import com.armedia.acm.services.labels.service.TranslationService;
import com.armedia.acm.services.note.dao.NoteDao;
import com.armedia.acm.services.note.model.Note;
import com.armedia.acm.services.notification.dao.NotificationDao;
import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.notification.model.NotificationConstants;
import com.armedia.acm.services.participants.dao.AcmParticipantDao;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.services.search.exception.SolrException;
import com.armedia.acm.services.search.model.solr.SolrCore;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;
import com.armedia.acm.services.search.service.SearchResults;
import com.armedia.acm.web.api.MDCConstants;
import com.google.common.collect.ImmutableMap;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.MDC;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by nebojsha on 22.06.2015.
 */
public class AcmTaskServiceImpl implements AcmTaskService
{

    private Logger log = LogManager.getLogger(getClass());
    private TaskDao taskDao;
    private NoteDao noteDao;
    private EcmFileDao fileDao;
    private TaskEventPublisher taskEventPublisher;
    private ExecuteSolrQuery executeSolrQuery;
    private AcmContainerDao acmContainerDao;
    private SearchResults searchResults = new SearchResults();
    private AcmFolderService acmFolderService;
    private EcmFileService ecmFileService;
    private AcmParticipantDao acmParticipantDao;
    private ObjectAssociationService objectAssociationService;
    private ObjectConverter objectConverter;
    private AcmAuthenticationManager authenticationManager;
    private SaveBusinessProcess saveBusinessProcess;
    private NotificationDao notificationDao;
    private AcmDataService acmDataService;
    private FileWorkflowBusinessRule fileWorkflowBusinessRule;
    private RuntimeService activitiRuntimeService;
    private TranslationService translationService;

    @Override
    public List<BuckslipProcess> getBuckslipProcessesForObject(String objectType, Long objectId)
    {
        Map<String, Object> matchProcessVariables = ImmutableMap.of(
                TaskConstants.VARIABLE_NAME_OBJECT_ID, objectId,
                TaskConstants.VARIABLE_NAME_OBJECT_TYPE, objectType,
                TaskConstants.VARIABLE_NAME_IS_BUCKSLIP_WORKFLOW, Boolean.TRUE);
        List<ProcessInstance> processInstances = taskDao.findProcessesByProcessVariables(matchProcessVariables);
        List<BuckslipProcess> buckslipProcesses = buckslipProcessesForProcessInstances(processInstances);

        return buckslipProcesses;
    }

    @Override
    public List<BuckslipProcess> getBuckslipProcessesForChildren(String parentObjectType, Long parentObjectId)
    {
        Map<String, Object> matchProcessVariables = ImmutableMap.of(
                TaskConstants.VARIABLE_NAME_PARENT_OBJECT_ID, parentObjectId,
                TaskConstants.VARIABLE_NAME_PARENT_OBJECT_TYPE, parentObjectType,
                TaskConstants.VARIABLE_NAME_IS_BUCKSLIP_WORKFLOW, Boolean.TRUE);
        List<ProcessInstance> processInstances = taskDao.findProcessesByProcessVariables(matchProcessVariables);
        List<BuckslipProcess> buckslipProcesses = buckslipProcessesForProcessInstances(processInstances);

        return buckslipProcesses;
    }

    @Override
    public Long getCompletedBuckslipProcessIdForObjectFromSolr(String objectType, Long objectId, Authentication authentication)
    {
        return getBusinessProcessIdFromSolr(objectType, objectId, authentication);
    }

    @Override
    public String getBusinessProcessVariable(String businessProcessId, String processVariableKey, boolean readFromHistory)
            throws AcmTaskException
    {
        return taskDao.readProcessVariable(businessProcessId, processVariableKey, readFromHistory).toString();
    }

    @Override
    public String getBusinessProcessVariableByObjectType(String objectType, Long objectId, String processVariableKey,
            boolean readFromHistory, Authentication authentication) throws AcmTaskException
    {
        List<BuckslipProcess> buckslipProcesses = getBuckslipProcessesForObject(objectType, objectId);

        // Takes the business process id from the process if active or from solr if completed
        Long businessProcessId = (buckslipProcesses != null && buckslipProcesses.size() > 0
                && !buckslipProcesses.get(0).getBusinessProcessId().isEmpty())
                        ? Long.valueOf(buckslipProcesses.get(0).getBusinessProcessId())
                        : getCompletedBuckslipProcessIdForObjectFromSolr(objectType, objectId, authentication);

        return getBusinessProcessVariable(String.valueOf(businessProcessId), processVariableKey, readFromHistory);
    }

    protected List<BuckslipProcess> buckslipProcessesForProcessInstances(List<ProcessInstance> processInstances)
    {
        List<BuckslipProcess> buckslipProcesses = new ArrayList<>(processInstances.size());
        for (ProcessInstance pi : processInstances)
        {
            BuckslipProcess bp = new BuckslipProcess();
            bp.setBusinessProcessId(pi.getProcessInstanceId());
            bp.setBusinessProcessName(pi.getProcessDefinitionId());
            bp.setObjectType((String) pi.getProcessVariables().get(TaskConstants.VARIABLE_NAME_OBJECT_TYPE));
            bp.setObjectId((Long) pi.getProcessVariables().get(TaskConstants.VARIABLE_NAME_OBJECT_ID));
            bp.setNonConcurEndsApprovals((Boolean) pi.getProcessVariables().get(TaskConstants.VARIABLE_NAME_NON_CONCUR_ENDS_APPROVALS));
            bp.setPastTasks(((String) pi.getProcessVariables().get(TaskConstants.VARIABLE_NAME_PAST_TASKS)));

            String futureTasksJson = ((String) pi.getProcessVariables().get(TaskConstants.VARIABLE_NAME_BUCKSLIP_FUTURE_TASKS));

            List<BuckslipFutureTask> futureTasks = futureTasksJson == null || futureTasksJson.trim().isEmpty() ? new ArrayList<>()
                    : getObjectConverter().getJsonUnmarshaller().unmarshallCollection(futureTasksJson, List.class,
                            BuckslipFutureTask.class);
            bp.setFutureTasks(futureTasks);
            buckslipProcesses.add(bp);
        }
        return buckslipProcesses;
    }

    @Override
    public BuckslipProcess updateBuckslipProcess(BuckslipProcess in) throws AcmTaskException
    {
        setBuckslipFutureTasks(in.getBusinessProcessId(), in.getFutureTasks());
        taskDao.writeProcessVariable(in.getBusinessProcessId(), TaskConstants.VARIABLE_NAME_NON_CONCUR_ENDS_APPROVALS,
                in.getNonConcurEndsApprovals());

        List<BuckslipProcess> processes = getBuckslipProcessesForObject(in.getObjectType(), in.getObjectId());
        BuckslipProcess updated = processes.stream().filter(bp -> Objects.equals(in.getBusinessProcessId(), bp.getBusinessProcessId()))
                .findFirst().orElse(null);
        return updated;
    }

    @Override
    public boolean isInitiatable(String businessProcessId) throws AcmTaskException
    {
        return taskDao.isWaitingOnReceiveTask(businessProcessId, TaskConstants.INITIATE_TASK_NAME);
    }

    @Override
    public boolean isWithdrawable(String businessProcessId) throws AcmTaskException
    {
        return taskDao.isProcessActive(businessProcessId)
                && !taskDao.isWaitingOnReceiveTask(businessProcessId, TaskConstants.INITIATE_TASK_NAME);
    }

    @Override
    public List<BuckslipFutureTask> getBuckslipFutureTasks(String businessProcessId) throws AcmTaskException
    {
        String futureTasksJson = taskDao.readProcessVariable(businessProcessId, TaskConstants.VARIABLE_NAME_BUCKSLIP_FUTURE_TASKS, false);
        if (futureTasksJson != null && !futureTasksJson.trim().isEmpty())
        {
            List<BuckslipFutureTask> buckslipFutureTasks = getObjectConverter().getJsonUnmarshaller().unmarshallCollection(futureTasksJson,
                    List.class, BuckslipFutureTask.class);
            if (buckslipFutureTasks == null)
            {
                throw new AcmTaskException(
                        String.format("Process with id %s has invalid or corrupt future tasks data structure", businessProcessId));
            }
            return buckslipFutureTasks;
        }
        return new ArrayList<>();
    }

    @Override
    public void setBuckslipFutureTasks(String businessProcessId, List<BuckslipFutureTask> buckslipFutureTasks) throws AcmTaskException
    {
        String futureTasksJson = getObjectConverter().getJsonMarshaller().marshal(buckslipFutureTasks);
        if (futureTasksJson == null)
        {
            throw new AcmTaskException("Could not set future tasks");
        }
        taskDao.writeProcessVariable(businessProcessId,
                TaskConstants.VARIABLE_NAME_BUCKSLIP_FUTURE_TASKS,
                futureTasksJson);
    }

    @Override
    public String getBuckslipPastTasks(String businessProcessId, boolean readFromHistory) throws AcmTaskException
    {
        return taskDao.readProcessVariable(businessProcessId, TaskConstants.VARIABLE_NAME_PAST_TASKS, readFromHistory);
    }

    @Override
    public void signalTask(String businessProcessId, String receiveTaskId) throws AcmTaskException
    {
        taskDao.signalTask(businessProcessId, receiveTaskId);
    }

    @Override
    public void messageTask(Long taskId, String messageName) throws AcmTaskException
    {
        taskDao.messageTask(String.valueOf(taskId), messageName);
    }

    @Override
    public void createTasks(String taskAssignees, String taskName, String owningGroup, String parentType,
            Long parentId) throws AcmCreateObjectFailedException, AcmUserActionFailedException {
        if (taskAssignees == null || taskAssignees.trim().isEmpty() || taskName == null || taskName.trim().isEmpty()
                || owningGroup == null || owningGroup.trim().isEmpty() || parentType == null || parentType.trim().isEmpty()
                || parentId == null)
        {
            log.error("Cannot create tasks - invalid input: assignees [{}], task name [{}], owning group [{}], "
                    + "parent type: [{}], parentId [{}]", taskAssignees, taskName, owningGroup, parentType, parentId);
            return;
        }

        String user = MDC.get(MDCConstants.EVENT_MDC_REQUEST_USER_ID_KEY);
        user = user == null ? "ACTIVITI_SYSTEM" : user;

        Date dueDate = Date.from(LocalDate.now().plusDays(3).atStartOfDay(ZoneId.systemDefault()).toInstant());

        for (String assignee : taskAssignees.split(","))
        {
            assignee = assignee.trim();

            AcmTask task = new AcmTask();
            task.setAssignee(assignee);
            task.setTitle(taskName);
            task.setParentObjectId(parentId);
            task.setParentObjectType(parentType);
            task.setPriority(TaskConstants.DEFAULT_PRIORITY_WORD);
            task.setOwner(assignee);
            task.setStatus(TaskConstants.STATE_ACTIVE);
            task.setParticipants(new ArrayList<>());
            task.setDueDate(dueDate);

            taskDao.ensureCorrectAssigneeInParticipants(task);

            try
            {

                task = taskDao.createAdHocTask(task);

                AcmParticipant group = new AcmParticipant();
                group.setParticipantType("owning group");
                group.setParticipantLdapId(owningGroup);
                group.setObjectId(task.getId());
                group.setObjectType(TaskConstants.OBJECT_TYPE);

                group = getAcmParticipantDao().save(group);

                task.getParticipants().add(group);

                AcmApplicationTaskEvent event = new AcmApplicationTaskEvent(task, "create", user, true, "");
                taskEventPublisher.publishTaskEvent(event);
            }
            catch (AcmTaskException ate)
            {
                log.error("Could not save task: {}", ate.getMessage(), ate);
            }

        }
    }

    @Override
    public byte[] getDiagram(Long id) throws AcmTaskException
    {
        return taskDao.getDiagram(id);
    }

    @Override
    public byte[] getDiagram(String processId) throws AcmTaskException
    {
        return taskDao.getDiagram(processId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void copyTasks(Long fromObjectId, String fromObjectType, Long toObjectId, String toObjectType, String toObjectName,
            Authentication auth, String ipAddress) throws AcmTaskException, AcmCreateObjectFailedException, AcmUserActionFailedException {
        List<Long> tasksIdsFromOriginal = getTaskIdsFromSolr(fromObjectType, fromObjectId, auth);
        if (tasksIdsFromOriginal == null)
            return;
        for (Long taskIdFromOriginal : tasksIdsFromOriginal)
        {
            AcmTask task = new AcmTask();
            AcmTask taskFromOriginal = taskDao.findById(taskIdFromOriginal);
            try
            {
                BeanUtils.copyProperties(task, taskFromOriginal);
            }
            catch (InvocationTargetException | IllegalAccessException e)
            {
                log.error("couldn't copy bean.", e);
                continue;
            }

            task.setTaskId(null);
            task.setAttachedToObjectId(toObjectId);
            task.setAttachedToObjectName(toObjectName);
            task.setAttachedToObjectType(toObjectType);
            task.setParentObjectId(toObjectId);
            task.setParentObjectType(toObjectType);
            task.setOwner(auth.getName());
            task.setContainer(null);

            // create the task
            task = taskDao.createAdHocTask(task);

            // create container and folder for the task
            taskDao.createFolderForTaskEvent(task);

            // save again to get container and folder ids, must be after creating folderForTaskEvent in order to create
            // cmisFolderId
            AcmTask savedTask = taskDao.save(task);

            // copy folder structure of the original task to the copy task
            try
            {
                AcmContainer originalTaskContainer = acmContainerDao.findFolderByObjectTypeAndId(taskFromOriginal.getObjectType(),
                        taskFromOriginal.getId());
                if (originalTaskContainer != null && originalTaskContainer.getFolder() != null)
                    acmFolderService.copyFolderStructure(originalTaskContainer.getFolder().getId(), savedTask.getContainer(),
                            savedTask.getContainer().getFolder());
            }
            catch (Exception e)
            {
                log.error("Error copying attachments for task id = {} into task id = {}", taskFromOriginal.getId(), savedTask.getId());
            }

            copyNotes(taskFromOriginal, savedTask);

            AcmApplicationTaskEvent event = new AcmApplicationTaskEvent(savedTask, "create", auth.getName(), true, ipAddress);

            taskEventPublisher.publishTaskEvent(event);
        }
    }

    private void copyNotes(AcmTask taskFrom, AcmTask taskTo)
    {
        try
        {
            // copy notes
            List<Note> notesFromOriginal = noteDao.listNotes("GENERAL", taskFrom.getId(), taskFrom.getObjectType());
            for (Note note : notesFromOriginal)
            {
                Note newNote = new Note();
                newNote.setNote(note.getNote());
                newNote.setType(note.getType());
                newNote.setParentId(taskTo.getId());
                newNote.setParentType(taskTo.getObjectType());
                newNote.setAuthor(note.getCreator());
                noteDao.save(newNote);
            }
        }
        catch (Exception e)
        {
            log.error("Error copying notes!", e);
        }
    }

    private List<Long> getTaskIdsFromSolr(String parentObjectType, Long parentObjectId, Authentication authentication)
    {
        List<Long> tasksIds = new LinkedList<>();

        log.debug("Taking task objects from Solr for parentObjectType = {} and parentObjectId={}", parentObjectType, parentObjectId);

        String query = "object_type_s:TASK AND parent_object_type_s :" + parentObjectType + " AND parent_object_id_i:" + parentObjectId;

        try
        {
            String retval = executeSolrQuery.getResultsByPredefinedQuery(authentication, SolrCore.QUICK_SEARCH, query, 0, 1000, "");

            if (retval != null && searchResults.getNumFound(retval) > 0)
            {
                JSONArray results = searchResults.getDocuments(retval);
                for (int index = 0; index < results.length(); index++)
                {
                    JSONObject result = results.getJSONObject(index);
                    if (result.has("object_id_s"))
                    {
                        tasksIds.add(result.getLong("object_id_s"));
                    }
                }
            }

            log.debug("Acm Task ids was retrieved. count({}).", tasksIds.size());
        }
        catch (SolrException e)
        {
            log.error("Cannot retrieve objects from Solr.", e);
        }

        return tasksIds;
    }

    @Override
    public void copyTaskFilesAndFoldersToParent(AcmTask task)
    {
        try
        {
            if (null != task.getParentObjectType() && null != task.getParentObjectId())
            {
                log.debug("Task event raised. Start coping folder to the parent folder ...");

                AcmContainer container = task.getContainer() != null ? task.getContainer()
                        : getAcmContainerDao().findFolderByObjectTypeAndId(task.getObjectType(), task.getId());

                String principal = container.getCreator();
                AcmAuthentication authentication;
                try
                {
                    authentication = authenticationManager.getAcmAuthentication(
                            new UsernamePasswordAuthenticationToken(principal, principal));
                }
                catch (AuthenticationServiceException e)
                {
                    authentication = new AcmAuthentication(Collections.emptySet(), principal, "",
                            true, principal);
                }

                AcmCmisObjectList files = getEcmFileService().allFilesForContainer(authentication, container);

                if (files != null && files.getChildren() != null && files.getTotalChildren() > 0)
                {
                    AcmFolder folderToBeCoppied = container.getFolder();

                    AcmContainer targetContainer = getAcmContainerDao().findFolderByObjectTypeAndId(task.getParentObjectType(),
                            task.getParentObjectId());

                    AcmFolder targetFolder = getAcmFolderService().addNewFolderByPath(task.getParentObjectType(), task.getParentObjectId(),
                            "/" + String.format("Task %d%n %s", task.getId(), task.getTitle()));

                    getAcmFolderService().copyFolderStructure(folderToBeCoppied.getId(), targetContainer, targetFolder);
                }
            }

        }
        catch (AcmFolderException | AcmListObjectsFailedException | AcmCreateObjectFailedException | AcmUserActionFailedException
                | AcmObjectNotFoundException e)
        {
            log.error("Could not copy folder for task id = {}", task.getId(), e);
        }
    }

    @Override
    public List<ObjectAssociation> findChildObjects(Long taskId)
    {
        try
        {
            AcmTask parentObject = taskDao.findById(taskId);
            String parentType = parentObject.getBusinessProcessId() == null ? TaskConstants.OBJECT_TYPE : TaskConstants.SYSTEM_OBJECT_TYPE;
            Long parentId = parentObject.getBusinessProcessId() == null ? parentObject.getTaskId() : parentObject.getBusinessProcessId();
            return objectAssociationService.findByParentTypeAndId(parentType, parentId);
        }
        catch (AcmTaskException e)
        {
            log.error("Task with id=[{}] not found!", taskId, e);
        }

        return new ArrayList<>();
    }

    @Override
    public AcmTask retrieveTask(Long id)
    {
        try
        {
            AcmTask retval = taskDao.findById(id);
            if (retval.getReviewDocumentPdfRenditionId() != null)
            {
                List<Long> docsIds = Stream.of(retval.getReviewDocumentPdfRenditionId().split(","))
                        .map(Long::parseLong)
                        .collect(Collectors.toList());
                List<EcmFile> docsUnderReview = fileDao.findByIds(docsIds);
                retval.setDocumentsToReview(docsUnderReview);
            }
            List<ObjectAssociation> childObjects = findChildObjects(id);
            retval.setChildObjects(childObjects);
            return retval;
        }
        catch (AcmTaskException | ActivitiException e)
        {
            log.error("Task with id=[{}] not found!", id, e);
        }
        return null;
    }

    @Override
    public List<AcmTask> startReviewDocumentsWorkflow(AcmTask task, String businessProcessName, Authentication authentication)
            throws AcmTaskException, AcmCreateObjectFailedException, AcmUserActionFailedException, LinkAlreadyExistException, AcmObjectNotFoundException
    {
        List<String> reviewers = new ArrayList<>();
        reviewers.add(task.getAssignee());
        List<AcmTask> createdAcmTasks = new ArrayList<>();
        Long parentObjectId = task.getAttachedToObjectId();
        String parentObjectType = (StringUtils.isNotBlank(task.getAttachedToObjectType())) ? task.getAttachedToObjectType() : null;

        if (task.getDocumentsToReview() == null || task.getDocumentsToReview().isEmpty())
        {
            throw new AcmTaskException("You must select at least one document to be reviewed.");
        }
        else
        {
            // Iterate through the list of documentsToReview and start business process for each of them
            for (EcmFile documentToReview : task.getDocumentsToReview())
            {
                Map<String, Object> pVars = new HashMap<>();

                pVars.put("reviewers", reviewers);
                pVars.put("assignee", task.getAssignee());
                pVars.put("taskName", task.getTitle());
                pVars.put("dueDate", task.getDueDate());
                pVars.put("documentAuthor", authentication.getName());
                pVars.put("pdfRenditionId", documentToReview.getFileId());
                pVars.put("formXmlId", null);
                pVars.put("candidateGroups", String.join(",", task.getCandidateGroups()));
                pVars.put("DETAILS", task.getDetails());
                pVars.put("OBJECT_TYPE", "FILE");
                pVars.put("OBJECT_ID", documentToReview.getFileId());
                pVars.put("OBJECT_NAME", documentToReview.getFileName());
                pVars.put("PARENT_OBJECT_TYPE", parentObjectType);
                pVars.put("PARENT_OBJECT_ID", parentObjectId);
                pVars.put("REQUEST_TYPE", "DOCUMENT_REVIEW");

                AcmTask createdAcmTask = taskDao.startBusinessProcess(pVars, businessProcessName);
                createdAcmTask.setDocumentUnderReview(documentToReview);
                AcmContainer container = getEcmFileService().getOrCreateContainer(createdAcmTask.getObjectType(),
                        createdAcmTask.getTaskId());
                createdAcmTask.setContainer(container);
                createdAcmTasks.add(createdAcmTask);
                if (task.getAttachedToObjectId() != null && task.getAttachedToObjectType() != null)
                {
                    createdAcmTask.setAttachedToObjectId(task.getAttachedToObjectId());
                    createdAcmTask.setAttachedToObjectType(task.getAttachedToObjectType());
                }
                createTaskFolderStructureInParentObject(createdAcmTask);
            }

            return createdAcmTasks;
        }
    }

    @Override
    @Transactional
    public List<AcmTask> startReviewDocumentsWorkflow(AcmTask task, String businessProcessName, Authentication authentication,
            List<MultipartFile> filesToUpload)
            throws AcmTaskException, AcmCreateObjectFailedException, AcmUserActionFailedException, LinkAlreadyExistException, AcmObjectNotFoundException {
        BusinessProcess businessProcess = new BusinessProcess();
        businessProcess.setStatus("DRAFT");
        businessProcess = saveBusinessProcess.save(businessProcess);
        AcmContainer container = ecmFileService.createContainerFolder(businessProcess.getObjectType(), businessProcess.getId(),
                EcmFileConstants.DEFAULT_CMIS_REPOSITORY_ID);
        getAcmContainerDao().getEm().flush();
        businessProcess.setContainer(container);

        AcmFolder folder = container.getAttachmentFolder();

        List<EcmFile> uploadedFiles = new ArrayList<>();

        String fileParentObjectType = businessProcess.getObjectType();
        Long fileParentObjectId = businessProcess.getId();

        if (StringUtils.isNotBlank(task.getAttachedToObjectType()) && task.getAttachedToObjectId() != null)
        {
            fileParentObjectType = task.getAttachedToObjectType();
            fileParentObjectId = task.getAttachedToObjectId();
        }

        if (filesToUpload != null)
        {

            for (MultipartFile file : filesToUpload)
            {

                EcmFile temp = ecmFileService.upload(file.getOriginalFilename(), "Other", file, authentication,
                        folder.getCmisFolderId(), fileParentObjectType, fileParentObjectId);
                uploadedFiles.add(temp);
            }
        }
        List<EcmFile> documentsToReview = task.getDocumentsToReview();
        if (!documentsToReview.isEmpty())
        {
            documentsToReview.addAll(uploadedFiles);
            task.setDocumentsToReview(documentsToReview);
        }
        else
        {
            task.setDocumentsToReview(uploadedFiles);
            task.setAttachedToObjectType(StringUtils.isNotBlank(task.getAttachedToObjectType()) ? task.getAttachedToObjectType()
                    : businessProcess.getObjectType());
            task.setAttachedToObjectId(task.getAttachedToObjectId() != null ? task.getAttachedToObjectId() : businessProcess.getId());
        }
        return startReviewDocumentsWorkflow(task, businessProcessName, authentication);
    }

    @Override
    public void sendArrestWarrantMail(Long objectId, String objectType, String approvers)
    {
        AcmAbstractDao<AcmObject> dao = getAcmDataService().getDaoByObjectType(objectType);
        AcmObject object = dao.find(objectId);
        Notification notification = new Notification();
        notification.setParentId(objectId);
        notification.setParentType(objectType);
        notification.setTitle(String.format(translationService.translate(NotificationConstants.ARREST_WARRANT), ((AcmNotifiableEntity) object).getNotifiableEntityNumber()));
        notification.setEmailAddresses(approvers);
        notification.setTemplateModelName("arrestWarrant");
        notificationDao.save(notification);

    }

    @Override
    public void startArrestWarrantWorkflow(AcmTask task) throws AcmCreateObjectFailedException, AcmUserActionFailedException, LinkAlreadyExistException, AcmObjectNotFoundException {
        EcmFile source = task.getDocumentsToReview().get(0);

        EcmFileWorkflowConfiguration configuration = new EcmFileWorkflowConfiguration();
        source.setFileType("file");
        configuration.setEcmFile(source);

        configuration = getFileWorkflowBusinessRule().applyRules(configuration);
        if (!configuration.isBuckslipProcess())
        {
            return;
        }

        if (configuration.isStartProcess())
        {
            // Create task container
            String processName = configuration.getProcessName();

            Map<String, Object> pvars = new HashMap<>();

            String approversCsv = configuration.getApprovers();
            List<String> approvers = approversCsv == null ? new ArrayList<>()
                    : Arrays.stream(approversCsv.split(",")).filter(s -> s != null).map(s -> s.trim()).collect(Collectors.toList());
            pvars.put("approvers", approversCsv);
            pvars.put("taskName", configuration.getTaskName());

            pvars.put("PARENT_OBJECT_TYPE", task.getParentObjectType());
            pvars.put("PARENT_OBJECT_ID", task.getParentObjectId());
            String documentsToReviewIds = task.getDocumentsToReview()
                    .stream()
                    .map(file -> String.valueOf(file.getFileId()))
                    .collect(Collectors.joining(","));
            pvars.put("pdfRenditionId", documentsToReviewIds);

            pvars.put("taskDueDateExpression", configuration.getTaskDueDateExpression());
            pvars.put("taskPriority", configuration.getTaskPriority());

            pvars.put("approver1", approvers.get(0));
            pvars.put("approver2", approvers.get(1));
            pvars.put("approver3", approvers.get(2));

            pvars.put("currentTaskName", configuration.getTaskName());
            pvars.put("owningGroup", "");
            pvars.put("dueDate", configuration.getTaskDueDateExpression());

            AcmTask createdAcmTask = taskDao.startBusinessProcess(pvars, processName);
            AcmContainer container = getEcmFileService().getOrCreateContainer(createdAcmTask.getObjectType(),
                    createdAcmTask.getTaskId());
            createdAcmTask.setContainer(container);
            createdAcmTask.setDocumentsToReview(task.getDocumentsToReview());

            createTaskFolderStructureInParentObject(createdAcmTask);

        }
    }

    @Override
    public void createTaskFolderStructureInParentObject(AcmTask task) throws AcmUserActionFailedException, AcmCreateObjectFailedException, AcmObjectNotFoundException, LinkAlreadyExistException {

        String taskFolderName = "Task-" + task.getTitle() + "-" + task.getId();
        Long parentObjectId = task.getParentObjectId() == null ? task.getAttachedToObjectId() : task.getParentObjectId();
        String parentObjectType = task.getParentObjectType() == null ? task.getAttachedToObjectType() : task.getParentObjectType();

        AcmAbstractDao<AcmObject> acmObjectAcmAbstractDao = getAcmDataService().getDaoByObjectType(parentObjectType);
        AcmContainerEntity containerEntity = (AcmContainerEntity) acmObjectAcmAbstractDao.find(parentObjectId);
        Long parentFolderId = containerEntity.getContainer().getFolder().getId();

        // Add Task-folder to parent
        AcmFolder taskFolder = getAcmFolderService().addNewFolder(parentFolderId, taskFolderName, parentObjectId, parentObjectType);

        // Create link to task attachment folder
        getAcmFolderService().copyFolderAsLink(task.getContainer().getFolder(), taskFolder, task.getContainer().getFolder().getId(), task.getContainer().getFolder().getObjectType(), "Attachments");
        // Check if arrayList is empty
        String documentsUnderReviewFolderName = "Documents Under Review";
        AcmFolder documentsUnderReviewFolder = null;

        if (task.getDocumentsToReview() != null)
        {
            if (!task.getDocumentsToReview().isEmpty()) {
                documentsUnderReviewFolder = getAcmFolderService().addNewFolder(taskFolder.getId(), documentsUnderReviewFolderName, parentObjectId, parentObjectType);
                addDocumentToReviewLinksToParentObject(task.getDocumentsToReview(), parentObjectId, parentObjectType, documentsUnderReviewFolder.getId());
            }
        }
        else if (task.getDocumentUnderReview() != null)
        {
            documentsUnderReviewFolder = getAcmFolderService().addNewFolder(taskFolder.getId(), documentsUnderReviewFolderName,
                    parentObjectId, parentObjectType);
            ArrayList<EcmFile> docToReview = new ArrayList<>();
            docToReview.add(task.getDocumentUnderReview());
            addDocumentToReviewLinksToParentObject(docToReview, parentObjectId, parentObjectType,
                    documentsUnderReviewFolder.getId());
        }
    }


    private void addDocumentToReviewLinksToParentObject(List<EcmFile> documentsUnderReview, Long parentObjectId, String parentObjectType,
            Long dstFolderId) throws AcmUserActionFailedException, AcmObjectNotFoundException, LinkAlreadyExistException {

        for (EcmFile documentUnderReview : documentsUnderReview)

        {
            getEcmFileService().copyFileAsLink(documentUnderReview.getFileId(), parentObjectId, parentObjectType, dstFolderId);
        }
    }

    private Long getBusinessProcessIdFromSolr(String objectType, Long objectId, Authentication authentication)
    {
        Long businessProcessId = null;
        String query = "object_type_s:TASK AND parent_object_type_s:" + objectType + " AND parent_object_id_i:" + objectId
                + " AND outcome_name_s:buckslipOutcome AND status_s:CLOSED";
        String retval = null;

        try
        {
            retval = executeSolrQuery.getResultsByPredefinedQuery(authentication,
                    SolrCore.QUICK_SEARCH,
                    query, 0, 1, "business_process_id_i DESC");

            if (retval != null && searchResults.getNumFound(retval) > 0)
            {
                JSONArray results = searchResults.getDocuments(retval);
                JSONObject result = results.getJSONObject(0);
                if (result.has("business_process_id_i"))
                {
                    businessProcessId = result.getLong("business_process_id_i");
                }
            }
        }
        catch (SolrException e)
        {
            log.warn(e.getMessage());
        }

        return businessProcessId;
    }

    public void setTaskEventPublisher(TaskEventPublisher taskEventPublisher)
    {
        this.taskEventPublisher = taskEventPublisher;
    }

    public void setExecuteSolrQuery(ExecuteSolrQuery executeSolrQuery)
    {
        this.executeSolrQuery = executeSolrQuery;
    }

    public void setTaskDao(TaskDao taskDao)
    {
        this.taskDao = taskDao;
    }

    public AcmContainerDao getAcmContainerDao()
    {
        return acmContainerDao;
    }

    public void setAcmContainerDao(AcmContainerDao acmContainerDao)
    {
        this.acmContainerDao = acmContainerDao;
    }

    public AcmFolderService getAcmFolderService()
    {
        return acmFolderService;
    }

    public void setAcmFolderService(AcmFolderService acmFolderService)
    {
        this.acmFolderService = acmFolderService;
    }

    public void setNoteDao(NoteDao noteDao)
    {
        this.noteDao = noteDao;
    }

    public EcmFileService getEcmFileService()
    {
        return ecmFileService;
    }

    public void setEcmFileService(EcmFileService ecmFileService)
    {
        this.ecmFileService = ecmFileService;
    }

    public void setObjectAssociationService(ObjectAssociationService objectAssociationService)
    {
        this.objectAssociationService = objectAssociationService;
    }

    public void setFileDao(EcmFileDao fileDao)
    {
        this.fileDao = fileDao;
    }

    public AcmParticipantDao getAcmParticipantDao()
    {
        return acmParticipantDao;
    }

    public void setAcmParticipantDao(AcmParticipantDao acmParticipantDao)
    {
        this.acmParticipantDao = acmParticipantDao;
    }

    public ObjectConverter getObjectConverter()
    {
        return objectConverter;
    }

    public void setObjectConverter(ObjectConverter objectConverter)
    {
        this.objectConverter = objectConverter;
    }

    public AcmAuthenticationManager getAuthenticationManager()
    {
        return authenticationManager;
    }

    public void setAuthenticationManager(AcmAuthenticationManager authenticationManager)
    {
        this.authenticationManager = authenticationManager;
    }

    public void setSaveBusinessProcess(SaveBusinessProcess saveBusinessProcess)
    {
        this.saveBusinessProcess = saveBusinessProcess;
    }

    public NotificationDao getNotificationDao()
    {
        return notificationDao;
    }

    public void setNotificationDao(NotificationDao notificationDao)
    {
        this.notificationDao = notificationDao;
    }

    public AcmDataService getAcmDataService()
    {
        return acmDataService;
    }

    public void setAcmDataService(AcmDataService acmDataService)
    {
        this.acmDataService = acmDataService;
    }

    public FileWorkflowBusinessRule getFileWorkflowBusinessRule()
    {
        return fileWorkflowBusinessRule;
    }

    public void setFileWorkflowBusinessRule(FileWorkflowBusinessRule fileWorkflowBusinessRule)
    {
        this.fileWorkflowBusinessRule = fileWorkflowBusinessRule;
    }

    public RuntimeService getActivitiRuntimeService()
    {
        return activitiRuntimeService;
    }

    public void setActivitiRuntimeService(RuntimeService activitiRuntimeService)
    {
        this.activitiRuntimeService = activitiRuntimeService;
    }

    public TranslationService getTranslationService()
    {
        return translationService;
    }

    public void setTranslationService(TranslationService translationService)
    {
        this.translationService = translationService;
    }
}
