package com.armedia.acm.plugins.task.service;

import com.armedia.acm.plugins.task.exception.AcmTaskException;
import com.armedia.acm.plugins.task.model.AcmTask;
import com.armedia.acm.plugins.task.model.NumberOfDays;
import com.armedia.acm.plugins.task.model.WorkflowHistoryInstance;

import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;

/**
 * Created by armdev on 6/2/14.
 */
public interface TaskDao
{
    @Transactional
    AcmTask createAdHocTask(AcmTask in) throws AcmTaskException;

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

    AcmTask findById(Long taskId) throws AcmTaskException;

    AcmTask save(AcmTask in) throws AcmTaskException;

    AcmTask completeTask(Principal userThatCompletedTheTask, Long taskId, String outcomePropertyName, String outcomeId)
        throws AcmTaskException;
    
    List<WorkflowHistoryInstance> getWorkflowHistory(String processId);
}
