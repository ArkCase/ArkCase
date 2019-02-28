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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.plugins.ecm.dao.AcmContainerDao;
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
import com.armedia.acm.plugins.ecm.service.impl.EcmFileParticipantService;
import com.armedia.acm.plugins.ecm.service.impl.EcmFileServiceImpl;
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
import java.util.ArrayList;
import java.util.Properties;

/**
 * @author ivana.shekerova on 1/8/2019.
 */
public class EcmFileFolderCopiedEventHandlerTest
{

    private EcmFileFolderCopiedEventHandler unit;

    private AcmFolderDao acmFolderDao = mock(AcmFolderDao.class);
    private AcmFolderService acmFolderService = mock(AcmFolderService.class);
    private EcmFileDao ecmFileDao = mock(EcmFileDao.class);
    private AcmContainerDao acmContainerDao = mock(AcmContainerDao.class);
    private AuditPropertyEntityAdapter auditPropertyEntityAdapter = mock(AuditPropertyEntityAdapter.class);
    private EcmFileService ecmFileService = mock(EcmFileService.class);
    private FolderAndFilesUtils spyFolderAndFilesUtils = spy(FolderAndFilesUtils.class);
    private Properties ecmFileServiceProperties = mock(Properties.class);
    private EcmFileParticipantService ecmFileParticipantService = mock(EcmFileParticipantService.class);
    private EcmFileServiceImpl spyEcmFileService = spy(EcmFileServiceImpl.class);
    private Document cmisDocument = mock(Document.class);
    private ContentStream contentStream = mock(ContentStream.class);
    private InputStream inputStream = mock(InputStream.class);
    private EcmEvent fileCopiedEvent;

    @Before
    public void setUp() throws Exception
    {
        unit = new EcmFileFolderCopiedEventHandler();

        unit.setAuditPropertyEntityAdapter(auditPropertyEntityAdapter);
        unit.setFileDao(ecmFileDao);
        unit.setFileService(ecmFileService);
        spyFolderAndFilesUtils.setFileDao(ecmFileDao);
        spyFolderAndFilesUtils.setFileService(ecmFileService);
        spyFolderAndFilesUtils.setFolderDao(acmFolderDao);
        spyFolderAndFilesUtils.setFolderService(acmFolderService);
        unit.setFolderAndFilesUtils(spyFolderAndFilesUtils);

        fileCopiedEvent = new EcmEvent(new JSONObject());
        fileCopiedEvent.setEcmEventType(EcmEventType.MOVE);
        fileCopiedEvent.setUserId("userId");
        fileCopiedEvent.setNodeType(EcmFileConstants.ECM_SYNC_NODE_TYPE_DOCUMENT);
        fileCopiedEvent.setNodeId("documentNodeId");
        fileCopiedEvent.setNodeName("newDocumentName.txt");
        fileCopiedEvent.setParentNodeId("parentFolderNodeId");
        fileCopiedEvent.setParentNodeType(EcmFileConstants.ECM_SYNC_NODE_TYPE_FOLDER);
        fileCopiedEvent.setSourceOfCopyNodeId("sourceOfCopyNodeId");

        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void onEcmFileCopied_ifTargetFolderIsNotArkcaseFolder_thenNoFurtherAction()
    {
        auditPropertyEntityAdapter.setUserId(fileCopiedEvent.getUserId());

        // target folder not in arkcase
        when(acmFolderDao.findByCmisFolderId(fileCopiedEvent.getParentNodeId())).thenThrow(new NoResultException());

        unit.onEcmFileCopied(fileCopiedEvent);

        verify(acmFolderDao, times(1)).findByCmisFolderId(anyString());
    }

    @Test
    public void onEcmFileCopied_ifTargetFolderIsArkcaseFolder_And_ifFileAlreadyExistsInArkcase_thenNoFurtherAction()
    {
        AcmFolder targetFolder = new AcmFolder();
        targetFolder.setId(500L);
        targetFolder.setCmisFolderId("targetFolderNodeId");
        targetFolder.setCmisRepositoryId("alfresco");

        EcmFile file = new EcmFile();
        file.setFileId(200L);
        file.setFolder(targetFolder);

        auditPropertyEntityAdapter.setUserId(fileCopiedEvent.getUserId());

        // target folder is in arkcase
        when(acmFolderDao.findByCmisFolderId(fileCopiedEvent.getParentNodeId())).thenReturn(targetFolder);

        // the file is already in ArkCase
        when(ecmFileDao.findByCmisFileIdAndFolderId(fileCopiedEvent.getNodeId(), targetFolder.getId())).thenReturn(file);

        unit.onEcmFileCopied(fileCopiedEvent);

        verify(acmFolderDao, times(1)).findByCmisFolderId(anyString());
        verify(ecmFileDao, times(1)).findByCmisFileIdAndFolderId(anyString(), anyLong());
    }

    @Test
    public void onEcmFileMoved_ifTargetFolderIsArkcaseFolder_And_ifOriginalFileIsNotInArkcase_uploadFile() throws Exception
    {
        AcmFolder targetFolder = new AcmFolder();
        targetFolder.setId(500L);
        targetFolder.setCmisFolderId("targetFolderNodeId");
        targetFolder.setCmisRepositoryId("alfresco");

        AcmContainer container = new AcmContainer();
        container.setId(600L);
        container.setContainerObjectType("containerObjectType");
        container.setContainerObjectId(700L);

        String mimeType = "mime/type";
        String fileType = "other";
        String category = "Document";

        auditPropertyEntityAdapter.setUserId(fileCopiedEvent.getUserId());

        ArgumentCaptor<Authentication> actualAuthentication = ArgumentCaptor.forClass(Authentication.class);

        // target folder is in arkcase
        when(acmFolderDao.findByCmisFolderId(fileCopiedEvent.getParentNodeId())).thenReturn(targetFolder);
        // the copy file is not in ArkCase
        when(ecmFileDao.findByCmisFileIdAndFolderId(fileCopiedEvent.getNodeId(), targetFolder.getId())).thenThrow(new NoResultException());
        // original file is not in Arkcase
        when(ecmFileDao.findByCmisFileId(fileCopiedEvent.getSourceOfCopyNodeId())).thenReturn(new ArrayList<>());

        // need to find the container
        when(acmFolderService.findContainerByFolderId(targetFolder.getId())).thenReturn(container);
        // get the CMIS repository id
        when(acmFolderService.getCmisRepositoryId(targetFolder)).thenReturn(targetFolder.getCmisRepositoryId());
        // find the CMISDocument
        when(ecmFileService.findObjectById(targetFolder.getCmisRepositoryId(), fileCopiedEvent.getNodeId())).thenReturn(cmisDocument);

        // cmis properties
        when(cmisDocument.getContentStreamMimeType()).thenReturn(mimeType);

        when(cmisDocument.getContentStream()).thenReturn(contentStream);
        when(contentStream.getStream()).thenReturn(inputStream);

        // call the file service to save the file
        when(ecmFileService.upload(
                eq(fileCopiedEvent.getNodeName()),
                eq(fileType),
                eq(category),
                eq(inputStream),
                eq(mimeType),
                eq(fileCopiedEvent.getNodeName()),
                actualAuthentication.capture(),
                eq(targetFolder.getCmisFolderId()),
                eq(container.getContainerObjectType()),
                eq(container.getContainerObjectId()),
                eq(targetFolder.getCmisRepositoryId()),
                eq(cmisDocument))).thenReturn(new EcmFile());

        unit.onEcmFileCopied(fileCopiedEvent);

        Authentication foundAuthentication = actualAuthentication.getValue();
        assertEquals(fileCopiedEvent.getUserId(), foundAuthentication.getName());
    }

    @Test
    public void onEcmFileMoved_ifTargetFolderIsArkcaseFolder_And_ifOriginalFileIsInArkcase_copyFile() throws Exception
    {
        spyEcmFileService.setEcmFileServiceProperties(ecmFileServiceProperties);
        spyEcmFileService.setContainerFolderDao(acmContainerDao);
        spyEcmFileService.setEcmFileDao(ecmFileDao);
        spyEcmFileService.setFileParticipantService(ecmFileParticipantService);
        unit.setFileService(spyEcmFileService);

        AcmFolder targetFolder = new AcmFolder();
        targetFolder.setId(500L);
        targetFolder.setObjectType("FOLDER");
        targetFolder.setCmisFolderId("targetFolderNodeId");
        targetFolder.setCmisRepositoryId("alfresco");

        AcmContainer container = new AcmContainer();
        container.setId(600L);
        container.setContainerObjectType("containerObjectType");
        container.setContainerObjectId(700L);

        EcmFile originalFile = new EcmFile();
        originalFile.setFileId(200L);
        originalFile.setFileType("other");
        originalFile.setFileName("fileName");
        originalFile.setStatus("ACTIVE");
        originalFile.setCategory("Document");
        originalFile.setFileActiveVersionMimeType("text/plain");
        originalFile.setClassName("com.armedia.acm.plugins.ecm.model.EcmFile");
        originalFile.setFileActiveVersionNameExtension(".txt");
        originalFile.setPageCount(1);
        ArrayList<EcmFile> listFiles = new ArrayList<>();
        listFiles.add(originalFile);

        auditPropertyEntityAdapter.setUserId(fileCopiedEvent.getUserId());

        // target folder is in arkcase
        when(acmFolderDao.findByCmisFolderId(fileCopiedEvent.getParentNodeId())).thenReturn(targetFolder);
        // the copy file is not in ArkCase
        when(ecmFileDao.findByCmisFileIdAndFolderId(fileCopiedEvent.getNodeId(), targetFolder.getId())).thenThrow(new NoResultException());
        // original file is in Arkcase
        when(ecmFileDao.findByCmisFileId(fileCopiedEvent.getSourceOfCopyNodeId())).thenReturn(listFiles);

        when(ecmFileServiceProperties.getProperty(anyString())).thenReturn("alfresco");
        when(acmContainerDao.findFolderByObjectTypeIdAndRepositoryId(anyString(), anyLong(), anyString())).thenReturn(container);
        when(ecmFileDao.save(any(EcmFile.class))).thenReturn(originalFile);
        when(ecmFileParticipantService.setFileParticipantsFromParentFolder(any(EcmFile.class))).thenReturn(originalFile);

        unit.onEcmFileCopied(fileCopiedEvent);

        verify(acmFolderDao, times(1)).findByCmisFolderId(anyString());
        verify(ecmFileDao, times(1)).findByCmisFileIdAndFolderId(anyString(), anyLong());
        verify(ecmFileDao, times(1)).findByCmisFileId(anyString());
        verify(spyEcmFileService, times(1)).copyFileInArkcase(any(EcmFile.class), anyString(), any(AcmFolder.class));
    }
}
