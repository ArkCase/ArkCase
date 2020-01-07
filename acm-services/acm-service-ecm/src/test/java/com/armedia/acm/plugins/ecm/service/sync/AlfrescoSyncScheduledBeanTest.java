package com.armedia.acm.plugins.ecm.service.sync;

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

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import com.armedia.acm.plugins.ecm.service.sync.impl.AlfrescoSyncScheduledBean;
import com.armedia.acm.plugins.ecm.service.sync.impl.AlfrescoSyncService;

import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;

/**
 * Created by dmiller on 5/15/17.
 */
public class AlfrescoSyncScheduledBeanTest extends EasyMockSupport
{
    private AlfrescoSyncService service;

    private AlfrescoSyncScheduledBean unit;
    private AlfrescoSyncConfig syncConfig;

    @Before
    public void setUp() throws Exception
    {
        service = createMock(AlfrescoSyncService.class);
        unit = new AlfrescoSyncScheduledBean();
        unit.setAlfrescoSyncService(service);

        syncConfig = new AlfrescoSyncConfig();
        unit.setAlfrescoSyncConfig(syncConfig);
    }

    @Test
    public void executeTask_ifDisabled_thenReturnImmediately() throws Exception
    {
        syncConfig.setEnabled(false);

        unit.executeJob(null);
    }

    @Test
    public void executeTask_ifEnabled_thenCallServiceOncePerAlfrescoAuditApplication() throws Exception
    {
        syncConfig.setEnabled(true);

        JobExecutionContext mockContext = createMock(JobExecutionContext.class);
        JobDetail jobDetail = createMock(JobDetail.class);
        JobDataMap jobDataMap = createMock(JobDataMap.class);

        expect(mockContext.getJobDetail()).andReturn(jobDetail);
        expect(jobDetail.getJobDataMap()).andReturn(jobDataMap);
        service.queryAlfrescoAuditApplications(jobDataMap);
        expectLastCall().once();

        replay(service, mockContext, jobDataMap, jobDetail);

        unit.executeJob(mockContext);

        verify(service, mockContext, jobDataMap, jobDetail);
    }
}
