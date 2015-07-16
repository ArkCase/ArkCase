package com.armedia.acm.plugins.alfrescorma.service;


import com.armedia.acm.plugins.alfrescorma.model.AlfrescoRmaPluginConstants;
import com.armedia.acm.plugins.casefile.model.CaseEvent;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.ecm.dao.AcmFolderDao;
import com.armedia.acm.plugins.ecm.model.AcmCmisObjectList;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.EcmFolderDeclareRequestEvent;
import com.armedia.acm.plugins.ecm.service.AcmFolderService;
import org.easymock.Capture;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Date;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;

public class AcmFolderDeclareRequestListenerTest extends EasyMockSupport
{
    private AcmFolderDeclareRequestListener unit;
    private AlfrescoRecordsService mockService;
    private Authentication mockAuthentication;
    private AcmFolderDao mockAcmFolderDao;
    private AcmFolderService mockAcmFolderService;

    @Before
    public void setUp()
    {
        unit = new AcmFolderDeclareRequestListener();
        mockService = createMock(AlfrescoRecordsService.class);
        mockAuthentication = createMock(Authentication.class);
        mockAcmFolderDao = createMock(AcmFolderDao.class);
        mockAcmFolderService = createMock(AcmFolderService.class);
        unit.setAlfrescoRecordsService(mockService);
        unit.setAcmFolderDao(mockAcmFolderDao);
        unit.setAcmFolderService(mockAcmFolderService);
    }

    @Test
    public void doNotProceed_shouldNotDeclareRecords()
    {
        AcmCmisObjectList acmCmisObjectList = new AcmCmisObjectList();
        acmCmisObjectList.setContainerObjectId(123L);
        acmCmisObjectList.setContainerObjectType("CASE_FILE");

        AcmContainer acmContainer = new AcmContainer();

        expect(mockService.checkIntegrationEnabled(AlfrescoRmaPluginConstants.FOLDER_DECLARE_REQUEST_INTEGRATION_KEY)).andReturn(Boolean.FALSE);
        expect(mockAuthentication.getDetails()).andReturn("details").anyTimes();
        expect(mockAuthentication.getName()).andReturn("user").anyTimes();

        replayAll();
        EcmFolderDeclareRequestEvent event = new EcmFolderDeclareRequestEvent(acmCmisObjectList,acmContainer,mockAuthentication);
        unit.onApplicationEvent(event);

        verifyAll();
    }

    @Test
    public void successfulFolderDeclareRequest_shouldNotDeclareRecords()
    {
        AcmCmisObjectList acmCmisObjectList = new AcmCmisObjectList();
        acmCmisObjectList.setContainerObjectId(123L);
        acmCmisObjectList.setContainerObjectType("CASE_FILE");
        acmCmisObjectList.setFolderId(54321L);

        AcmContainer acmContainer = new AcmContainer();
        acmContainer.setContainerObjectTitle("20140806_1055");

        AcmFolder acmFolder = new AcmFolder();
        acmFolder.setId(234L);

        expect(mockService.checkIntegrationEnabled(AlfrescoRmaPluginConstants.FOLDER_DECLARE_REQUEST_INTEGRATION_KEY)).andReturn(Boolean.TRUE);
        expect(mockAuthentication.getDetails()).andReturn("details").anyTimes();
        expect(mockAuthentication.getName()).andReturn("user").anyTimes();
        expect(mockAcmFolderService.findById(acmCmisObjectList.getFolderId())).andReturn(acmFolder);
        expect(mockAcmFolderDao.save(acmFolder)).andReturn(acmFolder);
        mockService.declareAllFilesInFolderAsRecords(eq(acmCmisObjectList),
                eq(acmContainer),
                anyObject(Date.class),
                eq(acmContainer.getContainerObjectTitle()));
        replayAll();


        EcmFolderDeclareRequestEvent event = new EcmFolderDeclareRequestEvent(acmCmisObjectList,acmContainer,mockAuthentication);
        event.setSucceeded(true);
        event.setUserId("user");
        event.setEventDate(new Date());
        unit.onApplicationEvent(event);


        verifyAll();
    }

    @Test
    public void unSuccessfulFolderDeclareRequest_shouldNotDeclareRecords()
    {
        AcmCmisObjectList acmCmisObjectList = new AcmCmisObjectList();
        acmCmisObjectList.setContainerObjectId(123L);
        acmCmisObjectList.setContainerObjectType("CASE_FILE");
        acmCmisObjectList.setFolderId(54321L);

        AcmContainer acmContainer = new AcmContainer();

        expect(mockService.checkIntegrationEnabled(AlfrescoRmaPluginConstants.FOLDER_DECLARE_REQUEST_INTEGRATION_KEY)).andReturn(Boolean.TRUE);
        expect(mockAuthentication.getDetails()).andReturn("details").anyTimes();
        expect(mockAuthentication.getName()).andReturn("user").anyTimes();

        replayAll();

        EcmFolderDeclareRequestEvent event = new EcmFolderDeclareRequestEvent(acmCmisObjectList,acmContainer,mockAuthentication);
        event.setSucceeded(false);
        event.setUserId("user");
        event.setEventDate(new Date());
        unit.onApplicationEvent(event);


        verifyAll();
    }
}
