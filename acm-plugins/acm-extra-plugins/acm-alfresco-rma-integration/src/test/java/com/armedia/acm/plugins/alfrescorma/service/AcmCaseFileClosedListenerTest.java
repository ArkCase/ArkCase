package com.armedia.acm.plugins.alfrescorma.service;


import com.armedia.acm.plugins.alfrescorma.model.AlfrescoRmaPluginConstants;
import com.armedia.acm.plugins.casefile.model.CaseEvent;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import org.easymock.Capture;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.Authentication;

import java.util.Date;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;

public class AcmCaseFileClosedListenerTest extends EasyMockSupport
{
    private AcmCaseFileClosedListener unit;
    private AlfrescoRecordsService mockService;
    private Authentication mockAuthentication;

    @Before
    public void setUp()
    {
        unit = new AcmCaseFileClosedListener();
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
        String user = "user";

        Capture<Authentication> captureAuth = Capture.newInstance();

        expect(mockService.checkIntegrationEnabled(AlfrescoRmaPluginConstants.CASE_CLOSE_INTEGRATION_KEY)).andReturn(Boolean.TRUE);
        mockService.declareAllContainerFilesAsRecords(
                capture(captureAuth),
                eq(caseFile.getContainer()),
                anyObject(Date.class),
                eq(caseFile.getCaseNumber()));


        CaseEvent caseEvent = new CaseEvent(caseFile, "ipAddress", user,
                AlfrescoRmaPluginConstants.CASE_CLOSED_EVENT, new Date(), true, mockAuthentication);

        replayAll();

        unit.onApplicationEvent(caseEvent);

        verifyAll();

        Authentication actual = captureAuth.getValue();

        assertEquals(user, actual.getName());
    }

}
