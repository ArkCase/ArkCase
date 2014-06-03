package com.armedia.acm.plugins.task.service;

import com.armedia.acm.plugins.task.exception.AcmTaskException;
import com.armedia.acm.plugins.task.model.AcmTask;

import java.security.Principal;
import java.util.List;

/**
 * Created by armdev on 6/2/14.
 */
public interface TaskDao
{
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
     * List of open tasks assigned to a user, sorted by descending due date.
     * @param user
     * @return
     */
    List<AcmTask> tasksForUser(String user);
}
