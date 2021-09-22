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

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;

import com.armedia.acm.camelcontext.arkcase.cmis.ArkCaseCMISActions;
import com.armedia.acm.camelcontext.context.CamelContextManager;
import com.armedia.acm.camelcontext.exception.ArkCaseFileRepositoryException;
import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.ecm.dao.AcmFolderDao;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.exception.AcmFolderException;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileConfig;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.plugins.ecm.utils.FolderAndFilesUtils;

import org.apache.chemistry.opencmis.client.runtime.FolderImpl;
import org.apache.chemistry.opencmis.client.runtime.util.EmptyItemIterable;
import org.easymock.Capture;
import org.easymock.CaptureType;
import org.easymock.EasyMock;
import org.easymock.EasyMockSupport;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class AcmFolderServiceImplTest extends EasyMockSupport
{
    private AcmFolderServiceImpl unit;
    private EcmFileDao fileDaoMock;
    private AcmFolderDao folderDaoMock;
    private CamelContextManager camelContextManagerMock;
    private FolderAndFilesUtils folderAndFilesUtilsMock;
    private EcmFileParticipantService fileParticipantServiceMock;

    @Before
    public void setUp() throws Exception
    {
        unit = new AcmFolderServiceImpl();
        fileDaoMock = createMock(EcmFileDao.class);
        unit.setFileDao(fileDaoMock);
        folderDaoMock = createMock(AcmFolderDao.class);
        unit.setFolderDao(folderDaoMock);
        EcmFileConfig config = new EcmFileConfig();
        config.setDefaultCmisId("default");
        unit.setEcmFileConfig(config);
        camelContextManagerMock = createMock(CamelContextManager.class);
        unit.setCamelContextManager(camelContextManagerMock);
        folderAndFilesUtilsMock = createMock(FolderAndFilesUtils.class);
        unit.setFolderAndFilesUtils(folderAndFilesUtilsMock);
        fileParticipantServiceMock = createMock(EcmFileParticipantService.class);
        unit.setFileParticipantService(fileParticipantServiceMock);
    }

    @Test
    public void testFindFolderChildren()
    {
        AcmFolder folderToDelete = new AcmFolder();
        folderToDelete.setId(1L);
        AcmFolder subFolder1 = new AcmFolder();
        subFolder1.setId(11L);
        AcmFolder subFolder2 = new AcmFolder();
        subFolder2.setId(12L);
        AcmFolder subFolder3 = new AcmFolder();
        subFolder3.setId(21L);

        EcmFile subFile1 = new EcmFile();
        subFile1.setFolder(folderToDelete);
        subFile1.setFileId(111L);
        EcmFile subFile2 = new EcmFile();
        subFile2.setFileId(112L);
        subFile2.setFolder(subFolder1);
        EcmFile subFile3 = new EcmFile();
        subFile3.setFileId(113L);
        subFile3.setFolder(subFolder3);

        subFolder1.setChildrenFolders(new ArrayList<>());
        subFolder2.setChildrenFolders(Collections.singletonList(subFolder3));
        subFolder3.setChildrenFolders(new ArrayList<>());
        folderToDelete.setChildrenFolders(Arrays.asList(subFolder1, subFolder2));

        Set<AcmFolder> childFolders = new HashSet<>();
        Set<EcmFile> childFiles = new HashSet<>();

        expect(fileDaoMock.findByFolderId(folderToDelete.getId())).andReturn(Collections.singletonList(subFile1));
        expect(fileDaoMock.findByFolderId(subFolder1.getId())).andReturn(Collections.singletonList(subFile2));
        expect(fileDaoMock.findByFolderId(subFolder3.getId())).andReturn(Collections.singletonList(subFile3));
        expect(fileDaoMock.findByFolderId(subFolder2.getId())).andReturn(new ArrayList<>());

        replayAll();
        unit.findFolderChildren(folderToDelete, childFiles, childFolders);

        verifyAll();
        Assert.assertTrue(childFolders.contains(subFolder1));
        Assert.assertTrue(childFolders.contains(subFolder2));
        Assert.assertTrue(childFolders.contains(subFolder3));
        Assert.assertFalse(childFolders.contains(folderToDelete));
        Assert.assertTrue(childFiles.contains(subFile1));
        Assert.assertTrue(childFiles.contains(subFile2));
        Assert.assertTrue(childFiles.contains(subFile3));
    }

    @Test
    public void testCopyFolderIntoItself()
    {
        AcmFolder source = new AcmFolder();
        source.setId(101L);
        source.setParentFolder(new AcmFolder());

        Exception exception = null;

        try
        {
            unit.copyFolder(source, source, 101L, "FOLDER");
        }
        catch (AcmUserActionFailedException | AcmObjectNotFoundException | AcmCreateObjectFailedException | AcmFolderException e)
        {
            exception = e;
        }

        Assert.assertNotNull(exception);
        Assert.assertTrue(exception instanceof AcmUserActionFailedException);
        Assert.assertEquals("Destination folder is a subfolder of the source folder",
                ((AcmUserActionFailedException) exception).getShortMessage());
    }

    @Test
    public void testCopyFolderIntoAChildFolder()
    {
        AcmFolder source = new AcmFolder();
        source.setId(101L);
        source.setParentFolder(new AcmFolder());

        AcmFolder dstFolder = new AcmFolder();
        dstFolder.setId(102L);
        dstFolder.setChildrenFolders(new ArrayList<>());

        source.setChildrenFolders(Collections.singletonList(dstFolder));
        dstFolder.setParentFolder(source);

        expect(fileDaoMock.findByFolderId(source.getId())).andReturn(new ArrayList<>());
        expect(fileDaoMock.findByFolderId(dstFolder.getId())).andReturn(new ArrayList<>());

        Exception exception = null;
        replayAll();
        try
        {
            unit.copyFolder(source, dstFolder, dstFolder.getId(), "FOLDER");
        }
        catch (AcmUserActionFailedException | AcmObjectNotFoundException | AcmCreateObjectFailedException | AcmFolderException e)
        {
            exception = e;
        }

        verifyAll();
        Assert.assertNotNull(exception);
        Assert.assertTrue(exception instanceof AcmUserActionFailedException);
        Assert.assertEquals("Destination folder is a subfolder of the source folder",
                ((AcmUserActionFailedException) exception).getShortMessage());
    }

    @Test
    public void testCopyChildFolderIntoSameParentFolder() throws ArkCaseFileRepositoryException
    {
        AcmFolder rootFolder = new AcmFolder();
        rootFolder.setId(100L);
        rootFolder.setParticipants(new ArrayList<>());

        AcmFolder dstFolder = new AcmFolder();
        dstFolder.setId(101L);
        dstFolder.setParticipants(new ArrayList<>());
        dstFolder.setParentFolder(rootFolder);

        AcmFolder source = new AcmFolder();
        source.setId(102L);
        source.setParentFolder(dstFolder);
        source.setChildrenFolders(new ArrayList<>());
        source.setName("sourceFolder");
        source.setParticipants(new ArrayList<>());

        dstFolder.setChildrenFolders(Collections.singletonList(source));

        AcmFolder resultFolder = new AcmFolder();

        expect(fileDaoMock.findByFolderId(source.getId())).andReturn(new ArrayList<>());

        FolderImpl folderMock = createMock(FolderImpl.class);
        expect(camelContextManagerMock.send(eq(ArkCaseCMISActions.GET_FOLDER), EasyMock.anyObject()))
                .andReturn(folderMock).anyTimes();
        expect(folderMock.getPropertyValue(EcmFileConstants.REPOSITORY_VERSION_ID)).andReturn("id").anyTimes();
        expect(folderMock.getChildren()).andReturn(new EmptyItemIterable<>());
        expect(camelContextManagerMock.send(eq(ArkCaseCMISActions.CREATE_FOLDER), EasyMock.anyObject()))
                .andReturn(folderMock);

        expect(folderAndFilesUtilsMock.createUniqueFolderName(source.getName())).andReturn("uniqueName");

        Capture<AcmFolder> newlyCreatedFolderCapture = Capture.newInstance(CaptureType.FIRST);
        expect(folderDaoMock.save(capture(newlyCreatedFolderCapture))).andReturn(resultFolder).anyTimes();
        fileParticipantServiceMock.setFolderParticipantsFromParentFolder(resultFolder);
        expectLastCall();

        expect(folderDaoMock.findByCmisFolderId("id")).andReturn(resultFolder);
        expect(folderDaoMock.findByCmisFolderId("id")).andReturn(source);

        expect(folderDaoMock.find(EasyMock.anyLong())).andReturn(null);
        expect(folderDaoMock.findSubFolders(EasyMock.anyLong())).andReturn(null);
        expect(fileDaoMock.findByFolderId(EasyMock.anyLong())).andReturn(null);

        Exception exception = null;

        replayAll();
        try
        {
            unit.copyFolder(source, dstFolder, dstFolder.getId(), "FOLDER");
        }
        catch (AcmUserActionFailedException | AcmObjectNotFoundException | AcmCreateObjectFailedException | AcmFolderException e)
        {
            exception = e;
        }

        verifyAll();
        Assert.assertNull(exception);

        AcmFolder copiedFolder = newlyCreatedFolderCapture.getValue();
        Assert.assertEquals(dstFolder.getId(), copiedFolder.getParentFolder().getId());
        Assert.assertEquals(source.getName(), copiedFolder.getName());
        Assert.assertNull(copiedFolder.getChildrenFolders());
    }

}
