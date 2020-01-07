package com.armedia.acm.plugins.ecm.service.sync.impl;

/*-
 * #%L
 * ACM Service: Enterprise Content Management
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

import com.armedia.acm.plugins.ecm.service.sync.AlfrescoSyncConfig;
import com.armedia.acm.quartz.scheduler.AcmJobDescriptor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Created by dmiller on 5/15/17.
 */
public class AlfrescoSyncScheduledBean extends AcmJobDescriptor
{
    private transient final Logger log = LogManager.getLogger(getClass());
    private AlfrescoSyncService alfrescoSyncService;
    private AlfrescoSyncConfig alfrescoSyncConfig;

    @Override
    public String getJobName()
    {
        return "alfrescoSyncScheduledJob";
    }

    @Override
    public void executeJob(JobExecutionContext context) throws JobExecutionException
    {
        if (!alfrescoSyncConfig.getEnabled())
        {
            log.info("Alfresco sync service is disabled - returning immediately.");
            return;
        }

        JobDataMap lastAuditIdsPerApplication = context.getJobDetail().getJobDataMap();
        alfrescoSyncService.queryAlfrescoAuditApplications(lastAuditIdsPerApplication);
    }

    public AlfrescoSyncConfig getAlfrescoSyncConfig()
    {
        return alfrescoSyncConfig;
    }

    public void setAlfrescoSyncConfig(AlfrescoSyncConfig alfrescoSyncConfig)
    {
        this.alfrescoSyncConfig = alfrescoSyncConfig;
    }

    public AlfrescoSyncService getAlfrescoSyncService()
    {
        return alfrescoSyncService;
    }

    public void setAlfrescoSyncService(AlfrescoSyncService alfrescoSyncService)
    {
        this.alfrescoSyncService = alfrescoSyncService;
    }
}
