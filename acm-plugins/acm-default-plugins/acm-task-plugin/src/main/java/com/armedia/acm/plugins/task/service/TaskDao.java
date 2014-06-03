package com.armedia.acm.plugins.task.service;

import com.armedia.acm.plugins.task.model.AcmTask;

import java.util.List;

/**
 * Created by armdev on 6/2/14.
 */
public interface TaskDao
{
    /**
     * List of open tasks assigned to a user, sorted by descending due date.
     * @param user
     * @return
     */
    List<AcmTask> tasksForUser(String user);
}
