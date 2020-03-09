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

import static org.easymock.EasyMock.expect;

import com.armedia.acm.plugins.alfrescorma.model.AlfrescoRmaConfig;
import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.complaint.model.ComplaintConstants;
import com.armedia.acm.plugins.complaint.model.ComplaintCreatedEvent;

import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;

public class AcmComplaintFolderListenerTest extends EasyMockSupport
{
    private AcmComplaintFolderListener unit;
    private AlfrescoRecordsService mockService;
    private AlfrescoRmaConfig rmaConfig;

    @Before
    public void setUp()
    {
        unit = new AcmComplaintFolderListener();
        mockService = createMock(AlfrescoRecordsService.class);

        unit.setAlfrescoRecordsService(mockService);
        rmaConfig = new AlfrescoRmaConfig();
        rmaConfig.setIntegrationEnabled(true);
    }

    @Test
    public void doNotProceed_shouldNotCreateRecordFolder()
    {
        rmaConfig.setCreateRecordFolderOnComplaintCreate(false);
        expect(mockService.getRmaConfig()).andReturn(rmaConfig);

        ComplaintCreatedEvent event = new ComplaintCreatedEvent(new Complaint());

        replayAll();

        unit.onApplicationEvent(event);

        verifyAll();
    }

    @Test
    public void complaintCreatedEvent_shouldCreateRecordFolder() throws Exception
    {
        Complaint complaint = new Complaint();
        complaint.setComplaintNumber("complaintNumber");


        rmaConfig.setCreateRecordFolderOnComplaintCreate(true);
        expect(mockService.getRmaConfig()).andReturn(rmaConfig);

        expect(mockService.findFolder(ComplaintConstants.OBJECT_TYPE)).andReturn(null);
        expect(mockService.addDateInAlfrescoStructure(null, ComplaintConstants.OBJECT_TYPE)).andReturn(null);
        expect(mockService.createOrFindRecordFolderOrRecordCategory(complaint.getComplaintNumber(), null, "Record Folder")).andReturn(null);

        ComplaintCreatedEvent event = new ComplaintCreatedEvent(new Complaint());
        event.setSucceeded(true);
        event.setComplaintNumber(complaint.getComplaintNumber());

        replayAll();

        unit.onApplicationEvent(event);

        verifyAll();
    }

}
