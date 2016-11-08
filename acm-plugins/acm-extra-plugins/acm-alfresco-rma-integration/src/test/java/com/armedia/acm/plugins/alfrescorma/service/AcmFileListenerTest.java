package com.armedia.acm.plugins.alfrescorma.service;


import com.armedia.acm.plugins.alfrescorma.model.AlfrescoRmaPluginConstants;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileAddedEvent;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.Authentication;

import java.util.Date;
import java.util.Properties;

import static org.easymock.EasyMock.*;

public class AcmFileListenerTest extends EasyMockSupport
{
    private AcmFileListener unit;
    private AlfrescoRecordsService mockService;
    private Authentication mockAuthentication;
    private GetTicketService mockTicketService;

    @Before
    public void setUp()
    {
        unit = new AcmFileListener();
        mockService = createMock(AlfrescoRecordsService.class);
        mockAuthentication = createMock(Authentication.class);
        mockTicketService = createMock(GetTicketService.class);

        unit.setAlfrescoRecordsService(mockService);
    }

    @Test
    public void doNotProceed_shouldNotDeclareRecord()
    {
        EcmFile file = new EcmFile();
        file.setContainer(new AcmContainer());

        expect(mockService.checkIntegrationEnabled(AlfrescoRmaPluginConstants.FILE_INTEGRATION_KEY)).andReturn(Boolean.FALSE);
        expect(mockAuthentication.getDetails()).andReturn("details").anyTimes();

        replayAll();

        EcmFileAddedEvent event = new EcmFileAddedEvent(file, mockAuthentication);
        unit.onApplicationEvent(event);

        verifyAll();
    }

    @Test
    public void ecmFileAddedEvent_shouldDeclareRecord() throws Exception
    {
        EcmFile file = new EcmFile();
        file.setContainer(new AcmContainer());
        file.getContainer().setContainerObjectType("containerObjectType");
        String categoryFolder = "categoryFolder";
        file.setStatus("ACTIVE");

        Properties p = new Properties();
        p.setProperty(
                AlfrescoRmaPluginConstants.CATEGORY_FOLDER_PROPERTY_KEY_PREFIX + file.getContainer().getContainerObjectType(),
                categoryFolder);
        String originatorOrg = "originatorOrg";
        p.setProperty(AlfrescoRmaPluginConstants.PROPERTY_ORIGINATOR_ORG, originatorOrg);


        expect(mockAuthentication.getDetails()).andReturn("details").anyTimes();

        expect(mockService.checkIntegrationEnabled(AlfrescoRmaPluginConstants.FILE_INTEGRATION_KEY)).andReturn(Boolean.TRUE);

        expect(mockService.getTicketService()).andReturn(mockTicketService);
        expect(mockTicketService.service(null)).andReturn("ticket");

        expect(mockService.getAlfrescoRmaProperties()).andReturn(p).atLeastOnce();

        mockService.declareFileAsRecord(
                eq(file.getContainer()),
                anyObject(Date.class),
                eq("parentObjectName"),
                eq(originatorOrg),
                eq("userId"),
                eq("ticket"),
                eq("cmisObjectId"),
                eq(file.getStatus()),
                eq(500L));


        replayAll();

        EcmFileAddedEvent event = new EcmFileAddedEvent(file, mockAuthentication);
        event.setSucceeded(true);
        event.setParentObjectName("parentObjectName");
        event.setUserId("userId");
        event.setEcmFileId("cmisObjectId");
        event.setObjectId(500L);

        unit.onApplicationEvent(event);

        verifyAll();

    }

}
