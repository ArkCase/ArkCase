package com.armedia.acm.plugins.task.service;

import com.armedia.acm.activiti.AcmTaskEvent;
import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.plugins.task.exception.AcmTaskException;
import com.armedia.acm.plugins.task.model.AcmTask;
import com.armedia.acm.plugins.task.model.NumberOfDays;
import com.armedia.acm.plugins.task.model.WorkflowHistoryInstance;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.Date;
import java.util.List;

/**
 * Created by armdev on 6/2/14.
 */
public interface TaskDao
{

    @Transactional
    AcmTask createAdHocTask(AcmTask in) throws AcmTaskException;

    void ensureCorrectAssigneeInParticipants(AcmTask in);

    /**
     * Complete a task on behalf of the given user.  Returns an AcmTask including historical information
     * (task start date, task duration in milliseconds, etc).
     *
     * @param userThatCompletedTheTask
     * @param taskId
     * @return
     * @throws AcmTaskException
     */
    AcmTask completeTask(Principal userThatCompletedTheTask, Long taskId) throws AcmTaskException;

    /**
     * Complete a task on behalf of the given user.  Returns an AcmTask including historical information
     * (task start date, task duration in milliseconds, etc).
     *
     * @param userThatDeletedTheTask
     * @param taskId
     * @return
     * @throws AcmTaskException
     */
    AcmTask deleteTask(Principal userThatDeletedTheTask, Long taskId) throws AcmTaskException;

    /**
     * List of open tasks assigned to a user, sorted by descending due date.
     * @param user
     * @return
     */
    List<AcmTask> tasksForUser(String user);

    /**
     * List of all open tasks assigned to all users, sorted by descending due date.
     * @return
     */
    List<AcmTask> allTasks();

    /**
     * List of all tasks assigned to all users that due date is until numberOfDaysFromToday.
     * @return
     */
    List<AcmTask> dueSpecificDateTasks(NumberOfDays numberOfDaysFromToday);

    /**
     * List of all tasks assigned to all users that due date is before today, sorted by descending due date.
     * @return
     */
    List<AcmTask> pastDueTasks();

    /**
     * The given user is made assignee for the task. An exception is thrown if another
     * user tries to claim already claimed task
     *
     * @param userId
     * @return
     * @throws AcmTaskException
     */
    AcmTask claimTask(Long taskId, String userId) throws AcmTaskException;


    /**
     * Unclaim a task i.e. the assignee will be set null.
     *
     * @param taskId
     * @return
     * @throws AcmTaskException
     */
    AcmTask unclaimTask(Long taskId) throws AcmTaskException;

    AcmTask findById(Long taskId) throws AcmTaskException;

    AcmTask save(AcmTask in) throws AcmTaskException;

    AcmTask completeTask(Principal userThatCompletedTheTask, Long taskId, String outcomePropertyName, String outcomeId)
        throws AcmTaskException;
    
    List<WorkflowHistoryInstance> getWorkflowHistory(String id, boolean adhoc);

    List<AcmTask> getTasksModifiedSince(Date lastModified, int start, int pageSize);

    void createFolderForTaskEvent(AcmTaskEvent event) throws AcmTaskException, AcmCreateObjectFailedException;

    void createFolderForTaskEvent(AcmTask task) throws AcmTaskException, AcmCreateObjectFailedException;
}
