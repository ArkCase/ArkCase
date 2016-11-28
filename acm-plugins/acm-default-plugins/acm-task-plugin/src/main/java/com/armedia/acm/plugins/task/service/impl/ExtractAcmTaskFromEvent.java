package com.armedia.acm.plugins.task.service.impl;

import com.armedia.acm.activiti.AcmTaskEvent;
import com.armedia.acm.data.AuditPropertyEntityAdapter;
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
    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;

    public AcmTask fromEvent(AcmTaskEvent event) throws AcmTaskException
    {

        getAuditPropertyEntityAdapter().setUserId(event.getUserId());

        if (event instanceof AcmApplicationTaskEvent)
        {
            return ((AcmApplicationTaskEvent) event).getAcmTask();
        } else
        {
            if (event.getTaskEvent().equals("complete"))
            {
                try
                {
                    // When approving automated task, let activiti to perform completion, before retrieving task
                    Thread.sleep(2000);
                } catch (InterruptedException e)
                {
                }
            }
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

    public AuditPropertyEntityAdapter getAuditPropertyEntityAdapter()
    {
        return auditPropertyEntityAdapter;
    }

    public void setAuditPropertyEntityAdapter(AuditPropertyEntityAdapter auditPropertyEntityAdapter)
    {
        this.auditPropertyEntityAdapter = auditPropertyEntityAdapter;
    }
}
