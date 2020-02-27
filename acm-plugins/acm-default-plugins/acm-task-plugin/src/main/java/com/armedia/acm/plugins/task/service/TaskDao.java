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
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.task.exception.AcmTaskException;
import com.armedia.acm.plugins.task.model.AcmTask;
import com.armedia.acm.plugins.task.model.NumberOfDays;
import com.armedia.acm.plugins.task.model.WorkflowHistoryInstance;

import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by armdev on 6/2/14.
 */
public interface TaskDao
{

    @Transactional
    AcmTask createAdHocTask(AcmTask in) throws AcmTaskException, AcmUserActionFailedException, AcmCreateObjectFailedException;

    <T> T readProcessVariable(String businessProcessId, String processVariableKey, boolean readFromHistory) throws AcmTaskException;

    void writeProcessVariable(String businessProcessId, String processVariableKey, Object processVariableValue)
            throws AcmTaskException;

    void signalTask(String processInstanceId, String receiveTaskId) throws AcmTaskException;

    void messageTask(String taskId, String messageName) throws AcmTaskException;

    boolean isProcessActive(String businessProcessId) throws AcmTaskException;

    boolean isWaitingOnReceiveTask(String businessProcessId, String receiveTaskId) throws AcmTaskException;

    void ensureCorrectAssigneeInParticipants(AcmTask in);

    /**
     * Complete a task on behalf of the given user. Returns an AcmTask including historical information (task start
     * date, task duration in milliseconds, etc).
     *
     * @param userThatCompletedTheTask
     * @param taskId
     * @return
     * @throws AcmTaskException
     */
    AcmTask completeTask(Principal userThatCompletedTheTask, Long taskId) throws AcmTaskException;

    /**
     * Complete a task on behalf of the given user. Returns an AcmTask including historical information (task start
     * date, task duration in milliseconds, etc).
     *
     * @param userThatDeletedTheTask
     * @param taskId
     * @return
     * @throws AcmTaskException
     */
    AcmTask deleteTask(Principal userThatDeletedTheTask, Long taskId) throws AcmTaskException;

    /**
     * List of open tasks assigned to a user, sorted by descending due date.
     *
     * @param user
     * @return
     */
    List<AcmTask> tasksForUser(String user);

    List<Long> findTasksIdsForParentObjectIdAndParentObjectType(String parentObjectType, Long parentObjectId);

    /**
     * List of all open tasks assigned to all users, sorted by descending due date.
     *
     * @return
     */
    List<AcmTask> allTasks();

    /**
     * List of all tasks assigned to all users that due date is until numberOfDaysFromToday.
     *
     * @return
     */
    List<AcmTask> dueSpecificDateTasks(NumberOfDays numberOfDaysFromToday);

    /**
     * List of all tasks assigned to all users that due date is before today, sorted by descending due date.
     *
     * @return
     */
    List<AcmTask> pastDueTasks();

    /**
     * The given user is made assignee for the task. An exception is thrown if another user tries to claim already
     * claimed task
     *
     * @param userId
     * @param taskId
     * @return
     * @throws AcmTaskException
     */
    void claimTask(Long taskId, String userId) throws AcmTaskException;

    /**
     * Unclaim a task i.e. the assignee will be set null.
     *
     * @param taskId
     * @return
     * @throws AcmTaskException
     */
    void unclaimTask(Long taskId) throws AcmTaskException;

    /**
     * Delete current process instance
     *
     * @param parentId
     * @param processId
     * @param deleteReason
     * @param authentication
     * @param ipAddress
     * @return
     * @throws AcmTaskException
     */

    void deleteProcessInstance(String parentId, String processId, String deleteReason, Authentication authentication, String ipAddress)
            throws AcmTaskException;

    AcmTask findById(Long taskId) throws AcmTaskException;

    AcmTask save(AcmTask in) throws AcmTaskException;

    AcmTask completeTask(Principal userThatCompletedTheTask, Long taskId, String outcomePropertyName, String outcomeId)
            throws AcmTaskException;

    List<WorkflowHistoryInstance> getWorkflowHistory(String id, boolean adhoc);

    List<AcmTask> getTasksModifiedSince(Date lastModified, int start, int pageSize);

    void createFolderForTaskEvent(AcmTask task) throws AcmTaskException, AcmCreateObjectFailedException;

    AcmTask acmTaskFromActivitiTask(Task activitiTask);

    AcmTask acmTaskFromActivitiTask(Task activitiTask, Map<String, Object> processVariables, Map<String, Object> localVariables);

    AcmTask acmTaskFromActivitiTask(Task activitiTask, Map<String, Object> processVariables, Map<String, Object> localVariables,
            String taskEventName);

    byte[] getDiagram(Long id) throws AcmTaskException;

    byte[] getDiagram(String processId) throws AcmTaskException;

    AcmTask startBusinessProcess(Map<String, Object> pVars, String businessProcessName) throws AcmTaskException;

    List<ProcessInstance> findProcessesByProcessVariables(Map<String, Object> matchProcessVariables);
}
