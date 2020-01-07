package com.armedia.acm.plugins.alfrescorma.service;

/*-
 * #%L
 * ACM Extra Plugin: Alfresco RMA Integration
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

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;

import com.armedia.acm.auth.AcmAuthentication;
import com.armedia.acm.auth.AcmAuthenticationManager;
import com.armedia.acm.plugins.alfrescorma.model.AlfrescoRmaConfig;
import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.complaint.model.ComplaintClosedEvent;
import com.armedia.acm.plugins.ecm.model.AcmContainer;

import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.Authentication;

import java.util.Date;

public class AcmComplaintClosedListenerTest extends EasyMockSupport
{
    private AcmComplaintClosedListener unit;
    private AlfrescoRecordsService mockService;
    private AcmAuthenticationManager mockAuthenticationManager;
    private AlfrescoRmaConfig rmaConfig;
    private String ipAddress = "ipAddress";

    @Before
    public void setUp()
    {
        unit = new AcmComplaintClosedListener();
        mockService = createMock(AlfrescoRecordsService.class);
        mockAuthenticationManager = createMock(AcmAuthenticationManager.class);

        unit.setAlfrescoRecordsService(mockService);
        unit.setAuthenticationManager(mockAuthenticationManager);
        rmaConfig = new AlfrescoRmaConfig();
        rmaConfig.setIntegrationEnabled(true);
    }

    @Test
    public void doNotProceed_shouldNotDeclareRecords()
    {
        rmaConfig.setDeclareRecordsOnComplaintClose(false);
        expect(mockService.getRmaConfig()).andReturn(rmaConfig);

        ComplaintClosedEvent event = new ComplaintClosedEvent(new Complaint(), true, "user", new Date(), ipAddress);

        replayAll();

        unit.onApplicationEvent(event);

        verifyAll();
    }

    @Test
    public void complaintClosedEvent_shouldDeclareRecords()
    {
        Complaint complaint = new Complaint();
        complaint.setContainer(new AcmContainer());
        complaint.setComplaintNumber("complaintNumber");

        rmaConfig.setDeclareRecordsOnComplaintClose(true);
        expect(mockService.getRmaConfig()).andReturn(rmaConfig);
        expect(mockAuthenticationManager.getAcmAuthentication(anyObject()))
                .andReturn(new AcmAuthentication(null, "", "", true, ""));
        mockService.declareAllContainerFilesAsRecords(
                anyObject(Authentication.class),
                eq(complaint.getContainer()),
                anyObject(Date.class),
                eq(complaint.getComplaintNumber()));

        ComplaintClosedEvent event = new ComplaintClosedEvent(complaint, true, "user", new Date(), ipAddress);

        replayAll();

        unit.onApplicationEvent(event);

        verifyAll();
    }

}
