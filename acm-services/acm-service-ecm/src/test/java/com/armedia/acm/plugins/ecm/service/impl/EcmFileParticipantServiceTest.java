package com.armedia.acm.plugins.ecm.service.impl;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import com.armedia.acm.core.exceptions.AcmAccessControlException;
import com.armedia.acm.core.exceptions.AcmParticipantsException;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.service.AcmFolderService;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.services.participants.service.AcmParticipantService;

import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class EcmFileParticipantServiceTest extends EasyMockSupport
{
    private EcmFileParticipantService fileParticipantService;
    private EcmFileDao mockFileDao;
    private AcmFolderService mockFolderService;
    private AcmParticipantService mockParticipantService;
    private Properties fileServiceProperties;

    @Before
    public void setUp()
    {
        fileParticipantService = new EcmFileParticipantService();

        mockFileDao = createMock(EcmFileDao.class);
        mockFolderService = createMock(AcmFolderService.class);
        mockParticipantService = createMock(AcmParticipantService.class);

        fileServiceProperties = new Properties();
        fileServiceProperties.put("ecm.documentsParticipantTypes.mappings.read", "");
        fileServiceProperties.put("ecm.documentsParticipantTypes.mappings.write", "");
        fileServiceProperties.put("ecm.documentsParticipantTypes.mappings.no-access", "");
        fileServiceProperties.put("ecm.documentsParticipantTypes.mappings.group-read", "");
        fileServiceProperties.put("ecm.documentsParticipantTypes.mappings.group-write", "");
        fileServiceProperties.put("ecm.documentsParticipantTypes.mappings.group-no-access", "");

        fileParticipantService.setFileDao(mockFileDao);
        fileParticipantService.setFolderService(mockFolderService);
        fileParticipantService.setParticipantService(mockParticipantService);
        fileParticipantService.setEcmFileServiceProperties(fileServiceProperties);
    }

    @Test
    public void testSetFileParticipantsFromParentFolderUpdatesExistingParticipantRole()
    {
        // given
        String participantLdapId = "userId";
        String participantType = "write";

        List<AcmParticipant> folderParticipants = new ArrayList<>();

        AcmParticipant folderParticipant = new AcmParticipant();
        folderParticipant.setParticipantLdapId(participantLdapId);
        folderParticipant.setParticipantType(participantType);

        folderParticipants.add(folderParticipant);

        AcmFolder folder = new AcmFolder();
        folder.setParticipants(folderParticipants);

        List<AcmParticipant> fileParticipants = new ArrayList<>();

        AcmParticipant fileParticipant = new AcmParticipant();
        fileParticipant.setParticipantLdapId(participantLdapId);
        fileParticipant.setParticipantType("someRole");

        fileParticipants.add(fileParticipant);

        EcmFile file = new EcmFile();
        file.setParticipants(fileParticipants);
        file.setFolder(folder);

        expect(mockFileDao.save(file)).andReturn(file);

        // when
        replayAll();
        EcmFile returnedFile = fileParticipantService.setFileParticipantsFromParentFolder(file);

        // then
        verifyAll();
        assertEquals(1, returnedFile.getParticipants().size());
        assertEquals(participantLdapId, returnedFile.getParticipants().get(0).getParticipantLdapId());
        assertEquals(participantType, returnedFile.getParticipants().get(0).getParticipantType());
    }

    @Test
    public void testSetFileParticipantsFromParentFolderAddsNotExistingParticipant()
    {
        // given
        String participantLdapId = "userId";
        String participantType = "write";

        List<AcmParticipant> folderParticipants = new ArrayList<>();

        AcmParticipant folderParticipant = new AcmParticipant();
        folderParticipant.setParticipantLdapId(participantLdapId);
        folderParticipant.setParticipantType(participantType);

        folderParticipants.add(folderParticipant);

        AcmFolder folder = new AcmFolder();
        folder.setParticipants(folderParticipants);

        EcmFile file = new EcmFile();
        file.setFolder(folder);

        expect(mockFileDao.save(file)).andReturn(file);

        // when
        replayAll();
        EcmFile returnedFile = fileParticipantService.setFileParticipantsFromParentFolder(file);

        // then
        verifyAll();
        assertEquals(1, returnedFile.getParticipants().size());
        assertEquals(participantLdapId, returnedFile.getParticipants().get(0).getParticipantLdapId());
        assertEquals(participantType, returnedFile.getParticipants().get(0).getParticipantType());
    }

    @Test
    public void testSetFileParticipantsFromParentFolderRemovesExistingParticipant()
    {
        // given
        String participantLdapId = "userId";
        String participantType = "write";

        AcmFolder folder = new AcmFolder();

        List<AcmParticipant> fileParticipants = new ArrayList<>();

        AcmParticipant fileParticipant = new AcmParticipant();
        fileParticipant.setParticipantLdapId(participantLdapId);
        fileParticipant.setParticipantType(participantType);

        fileParticipants.add(fileParticipant);

        EcmFile file = new EcmFile();
        file.setParticipants(fileParticipants);
        file.setFolder(folder);

        expect(mockFileDao.save(file)).andReturn(file);

        // when
        replayAll();
        EcmFile returnedFile = fileParticipantService.setFileParticipantsFromParentFolder(file);

        // then
        verifyAll();
        assertEquals(0, returnedFile.getParticipants().size());
    }

    @Test
    public void testSetFileParticipantsFromParentFolderUpdatesExistingParticipantRoleAddsNotExistingParticipantRemovesExistingParticipant()
    {
        // given
        String addParticipantLdapId = "addUserId";
        String addParticipantType = "write";
        String updateParticipantLdapId = "updateUserId";
        String updateParticipantType = "read";
        String removeParticipantLdapId = "removeUserId";
        String removeParticipantType = "no-access";

        List<AcmParticipant> folderParticipants = new ArrayList<>();

        AcmParticipant addFolderParticipant = new AcmParticipant();
        addFolderParticipant.setParticipantLdapId(addParticipantLdapId);
        addFolderParticipant.setParticipantType(addParticipantType);
        folderParticipants.add(addFolderParticipant);

        AcmParticipant updateFolderParticipant = new AcmParticipant();
        updateFolderParticipant.setParticipantLdapId(updateParticipantLdapId);
        updateFolderParticipant.setParticipantType(updateParticipantType);
        folderParticipants.add(updateFolderParticipant);

        AcmFolder folder = new AcmFolder();
        folder.setParticipants(folderParticipants);

        List<AcmParticipant> fileParticipants = new ArrayList<>();

        AcmParticipant updateFileParticipant = new AcmParticipant();
        updateFileParticipant.setParticipantLdapId(updateParticipantLdapId);
        updateFileParticipant.setParticipantType("someRole");
        fileParticipants.add(updateFileParticipant);

        AcmParticipant deleteFileParticipant = new AcmParticipant();
        deleteFileParticipant.setParticipantLdapId(removeParticipantLdapId);
        deleteFileParticipant.setParticipantType(removeParticipantType);
        fileParticipants.add(deleteFileParticipant);

        EcmFile file = new EcmFile();
        file.setParticipants(fileParticipants);
        file.setFolder(folder);

        expect(mockFileDao.save(file)).andReturn(file);

        // when
        replayAll();
        EcmFile returnedFile = fileParticipantService.setFileParticipantsFromParentFolder(file);

        // then
        verifyAll();
        assertEquals(2, returnedFile.getParticipants().size());
        for (AcmParticipant acmParticipant : returnedFile.getParticipants())
        {
            if (acmParticipant.getParticipantLdapId().equals(addParticipantLdapId))
            {
                assertEquals(addParticipantType, acmParticipant.getParticipantType());
            }
            else if (acmParticipant.getParticipantLdapId().equals(updateParticipantLdapId))
            {
                assertEquals(updateParticipantType, acmParticipant.getParticipantType());
            }
            else
            {
                fail("ParticipantLdapId not expected in the file's participants!");
            }
        }
    }

    @Test(expected = IllegalStateException.class)
    public void testSetFileParticipantsFromParentFolderThrowsIllegalStateExceptionWhenFileHasNoFolder()
    {
        // given
        EcmFile file = new EcmFile();
        file.setFolder(null);

        // when
        fileParticipantService.setFileParticipantsFromParentFolder(file);

        // then
        fail("IllegalStateException should have been thrown!");
    }

    @Test
    public void testSetFolderParticipantsFromParentFolderUpdatesExistingParticipantRoleToFolderAndChildren()
    {
        // given
        String participantLdapId = "userId";
        String participantType = "write";
        Long folderId = 1L;
        Long childFolderId = 2L;

        List<AcmParticipant> parentFolderParticipants = new ArrayList<>();

        AcmParticipant parentFolderParticipant = new AcmParticipant();
        parentFolderParticipant.setParticipantLdapId(participantLdapId);
        parentFolderParticipant.setParticipantType(participantType);

        parentFolderParticipants.add(parentFolderParticipant);

        AcmFolder parentFolder = new AcmFolder();
        parentFolder.setParticipants(parentFolderParticipants);

        List<AcmParticipant> folderParticipants = new ArrayList<>();

        AcmParticipant folderParticipant = new AcmParticipant();
        folderParticipant.setParticipantLdapId(participantLdapId);
        folderParticipant.setParticipantType("someRole");

        folderParticipants.add(folderParticipant);

        AcmFolder folder = new AcmFolder();
        folder.setId(folderId);
        folder.setParticipants(folderParticipants);
        folder.setParentFolder(parentFolder);

        List<AcmParticipant> childFolderParticipants = new ArrayList<>();

        AcmParticipant childFolderParticipant = new AcmParticipant();
        childFolderParticipant.setParticipantLdapId(participantLdapId);
        childFolderParticipant.setParticipantType("someRole");

        childFolderParticipants.add(childFolderParticipant);

        AcmFolder childFolder = new AcmFolder();
        childFolder.setId(childFolderId);
        childFolder.setParticipants(childFolderParticipants);
        childFolder.setParentFolder(folder);

        folder.setChildrenFolders(Arrays.asList(childFolder));

        List<AcmParticipant> fileParticipants = new ArrayList<>();

        AcmParticipant fileParticipant = new AcmParticipant();
        fileParticipant.setParticipantLdapId(participantLdapId);
        fileParticipant.setParticipantType("someRole");

        fileParticipants.add(fileParticipant);

        EcmFile file = new EcmFile();
        file.setParticipants(fileParticipants);
        file.setFolder(parentFolder);

        expect(mockFileDao.findByFolderId(folder.getId(), FlushModeType.COMMIT)).andReturn(Arrays.asList(file));
        expect(mockFileDao.findByFolderId(childFolder.getId(), FlushModeType.COMMIT)).andReturn(new ArrayList<>());
        expect(mockFileDao.save(file)).andReturn(file);

        // when
        replayAll();
        fileParticipantService.setFolderParticipantsFromParentFolder(folder);

        // then
        verifyAll();
        assertEquals(1, folder.getParticipants().size());
        assertEquals(participantLdapId, folder.getParticipants().get(0).getParticipantLdapId());
        assertEquals(participantType, folder.getParticipants().get(0).getParticipantType());
        assertEquals(1, childFolder.getParticipants().size());
        assertEquals(participantLdapId, childFolder.getParticipants().get(0).getParticipantLdapId());
        assertEquals(participantType, childFolder.getParticipants().get(0).getParticipantType());
        assertEquals(1, file.getParticipants().size());
        assertEquals(participantLdapId, file.getParticipants().get(0).getParticipantLdapId());
        assertEquals(participantType, file.getParticipants().get(0).getParticipantType());
    }

    @Test
    public void testSetFolderParticipantsFromParentFolderAddsNotExistingParticipantToFolderAndChildren()
    {
        // given
        String participantLdapId = "userId";
        String participantType = "write";
        Long folderId = 1L;
        Long childFolderId = 2L;

        List<AcmParticipant> parentFolderParticipants = new ArrayList<>();

        AcmParticipant parentFolderParticipant = new AcmParticipant();
        parentFolderParticipant.setParticipantLdapId(participantLdapId);
        parentFolderParticipant.setParticipantType(participantType);

        parentFolderParticipants.add(parentFolderParticipant);

        AcmFolder parentFolder = new AcmFolder();
        parentFolder.setParticipants(parentFolderParticipants);

        AcmFolder folder = new AcmFolder();
        folder.setId(folderId);
        folder.setParentFolder(parentFolder);

        AcmFolder childFolder = new AcmFolder();
        childFolder.setId(childFolderId);
        childFolder.setParentFolder(folder);

        folder.setChildrenFolders(Arrays.asList(childFolder));

        EcmFile file = new EcmFile();
        file.setFolder(parentFolder);

        expect(mockFileDao.findByFolderId(folder.getId(), FlushModeType.COMMIT)).andReturn(Arrays.asList(file));
        expect(mockFileDao.findByFolderId(childFolder.getId(), FlushModeType.COMMIT)).andReturn(new ArrayList<>());
        expect(mockFileDao.save(file)).andReturn(file);

        // when
        replayAll();
        fileParticipantService.setFolderParticipantsFromParentFolder(folder);

        // then
        verifyAll();
        assertEquals(1, folder.getParticipants().size());
        assertEquals(participantLdapId, folder.getParticipants().get(0).getParticipantLdapId());
        assertEquals(participantType, folder.getParticipants().get(0).getParticipantType());
        assertEquals(1, childFolder.getParticipants().size());
        assertEquals(participantLdapId, childFolder.getParticipants().get(0).getParticipantLdapId());
        assertEquals(participantType, childFolder.getParticipants().get(0).getParticipantType());
        assertEquals(1, file.getParticipants().size());
        assertEquals(participantLdapId, file.getParticipants().get(0).getParticipantLdapId());
        assertEquals(participantType, file.getParticipants().get(0).getParticipantType());
    }

    @Test
    public void testSetFolderParticipantsFromParentFolderRemovesExistingParticipantFromFolderAndChildren()
    {
        // given
        String participantLdapId = "userId";
        String participantType = "write";
        Long folderId = 1L;
        Long childFolderId = 2L;

        AcmFolder parentFolder = new AcmFolder();

        List<AcmParticipant> folderParticipants = new ArrayList<>();

        AcmParticipant folderParticipant = new AcmParticipant();
        folderParticipant.setParticipantLdapId(participantLdapId);
        folderParticipant.setParticipantType(participantType);

        folderParticipants.add(folderParticipant);

        AcmFolder folder = new AcmFolder();
        folder.setId(folderId);
        folder.setParticipants(folderParticipants);
        folder.setParentFolder(parentFolder);

        List<AcmParticipant> childFolderParticipants = new ArrayList<>();

        AcmParticipant childFolderParticipant = new AcmParticipant();
        childFolderParticipant.setParticipantLdapId(participantLdapId);
        childFolderParticipant.setParticipantType(participantType);

        childFolderParticipants.add(childFolderParticipant);

        AcmFolder childFolder = new AcmFolder();
        childFolder.setId(childFolderId);
        childFolder.setParticipants(childFolderParticipants);
        childFolder.setParentFolder(folder);

        folder.setChildrenFolders(Arrays.asList(childFolder));

        List<AcmParticipant> fileParticipants = new ArrayList<>();

        AcmParticipant fileParticipant = new AcmParticipant();
        fileParticipant.setParticipantLdapId(participantLdapId);
        fileParticipant.setParticipantType(participantType);

        fileParticipants.add(fileParticipant);

        EcmFile file = new EcmFile();
        file.setParticipants(fileParticipants);
        file.setFolder(parentFolder);

        expect(mockFileDao.findByFolderId(folder.getId(), FlushModeType.COMMIT)).andReturn(Arrays.asList(file));
        expect(mockFileDao.findByFolderId(childFolder.getId(), FlushModeType.COMMIT)).andReturn(new ArrayList<>());
        expect(mockFileDao.save(file)).andReturn(file);

        // when
        replayAll();
        fileParticipantService.setFolderParticipantsFromParentFolder(folder);

        // then
        verifyAll();
        assertEquals(0, folder.getParticipants().size());
        assertEquals(0, childFolder.getParticipants().size());
        assertEquals(0, file.getParticipants().size());
    }

    @Test(expected = IllegalStateException.class)
    public void testSetFolderParticipantsFromParentFolderThrowsIllegalStateExceptionWhenFolderHasNoParentFolder()
            throws AcmAccessControlException
    {
        // given
        AcmFolder folder = new AcmFolder();
        folder.setParentFolder(null);

        // when
        fileParticipantService.setFolderParticipantsFromParentFolder(folder);

        // then
        fail("IllegalStateException should have been thrown!");
    }

    @Test
    public void testGetFolderParticipantsFromAssignedObjectReturnsCorrectParticipants()
    {
        // given
        fileParticipantService.getEcmFileServiceProperties().setProperty("ecm.documentsParticipantTypes.mappings.write",
                "assignee,owner");
        fileParticipantService.getEcmFileServiceProperties().setProperty("ecm.documentsParticipantTypes.mappings.read",
                "follower,reader");
        fileParticipantService.getEcmFileServiceProperties().setProperty("ecm.documentsParticipantTypes.mappings.no-access",
                "No Access");
        fileParticipantService.getEcmFileServiceProperties().setProperty("ecm.documentsParticipantTypes.mappings.group-write",
                "owning group");
        fileParticipantService.getEcmFileServiceProperties().setProperty("ecm.documentsParticipantTypes.mappings.group-read",
                "follower group");

        List<AcmParticipant> assignedObjectParticipants = new ArrayList<>();

        AcmParticipant participantAssignee = new AcmParticipant();
        participantAssignee.setParticipantLdapId("userId1");
        participantAssignee.setParticipantType("assignee");
        assignedObjectParticipants.add(participantAssignee);

        AcmParticipant participantOwner = new AcmParticipant();
        participantOwner.setParticipantLdapId("userId2");
        participantOwner.setParticipantType("owner");
        assignedObjectParticipants.add(participantOwner);

        AcmParticipant participantReader = new AcmParticipant();
        participantReader.setParticipantLdapId("userId3");
        participantReader.setParticipantType("reader");
        assignedObjectParticipants.add(participantReader);

        AcmParticipant participantFollower = new AcmParticipant();
        participantFollower.setParticipantLdapId("userId4");
        participantFollower.setParticipantType("follower");
        assignedObjectParticipants.add(participantFollower);

        AcmParticipant participantNoAccess = new AcmParticipant();
        participantNoAccess.setParticipantLdapId("userId5");
        participantNoAccess.setParticipantType("No Access");
        assignedObjectParticipants.add(participantNoAccess);

        AcmParticipant participantOwningGroup = new AcmParticipant();
        participantOwningGroup.setParticipantLdapId("groupId1");
        participantOwningGroup.setParticipantType("owning group");
        assignedObjectParticipants.add(participantOwningGroup);

        AcmParticipant participantFollowerGroup = new AcmParticipant();
        participantFollowerGroup.setParticipantLdapId("groupId2");
        participantFollowerGroup.setParticipantType("follower group");
        assignedObjectParticipants.add(participantFollowerGroup);

        // when
        List<AcmParticipant> returnedFileParticipants = fileParticipantService
                .getFolderParticipantsFromAssignedObject(assignedObjectParticipants);

        // then
        assertEquals(7, returnedFileParticipants.size());
        for (AcmParticipant participant : returnedFileParticipants)
        {
            switch (participant.getParticipantLdapId())
            {
            case "userId1":
                assertEquals("write", participant.getParticipantType());
                break;
            case "userId2":
                assertEquals("write", participant.getParticipantType());
                break;
            case "userId3":
                assertEquals("read", participant.getParticipantType());
                break;
            case "userId4":
                assertEquals("read", participant.getParticipantType());
                break;
            case "userId5":
                assertEquals("no-access", participant.getParticipantType());
                break;
            case "groupId1":
                assertEquals("group-write", participant.getParticipantType());
                break;
            case "groupId2":
                assertEquals("group-read", participant.getParticipantType());
                break;
            default:
                fail("Unknown participantLdapId: [" + participant.getParticipantLdapId() + "] returned!");
                break;
            }
        }
    }

    @Test
    public void testGetFolderParticipantsFromAssignedObjectReturnsUniqueFileParticipant()
    {
        // given
        String userId = "userId";

        fileParticipantService.getEcmFileServiceProperties().setProperty("ecm.documentsParticipantTypes.mappings.write",
                "assignee,owner");

        List<AcmParticipant> assignedObjectParticipants = new ArrayList<>();

        AcmParticipant participantAssignee = new AcmParticipant();
        participantAssignee.setParticipantLdapId(userId);
        participantAssignee.setParticipantType("assignee");
        assignedObjectParticipants.add(participantAssignee);

        AcmParticipant participantOwner = new AcmParticipant();
        participantOwner.setParticipantLdapId(userId);
        participantOwner.setParticipantType("owner");
        assignedObjectParticipants.add(participantOwner);

        // when
        List<AcmParticipant> returnedFileParticipants = fileParticipantService
                .getFolderParticipantsFromAssignedObject(assignedObjectParticipants);

        // then
        assertEquals(1, returnedFileParticipants.size());
        assertEquals(userId, returnedFileParticipants.get(0).getParticipantLdapId());
        assertEquals("write", returnedFileParticipants.get(0).getParticipantType());
    }

    @Test(expected = IllegalStateException.class)
    public void testGetFolderParticipantsFromAssignedObjectThrowsIllegalStateExceptionWhenAnAssignedObjectParticipantTypeIsNotMapped()
    {
        // given
        String mappedParticipantType = "someType";

        fileParticipantService.getEcmFileServiceProperties().setProperty("ecm.documentsParticipantTypes.mappings.write",
                mappedParticipantType);

        List<AcmParticipant> assignedObjectParticipants = new ArrayList<>();

        AcmParticipant participantWithMappedType = new AcmParticipant();
        participantWithMappedType.setParticipantLdapId("userId");
        participantWithMappedType.setParticipantType(mappedParticipantType);
        assignedObjectParticipants.add(participantWithMappedType);

        AcmParticipant participantWithNotMappedType = new AcmParticipant();
        participantWithNotMappedType.setParticipantLdapId("userId");
        participantWithNotMappedType.setParticipantType("unmappedType");
        assignedObjectParticipants.add(participantWithNotMappedType);

        // when
        fileParticipantService.getFolderParticipantsFromAssignedObject(assignedObjectParticipants);

        // then
        fail("IllegalStateException should have been thrown!");
    }

    @Test
    public void testSetFileFolderParticipantsSetsParticipantsCorrectlyOnFile() throws AcmParticipantsException
    {
        // given
        final Long objectId = 1L;
        final String objectType = "FILE";
        final String participantLdapId1 = "userId1";
        final String participantType1 = "write";
        final String participantLdapId2 = "userId2";
        final String participantType2 = "read";

        List<AcmParticipant> participants = new ArrayList<>();

        AcmParticipant participant1 = new AcmParticipant();
        participant1.setParticipantLdapId(participantLdapId1);
        participant1.setParticipantType(participantType1);
        participants.add(participant1);

        AcmParticipant participant2 = new AcmParticipant();
        participant2.setParticipantLdapId(participantLdapId2);
        participant2.setParticipantType(participantType2);
        participants.add(participant2);

        EcmFile file = new EcmFile();

        expect(mockFileDao.find(objectId)).andReturn(file);
        expect(mockFileDao.save(file)).andReturn(file);
        EntityManager em = mock(EntityManager.class);
        expect(mockFileDao.getEm()).andReturn(em);
        em.flush();
        expectLastCall();

        // when
        replayAll();
        List<AcmParticipant> returnedParticipants = fileParticipantService.setFileFolderParticipants(objectId, objectType, participants);

        // then
        verifyAll();
        assertEquals(2, returnedParticipants.size());
        for (AcmParticipant participant : returnedParticipants)
        {
            switch (participant.getParticipantLdapId())
            {
            case participantLdapId1:
                assertEquals(participantType1, participant.getParticipantType());
                break;
            case participantLdapId2:
                assertEquals(participantType2, participant.getParticipantType());
                break;
            default:
                fail("Unknown participantLdapId: [" + participant.getParticipantLdapId() + "] returned!");
                break;
            }
        }
    }

    @Test
    public void testSetFileFolderParticipantsSetsParticipantsCorrectlyOnFolder() throws AcmParticipantsException
    {
        // given
        final Long objectId = 1L;
        final String objectType = "FOLDER";
        final String participantLdapId1 = "userId1";
        final String participantType1 = "write";
        final String participantLdapId2 = "userId2";
        final String participantType2 = "read";

        List<AcmParticipant> participants = new ArrayList<>();

        AcmParticipant participant1 = new AcmParticipant();
        participant1.setParticipantLdapId(participantLdapId1);
        participant1.setParticipantType(participantType1);
        participant1.setReplaceChildrenParticipant(true);
        participants.add(participant1);

        AcmParticipant participant2 = new AcmParticipant();
        participant2.setParticipantLdapId(participantLdapId2);
        participant2.setParticipantType(participantType2);
        participants.add(participant2);

        AcmFolder folder = new AcmFolder();
        folder.setId(objectId);
        EcmFile file = new EcmFile();

        expect(mockFolderService.findById(objectId)).andReturn(folder);
        expect(mockFileDao.findByFolderId(objectId, FlushModeType.COMMIT)).andReturn(Arrays.asList(file));
        expect(mockFileDao.save(file)).andReturn(file);
        expect(mockFolderService.saveFolder(folder)).andReturn(folder);
        EntityManager em = mock(EntityManager.class);
        expect(mockFileDao.getEm()).andReturn(em);
        em.flush();
        expectLastCall();

        // when
        replayAll();
        List<AcmParticipant> returnedParticipants = fileParticipantService.setFileFolderParticipants(objectId, objectType, participants);

        // then
        verifyAll();

        // assert folder participants
        assertEquals(2, returnedParticipants.size());
        for (AcmParticipant participant : returnedParticipants)
        {
            switch (participant.getParticipantLdapId())
            {
            case participantLdapId1:
                assertEquals(participantType1, participant.getParticipantType());
                break;
            case participantLdapId2:
                assertEquals(participantType2, participant.getParticipantType());
                break;
            default:
                fail("Unknown participantLdapId: [" + participant.getParticipantLdapId() + "] returned!");
                break;
            }
        }

        // assert file participants
        assertEquals(1, file.getParticipants().size());
        assertEquals(participantLdapId1, file.getParticipants().get(0).getParticipantLdapId());
        assertEquals(participantType1, file.getParticipants().get(0).getParticipantType());
    }

    @Test(expected = AcmParticipantsException.class)
    public void testSetFileFolderParticipantsThrowsAcmParticipantsExceptionOnOtherObjectType() throws AcmParticipantsException
    {
        // given
        final Long objectId = 1L;
        final String objectType = "OTHER";

        List<AcmParticipant> participants = new ArrayList<>();

        // when
        fileParticipantService.setFileFolderParticipants(objectId, objectType, participants);

        // then
        fail("AcmParticipantsException should have been thrown!");
    }

    @Test(expected = AcmParticipantsException.class)
    public void testSetFileFolderParticipantsThrowsAcmParticipantsExceptionOnDuplicateParticipantsLdapIds() throws AcmParticipantsException
    {
        // given
        final Long objectId = 1L;
        final String objectType = "FOLDER";
        final String participantLdapId = "userId";
        final String participantType1 = "write";
        final String participantType2 = "read";

        List<AcmParticipant> participants = new ArrayList<>();

        AcmParticipant participant1 = new AcmParticipant();
        participant1.setParticipantLdapId(participantLdapId);
        participant1.setParticipantType(participantType1);
        participant1.setReplaceChildrenParticipant(true);
        participants.add(participant1);

        AcmParticipant participant2 = new AcmParticipant();
        participant2.setParticipantLdapId(participantLdapId);
        participant2.setParticipantType(participantType2);
        participants.add(participant2);

        // when
        fileParticipantService.setFileFolderParticipants(objectId, objectType, participants);

        // then
        fail("AcmParticipantsException should have been thrown!");
    }

    @Test(expected = AcmParticipantsException.class)
    public void testSetFileFolderParticipantsThrowsAcmParticipantsExceptionOnNullParticipantLdapId() throws AcmParticipantsException
    {
        // given
        final Long objectId = 1L;
        final String objectType = "FOLDER";
        final String participantLdapId = null;
        final String participantType1 = "write";

        List<AcmParticipant> participants = new ArrayList<>();

        AcmParticipant participant1 = new AcmParticipant();
        participant1.setParticipantLdapId(participantLdapId);
        participant1.setParticipantType(participantType1);
        participant1.setReplaceChildrenParticipant(true);
        participants.add(participant1);

        // when
        fileParticipantService.setFileFolderParticipants(objectId, objectType, participants);

        // then
        fail("AcmParticipantsException should have been thrown!");
    }

    @Test(expected = AcmParticipantsException.class)
    public void testSetFileFolderParticipantsThrowsAcmParticipantsExceptionOnNullParticipantType() throws AcmParticipantsException
    {
        // given
        final Long objectId = 1L;
        final String objectType = "FOLDER";
        final String participantLdapId = "userId";
        final String participantType1 = null;

        List<AcmParticipant> participants = new ArrayList<>();

        AcmParticipant participant1 = new AcmParticipant();
        participant1.setParticipantLdapId(participantLdapId);
        participant1.setParticipantType(participantType1);
        participant1.setReplaceChildrenParticipant(true);
        participants.add(participant1);

        // when
        fileParticipantService.setFileFolderParticipants(objectId, objectType, participants);

        // then
        fail("AcmParticipantsException should have been thrown!");
    }

    @Test(expected = AcmParticipantsException.class)
    public void testSetFileFolderParticipantsThrowsAcmParticipantsExceptionOnInvalidParticipantType() throws AcmParticipantsException
    {
        // given
        final Long objectId = 1L;
        final String objectType = "FOLDER";
        final String participantLdapId = "userId";
        final String participantType1 = "invalidFileParticipantType";

        List<AcmParticipant> participants = new ArrayList<>();

        AcmParticipant participant1 = new AcmParticipant();
        participant1.setParticipantLdapId(participantLdapId);
        participant1.setParticipantType(participantType1);
        participant1.setReplaceChildrenParticipant(true);
        participants.add(participant1);

        // when
        fileParticipantService.setFileFolderParticipants(objectId, objectType, participants);

        // then
        fail("AcmParticipantsException should have been thrown!");
    }
}
