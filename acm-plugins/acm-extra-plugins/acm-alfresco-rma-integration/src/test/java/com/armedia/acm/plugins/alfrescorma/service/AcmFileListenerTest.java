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
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileAddedEvent;

import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.Authentication;

import java.util.Date;

public class AcmFileListenerTest extends EasyMockSupport
{
    private AcmFileListener unit;
    private AlfrescoRecordsService mockService;
    private Authentication mockAuthentication;
    private AlfrescoRmaConfig rmaConfig;

    @Before
    public void setUp()
    {
        unit = new AcmFileListener();
        mockService = createMock(AlfrescoRecordsService.class);
        mockAuthentication = createMock(Authentication.class);

        unit.setAlfrescoRecordsService(mockService);
        rmaConfig = new AlfrescoRmaConfig();
        rmaConfig.setIntegrationEnabled(true);
    }

    @Test
    public void doNotProceed_shouldNotDeclareRecord()
    {
        EcmFile file = new EcmFile();
        file.setContainer(new AcmContainer());

        rmaConfig.setDeclareRecordFolderOnFileUpload(false);
        expect(mockService.getRmaConfig()).andReturn(rmaConfig);
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
        String originatorOrg = "originatorOrg";

        rmaConfig.setDefaultOriginatorOrg(originatorOrg);
        rmaConfig.setCategoryFolderCaseFile(categoryFolder);
        rmaConfig.setDeclareRecordFolderOnFileUpload(true);
        expect(mockService.getRmaConfig()).andReturn(rmaConfig);
        expect(mockAuthentication.getDetails()).andReturn("details").anyTimes();
        mockService.declareFileAsRecord(eq(file.getContainer()), anyObject(Date.class), eq("parentObjectName"), eq(originatorOrg),
                eq("userId"), eq("cmisObjectId"), eq(file.getStatus()), eq(500L));

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
