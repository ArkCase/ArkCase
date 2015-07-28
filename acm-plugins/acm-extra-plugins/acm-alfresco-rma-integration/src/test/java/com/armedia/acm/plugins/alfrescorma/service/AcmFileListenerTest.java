package com.armedia.acm.plugins.alfrescorma.service;


import com.armedia.acm.muletools.mulecontextmanager.MuleContextManager;
import com.armedia.acm.plugins.alfrescorma.model.AcmRecord;
import com.armedia.acm.plugins.alfrescorma.model.AlfrescoRmaPluginConstants;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileAddedEvent;
import org.easymock.Capture;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.Authentication;

import java.util.Collections;
import java.util.Properties;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

public class AcmFileListenerTest extends EasyMockSupport
{
    private AcmFileListener unit;
    private AlfrescoRecordsService mockService;
    private MuleContextManager mockMuleContextManager;
    private Authentication mockAuthentication;

    @Before
    public void setUp()
    {
        unit = new AcmFileListener();
        mockService = createMock(AlfrescoRecordsService.class);
        mockMuleContextManager = createMock(MuleContextManager.class);
        mockAuthentication = createMock(Authentication.class);

        unit.setAlfrescoRecordsService(mockService);
        unit.setMuleContextManager(mockMuleContextManager);
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

        Properties p = new Properties();
        p.setProperty(
                AlfrescoRmaPluginConstants.CATEGORY_FOLDER_PROPERTY_KEY_PREFIX + file.getContainer().getContainerObjectType(),
                categoryFolder);
        String originatorOrg = "originatorOrg";
        p.setProperty(AlfrescoRmaPluginConstants.PROPERTY_ORIGINATOR_ORG, originatorOrg);

        Capture<AcmRecord> captureRecord = new Capture<>();

        expect(mockAuthentication.getDetails()).andReturn("details").anyTimes();

        expect(mockService.checkIntegrationEnabled(AlfrescoRmaPluginConstants.FILE_INTEGRATION_KEY)).andReturn(Boolean.TRUE);

        expect(mockService.getAlfrescoRmaProperties()).andReturn(p).atLeastOnce();

        expect(mockService.getRmaMessageProperties()).andReturn(Collections.emptyMap());

        mockMuleContextManager.dispatch(
                eq(AlfrescoRmaPluginConstants.RECORD_MULE_ENDPOINT),
                capture(captureRecord),
                eq(Collections.emptyMap()));

        replayAll();

        EcmFileAddedEvent event = new EcmFileAddedEvent(file, mockAuthentication);
        event.setSucceeded(true);

        unit.onApplicationEvent(event);

        verifyAll();

        AcmRecord actual = captureRecord.getValue();

        assertEquals(originatorOrg, actual.getOriginatorOrg());
        assertEquals(categoryFolder, actual.getCategoryFolder());
    }

}
