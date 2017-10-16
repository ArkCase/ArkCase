package com.armedia.acm.plugins.alfrescorma.service;

import static org.easymock.EasyMock.expect;

import com.armedia.acm.plugins.alfrescorma.model.AlfrescoRmaPluginConstants;
import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.complaint.model.ComplaintConstants;
import com.armedia.acm.plugins.complaint.model.ComplaintCreatedEvent;

import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;

public class AcmComplaintFolderListenerTest extends EasyMockSupport
{
    private AcmComplaintFolderListener unit;
    private AlfrescoRecordsService mockService;

    @Before
    public void setUp()
    {
        unit = new AcmComplaintFolderListener();
        mockService = createMock(AlfrescoRecordsService.class);

        unit.setAlfrescoRecordsService(mockService);
    }

    @Test
    public void doNotProceed_shouldNotCreateRecordFolder()
    {
        expect(mockService.checkIntegrationEnabled(AlfrescoRmaPluginConstants.COMPLAINT_FOLDER_INTEGRATION_KEY)).andReturn(Boolean.FALSE);

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

        expect(mockService.checkIntegrationEnabled(AlfrescoRmaPluginConstants.COMPLAINT_FOLDER_INTEGRATION_KEY)).andReturn(Boolean.TRUE);

        expect(mockService.findFolder(ComplaintConstants.OBJECT_TYPE)).andReturn(null);
        expect(mockService.createOrFindRecordFolder(complaint.getComplaintNumber(), null)).andReturn(null);

        ComplaintCreatedEvent event = new ComplaintCreatedEvent(new Complaint());
        event.setSucceeded(true);
        event.setComplaintNumber(complaint.getComplaintNumber());

        replayAll();

        unit.onApplicationEvent(event);

        verifyAll();
    }

}
