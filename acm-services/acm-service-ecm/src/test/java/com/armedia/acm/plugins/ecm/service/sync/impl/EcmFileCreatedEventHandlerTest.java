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
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.plugins.ecm.dao.AcmFolderDao;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.plugins.ecm.model.sync.EcmEvent;
import com.armedia.acm.plugins.ecm.model.sync.EcmEventType;
import com.armedia.acm.plugins.ecm.service.AcmFolderService;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.plugins.ecm.utils.FolderAndFilesUtils;
import com.armedia.acm.service.objectlock.model.AcmObjectLock;
import com.armedia.acm.service.objectlock.service.AcmObjectLockService;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.easymock.Capture;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.security.core.Authentication;

import javax.persistence.NoResultException;

import java.io.InputStream;

/**
 * Created by dmiller on 5/17/17.
 */
public class EcmFileCreatedEventHandlerTest
{
    private EcmFileCreatedEventHandler unit;

    private AcmFolderDao acmFolderDao = createMock(AcmFolderDao.class);
    private AcmFolderService acmFolderService = createMock(AcmFolderService.class);
    private EcmFileDao ecmFileDao = createMock(EcmFileDao.class);
    private FolderAndFilesUtils spyFolderAndFilesUtils = spy(FolderAndFilesUtils.class);
    private AuditPropertyEntityAdapter auditPropertyEntityAdapter = createMock(AuditPropertyEntityAdapter.class);
    private EcmFileService ecmFileService = createMock(EcmFileService.class);
    private Document cmisDocument = createMock(Document.class);
    private ContentStream contentStream = createMock(ContentStream.class);
    private InputStream inputStream = createMock(InputStream.class);
    private MessageChannel messageChannel = mock(MessageChannel.class);
    private AcmObjectLockService objectLockService = mock(AcmObjectLockService.class);

    private Object[] mocks = { acmFolderDao, acmFolderService, ecmFileDao, auditPropertyEntityAdapter, ecmFileService,
            cmisDocument, contentStream, inputStream };
    private EcmEvent fileCreated;

    @Before
    public void setUp() throws Exception
    {
        unit = new EcmFileCreatedEventHandler();

        unit.setFolderService(acmFolderService);
        unit.setAuditPropertyEntityAdapter(auditPropertyEntityAdapter);
        unit.setFileService(ecmFileService);
        spyFolderAndFilesUtils.setFileDao(ecmFileDao);
        spyFolderAndFilesUtils.setFileService(ecmFileService);
        spyFolderAndFilesUtils.setFolderDao(acmFolderDao);
        spyFolderAndFilesUtils.setFolderService(acmFolderService);
        unit.setFolderAndFilesUtils(spyFolderAndFilesUtils);

        fileCreated = new EcmEvent(new JSONObject());
        fileCreated.setEcmEventType(EcmEventType.CREATE);
        fileCreated.setNodeType(EcmFileConstants.ECM_SYNC_NODE_TYPE_DOCUMENT);
        fileCreated.setNodeId("documentNodeId");
        fileCreated.setParentNodeId("parentFolderNodeId");
        fileCreated.setNodeName("newDocumentName.txt");
        fileCreated.setUserId("userId");
    }

    @Test
    public void onEcmFileCreated_createNewArkCaseFile() throws Exception
    {
        AcmFolder parentFolder = new AcmFolder();
        parentFolder.setId(500L);
        parentFolder.setCmisRepositoryId("cmisRepositoryId");

        AcmContainer container = new AcmContainer();
        container.setId(600L);
        container.setContainerObjectType("containerObjectType");
        container.setContainerObjectId(700L);

        String mimeType = "mime/type";
        String fileType = "other";
        String category = "Document";

        Capture<Authentication> actualAuthentication = Capture.newInstance();

        // parent folder does exist
        expect(acmFolderDao.findByCmisFolderId(fileCreated.getParentNodeId())).andReturn(parentFolder);

        // ... but the file is not in ArkCase yet
        expect(ecmFileDao.findByCmisFileIdAndFolderId(fileCreated.getNodeId(), parentFolder.getId())).andThrow(new NoResultException());

        // need to find the container
        expect(acmFolderService.findContainerByFolderId(parentFolder.getId())).andReturn(container);

        // get the CMIS repository id
        expect(acmFolderService.getCmisRepositoryId(parentFolder)).andReturn(parentFolder.getCmisRepositoryId());

        // find the CMISDocument
        expect(ecmFileService.findObjectById(parentFolder.getCmisRepositoryId(), fileCreated.getNodeId())).andReturn(cmisDocument);

        // cmis properties
        expect(cmisDocument.getContentStreamMimeType()).andReturn(mimeType);

        expect(cmisDocument.getContentStream()).andReturn(contentStream);
        expect(contentStream.getStream()).andReturn(inputStream);

        auditPropertyEntityAdapter.setUserId(fileCreated.getUserId());

        // call the file service to save the file
        expect(ecmFileService.upload(
                eq(fileCreated.getNodeName()),
                eq(fileType),
                eq(category),
                eq(inputStream),
                eq(mimeType),
                eq(fileCreated.getNodeName()),
                capture(actualAuthentication),
                eq(parentFolder.getCmisFolderId()),
                eq(container.getContainerObjectType()),
                eq(container.getContainerObjectId()),
                eq(parentFolder.getCmisRepositoryId()),
                eq(cmisDocument))).andReturn(new EcmFile());

        when(objectLockService.findLock(anyLong(), anyString())).thenReturn(new AcmObjectLock());
        when(messageChannel.send(any(Message.class))).thenReturn(true);

        replay(mocks);

        unit.onEcmFileCreated(fileCreated);

        verify(mocks);

        Authentication foundAuthentication = actualAuthentication.getValue();
        assertEquals(fileCreated.getUserId(), foundAuthentication.getName());

    }

    @Test
    public void onEcmFileCreated_ifContainerNotInArkCase_thenNoFurtherAction() throws Exception
    {
        AcmFolder parentFolder = new AcmFolder();
        parentFolder.setId(500L);

        auditPropertyEntityAdapter.setUserId(fileCreated.getUserId());

        // parent folder does exist
        expect(acmFolderDao.findByCmisFolderId(fileCreated.getParentNodeId())).andReturn(parentFolder);

        // ... but the file is not in ArkCase yet
        expect(ecmFileDao.findByCmisFileIdAndFolderId(fileCreated.getNodeId(), parentFolder.getId())).andThrow(new NoResultException());

        // need to find the container
        expect(acmFolderService.findContainerByFolderId(parentFolder.getId())).andThrow(new AcmObjectNotFoundException(
                "objectType", 600L, "test exception"));

        replay(mocks);

        unit.onEcmFileCreated(fileCreated);

        verify(mocks);
    }

    @Test
    public void onEcmFileCreated_ifAlreadyInArkcase_thenNoFurtherAction() throws Exception
    {
        AcmFolder parentFolder = new AcmFolder();
        parentFolder.setId(500L);

        auditPropertyEntityAdapter.setUserId(fileCreated.getUserId());

        expect(acmFolderDao.findByCmisFolderId(fileCreated.getParentNodeId())).andReturn(parentFolder);

        expect(ecmFileDao.findByCmisFileIdAndFolderId(fileCreated.getNodeId(), parentFolder.getId())).andReturn(new EcmFile());

        replay(mocks);

        unit.onEcmFileCreated(fileCreated);

        verify(mocks);
    }

    @Test
    public void onEcmFileCreated_ifParentFolderNotInArkcase_thenNoFurtherAction() throws Exception
    {
        auditPropertyEntityAdapter.setUserId(fileCreated.getUserId());

        expect(acmFolderDao.findByCmisFolderId(fileCreated.getParentNodeId())).andThrow(new NoResultException());

        replay(mocks);

        unit.onEcmFileCreated(fileCreated);

        verify(mocks);
    }
}
