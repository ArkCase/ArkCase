package com.armedia.acm.plugins.ecm.service.impl;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.muletools.mulecontextmanager.MuleContextManager;
import com.armedia.acm.plugins.ecm.dao.AcmContainerDao;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.plugins.ecm.utils.CmisConfigUtils;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.easymock.Capture;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.mule.api.MuleMessage;
import org.mule.module.cmis.connectivity.CMISCloudConnectorConnectionManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;

/**
 * Created by armdev on 3/11/15.
 */
public class EcmFileServiceImplTest extends EasyMockSupport
{
    private final String defaultCmisId = "defaultCmisId";
    private EcmFileServiceImpl unit;

    private MuleContextManager mockMuleContextManager;
    private MuleMessage mockMuleMessage;
    private CmisObject mockCmisObject;
    private CmisConfigUtils mockCmisConfigUtils;
    private Properties ecmFileServiceProperties;
    private EcmFileDao mockEcmFileDao;
    private AcmContainerDao mockContainerDao;

    @Before
    public void setUp() throws Exception
    {
        unit = new EcmFileServiceImpl();

        mockMuleContextManager = createMock(MuleContextManager.class);
        mockMuleMessage = createMock(MuleMessage.class);
        mockCmisObject = createMock(CmisObject.class);
        mockCmisConfigUtils = createMock(CmisConfigUtils.class);
        mockEcmFileDao = createMock(EcmFileDao.class);
        mockContainerDao = createMock(AcmContainerDao.class);

        ecmFileServiceProperties = new Properties();
        ecmFileServiceProperties.setProperty("ecm.defaultCmisId", defaultCmisId);

        unit.setMuleContextManager(mockMuleContextManager);
        unit.setCmisConfigUtils(mockCmisConfigUtils);
        unit.setEcmFileServiceProperties(ecmFileServiceProperties);
        unit.setEcmFileDao(mockEcmFileDao);
        unit.setContainerFolderDao(mockContainerDao);
    }

    @Test
    public void moveFile() throws Exception
    {
        Long fileId = 500L;
        String targetObjectType = "TARGET";
        Long targetObjectId = 42L;
        AcmFolder targetFolder = new AcmFolder();
        targetFolder.setCmisFolderId("targetCmisFolderId");
        targetFolder.setCmisRepositoryId("targetCmisRepositoryId");

        EcmFile toMove = new EcmFile();
        AcmFolder sourceFolder = new AcmFolder();
        toMove.setFolder(sourceFolder);
        sourceFolder.setCmisFolderId("sourceCmisFolderId");
        toMove.setVersionSeriesId("versionSeriesId");

        AcmContainer targetContainer = new AcmContainer();

        Map<String, Object> props = new HashMap<>();
        props.put(EcmFileConstants.CMIS_OBJECT_ID, toMove.getVersionSeriesId());
        props.put(EcmFileConstants.DST_FOLDER_ID, targetFolder.getCmisFolderId());
        props.put(EcmFileConstants.SRC_FOLDER_ID, toMove.getFolder().getCmisFolderId());
        props.put(EcmFileConstants.CONFIGURATION_REFERENCE, null);
        props.put(EcmFileConstants.VERSIONING_STATE, "versioningState");

        Document cmisDocument = createMock(Document.class);

        expect(mockEcmFileDao.find(fileId)).andReturn(toMove);
        expect(mockCmisConfigUtils.getCmisConfiguration(targetFolder.getCmisRepositoryId())).andReturn(null);
        expect(mockCmisConfigUtils.getVersioningState(targetFolder.getCmisRepositoryId())).andReturn("versioningState");
        expect(mockContainerDao.findFolderByObjectTypeIdAndRepositoryId(targetObjectType, targetObjectId,
                targetFolder.getCmisRepositoryId())).andReturn(targetContainer);
        expect(mockMuleContextManager.send(EcmFileConstants.MULE_ENDPOINT_MOVE_FILE, toMove, props)).andReturn(mockMuleMessage);
        expect(mockMuleMessage.getPayload(CmisObject.class)).andReturn(cmisDocument);
        expect(cmisDocument.getVersionSeriesId()).andReturn("newVersionSeriesId");

        Capture<EcmFile> saved = Capture.newInstance();
        expect(mockEcmFileDao.save(capture(saved))).andReturn(null);

        replayAll();

        unit.moveFile(fileId, targetObjectId, targetObjectType, targetFolder);

        verifyAll();

        EcmFile movedFile = saved.getValue();
        assertEquals("newVersionSeriesId", movedFile.getVersionSeriesId());
        assertEquals(targetFolder, movedFile.getFolder());
        assertEquals(targetContainer, movedFile.getContainer());
    }

    @Test
    public void createFolder() throws Exception
    {
        String path = "/some/path";
        String id = "id";
        CMISCloudConnectorConnectionManager cmisConfig = new CMISCloudConnectorConnectionManager();
        Map<String, Object> messageProps = new HashMap<>();
        messageProps.put(EcmFileConstants.CONFIGURATION_REFERENCE, cmisConfig);

        expect(mockMuleContextManager.send("vm://createFolder.in", path, messageProps)).andReturn(mockMuleMessage);
        expect(mockMuleMessage.getPayload(CmisObject.class)).andReturn(mockCmisObject);
        expect(mockCmisObject.getId()).andReturn(id);
        expect(mockCmisConfigUtils.getCmisConfiguration(defaultCmisId)).andReturn(cmisConfig);


        replayAll();

        String folderId = unit.createFolder(path);

        verifyAll();

        assertEquals(id, folderId);
    }

    @Test
    public void updateFile() throws Exception
    {
        AcmContainer acmContainer = new AcmContainer();
        acmContainer.setContainerObjectType("CASE_FILE");
        acmContainer.setContainerObjectId(101L);

        EcmFile in = new EcmFile();
        in.setFileId(100L);
        in.setFileType("file_type");
        in.setStatus("new_status");
        in.setContainer(acmContainer);

        Capture<EcmFile> saved = Capture.newInstance();

        expect(mockEcmFileDao.find(in.getFileId())).andReturn(in);
        expect(mockEcmFileDao.save(capture(saved))).andReturn(in);

        replayAll();

        in = unit.updateFile(in);

        verifyAll();

        assertEquals(in.getFileId(), saved.getValue().getFileId());
        assertEquals(in.getStatus(), saved.getValue().getStatus());
    }

    @Test(expected = AcmObjectNotFoundException.class)
    public void updateFile_exception() throws Exception
    {
        AcmContainer acmContainer = new AcmContainer();
        acmContainer.setContainerObjectType("CASE_FILE");
        acmContainer.setContainerObjectId(101L);

        EcmFile in = new EcmFile();
        in.setFileId(100L);
        in.setFileType("file_type");
        in.setStatus("new_status");
        in.setContainer(acmContainer);

        expect(mockEcmFileDao.find(in.getFileId())).andReturn(null);

        replayAll();

        unit.updateFile(in);

        verifyAll();
    }
}
