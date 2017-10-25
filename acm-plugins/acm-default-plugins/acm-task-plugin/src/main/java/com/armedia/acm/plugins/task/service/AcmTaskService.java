package com.armedia.acm.plugins.task.service;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.plugins.objectassociation.model.ObjectAssociation;
import com.armedia.acm.plugins.objectassociation.model.Reference;
import com.armedia.acm.plugins.task.exception.AcmTaskException;
import com.armedia.acm.plugins.task.model.AcmTask;
import com.armedia.acm.plugins.task.model.BuckslipFutureTask;
import com.armedia.acm.plugins.task.model.BuckslipProcess;
import org.springframework.security.core.Authentication;

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
                   String ipAddress) throws AcmTaskException, AcmCreateObjectFailedException;

    void copyTaskFilesAndFoldersToParent(AcmTask task);

    ObjectAssociation saveReferenceToTask(Reference reference, Authentication authentication)
            throws AcmCreateObjectFailedException;

    List<ObjectAssociation> findChildObjects(Long taskId);

    AcmTask retrieveTask(Long id);

    void createTasks(String taskAssignees, String taskName, String owningGroup, String parentType,
                     Long parentId);

    byte[] getDiagram(Long id) throws AcmTaskException;

    /**
     * Send a signal to an Activiti receive task.  Activiti receive tasks cause the business process to halt until
     * the receive task is signaled.  Receive tasks are used when the business process is waiting for user input, but
     * there should not be a user task assigned to anybody.
     *
     * @param businessProcessId processInstanceId of the Activiti business process.
     * @param receiveTaskId     BPMN/XML id of the receive task in the business process BPMN model (NOT the ID of a specific Activiti task)
     * @throws AcmTaskException
     */
    void signalTask(String businessProcessId, String receiveTaskId) throws AcmTaskException;

    /**
     * Send a message to an Activiti task.  Messages are used to alter the normal task flow, for instance when an
     * ongoing process should be interrupted and sent back to a specific starting point.  In this example, a message
     * event could be attached to the current user task, and on receipt of that message, the task would be cancelled
     * and the process could resume at some other point (compared to the normal next step if the task would be
     * completed normally).
     *
     * @param taskId      the ID of a specific Activiti user task
     * @param messageName the message to be sent.
     * @throws AcmTaskException
     */
    void messageTask(Long taskId, String messageName) throws AcmTaskException;

    /**
     * Whether a given process can be initiated.  This method is designed for the ArkCase buckslip / routing workflow.
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
     * Whether a given process can be withdrawn.  This method is designed for the ArkCase buckslip / routing workflow.
     * To be withdrawable, the process must be active (not ended or closed), and the current process activity must be
     * a user task.
     * <p>
     * To withdraw such a process, call the <code>messageTask</code> method with the current user task id as the task id,
     * and <code>Withdraw Message</code> as the message name.
     *
     * @param businessProcessId
     * @return
     * @throws AcmTaskException
     */
    boolean isWithdrawable(String businessProcessId) throws AcmTaskException;

    /**
     * Retrieves the list of future tasks for a business process.  Designed for the ArkCase buckslip / routing workflow.
     *
     * @param businessProcessId
     * @return
     * @throws AcmTaskException
     */
    List<BuckslipFutureTask> getBuckslipFutureTasks(String businessProcessId) throws AcmTaskException;

    /**
     * Sets the list of future tasks for a business process.  Designed for the ArkCase buckslip / routing workflow.
     *
     * @param businessProcessId
     * @param buckslipFutureTasks
     * @return
     * @throws AcmTaskException
     */
    void setBuckslipFutureTasks(String businessProcessId, List<BuckslipFutureTask> buckslipFutureTasks) throws AcmTaskException;

    /**
     * Retrieves the set of completed tasks for a business process.  Designed for the ArkCase buckslip / routing workflow.
     *
     * @param businessProcessId
     * @return
     * @throws AcmTaskException
     */
    String getBuckslipPastTasks(String businessProcessId) throws AcmTaskException;

    /**
     * Retrieves a list of buckslip processes for a given object type and id; any particular object may have zero to
     * many active buckslip processes.  Use this method if the buckslip process was started on the given object.
     *
     * @param objectType CASE_FILE, COMPLAINT, ...
     * @param objectId   Id of the desired object
     */
    List<BuckslipProcess> getBuckslipProcessesForObject(String objectType, Long objectId) throws AcmTaskException;

    /**
     * Retrieves a list of buckslip processes for a given parent object type and id; any particular object may have zero to
     * many active buckslip processes.  Use this method if the buckslip process was started on a child of the given
     * object (e.g., on a file within a case).
     *
     * @param parentObjectType CASE_FILE, COMPLAINT, ...
     * @param parentObjectId   Id of the desired object
     */
    List<BuckslipProcess> getBuckslipProcessesForChildren(String parentObjectType, Long parentObjectId) throws AcmTaskException;


    /**
     * Update an existing buckslip process; only the <code>nonConcurEndsApprovals</code> and <code>futureTasks</code>
     * properties can be updated.  All other changes are ignored.
     *
     * @param in
     * @return The updated process, or null if there is no existing active process with the given process id.
     * @throws AcmTaskException
     */
    BuckslipProcess updateBuckslipProcess(BuckslipProcess in) throws AcmTaskException;
}
