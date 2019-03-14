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

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;

import com.armedia.acm.plugins.alfrescorma.model.AlfrescoRmaConfig;
import com.armedia.acm.plugins.ecm.dao.AcmFolderDao;
import com.armedia.acm.plugins.ecm.model.AcmCmisObjectList;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.EcmFolderDeclareRequestEvent;
import com.armedia.acm.plugins.ecm.service.AcmFolderService;

import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.Authentication;

import java.util.Date;

public class AcmFolderDeclareRequestListenerTest extends EasyMockSupport
{
    private AcmFolderDeclareRequestListener unit;
    private AlfrescoRecordsService mockService;
    private Authentication mockAuthentication;
    private AcmFolderDao mockAcmFolderDao;
    private AcmFolderService mockAcmFolderService;
    private AlfrescoRmaConfig rmaConfig;

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
        rmaConfig = new AlfrescoRmaConfig();
        rmaConfig.setIntegrationEnabled(true);
    }

    @Test
    public void doNotProceed_shouldNotDeclareRecords()
    {
        AcmCmisObjectList acmCmisObjectList = new AcmCmisObjectList();
        acmCmisObjectList.setContainerObjectId(123L);
        acmCmisObjectList.setContainerObjectType("CASE_FILE");

        AcmContainer acmContainer = new AcmContainer();

        rmaConfig.setDeclareFolderRecordOnDeclareRequest(false);
        expect(mockService.getRmaConfig()).andReturn(rmaConfig);
        expect(mockAuthentication.getDetails()).andReturn("details").anyTimes();
        expect(mockAuthentication.getName()).andReturn("user").anyTimes();

        replayAll();
        EcmFolderDeclareRequestEvent event = new EcmFolderDeclareRequestEvent(acmCmisObjectList, acmContainer, mockAuthentication);
        unit.onApplicationEvent(event);

        verifyAll();
    }

    @Test
    public void successfulFolderDeclareRequest_shouldDeclareRecords()
    {
        AcmCmisObjectList acmCmisObjectList = new AcmCmisObjectList();
        acmCmisObjectList.setContainerObjectId(123L);
        acmCmisObjectList.setContainerObjectType("CASE_FILE");
        acmCmisObjectList.setFolderId(54321L);

        AcmContainer acmContainer = new AcmContainer();
        acmContainer.setContainerObjectTitle("20140806_1055");

        AcmFolder acmFolder = new AcmFolder();
        acmFolder.setId(234L);

        rmaConfig.setDeclareFolderRecordOnDeclareRequest(true);
        expect(mockService.getRmaConfig()).andReturn(rmaConfig);
        expect(mockAuthentication.getDetails()).andReturn("details").anyTimes();
        expect(mockAuthentication.getName()).andReturn("user").anyTimes();
        expect(mockAcmFolderService.findById(acmCmisObjectList.getFolderId())).andReturn(acmFolder);
        expect(mockAcmFolderDao.save(acmFolder)).andReturn(acmFolder);
        mockService.declareAllFilesInFolderAsRecords(eq(acmCmisObjectList),
                eq(acmContainer),
                anyObject(Date.class),
                eq(acmContainer.getContainerObjectTitle()));
        replayAll();

        EcmFolderDeclareRequestEvent event = new EcmFolderDeclareRequestEvent(acmCmisObjectList, acmContainer, mockAuthentication);
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

        rmaConfig.setDeclareFolderRecordOnDeclareRequest(true);
        expect(mockService.getRmaConfig()).andReturn(rmaConfig);
        expect(mockAuthentication.getDetails()).andReturn("details").anyTimes();
        expect(mockAuthentication.getName()).andReturn("user").anyTimes();

        replayAll();

        EcmFolderDeclareRequestEvent event = new EcmFolderDeclareRequestEvent(acmCmisObjectList, acmContainer, mockAuthentication);
        event.setSucceeded(false);
        event.setUserId("user");
        event.setEventDate(new Date());
        unit.onApplicationEvent(event);

        verifyAll();
    }
}
