package com.armedia.acm.plugins.alfrescorma.service;


import com.armedia.acm.plugins.alfrescorma.model.AlfrescoRmaPluginConstants;
import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.complaint.model.ComplaintClosedEvent;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.Authentication;

import java.util.Date;

import static org.easymock.EasyMock.*;

public class AcmComplaintClosedListenerTest extends EasyMockSupport
{
    private AcmComplaintClosedListener unit;
    private AlfrescoRecordsService mockService;

    @Before
    public void setUp()
    {
        unit = new AcmComplaintClosedListener();
        mockService = createMock(AlfrescoRecordsService.class);

        unit.setAlfrescoRecordsService(mockService);
    }

    @Test
    public void doNotProceed_shouldNotDeclareRecords()
    {
        expect(mockService.checkIntegrationEnabled(AlfrescoRmaPluginConstants.COMPLAINT_CLOSE_INTEGRATION_KEY)).andReturn(Boolean.FALSE);

        ComplaintClosedEvent event = new ComplaintClosedEvent(new Complaint(), true, "user", new Date());

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

        expect(mockService.checkIntegrationEnabled(AlfrescoRmaPluginConstants.COMPLAINT_CLOSE_INTEGRATION_KEY)).andReturn(Boolean.TRUE);
        mockService.declareAllContainerFilesAsRecords(
                anyObject(Authentication.class),
                eq(complaint.getContainer()),
                anyObject(Date.class),
                eq(complaint.getComplaintNumber()));

        ComplaintClosedEvent event = new ComplaintClosedEvent(complaint, true, "user", new Date());

        replayAll();

        unit.onApplicationEvent(event);

        verifyAll();
    }

}
