package com.armedia.acm.plugins.ecm.service.sync.impl;

import com.armedia.acm.scheduler.AcmSchedulableBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by dmiller on 5/15/17.
 */
public class AlfrescoSyncScheduledBean implements AcmSchedulableBean
{
    private AlfrescoSyncService alfrescoSyncService;
    private boolean enabled;
    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void executeTask()
    {
        if (!isEnabled())
        {
            log.info("Alfresco sync service is disabled - returning immediately.");
            return;
        }

        getAlfrescoSyncService().queryAlfrescoAuditApplications();


    }

    public AlfrescoSyncService getAlfrescoSyncService()
    {
        return alfrescoSyncService;
    }

    public void setAlfrescoSyncService(AlfrescoSyncService alfrescoSyncService)
    {
        this.alfrescoSyncService = alfrescoSyncService;
    }


    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    public boolean isEnabled()
    {
        return enabled;
    }

}
