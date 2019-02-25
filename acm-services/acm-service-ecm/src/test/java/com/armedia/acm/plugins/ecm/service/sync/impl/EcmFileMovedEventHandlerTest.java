package com.armedia.acm.plugins.ecm.service.sync.impl;

/*-
 * #%L
 * ACM Service: Enterprise Content Management
 * %%
 * Copyright (C) 2014 - 2019 ArkCase LLC
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

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;

import javax.persistence.NoResultException;

import java.io.InputStream;

/**
 * @author ivana.shekerova on 12/21/2018.
 */
public class EcmFileMovedEventHandlerTest
{
    private EcmFileMovedEventHandler unit;

    private AcmFolderDao acmFolderDao = mock(AcmFolderDao.class);
    private AcmFolderService acmFolderService = mock(AcmFolderService.class);
    private EcmFileDao ecmFileDao = mock(EcmFileDao.class);
    private AuditPropertyEntityAdapter auditPropertyEntityAdapter = mock(AuditPropertyEntityAdapter.class);
    private EcmFileService ecmFileService = mock(EcmFileService.class);
    private EcmFileService spyEcmFileService = spy(EcmFileService.class);
    private FolderAndFilesUtils spyFolderAndFilesUtils = spy(FolderAndFilesUtils.class);
    private Document cmisDocument = mock(Document.class);
    private ContentStream contentStream = mock(ContentStream.class);
    private InputStream inputStream = mock(InputStream.class);
    private EcmEvent fileMovedEvent;

    @Before
    public void setUp() throws Exception
    {
        unit = new EcmFileMovedEventHandler();

        unit.setAuditPropertyEntityAdapter(auditPropertyEntityAdapter);
        unit.setFileService(ecmFileService);
        spyFolderAndFilesUtils.setFileDao(ecmFileDao);
        spyFolderAndFilesUtils.setFileService(ecmFileService);
        spyFolderAndFilesUtils.setFolderDao(acmFolderDao);
        spyFolderAndFilesUtils.setFolderService(acmFolderService);
        unit.setFolderAndFilesUtils(spyFolderAndFilesUtils);

        fileMovedEvent = new EcmEvent(new JSONObject());
        fileMovedEvent.setEcmEventType(EcmEventType.MOVE);
        fileMovedEvent.setUserId("userId");
        fileMovedEvent.setNodeType(EcmFileConstants.ECM_SYNC_NODE_TYPE_DOCUMENT);
        fileMovedEvent.setNodeId("documentNodeId");
        fileMovedEvent.setNodeName("newDocumentName.txt");
        fileMovedEvent.setSourceParentNodeId("sourceParentFolderNodeId");
        fileMovedEvent.setSourceParentNodeType(EcmFileConstants.ECM_SYNC_NODE_TYPE_FOLDER);
        fileMovedEvent.setTargetParentNodeId("targetParentFolderNodeId");
        fileMovedEvent.setTargetParentNodeType(EcmFileConstants.ECM_SYNC_NODE_TYPE_FOLDER);

        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void onEcmFileMoved_ifSourceFolderIsArkcaseFolder_And_TargetFolderIsArkcaseFolder_moveFile() throws Exception
    {
        AcmFolder sourceParentFolder = new AcmFolder();
        sourceParentFolder.setId(400L);
        sourceParentFolder.setCmisFolderId("sourceParentFolderNodeId");
        sourceParentFolder.setCmisRepositoryId("alfresco");

        AcmFolder targetParentFolder = new AcmFolder();
        targetParentFolder.setId(500L);
        targetParentFolder.setCmisFolderId("targetParentFolderNodeId");
        targetParentFolder.setCmisRepositoryId("alfresco");

        AcmContainer container = new AcmContainer();
        container.setId(600L);
        container.setContainerObjectType("containerObjectType");
        container.setContainerObjectId(700L);

        EcmFile file = new EcmFile();
        file.setFileId(200L);
        file.setFolder(sourceParentFolder);

        auditPropertyEntityAdapter.setUserId(fileMovedEvent.getUserId());

        // source parent folder does exist
        when(acmFolderDao.findByCmisFolderId(fileMovedEvent.getSourceParentNodeId())).thenReturn(sourceParentFolder);
        // target parent folder does exist
        when(acmFolderDao.findByCmisFolderId(fileMovedEvent.getTargetParentNodeId())).thenReturn(targetParentFolder);
        // the file is in ArkCase
        when(ecmFileDao.findByCmisFileIdAndFolderId(fileMovedEvent.getNodeId(), sourceParentFolder.getId())).thenReturn(file);

        assertEquals(file.getFolder().getCmisFolderId(), fileMovedEvent.getSourceParentNodeId());

        EcmFile returnFile = file;
        returnFile.setContainer(container);
        returnFile.setFolder(targetParentFolder);

        when(ecmFileService.moveFileInArkcase(file, targetParentFolder, fileMovedEvent.getTargetParentNodeType())).thenReturn(returnFile);

        EcmFile movedFile = unit.onEcmFileMoved(fileMovedEvent);

        assertEquals(movedFile.getFolder().getCmisFolderId(), fileMovedEvent.getTargetParentNodeId());
        verify(acmFolderDao, times(2)).findByCmisFolderId(anyString());
        verify(ecmFileDao, times(1)).findByCmisFileIdAndFolderId(anyString(), anyLong());
    }

    @Test
    public void onEcmFileMoved_ifSourceFolderIsArkcaseFolder_And_TargetFolderIsNotArkcaseFolder_deleteFile() throws Exception
    {
        unit.setFileService(spyEcmFileService);

        AcmFolder sourceParentFolder = new AcmFolder();
        sourceParentFolder.setId(400L);
        sourceParentFolder.setCmisFolderId("sourceParentFolderNodeId");

        EcmFile file = new EcmFile();
        file.setFileId(200L);
        file.setFolder(sourceParentFolder);

        auditPropertyEntityAdapter.setUserId(fileMovedEvent.getUserId());

        // source parent folder does exist
        when(acmFolderDao.findByCmisFolderId(fileMovedEvent.getSourceParentNodeId())).thenReturn(sourceParentFolder);
        // target parent folder does not exist
        when(acmFolderDao.findByCmisFolderId(fileMovedEvent.getTargetParentNodeId())).thenThrow(new NoResultException());
        // the file is in ArkCase
        when(ecmFileDao.findByCmisFileIdAndFolderId(fileMovedEvent.getNodeId(), sourceParentFolder.getId())).thenReturn(file);

        when(ecmFileDao.find(file.getFileId())).thenReturn(file);
        doNothing().when(ecmFileDao).deleteFile(file.getFileId());

        unit.onEcmFileMoved(fileMovedEvent);

        verify(acmFolderDao, times(2)).findByCmisFolderId(anyString());
        verify(ecmFileDao, times(1)).findByCmisFileIdAndFolderId(anyString(), anyLong());
        verify(spyEcmFileService, times(1)).deleteFileInArkcase(any(EcmFile.class));
    }

    @Test
    public void onEcmFileMoved_ifSourceFolderIsNotArkcaseFolder_And_TargetFolderIsArkcaseFolder_uploadFile() throws Exception
    {
        AcmFolder targetParentFolder = new AcmFolder();
        targetParentFolder.setId(500L);
        targetParentFolder.setCmisFolderId("targetParentFolderNodeId");
        targetParentFolder.setCmisRepositoryId("alfresco");

        AcmContainer container = new AcmContainer();
        container.setId(600L);
        container.setContainerObjectType("containerObjectType");
        container.setContainerObjectId(700L);

        String mimeType = "mime/type";
        String fileType = "other";
        String category = "Document";

        auditPropertyEntityAdapter.setUserId(fileMovedEvent.getUserId());

        ArgumentCaptor<Authentication> actualAuthentication = ArgumentCaptor.forClass(Authentication.class);

        // source parent folder not in arkcase
        when(acmFolderDao.findByCmisFolderId(fileMovedEvent.getSourceParentNodeId())).thenThrow(new NoResultException());
        // target parent folder does exist in arkcase
        when(acmFolderDao.findByCmisFolderId(fileMovedEvent.getTargetParentNodeId())).thenReturn(targetParentFolder);

        // need to find the container
        when(acmFolderService.findContainerByFolderId(targetParentFolder.getId())).thenReturn(container);
        // get the CMIS repository id
        when(acmFolderService.getCmisRepositoryId(targetParentFolder)).thenReturn(targetParentFolder.getCmisRepositoryId());
        // find the CMISDocument
        when(ecmFileService.findObjectById(targetParentFolder.getCmisRepositoryId(), fileMovedEvent.getNodeId())).thenReturn(cmisDocument);

        // cmis properties
        when(cmisDocument.getContentStreamMimeType()).thenReturn(mimeType);

        when(cmisDocument.getContentStream()).thenReturn(contentStream);
        when(contentStream.getStream()).thenReturn(inputStream);

        // call the file service to save the file
        when(ecmFileService.upload(
                eq(fileMovedEvent.getNodeName()),
                eq(fileType),
                eq(category),
                eq(inputStream),
                eq(mimeType),
                eq(fileMovedEvent.getNodeName()),
                actualAuthentication.capture(),
                eq(targetParentFolder.getCmisFolderId()),
                eq(container.getContainerObjectType()),
                eq(container.getContainerObjectId()),
                eq(targetParentFolder.getCmisRepositoryId()),
                eq(cmisDocument))).thenReturn(new EcmFile());

        unit.onEcmFileMoved(fileMovedEvent);

        Authentication foundAuthentication = actualAuthentication.getValue();
        assertEquals(fileMovedEvent.getUserId(), foundAuthentication.getName());
    }

    @Test
    public void onEcmFileMoved_ifSourceFolderIsNotArkcaseFolder_And_TargetFolderIsNotArkcaseFolder_thenNoFurtherAction() throws Exception
    {
        auditPropertyEntityAdapter.setUserId(fileMovedEvent.getUserId());

        // source parent folder not in arkcase
        when(acmFolderDao.findByCmisFolderId(fileMovedEvent.getSourceParentNodeId())).thenThrow(new NoResultException());
        // target parent folder not in arkcase
        when(acmFolderDao.findByCmisFolderId(fileMovedEvent.getTargetParentNodeId())).thenThrow(new NoResultException());

        unit.onEcmFileMoved(fileMovedEvent);

        verify(acmFolderDao, times(2)).findByCmisFolderId(anyString());
    }

}
