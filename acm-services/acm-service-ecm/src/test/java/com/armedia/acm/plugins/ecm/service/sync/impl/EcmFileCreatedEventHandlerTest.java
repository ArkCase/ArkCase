package com.armedia.acm.plugins.ecm.service.sync.impl;

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
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.easymock.Capture;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.Authentication;

import javax.persistence.NoResultException;
import java.io.InputStream;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;

/**
 * Created by dmiller on 5/17/17.
 */
public class EcmFileCreatedEventHandlerTest
{
    private EcmFileCreatedEventHandler unit;

    private AcmFolderDao acmFolderDao = createMock(AcmFolderDao.class);
    private AcmFolderService acmFolderService = createMock(AcmFolderService.class);
    private EcmFileDao ecmFileDao = createMock(EcmFileDao.class);
    private AuditPropertyEntityAdapter auditPropertyEntityAdapter = createMock(AuditPropertyEntityAdapter.class);
    private EcmFileService ecmFileService = createMock(EcmFileService.class);
    private Document cmisDocument = createMock(Document.class);
    private ContentStream contentStream = createMock(ContentStream.class);
    private InputStream inputStream = createMock(InputStream.class);

    private Object[] mocks = {acmFolderDao, acmFolderService, ecmFileDao, auditPropertyEntityAdapter, ecmFileService,
            cmisDocument, contentStream, inputStream};
    private EcmEvent fileCreated;

    @Before
    public void setUp() throws Exception
    {
        unit = new EcmFileCreatedEventHandler();

        unit.setFolderService(acmFolderService);
        unit.setAuditPropertyEntityAdapter(auditPropertyEntityAdapter);
        unit.setFolderDao(acmFolderDao);
        unit.setFileDao(ecmFileDao);
        unit.setFileService(ecmFileService);

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
        String fileType = "Other";
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

        expect(acmFolderDao.findByCmisFolderId(fileCreated.getParentNodeId())).andReturn(parentFolder);

        expect(ecmFileDao.findByCmisFileIdAndFolderId(fileCreated.getNodeId(), parentFolder.getId())).andReturn(new EcmFile());

        replay(mocks);

        unit.onEcmFileCreated(fileCreated);

        verify(mocks);
    }

    @Test
    public void onEcmFileCreated_ifParentFolderNotInArkcase_thenNoFurtherAction() throws Exception
    {

        expect(acmFolderDao.findByCmisFolderId(fileCreated.getParentNodeId())).andThrow(new NoResultException());

        replay(mocks);

        unit.onEcmFileCreated(fileCreated);

        verify(mocks);
    }
}
