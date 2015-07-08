package com.armedia.acm.plugins.alfrescorma.service;


import com.armedia.acm.plugins.alfrescorma.model.AlfrescoRmaPluginConstants;
import com.armedia.acm.plugins.casefile.model.CaseEvent;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.Authentication;

import java.util.Date;

import static org.easymock.EasyMock.*;

public class AcmCaseFileStatusChangedListenerTest extends EasyMockSupport
{
    private AcmCaseFileStatusChangedListener unit;
    private AlfrescoRecordsService mockService;
    private Authentication mockAuthentication;

    @Before
    public void setUp()
    {
        unit = new AcmCaseFileStatusChangedListener();
        mockService = createMock(AlfrescoRecordsService.class);
        mockAuthentication = createMock(Authentication.class);

        unit.setAlfrescoRecordsService(mockService);
    }

    @Test
    public void doNotProceed_shouldNotDeclareRecords()
    {
        expect(mockService.checkIntegrationEnabled(AlfrescoRmaPluginConstants.CASE_CLOSE_INTEGRATION_KEY)).andReturn(Boolean.FALSE);

        CaseEvent caseEvent = new CaseEvent(new CaseFile(), "ipAddress", "user", "eventType", new Date(), true,
                mockAuthentication);

        replayAll();

        unit.onApplicationEvent(caseEvent);

        verifyAll();
    }

    @Test
    public void notACaseClosedEvent_shouldNotDeclareRecords()
    {
        expect(mockService.checkIntegrationEnabled(AlfrescoRmaPluginConstants.CASE_CLOSE_INTEGRATION_KEY)).andReturn(Boolean.TRUE);

        CaseEvent caseEvent = new CaseEvent(new CaseFile(), "ipAddress", "user", "caseOpenEvent", new Date(), true,
                mockAuthentication);

        replayAll();

        unit.onApplicationEvent(caseEvent);

        verifyAll();
    }

    @Test
    public void caseClosedEvent_shouldDeclareRecords()
    {
        CaseFile caseFile = new CaseFile();
        caseFile.setContainer(new AcmContainer());
        caseFile.setCaseNumber("caseNumber");

        expect(mockService.checkIntegrationEnabled(AlfrescoRmaPluginConstants.CASE_CLOSE_INTEGRATION_KEY)).andReturn(Boolean.TRUE);
        mockService.declareAllContainerFilesAsRecords(
                eq(mockAuthentication),
                eq(caseFile.getContainer()),
                anyObject(Date.class),
                eq(caseFile.getCaseNumber()));

        CaseEvent caseEvent = new CaseEvent(caseFile, "ipAddress", "user",
                AlfrescoRmaPluginConstants.CASE_CLOSED_EVENT, new Date(), true, mockAuthentication);

        replayAll();

        unit.onApplicationEvent(caseEvent);

        verifyAll();
    }

}
