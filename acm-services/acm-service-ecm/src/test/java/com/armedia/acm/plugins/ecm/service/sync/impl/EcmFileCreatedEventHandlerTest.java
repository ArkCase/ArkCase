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
import com.armedia.acm.plugins.ecm.pipeline.EcmFileTransactionPipelineContext;
import com.armedia.acm.plugins.ecm.service.AcmFolderService;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.services.pipeline.PipelineManager;
import com.armedia.acm.spring.SpringContextHolder;
import org.apache.chemistry.opencmis.client.api.Document;
import org.easymock.Capture;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.NoResultException;

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
    private PipelineManager pipelineManager = createMock(PipelineManager.class);
    private SpringContextHolder springContextHolder = createMock(SpringContextHolder.class);

    private Object[] mocks = {acmFolderDao, acmFolderService, ecmFileDao, auditPropertyEntityAdapter, ecmFileService,
            cmisDocument, pipelineManager, springContextHolder};
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
        unit.setSpringContextHolder(springContextHolder);

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

        String mimeType = "mime/type";
        String extension = ".txt";
        String fileType = "Other";
        String category = "Document";


        Capture<EcmFileTransactionPipelineContext> actualContext = Capture.newInstance();
        Capture<EcmFile> actualFile = Capture.newInstance();

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

        // run the file save pipeline
        expect(springContextHolder.getBeanByName("ecmFileUploadPipelineManager", PipelineManager.class)).andReturn(pipelineManager);

        expect(pipelineManager.executeOperation(capture(actualFile), capture(actualContext), anyObject(PipelineManager.PipelineManagerOperation.class))).andReturn(new EcmFile());

        replay(mocks);

        unit.onEcmFileCreated(fileCreated);

        verify(mocks);

        EcmFileTransactionPipelineContext foundContext = actualContext.getValue();
        assertEquals(fileCreated.getParentNodeId(), foundContext.getCmisFolderId());
        assertEquals(container, foundContext.getContainer());
        assertEquals(fileCreated.getUserId(), foundContext.getAuthentication().getName());
        assertEquals(fileCreated.getNodeName(), foundContext.getOriginalFileName());

        EcmFile foundFile = actualFile.getValue();
        assertEquals(mimeType, foundFile.getFileActiveVersionMimeType());
        assertEquals(extension, foundFile.getFileActiveVersionNameExtension());
        assertEquals(fileCreated.getNodeName(), foundFile.getFileName());
        assertEquals(fileType, foundFile.getFileType());
        assertEquals(category, foundFile.getCategory());
        assertEquals(parentFolder.getCmisRepositoryId(), foundFile.getCmisRepositoryId());


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
