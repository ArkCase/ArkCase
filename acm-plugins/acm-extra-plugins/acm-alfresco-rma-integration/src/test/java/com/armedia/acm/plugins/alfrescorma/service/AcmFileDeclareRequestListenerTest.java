package com.armedia.acm.plugins.alfrescorma.service;


import com.armedia.acm.plugins.alfrescorma.model.AlfrescoRmaPluginConstants;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileDeclareRequestEvent;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.Authentication;

import java.util.Date;
import java.util.Properties;

import static org.easymock.EasyMock.*;

public class AcmFileDeclareRequestListenerTest extends EasyMockSupport
{
    private AcmFileDeclareRequestListener unit;
    private AlfrescoRecordsService mockService;
    private Authentication mockAuthentication;
    private GetTicketService mockTicketService;

    @Before
    public void setUp()
    {
        unit = new AcmFileDeclareRequestListener();
        mockService = createMock(AlfrescoRecordsService.class);
        mockAuthentication = createMock(Authentication.class);
        unit.setAlfrescoRecordsService(mockService);
        mockTicketService = createMock(GetTicketService.class);
    }

    @Test
    public void doNotProceed_shouldNotDeclareRecord()
    {
        EcmFile file = new EcmFile();
        file.setContainer(new AcmContainer());

        expect(mockService.checkIntegrationEnabled(AlfrescoRmaPluginConstants.FILE_DECLARE_REQUEST_INTEGRATION_KEY)).andReturn(Boolean.FALSE);
        expect(mockAuthentication.getDetails()).andReturn("details").anyTimes();

        replayAll();

        EcmFileDeclareRequestEvent event = new EcmFileDeclareRequestEvent(file, mockAuthentication);
        unit.onApplicationEvent(event);

        verifyAll();
    }

    @Test
    public void ecmFileDeclareEvent_shouldDeclareRecord() throws Exception
    {
        EcmFile file = new EcmFile();
        file.setContainer(new AcmContainer());
        file.setFileId(123L);
        file.getContainer().setContainerObjectType("containerObjectType");
        file.setStatus("ACTIVE");

        Properties p = new Properties();
        p.setProperty(AlfrescoRmaPluginConstants.PROPERTY_ORIGINATOR_ORG, "Grateful Dead");

        expect(mockService.getAlfrescoRmaProperties()).andReturn(p);

        expect(mockService.getTicketService()).andReturn(mockTicketService);
        expect(mockTicketService.service(null)).andReturn("ticket");

        expect(mockAuthentication.getDetails()).andReturn("details").anyTimes();

        expect(mockService.checkIntegrationEnabled(AlfrescoRmaPluginConstants.FILE_DECLARE_REQUEST_INTEGRATION_KEY)).andReturn(Boolean.TRUE);

        mockService.declareFileAsRecord(
                eq(file.getContainer()),
                anyObject(Date.class),
                eq("parentObjectName"),
                eq("Grateful Dead"),
                eq("userId"),
                eq("ticket"),
                eq("cmisObjectId"),
                eq(file.getStatus()),
                eq(500L));

        replayAll();

        EcmFileDeclareRequestEvent event = new EcmFileDeclareRequestEvent(file, mockAuthentication);
        event.setSucceeded(true);
        event.setParentObjectName("parentObjectName");
        event.setUserId("userId");
        event.setEcmFileId("cmisObjectId");
        event.setObjectId(500L);

        unit.onApplicationEvent(event);

        verifyAll();

    }

}
