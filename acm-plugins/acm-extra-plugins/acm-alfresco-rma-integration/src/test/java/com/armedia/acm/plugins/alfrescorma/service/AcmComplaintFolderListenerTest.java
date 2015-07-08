package com.armedia.acm.plugins.alfrescorma.service;


import com.armedia.acm.plugins.alfrescorma.model.AcmRecordFolder;
import com.armedia.acm.plugins.alfrescorma.model.AlfrescoRmaPluginConstants;
import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.complaint.model.ComplaintCreatedEvent;
import org.easymock.Capture;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.mule.api.client.MuleClient;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

public class AcmComplaintFolderListenerTest extends EasyMockSupport
{
    private AcmComplaintFolderListener unit;
    private AlfrescoRecordsService mockService;
    private MuleClient mockMuleClient;

    @Before
    public void setUp()
    {
        unit = new AcmComplaintFolderListener();
        mockService = createMock(AlfrescoRecordsService.class);
        mockMuleClient = createMock(MuleClient.class);

        unit.setAlfrescoRecordsService(mockService);
        unit.setMuleClient(mockMuleClient);
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

        Capture<AcmRecordFolder> captureFolder = new Capture<>();

        expect(mockService.checkIntegrationEnabled(AlfrescoRmaPluginConstants.COMPLAINT_FOLDER_INTEGRATION_KEY)).andReturn(Boolean.TRUE);
        mockMuleClient.dispatch(
                eq(AlfrescoRmaPluginConstants.FOLDER_MULE_ENDPOINT),
                capture(captureFolder),
                eq(null));

        ComplaintCreatedEvent event = new ComplaintCreatedEvent(new Complaint());
        event.setSucceeded(true);
        event.setComplaintNumber(complaint.getComplaintNumber());

        replayAll();

        unit.onApplicationEvent(event);

        verifyAll();

        AcmRecordFolder actual = captureFolder.getValue();

        assertEquals("COMPLAINT", actual.getFolderType());
        assertEquals(complaint.getComplaintNumber(), actual.getFolderName());
    }

}
