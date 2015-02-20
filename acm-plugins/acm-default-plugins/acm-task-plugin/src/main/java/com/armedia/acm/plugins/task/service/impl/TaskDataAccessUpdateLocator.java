package com.armedia.acm.plugins.task.service.impl;

import com.armedia.acm.plugins.task.exception.AcmTaskException;
import com.armedia.acm.plugins.task.model.AcmTask;
import com.armedia.acm.plugins.task.service.TaskDao;
import com.armedia.acm.services.dataaccess.service.AcmObjectDataAccessBatchUpdateLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

/**
 * Created by armdev on 2/17/15.
 */
public class TaskDataAccessUpdateLocator implements AcmObjectDataAccessBatchUpdateLocator<AcmTask>
{
    private TaskDao taskDao;

    private final transient Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public List<AcmTask> getObjectsModifiedSince(Date lastUpdate, int start, int pageSize)
    {
        return getTaskDao().getTasksModifiedSince(lastUpdate, start, pageSize);
    }

    @Override
    public void save(AcmTask task)
    {
        try
        {
            getTaskDao().save(task);
        }
        catch (AcmTaskException e)
        {
            log.error("Cannot save task: " + e.getMessage());
        }
    }

    public TaskDao getTaskDao()
    {
        return taskDao;
    }

    public void setTaskDao(TaskDao taskDao)
    {
        this.taskDao = taskDao;
    }
}
