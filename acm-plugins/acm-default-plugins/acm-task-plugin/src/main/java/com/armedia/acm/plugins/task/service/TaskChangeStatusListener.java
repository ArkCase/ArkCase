package com.armedia.acm.plugins.task.service;

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.plugins.task.model.AcmApplicationTaskEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

/**
 * @author nikolche
 */
public class TaskChangeStatusListener implements ApplicationListener<AcmApplicationTaskEvent>
{

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;

    private AcmTaskService acmTaskService;

    @Override
    public void onApplicationEvent(AcmApplicationTaskEvent event)
    {

        if (event != null)
        {

            boolean execute = checkExecution(event.getEventType());

            if (execute)
            {
                getAuditPropertyEntityAdapter().setUserId(event.getUserId());
                getAcmTaskService().copyTaskFilesAndFoldersToParent(event.getAcmTask());
                LOG.debug("Copied task files and folders to parent for task {}", event.getAcmTask().getId());
            }
        }
    }

    /**
     * @return the auditPropertyEntityAdapter
     */
    public AuditPropertyEntityAdapter getAuditPropertyEntityAdapter()
    {
        return auditPropertyEntityAdapter;
    }

    /**
     * @param auditPropertyEntityAdapter
     *            the auditPropertyEntityAdapter to set
     */
    public void setAuditPropertyEntityAdapter(AuditPropertyEntityAdapter auditPropertyEntityAdapter)
    {
        this.auditPropertyEntityAdapter = auditPropertyEntityAdapter;
    }

    /**
     * @return the acmTaskService
     */
    public AcmTaskService getAcmTaskService()
    {
        return acmTaskService;
    }

    /**
     * @param acmTaskService
     *            the acmTaskService to set
     */
    public void setAcmTaskService(AcmTaskService acmTaskService)
    {
        this.acmTaskService = acmTaskService;
    }

    private boolean checkExecution(String eventType)
    {

        return "com.armedia.acm.app.task.complete".equals(eventType) || "com.armedia.acm.activiti.task.complete".equals(eventType);
    }

}
