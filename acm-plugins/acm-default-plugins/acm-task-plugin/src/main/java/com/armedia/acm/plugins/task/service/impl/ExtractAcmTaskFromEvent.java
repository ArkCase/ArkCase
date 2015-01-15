package com.armedia.acm.plugins.task.service.impl;

import com.armedia.acm.activiti.AcmTaskEvent;
import com.armedia.acm.plugins.task.exception.AcmTaskException;
import com.armedia.acm.plugins.task.model.AcmApplicationTaskEvent;
import com.armedia.acm.plugins.task.model.AcmTask;
import com.armedia.acm.plugins.task.service.TaskDao;

/**
 * Created by armdev on 1/14/15.
 */
public class ExtractAcmTaskFromEvent
{
    private TaskDao dao;

    public AcmTask fromEvent(AcmTaskEvent event) throws AcmTaskException
    {
        if ( event instanceof AcmApplicationTaskEvent )
        {
            return ((AcmApplicationTaskEvent) event).getAcmTask();
        }
        else
        {
            return getDao().findById(event.getObjectId());
        }
    }

    public TaskDao getDao()
    {
        return dao;
    }

    public void setDao(TaskDao dao)
    {
        this.dao = dao;
    }
}
