package com.armedia.acm.plugins.ecm.service.sync.impl;

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.plugins.ecm.dao.AcmFolderDao;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.plugins.ecm.model.sync.EcmEvent;
import com.armedia.acm.plugins.ecm.model.sync.EcmEventType;
import com.armedia.acm.plugins.ecm.service.AcmFolderService;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.NoResultException;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by dmiller on 5/17/17.
 */
public class EcmFolderCreatedEventHandlerTest
{
    private EcmFolderCreatedEventHandler unit;

    private AcmFolderDao acmFolderDao = EasyMock.createMock(AcmFolderDao.class);
    private AcmFolderService acmFolderService = EasyMock.createMock(AcmFolderService.class);
    private AuditPropertyEntityAdapter auditPropertyEntityAdapter = EasyMock.createMock(AuditPropertyEntityAdapter.class);

    private Object[] mocks = {acmFolderDao, acmFolderService, auditPropertyEntityAdapter};
    private EcmEvent folderCreated;

    @Before
    public void setUp() throws Exception
    {
        unit = new EcmFolderCreatedEventHandler();

        unit.setFolderDao(acmFolderDao);
        unit.setFolderService(acmFolderService);
        unit.setAuditPropertyEntityAdapter(auditPropertyEntityAdapter);

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
        expect(acmFolderDao.findByCmisFolderId(folderCreated.getNodeId())).andThrow(new NoResultException());

        // ... and the parent folder is in ArkCase
        expect(acmFolderDao.findByCmisFolderId(folderCreated.getParentNodeId())).andReturn(parentFolder);

        // get the CMIS repository id
        expect(acmFolderService.getCmisRepositoryId(parentFolder)).andReturn(parentFolder.getCmisRepositoryId());

        // be sure the folder has creator and modifier of the user that made the change in the ECM service
        auditPropertyEntityAdapter.setUserId(folderCreated.getUserId());

        expect(acmFolderDao.save(capture(newFolder))).andReturn(new AcmFolder());

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
        expect(acmFolderDao.findByCmisFolderId(folderCreated.getNodeId())).andReturn(new AcmFolder());

        replay(mocks);

        unit.onEcmFolderCreated(folderCreated);

        verify(mocks);
    }

    @Test
    public void onEcmFolderCreated_ifParentFolderNotInArkcase_thenNoFurtherAction() throws Exception
    {
        // new folder is not in ArkCase ...
        expect(acmFolderDao.findByCmisFolderId(folderCreated.getNodeId())).andThrow(new NoResultException());

        // ... and the parent folder also is not in ArkCase
        expect(acmFolderDao.findByCmisFolderId(folderCreated.getParentNodeId())).andThrow(new NoResultException());

        replay(mocks);

        unit.onEcmFolderCreated(folderCreated);

        verify(mocks);
    }
}
