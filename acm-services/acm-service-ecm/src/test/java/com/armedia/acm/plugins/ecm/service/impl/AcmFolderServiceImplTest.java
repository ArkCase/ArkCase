package com.armedia.acm.plugins.ecm.service.impl;


import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import org.easymock.EasyMockSupport;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.easymock.EasyMock.expect;

public class AcmFolderServiceImplTest extends EasyMockSupport
{
    private AcmFolderServiceImpl unit;
    private EcmFileDao fileDaoMock;

    @Before
    public void setUp() throws Exception
    {
        unit = new AcmFolderServiceImpl();
        fileDaoMock = createMock(EcmFileDao.class);
        unit.setFileDao(fileDaoMock);
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

}
