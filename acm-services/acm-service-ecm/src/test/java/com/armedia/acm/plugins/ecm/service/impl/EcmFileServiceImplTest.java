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
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.muletools.mulecontextmanager.MuleContextManager;
import com.armedia.acm.plugins.ecm.dao.AcmContainerDao;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileConfig;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.plugins.ecm.model.EcmFileUpdatedEvent;
import com.armedia.acm.plugins.ecm.model.EcmFileVersion;
import com.armedia.acm.plugins.ecm.utils.CmisConfigUtils;
import com.armedia.acm.services.authenticationtoken.dao.AuthenticationTokenDao;
import com.armedia.acm.services.authenticationtoken.model.AuthenticationToken;
import com.armedia.acm.services.authenticationtoken.model.AuthenticationTokenConstants;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.easymock.Capture;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.mule.api.MuleMessage;
import org.mule.module.cmis.connectivity.CMISCloudConnectorConnectionManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.persistence.EntityManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
    private EcmFileDao mockEcmFileDao;
    private EntityManager mockEntityManager;
    private ApplicationEventPublisher mockApplicationEventPublisher;
    private AcmContainerDao mockContainerDao;
    private EcmFileParticipantService mockFileParticipantService;
    private Authentication mockAuthentication;
    private AuthenticationTokenDao mockAuthenticationTokenDao;

    private List<AuthenticationToken> authenticationTokens;

    @Before
    public void setUp() throws Exception
    {
        unit = new EcmFileServiceImpl();

        mockMuleContextManager = createMock(MuleContextManager.class);
        mockMuleMessage = createMock(MuleMessage.class);
        mockCmisObject = createMock(CmisObject.class);
        mockCmisConfigUtils = createMock(CmisConfigUtils.class);
        mockEcmFileDao = createMock(EcmFileDao.class);
        mockEntityManager = createMock(EntityManager.class);
        mockApplicationEventPublisher = createMock(ApplicationEventPublisher.class);
        mockContainerDao = createMock(AcmContainerDao.class);
        mockFileParticipantService = createMock(EcmFileParticipantService.class);
        mockAuthentication = createMock(Authentication.class);
        mockAuthenticationTokenDao = createMock(AuthenticationTokenDao.class);

        EcmFileConfig ecmFileConfig = new EcmFileConfig();
        ecmFileConfig.setDefaultCmisId(defaultCmisId);
        unit.setEcmFileConfig(ecmFileConfig);

        unit.setMuleContextManager(mockMuleContextManager);
        unit.setCmisConfigUtils(mockCmisConfigUtils);
        unit.setEcmFileDao(mockEcmFileDao);
        unit.setApplicationEventPublisher(mockApplicationEventPublisher);
        unit.setContainerFolderDao(mockContainerDao);
        unit.setFileParticipantService(mockFileParticipantService);
        unit.setAuthenticationTokenDao(mockAuthenticationTokenDao);
        SecurityContextHolder.getContext().setAuthentication(mockAuthentication);

        authenticationTokens = new ArrayList<>();

        AuthenticationToken token = new AuthenticationToken();
        token.setFileId(500L);
        token.setStatus(AuthenticationTokenConstants.ACTIVE);
        token.setEmail("user_email");
        token.setKey("token");
        authenticationTokens.add(token);
    }

    @Test
    public void deleteFile_oneVersion_shouldNotRemoveOtherVersions() throws Exception
    {

        EcmFile toBeDeleted = new EcmFile();
        toBeDeleted.setFileId(500L);
        toBeDeleted.setVersionSeriesId(UUID.randomUUID().toString());
        toBeDeleted.setCmisRepositoryId("Grateful Dead");

        EcmFileVersion first = new EcmFileVersion();
        first.setVersionTag(UUID.randomUUID().toString());
        first.setVersionMimeType("text/plain");
        first.setVersionFileNameExtension(".txt");
        EcmFileVersion second = new EcmFileVersion();
        second.setVersionTag(UUID.randomUUID().toString());
        second.setVersionMimeType("text/xml");
        second.setVersionFileNameExtension(".xml");
        toBeDeleted.getVersions().add(first);
        toBeDeleted.getVersions().add(second);

        Map<String, Object> props = new HashMap<>();
        props.put(EcmFileConstants.ECM_FILE_ID, second.getVersionTag());
        props.put(EcmFileConstants.CONFIGURATION_REFERENCE, null);
        props.put(EcmFileConstants.ALL_VERSIONS, false);

        expect(mockEcmFileDao.find(toBeDeleted.getFileId())).andReturn(toBeDeleted);
        expect(mockCmisConfigUtils.getCmisConfiguration(toBeDeleted.getCmisRepositoryId())).andReturn(null);
        expect(mockMuleContextManager.send(EcmFileConstants.MULE_ENDPOINT_DELETE_FILE, toBeDeleted, props)).andReturn(mockMuleMessage);
        Capture<EcmFile> updated = Capture.newInstance();
        expect(mockEcmFileDao.save(capture(updated))).andReturn(new EcmFile());

        replayAll();

        unit.deleteFile(toBeDeleted.getFileId(), false);

        verifyAll();

        EcmFile saved = updated.getValue();
        assertEquals(1, saved.getVersions().size());
        assertEquals(first.getVersionTag(), saved.getVersions().get(0).getVersionTag());

        assertNotNull(saved.getActiveVersionTag());
        assertEquals(first.getVersionTag(), saved.getActiveVersionTag());

        assertNotNull("version name extension", saved.getFileActiveVersionNameExtension());
        assertNotNull("file extension", saved.getFileExtension());
        assertEquals("txt", saved.getFileExtension());

        assertEquals(first.getVersionFileNameExtension(), saved.getFileActiveVersionNameExtension());

        assertNotNull("file active version mime type", saved.getFileActiveVersionMimeType());
        assertEquals(first.getVersionMimeType(), saved.getFileActiveVersionMimeType());
    }

    @Test
    public void deleteFile_allVersions_shouldDeleteFile() throws Exception
    {

        EcmFile toBeDeleted = new EcmFile();
        toBeDeleted.setFileId(500L);
        toBeDeleted.setVersionSeriesId(UUID.randomUUID().toString());
        toBeDeleted.setCmisRepositoryId("Grateful Dead");

        EcmFileVersion first = new EcmFileVersion();
        first.setVersionTag(UUID.randomUUID().toString());
        EcmFileVersion second = new EcmFileVersion();
        second.setVersionTag(UUID.randomUUID().toString());
        toBeDeleted.getVersions().add(first);
        toBeDeleted.getVersions().add(second);

        Map<String, Object> props = new HashMap<>();
        props.put(EcmFileConstants.ECM_FILE_ID, toBeDeleted.getVersionSeriesId());
        props.put(EcmFileConstants.CONFIGURATION_REFERENCE, null);
        props.put(EcmFileConstants.ALL_VERSIONS, true);

        expect(mockEcmFileDao.find(toBeDeleted.getFileId())).andReturn(toBeDeleted);
        expect(mockCmisConfigUtils.getCmisConfiguration(toBeDeleted.getCmisRepositoryId())).andReturn(null);
        expect(mockMuleContextManager.send(EcmFileConstants.MULE_ENDPOINT_DELETE_FILE, toBeDeleted, props)).andReturn(mockMuleMessage);
        expect(mockAuthenticationTokenDao.findAuthenticationTokenByTokenFileId(toBeDeleted.getFileId())).andReturn(authenticationTokens);
        expect(mockAuthenticationTokenDao.save(authenticationTokens.get(0))).andReturn(authenticationTokens.get(0));
        expect(mockAuthenticationTokenDao.getEntityManager()).andReturn(mockEntityManager);
        mockEntityManager.flush();
        expectLastCall();

        mockEcmFileDao.deleteFile(toBeDeleted.getFileId());

        replayAll();

        unit.deleteFile(toBeDeleted.getFileId(), true);

        verifyAll();

    }

    @Test
    public void deleteFile_oneVersion_butFileOnlyHasOneVersion_shouldDeleteFile() throws Exception
    {

        EcmFile toBeDeleted = new EcmFile();
        toBeDeleted.setFileId(500L);
        toBeDeleted.setVersionSeriesId(UUID.randomUUID().toString());
        toBeDeleted.setCmisRepositoryId("Grateful Dead");

        EcmFileVersion only = new EcmFileVersion();
        only.setVersionTag(UUID.randomUUID().toString());
        toBeDeleted.getVersions().add(only);

        Map<String, Object> props = new HashMap<>();
        props.put(EcmFileConstants.ECM_FILE_ID, toBeDeleted.getVersionSeriesId());
        props.put(EcmFileConstants.CONFIGURATION_REFERENCE, null);
        props.put(EcmFileConstants.ALL_VERSIONS, true);

        expect(mockEcmFileDao.find(toBeDeleted.getFileId())).andReturn(toBeDeleted);
        expect(mockCmisConfigUtils.getCmisConfiguration(toBeDeleted.getCmisRepositoryId())).andReturn(null);
        expect(mockMuleContextManager.send(EcmFileConstants.MULE_ENDPOINT_DELETE_FILE, toBeDeleted, props)).andReturn(mockMuleMessage);
        expect(mockAuthenticationTokenDao.findAuthenticationTokenByTokenFileId(toBeDeleted.getFileId())).andReturn(authenticationTokens);
        expect(mockAuthenticationTokenDao.save(authenticationTokens.get(0))).andReturn(authenticationTokens.get(0));
        expect(mockAuthenticationTokenDao.getEntityManager()).andReturn(mockEntityManager);
        mockEntityManager.flush();
        expectLastCall();
        mockEcmFileDao.deleteFile(toBeDeleted.getFileId());

        replayAll();

        unit.deleteFile(toBeDeleted.getFileId(), true);

        verifyAll();

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

        expect(mockFileParticipantService.setFileParticipantsFromParentFolder(null)).andReturn(null);

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
        Capture<EcmFileUpdatedEvent> capturedEvent = Capture.newInstance();

        expect(mockEcmFileDao.find(in.getFileId())).andReturn(in);
        expect(mockEcmFileDao.getEm()).andReturn(mockEntityManager);
        mockEntityManager.detach(in);
        expect(mockEcmFileDao.save(capture(saved))).andReturn(in);
        expect(mockAuthentication.getName()).andReturn("user").anyTimes();
        expect(mockAuthentication.getDetails()).andReturn("details").anyTimes();
        mockApplicationEventPublisher.publishEvent(capture(capturedEvent));

        replayAll();

        in = unit.updateFile(in);

        verifyAll();

        assertEquals(in.getFileId(), saved.getValue().getFileId());
        assertEquals(in.getStatus(), saved.getValue().getStatus());

        EcmFileUpdatedEvent event = capturedEvent.getValue();
        assertEquals(in.getFileId(), event.getObjectId());
        assertEquals("FILE", event.getObjectType());
        assertTrue(event.isSucceeded());
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
