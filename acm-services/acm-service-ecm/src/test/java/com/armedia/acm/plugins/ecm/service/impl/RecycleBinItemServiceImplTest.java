package com.armedia.acm.plugins.ecm.service.impl;

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

import com.armedia.acm.plugins.ecm.dao.AcmContainerDao;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.dao.RecycleBinItemDao;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.plugins.ecm.model.RecycleBinConstants;
import com.armedia.acm.plugins.ecm.model.RecycleBinItem;
import com.armedia.acm.plugins.ecm.model.RecycleBinItemDTO;
import com.armedia.acm.plugins.ecm.service.AcmFolderService;
import com.armedia.acm.plugins.ecm.service.FileEventPublisher;
import org.easymock.Capture;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.Authentication;

import java.util.ArrayList;
import java.util.List;

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.junit.Assert.assertEquals;

/**
 * @author darko.dimitrievski
 */

public class RecycleBinItemServiceImplTest extends EasyMockSupport
{
    private RecycleBinItemServiceImpl unit;

    private EcmFileDao mockEcmFileDao;
    private EcmFileServiceImpl mockEcmFileService;
    private RecycleBinItemDao mockRecycleBinItemDao;
    private AcmContainerDao mockAcmContainerDao;
    private Authentication mockAuthentication;
    private AcmFolderService mockAcmFolderService;
    private MockHttpSession mockSession;
    private FileEventPublisher mockEventPublisher;

    @Before
    public void setUp() throws Exception
    {
        unit = new RecycleBinItemServiceImpl();

        mockEcmFileDao = createMock(EcmFileDao.class);
        mockRecycleBinItemDao = createMock(RecycleBinItemDao.class);
        mockEcmFileService = createMock(EcmFileServiceImpl.class);
        mockAcmContainerDao = createMock(AcmContainerDao.class);
        mockAuthentication = createMock(Authentication.class);
        mockAcmFolderService = createMock(AcmFolderService.class);
        mockSession = new MockHttpSession();
        mockEventPublisher = createMock(FileEventPublisher.class);

        unit.setEcmFileDao(mockEcmFileDao);
        unit.setRecycleBinItemDao(mockRecycleBinItemDao);
        unit.setEcmFileService(mockEcmFileService);
        unit.setFolderService(mockAcmFolderService);
        unit.setFileEventPublisher(mockEventPublisher);
    }

    @Test
    public void putFileIntoRecycleBin() throws Exception
    {
        Long fileId = 500L;
        String targetObjectType = RecycleBinConstants.OBJECT_TYPE;
        Long targetObjectId = 42L;
        Long sourceFolderId = 131L;
        Long folderId = 514L;
        Long destinationContainerId = 666L;
        Long recycleBinItemId = 555L;
        Long sourceContainerId = 1314L;

        EcmFile toBeDeleted = new EcmFile();
        toBeDeleted.setFileId(fileId);
        AcmFolder sourceFolder = new AcmFolder();
        sourceFolder.setId(sourceFolderId);
        toBeDeleted.setFolder(sourceFolder);
        sourceFolder.setCmisFolderId("sourceCmisFolderId");
        toBeDeleted.setVersionSeriesId("versionSeriesId");
        toBeDeleted.setCmisRepositoryId(EcmFileConstants.DEFAULT_CMIS_REPOSITORY_ID);
        AcmContainer sourceContainer = new AcmContainer();

        AcmFolder targetFolder = new AcmFolder();
        targetFolder.setId(folderId);
        targetFolder.setCmisFolderId("targetCmisFolderId");
        targetFolder.setCmisRepositoryId("targetCmisRepositoryId");

        sourceContainer.setId(sourceContainerId);
        sourceContainer.setContainerObjectId(targetObjectId);
        sourceContainer.setContainerObjectType(targetObjectType);
        toBeDeleted.setContainer(sourceContainer);
        sourceContainer.setFolder(sourceFolder);

        List<AcmContainer> acmContainerList = new ArrayList<>();
        acmContainerList.add(sourceContainer);

        RecycleBinItem recycleBinItem = new RecycleBinItem();
        recycleBinItem.setId(recycleBinItemId);
        recycleBinItem.setSourceObjectId(destinationContainerId);
        recycleBinItem.setSourceFolderId(folderId);
        recycleBinItem.setSourceCmisRepositoryId(EcmFileConstants.DEFAULT_CMIS_REPOSITORY_ID);

        AcmContainer destinationContainer = new AcmContainer();
        destinationContainer.setId(destinationContainerId);
        destinationContainer.setContainerObjectType(RecycleBinConstants.OBJECT_TYPE);
        destinationContainer.setCmisRepositoryId(EcmFileConstants.DEFAULT_CMIS_REPOSITORY_ID);
        destinationContainer.setFolder(targetFolder);

        mockSession.setAttribute("acm_ip_address", "ipAddress");

        expect(mockAuthentication.getName()).andReturn("user").anyTimes();

        expect(mockRecycleBinItemDao.getContainerForRecycleBin(RecycleBinConstants.OBJECT_TYPE, EcmFileConstants.DEFAULT_CMIS_REPOSITORY_ID)).andReturn(destinationContainer);

        expect(mockAcmFolderService.findContainerByFolderIdTransactionIndependent(sourceFolderId)).andReturn(sourceContainer);

        Capture<RecycleBinItem> saved = Capture.newInstance();
        expect(mockRecycleBinItemDao.save(capture(saved))).andReturn(recycleBinItem);

        expect(mockEcmFileService.moveFile(toBeDeleted.getFileId(), destinationContainer.getContainerObjectId(),
                destinationContainer.getContainerObjectType(), destinationContainer.getFolder().getId())).andReturn(toBeDeleted);

        mockEcmFileService.checkDuplicatesByHash(toBeDeleted);

        mockEventPublisher.publishFileMovedInRecycleBinEvent(toBeDeleted, mockAuthentication, "ipAddress", true);
        expectLastCall();
        replayAll();

        RecycleBinItem deletedFile = unit.putFileIntoRecycleBin(toBeDeleted, mockAuthentication, mockSession);

        verifyAll();

        assertEquals(sourceFolderId, deletedFile.getSourceFolderId());
        assertEquals(EcmFileConstants.DEFAULT_CMIS_REPOSITORY_ID, deletedFile.getSourceCmisRepositoryId());
    }

    @Test
    public void putFolderIntoRecycleBin() throws Exception
    {
        Long fileId = 500L;
        String targetObjectType = RecycleBinConstants.OBJECT_TYPE;
        Long targetObjectId = 42L;
        Long sourceFolderId = 131L;
        Long folderId = 514L;
        Long destinationContainerId = 666L;
        Long recycleBinItemId = 555L;
        Long sourceContainerId = 1314L;

        EcmFile toBeDeleted = new EcmFile();
        toBeDeleted.setFileId(fileId);
        AcmFolder folderToDelete = new AcmFolder();
        folderToDelete.setId(sourceFolderId);
        toBeDeleted.setFolder(folderToDelete);
        folderToDelete.setCmisFolderId("sourceCmisFolderId");
        folderToDelete.setCmisRepositoryId(EcmFileConstants.DEFAULT_CMIS_REPOSITORY_ID);
        toBeDeleted.setVersionSeriesId("versionSeriesId");
        toBeDeleted.setCmisRepositoryId(EcmFileConstants.DEFAULT_CMIS_REPOSITORY_ID);
        AcmContainer sourceContainer = new AcmContainer();

        AcmFolder parentFolder = new AcmFolder();
        parentFolder.setId(564L);

        folderToDelete.setParentFolder(parentFolder);

        AcmFolder targetFolder = new AcmFolder();
        targetFolder.setId(folderId);
        targetFolder.setCmisFolderId("targetCmisFolderId");
        targetFolder.setCmisRepositoryId("targetCmisRepositoryId");

        sourceContainer.setId(sourceContainerId);
        sourceContainer.setContainerObjectId(targetObjectId);
        sourceContainer.setContainerObjectType(targetObjectType);
        toBeDeleted.setContainer(sourceContainer);
        sourceContainer.setFolder(folderToDelete);

        List<AcmContainer> acmContainerList = new ArrayList<>();
        acmContainerList.add(sourceContainer);

        RecycleBinItem recycleBinItem = new RecycleBinItem();
        recycleBinItem.setId(recycleBinItemId);
        recycleBinItem.setSourceObjectId(destinationContainerId);
        recycleBinItem.setSourceFolderId(folderId);
        recycleBinItem.setSourceCmisRepositoryId(EcmFileConstants.DEFAULT_CMIS_REPOSITORY_ID);

        AcmContainer destinationContainer = new AcmContainer();
        destinationContainer.setId(destinationContainerId);
        destinationContainer.setContainerObjectType(RecycleBinConstants.OBJECT_TYPE);
        destinationContainer.setCmisRepositoryId(EcmFileConstants.DEFAULT_CMIS_REPOSITORY_ID);
        destinationContainer.setFolder(targetFolder);

        expect(mockRecycleBinItemDao.getContainerForRecycleBin(RecycleBinConstants.OBJECT_TYPE,
                EcmFileConstants.DEFAULT_CMIS_REPOSITORY_ID)).andReturn(destinationContainer);

        expect(mockAcmFolderService.findContainerByFolderIdTransactionIndependent(sourceFolderId)).andReturn(sourceContainer);

        Capture<RecycleBinItem> saved = Capture.newInstance();
        expect(mockRecycleBinItemDao.save(capture(saved))).andReturn(recycleBinItem);

        expect(mockAcmFolderService.moveFolder(folderToDelete, targetFolder)).andReturn(folderToDelete);

        expectLastCall();
        replayAll();

        RecycleBinItem deletedFolder = unit.putFolderIntoRecycleBin(folderToDelete);

        verifyAll();

        assertEquals(folderToDelete.getId(), deletedFolder.getSourceObjectId());
        assertEquals(EcmFileConstants.DEFAULT_CMIS_REPOSITORY_ID, deletedFolder.getSourceCmisRepositoryId());
    }

    @Test
    public void restoreItemsFromRecycleBin() throws Exception
    {
        Long fileFromRecycleBinId = 231L;
        Long recycleBinItemId = 141L;
        Long recycleBinFolderId = 1231L;
        RecycleBinItemDTO recycleBinItemDTO = new RecycleBinItemDTO();
        recycleBinItemDTO.setObjectId(fileFromRecycleBinId);
        recycleBinItemDTO.setObjectType(EcmFileConstants.OBJECT_FILE_TYPE);
        recycleBinItemDTO.setId(recycleBinItemId);
        List<RecycleBinItemDTO> itemsFromRecycleBin = new ArrayList<>();
        itemsFromRecycleBin.add(recycleBinItemDTO);

        EcmFile fileToBeRestored = new EcmFile();
        fileToBeRestored.setFileId(fileFromRecycleBinId);
        RecycleBinItem recycleBinItem = new RecycleBinItem();
        recycleBinItem.setId(recycleBinItemId);
        recycleBinItem.setSourceObjectType(EcmFileConstants.OBJECT_FILE_TYPE);
        recycleBinItem.setSourceFolderId(recycleBinFolderId);

        Long containerId = 1314L;
        Long folderId = 214L;

        AcmFolder targetFolder = new AcmFolder();
        targetFolder.setId(folderId);
        targetFolder.setCmisFolderId("targetCmisFolderId");
        targetFolder.setCmisRepositoryId("targetCmisRepositoryId");

        AcmContainer container = new AcmContainer();
        container.setId(containerId);
        container.setContainerObjectId(containerId);
        container.setContainerObjectType(RecycleBinConstants.OBJECT_TYPE);
        container.setFolder(targetFolder);
        fileToBeRestored.setContainer(container);

        expect(mockAuthentication.getName()).andReturn("user").anyTimes();

        List<RecycleBinItemDTO> restoredFiles = new ArrayList<>();

        expect(mockEcmFileDao.find(fileFromRecycleBinId)).andReturn(fileToBeRestored);
        expect(mockRecycleBinItemDao.find(recycleBinItemId)).andReturn(recycleBinItem);
        expect(mockRecycleBinItemDao.removeItemFromRecycleBin(recycleBinItemId)).andReturn(recycleBinItem);
        expect(mockAcmFolderService.findContainerByFolderIdTransactionIndependent(recycleBinFolderId)).andReturn(container);

        expect(mockEcmFileService.moveFile(recycleBinItemDTO.getObjectId(), container.getContainerObjectId(),
                container.getContainerObjectType(), recycleBinItem.getSourceFolderId())).andReturn(fileToBeRestored);

        replayAll();

        restoredFiles = unit.restoreItemsFromRecycleBin(itemsFromRecycleBin, mockAuthentication);

        verifyAll();

        assertEquals(fileToBeRestored.getFileId(), restoredFiles.get(0).getObjectId());
        assertEquals(fileToBeRestored.getFileExtension(), restoredFiles.get(0).getFileActiveVersionNameExtension());
        assertEquals(fileToBeRestored.getFileName(), restoredFiles.get(0).getObjectName());
    }
}