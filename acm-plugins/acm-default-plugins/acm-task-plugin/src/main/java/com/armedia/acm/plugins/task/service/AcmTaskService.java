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

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.data.BuckslipFutureTask;
import com.armedia.acm.plugins.ecm.exception.LinkAlreadyExistException;
import com.armedia.acm.plugins.objectassociation.model.ObjectAssociation;
import com.armedia.acm.plugins.task.exception.AcmTaskException;
import com.armedia.acm.plugins.task.model.AcmTask;
import com.armedia.acm.plugins.task.model.BuckslipProcess;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Created by nebojsha on 22.06.2015.
 */
public interface AcmTaskService
{

    void copyTasks(Long fromObjectId,
            String fromObjectType,
            Long toObjectId,
            String toObjectType,
            String toObjectName,
            Authentication auth,
            String ipAddress) throws AcmTaskException, AcmCreateObjectFailedException, AcmUserActionFailedException;

    void copyTaskFilesAndFoldersToParent(AcmTask task);

    List<ObjectAssociation> findChildObjects(Long taskId);

    AcmTask retrieveTask(Long id);

    void createTasks(String taskAssignees, String taskName, String owningGroup, String parentType,
            Long parentId) throws AcmCreateObjectFailedException, AcmUserActionFailedException;

    byte[] getDiagram(Long id) throws AcmTaskException;

    byte[] getDiagram(String processId) throws AcmTaskException;

    /**
     * Send a signal to an Activiti receive task. Activiti receive tasks cause the business process to halt until
     * the receive task is signaled. Receive tasks are used when the business process is waiting for user input, but
     * there should not be a user task assigned to anybody.
     *
     * @param businessProcessId
     *            processInstanceId of the Activiti business process.
     * @param receiveTaskId
     *            BPMN/XML id of the receive task in the business process BPMN model (NOT the ID of a specific Activiti
     *            task)
     * @throws AcmTaskException
     */
    void signalTask(String businessProcessId, String receiveTaskId) throws AcmTaskException;

    /**
     * Send a message to an Activiti task. Messages are used to alter the normal task flow, for instance when an
     * ongoing process should be interrupted and sent back to a specific starting point. In this example, a message
     * event could be attached to the current user task, and on receipt of that message, the task would be cancelled
     * and the process could resume at some other point (compared to the normal next step if the task would be
     * completed normally).
     *
     * @param taskId
     *            the ID of a specific Activiti user task
     * @param messageName
     *            the message to be sent.
     * @throws AcmTaskException
     */
    void messageTask(Long taskId, String messageName) throws AcmTaskException;

    /**
     * Whether a given process can be initiated. This method is designed for the ArkCase buckslip / routing workflow.
     * To be initiatable, the process must be active (not ended or closed), and the current process activity must be
     * a receive task with BPMN ID of "rtInitiate".
     * <p/>
     * To initiate such a process, call the <code>signalTask</code> method with "rtInitiate" as the receiveTaskId.
     *
     * @param businessProcessId
     * @return
     * @throws AcmTaskException
     */
    boolean isInitiatable(String businessProcessId) throws AcmTaskException;

    /**
     * Whether a given process can be withdrawn. This method is designed for the ArkCase buckslip / routing workflow.
     * To be withdrawable, the process must be active (not ended or closed), and the current process activity must be
     * a user task.
     * <p>
     * To withdraw such a process, call the <code>messageTask</code> method with the current user task id as the task
     * id,
     * and <code>Withdraw Message</code> as the message name.
     *
     * @param businessProcessId
     * @return
     * @throws AcmTaskException
     */
    boolean isWithdrawable(String businessProcessId) throws AcmTaskException;

    /**
     * Retrieves the list of future tasks for a business process. Designed for the ArkCase buckslip / routing workflow.
     *
     * @param businessProcessId
     * @return
     * @throws AcmTaskException
     */
    List<BuckslipFutureTask> getBuckslipFutureTasks(String businessProcessId) throws AcmTaskException;

    /**
     * Sets the list of future tasks for a business process. Designed for the ArkCase buckslip / routing workflow.
     *
     * @param businessProcessId
     * @param buckslipFutureTasks
     * @return
     * @throws AcmTaskException
     */
    void setBuckslipFutureTasks(String businessProcessId, List<BuckslipFutureTask> buckslipFutureTasks) throws AcmTaskException;

    /**
     * Retrieves the set of completed tasks for a business process. Designed for the ArkCase buckslip / routing
     * workflow.
     *
     * @param businessProcessId
     * @param readFromHistory
     * @return
     * @throws AcmTaskException
     */
    String getBuckslipPastTasks(String businessProcessId, boolean readFromHistory) throws AcmTaskException;

    /**
     * Retrieves a list of buckslip processes for a given object type and id; any particular object may have zero to
     * many active buckslip processes. Use this method if the buckslip process was started on the given object.
     *
     * @param objectType
     *            CASE_FILE, COMPLAINT, ...
     * @param objectId
     *            Id of the desired object
     */
    List<BuckslipProcess> getBuckslipProcessesForObject(String objectType, Long objectId) throws AcmTaskException;

    /**
     * Retrieves a list of buckslip processes for a given parent object type and id; any particular object may have zero
     * to
     * many active buckslip processes. Use this method if the buckslip process was started on a child of the given
     * object (e.g., on a file within a case).
     *
     * @param parentObjectType
     *            CASE_FILE, COMPLAINT, ...
     * @param parentObjectId
     *            Id of the desired object
     */
    List<BuckslipProcess> getBuckslipProcessesForChildren(String parentObjectType, Long parentObjectId) throws AcmTaskException;

    /**
     * Retrieves the ID of the completed business process for some Object ex.CASE_FILE
     *
     * @param objectType
     *            CASE_FILE, COMPLAINT, ...
     * @param objectId
     *            Id of the desired object
     */
    Long getCompletedBuckslipProcessIdForObjectFromSolr(String objectType, Long objectId, Authentication authentication);

    /**
     * Retrieves the value of process variable of active business process or from the history
     *
     * @param businessProcessId
     *            active or non active business process id, ...
     * @param processVariableKey
     *            variable that we want to get
     * @param readFromHistory
     *            does the variable need to be fetched from history if the business process is over
     */
    String getBusinessProcessVariable(String businessProcessId, String processVariableKey, boolean readFromHistory) throws AcmTaskException;

    /**
     * Retrieves the value of process variable of active business process or from the history by object type and
     * objectId
     *
     * @param objectType
     *            object type for which the business process Id will be fetched
     * @param objectId
     *            object id for which the business process Id will be fetched
     * @param processVariableKey
     *            variable that we want to get
     * @param readFromHistory
     *            does the variable need to be fetched from history if the business process is over
     */
    String getBusinessProcessVariableByObjectType(String objectType, Long objectId, String processVariableKey, boolean readFromHistory,
            Authentication authentication) throws AcmTaskException;

    /**
     * Update an existing buckslip process; only the <code>nonConcurEndsApprovals</code> and <code>futureTasks</code>
     * properties can be updated. All other changes are ignored.
     *
     * @param in
     * @return The updated process, or null if there is no existing active process with the given process id.
     * @throws AcmTaskException
     */
    BuckslipProcess updateBuckslipProcess(BuckslipProcess in) throws AcmTaskException;

    List<AcmTask> startReviewDocumentsWorkflow(AcmTask task, String businessProcessName, Authentication authentication) throws AcmCreateObjectFailedException, AcmUserActionFailedException, LinkAlreadyExistException, AcmObjectNotFoundException;

    List<AcmTask> startReviewDocumentsWorkflow(AcmTask task, String businessProcessName, Authentication authentication,
            List<MultipartFile> filesToUpload) throws AcmCreateObjectFailedException, AcmUserActionFailedException, LinkAlreadyExistException, AcmObjectNotFoundException;

    List<AcmTask> startAcmDocumentSingleTaskWorkflow(AcmTask task) throws AcmCreateObjectFailedException, AcmUserActionFailedException, LinkAlreadyExistException, AcmObjectNotFoundException;

    void sendAcmDocumentSingleTaskWorkflowMail(Long objectId, String objectType, String approvers);

    void createTaskFolderStructureInParentObject(AcmTask task) throws AcmUserActionFailedException, AcmCreateObjectFailedException, AcmObjectNotFoundException, LinkAlreadyExistException;

    public void setParticipantsToTaskFolderLink(AcmTask task) throws AcmObjectNotFoundException;

    String getTaskFolderNameInParentObject(AcmTask acmTask);

    boolean existFilesInTaskAttachFolder(AcmTask acmTask);
}
