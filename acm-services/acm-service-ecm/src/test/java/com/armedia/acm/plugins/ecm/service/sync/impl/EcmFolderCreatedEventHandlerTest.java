package com.armedia.acm.plugins.ecm.service.sync.impl;

/*-
 * #%L
 * ACM Service: Enterprise Content Management
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

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.plugins.ecm.dao.AcmFolderDao;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.plugins.ecm.model.sync.EcmEvent;
import com.armedia.acm.plugins.ecm.model.sync.EcmEventType;
import com.armedia.acm.plugins.ecm.service.AcmFolderService;
import com.armedia.acm.plugins.ecm.service.impl.EcmFileParticipantService;
import com.armedia.acm.plugins.ecm.utils.FolderAndFilesUtils;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.NoResultException;

/**
 * Created by dmiller on 5/17/17.
 */
public class EcmFolderCreatedEventHandlerTest
{
    private EcmFolderCreatedEventHandler unit;

    private AcmFolderDao acmFolderDao = EasyMock.createMock(AcmFolderDao.class);
    private AcmFolderService acmFolderService = EasyMock.createMock(AcmFolderService.class);
    private AuditPropertyEntityAdapter auditPropertyEntityAdapter = EasyMock.createMock(AuditPropertyEntityAdapter.class);
    private EcmFileParticipantService fileParticipantService = EasyMock.createMock(EcmFileParticipantService.class);
    private FolderAndFilesUtils folderAndFilesUtils = EasyMock.createMock(FolderAndFilesUtils.class);

    private Object[] mocks = { folderAndFilesUtils, acmFolderDao, acmFolderService, auditPropertyEntityAdapter, fileParticipantService };

    private EcmEvent folderCreated;

    @Before
    public void setUp() throws Exception
    {
        unit = new EcmFolderCreatedEventHandler();

        unit.setFolderDao(acmFolderDao);
        unit.setFolderService(acmFolderService);
        unit.setAuditPropertyEntityAdapter(auditPropertyEntityAdapter);
        unit.setFileParticipantService(fileParticipantService);
        unit.setFolderAndFilesUtils(folderAndFilesUtils);

        folderCreated = new EcmEvent(new JSONObject());
        folderCreated.setEcmEventType(EcmEventType.CREATE);
        folderCreated.setNodeType(EcmFileConstants.ECM_SYNC_NODE_TYPE_FOLDER);
        folderCreated.setNodeId("folderNodeId");
        folderCreated.setParentNodeId("parentFolderNodeId");
        folderCreated.setNodeName("newFolderName");
        folderCreated.setUserId("userId");
    }

    @Test
    public void onEcmFolderCreated_createNewArkCaseFolder() throws Exception
    {
        AcmFolder parentFolder = new AcmFolder();
        parentFolder.setCmisRepositoryId("cmisRepositoryId");

        Capture<AcmFolder> newFolder = Capture.newInstance();

        // new folder is not in ArkCase ...
        expect(folderAndFilesUtils.lookupArkCaseFolder(folderCreated.getNodeId())).andReturn(null);

        // ... and the parent folder is in ArkCase
        expect(folderAndFilesUtils.lookupArkCaseFolder(folderCreated.getParentNodeId())).andReturn(parentFolder);

        // get the CMIS repository id
        expect(acmFolderService.getCmisRepositoryId(parentFolder)).andReturn(parentFolder.getCmisRepositoryId());

        // be sure the folder has creator and modifier of the user that made the change in the ECM service
        auditPropertyEntityAdapter.setUserId(folderCreated.getUserId());

        AcmFolder savedFolder = new AcmFolder();
        expect(acmFolderDao.save(capture(newFolder))).andReturn(savedFolder);

        fileParticipantService.setFolderParticipantsFromParentFolder(savedFolder);
        expectLastCall();

        expect(acmFolderDao.save(savedFolder)).andReturn(savedFolder);

        replay(mocks);

        unit.onEcmFolderCreated(folderCreated);

        verify(mocks);

        AcmFolder created = newFolder.getValue();
        assertNotNull(created);
        assertEquals(parentFolder, created.getParentFolder());
        assertEquals(folderCreated.getNodeName(), created.getName());
        assertEquals(folderCreated.getNodeId(), created.getCmisFolderId());
        assertEquals(parentFolder.getCmisRepositoryId(), created.getCmisRepositoryId());

    }

    @Test
    public void onEcmFolderCreated_ifAlreadyInArkcase_thenNoFurtherAction() throws Exception
    {
        // new folder is in ArkCase ...
        expect(folderAndFilesUtils.lookupArkCaseFolder(folderCreated.getNodeId())).andReturn(new AcmFolder());

        replay(mocks);

        unit.onEcmFolderCreated(folderCreated);

        verify(mocks);
    }

    @Test
    public void onEcmFolderCreated_ifParentFolderNotInArkcase_thenNoFurtherAction() throws Exception
    {
        // new folder is not in ArkCase ...
        expect(folderAndFilesUtils.lookupArkCaseFolder(folderCreated.getNodeId())).andReturn(null);

        // ... and the parent folder is in ArkCase
        expect(folderAndFilesUtils.lookupArkCaseFolder(folderCreated.getParentNodeId())).andReturn(null);
        
        replay(mocks);

        unit.onEcmFolderCreated(folderCreated);

        verify(mocks);
    }
}
