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

import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import com.armedia.acm.plugins.ecm.service.sync.impl.AlfrescoSyncScheduledBean;
import com.armedia.acm.plugins.ecm.service.sync.impl.AlfrescoSyncService;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by dmiller on 5/15/17.
 */
public class AlfrescoSyncScheduledBeanTest
{
    private AlfrescoSyncService service = EasyMock.createMock(AlfrescoSyncService.class);

    private AlfrescoSyncScheduledBean unit;

    @Before
    public void setUp() throws Exception
    {
        unit = new AlfrescoSyncScheduledBean();

        unit.setAlfrescoSyncService(service);
    }

    @Test
    public void executeTask_ifDisabled_thenReturnImmediately() throws Exception
    {
        unit.setEnabled(false);

        replay(service);

        unit.executeTask();

        verify(service);
    }

    @Test
    public void executeTask_ifEnabled_thenCallServiceOncePerAlfrescoAuditApplication() throws Exception
    {
        unit.setEnabled(true);

        service.queryAlfrescoAuditApplications();

        replay(service);

        unit.executeTask();

        verify(service);
    }
}
