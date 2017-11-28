package com.armedia.acm.plugins.ecm.service.impl;

import com.armedia.acm.core.exceptions.AcmAccessControlException;
import com.armedia.acm.core.exceptions.AcmParticipantsException;
import com.armedia.acm.plugins.ecm.dao.AcmFolderDao;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.services.participants.model.ParticipantTypes;
import com.armedia.acm.services.participants.service.AcmParticipantService;

import org.apache.commons.lang.StringUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.FlushModeType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by bojan.milenkoski on 06.10.2017
 */
public class EcmFileParticipantService
{
    private AcmFolderDao folderDao;
    private EcmFileDao fileDao;
    private AcmParticipantService participantService;
    private Properties ecmFileServiceProperties;

    private static List<String> fileParticipantTypes = Arrays.asList("group-write", "group-read", "group-no-access", "write", "read",
            "no-access");

    @Transactional(rollbackFor = Exception.class)
    public EcmFile setFileParticipantsFromParentFolder(EcmFile file)
    {
        if (file.getFolder() == null)
        {
            throw new RuntimeException("File doesn't have parent folder!");
        }

        // Use the participants service to avoid execution of Drools Assignment and Access rules
        // if the user doesn't have permissions on the file, the participants cannot be changed by inheriting
        // participants from parent entity
        file.getParticipants().forEach(participant -> getParticipantService().removeParticipant(participant.getId()));
        file.getParticipants().clear();

        // set participants from parent folder
        file.getFolder().getParticipants().forEach(folderParticipant -> {
            AcmParticipant participant = new AcmParticipant();
            participant.setParticipantType(folderParticipant.getParticipantType());
            participant.setParticipantLdapId(folderParticipant.getParticipantLdapId());
            file.getParticipants().add(participant);
        });

        return getFileDao().save(file);
    }

    @Transactional(rollbackFor = Exception.class)
    public AcmFolder setFolderParticipantsFromParentFolder(AcmFolder folder) throws AcmAccessControlException
    {
        if (folder.getParentFolder() == null)
        {
            throw new RuntimeException("Folder doesn't have parent folder!");
        }

        // clear existing participants
        // Use the participants service to avoid execution of Drools Assignment rules
        folder.getParticipants().forEach(participant -> getParticipantService().removeParticipant(participant.getId()));
        folder.getParticipants().clear();

        // set participants from parent folder
        for (AcmParticipant folderParticipant : folder.getParentFolder().getParticipants())
        {
            AcmParticipant participant = new AcmParticipant();
            participant.setParticipantType(folderParticipant.getParticipantType());
            participant.setParticipantLdapId(folderParticipant.getParticipantLdapId());
            folder.getParticipants().add(participant);
        }
        folder = getFolderDao().save(folder);

        // set participants to child folders
        List<AcmFolder> subfolders = folder.getChildrenFolders();
        for (AcmFolder subFolder : subfolders)
        {
            AcmFolder updatedSubfolder = setFolderParticipantsFromParentFolder(subFolder);
            // replace subfolder with updated instance
            subfolders.set(subfolders.indexOf(subFolder), updatedSubfolder);
        }

        // set participants to files in the folder
        getFileDao().findByFolderId(folder.getId(), FlushModeType.COMMIT).forEach(file -> setFileParticipantsFromParentFolder(file));

        return folder;
    }

    @Transactional(rollbackFor = Exception.class)
    public AcmFolder setParticipantToFolderChildren(AcmFolder folder, AcmParticipant participant) throws AcmAccessControlException
    {
        if (StringUtils.isEmpty(participant.getParticipantLdapId()))
        {
            // we don't use participants without ldapId for files/folders as we do for AssignedObject
            return folder;
        }

        // set participant to child folders
        List<AcmFolder> subfolders = folder.getChildrenFolders();
        for (AcmFolder subFolder : subfolders)
        {
            AcmFolder updatedSubfolder = setParticipantToFolderChildren(subFolder, participant);
            // replace subfolder with updated instance
            subfolders.set(subfolders.indexOf(subFolder), updatedSubfolder);
        }

        // set participant to current folder
        Optional<AcmParticipant> existingFolderParticipants = folder.getParticipants().stream()
                .filter(existingParticipant -> existingParticipant.getParticipantLdapId().equals(participant.getParticipantLdapId()))
                .findFirst();

        // for files and folders only one AcmParticipant per user is allowed
        if (existingFolderParticipants.isPresent())
        {
            // change the role of the existing participant if needed
            if (!existingFolderParticipants.get().getParticipantType().equals(participant.getParticipantType()))
            {
                getParticipantService().changeParticipantRole(existingFolderParticipants.get(), participant.getParticipantType());
                existingFolderParticipants.get().setParticipantType(participant.getParticipantType());
            }
        }
        else
        {
            AcmParticipant savedParticipant = getParticipantService().saveParticipant(participant.getParticipantLdapId(),
                    participant.getParticipantType(), folder.getId(),
                    EcmFileConstants.OBJECT_FOLDER_TYPE);
            folder.getParticipants().add(savedParticipant);
        }

        // set participant to files in folder
        List<EcmFile> files = getFileDao().findByFolderId(folder.getId(), FlushModeType.COMMIT);
        for (EcmFile file : files)
        {
            Optional<AcmParticipant> existingFileParticipant = file.getParticipants().stream()
                    .filter(existingParticipant -> existingParticipant.getParticipantLdapId().equals(participant.getParticipantLdapId()))
                    .findFirst();

            // for files and folders only one AcmParticipant per user is allowed
            if (existingFileParticipant.isPresent())
            {
                // change the role of the existing participant if needed
                if (!existingFileParticipant.get().getParticipantType().equals(participant.getParticipantType()))
                {
                    getParticipantService().changeParticipantRole(existingFileParticipant.get(), participant.getParticipantType());
                }
            }
            else
            {
                getParticipantService().saveParticipant(participant.getParticipantLdapId(), participant.getParticipantType(), file.getId(),
                        EcmFileConstants.OBJECT_FILE_TYPE);
            }
        }

        return folder;
    }

    @Transactional(rollbackFor = Exception.class)
    public AcmFolder removeParticipantFromFolderAndChildren(AcmFolder folder, String participantLdapId, String participantType)
            throws AcmAccessControlException
    {
        // remove participant from child folders
        List<AcmFolder> subfolders = folder.getChildrenFolders();
        for (AcmFolder subFolder : subfolders)
        {
            AcmFolder updatedSubfolder = removeParticipantFromFolderAndChildren(subFolder, participantLdapId, participantType);
            // replace subfolder with updated instance
            subfolders.set(subfolders.indexOf(subFolder), updatedSubfolder);
        }

        // remove participant from current folder
        getParticipantService().removeParticipant(participantLdapId, participantType, EcmFileConstants.OBJECT_FOLDER_TYPE, folder.getId());
        folder.getParticipants().removeIf(participant -> participant.getParticipantLdapId().equals(participantLdapId)
                && participant.getParticipantType().equals(participantType));

        // remove participants from files in folder
        getFileDao().findByFolderId(folder.getId(), FlushModeType.COMMIT)
                .forEach(file -> getParticipantService().removeParticipant(participantLdapId,
                        participantType, EcmFileConstants.OBJECT_FILE_TYPE, file.getId()));

        return folder;
    }

    public List<AcmParticipant> getFolderParticipantsFromParentAssignedObject(String objectType, Long objectId)
    {
        List<AcmParticipant> participants = getParticipantService().getParticipantsFromParentObject(objectType, objectId);
        return getFolderParticipantsFromAssignedObject(participants);
    }

    public List<AcmParticipant> getFolderParticipantsFromAssignedObject(List<AcmParticipant> assignedObjectParticipants)
    {
        List<AcmParticipant> participants = new ArrayList<>();
        assignedObjectParticipants.forEach(parentObjectParticipant -> {

            AcmParticipant participant = new AcmParticipant();
            participant.setParticipantType(
                    getDocumentParticipantTypeFromAssignedObjectParticipantType(parentObjectParticipant.getParticipantType()));
            participant.setParticipantLdapId(parentObjectParticipant.getParticipantLdapId());

            // for files and folders we allow only one AcmParticipant for one user
            if (!participants.stream()
                    .anyMatch(existingParticipant -> existingParticipant.getParticipantLdapId().equals(participant.getParticipantLdapId())))
            {
                participants.add(participant);
            }
        });

        return participants;
    }

    @Transactional(rollbackFor = Exception.class)
    public void inheritParticipantsFromAssignedObject(List<AcmParticipant> assignedObjectParticipants,
            List<AcmParticipant> originalAssignedObjectParticipants, AcmContainer acmContainer) throws AcmAccessControlException
    {
        if (acmContainer.getFolder() != null)
        {
            inheritParticipantsFromAssignedObject(assignedObjectParticipants,
                    originalAssignedObjectParticipants, acmContainer.getFolder());
        }
        if (acmContainer.getAttachmentFolder() != null)
        {
            inheritParticipantsFromAssignedObject(assignedObjectParticipants,
                    originalAssignedObjectParticipants, acmContainer.getAttachmentFolder());
        }
    }

    private AcmFolder inheritParticipantsFromAssignedObject(List<AcmParticipant> assignedObjectParticipants,
            List<AcmParticipant> originalAssignedObjectParticipants, AcmFolder folder) throws AcmAccessControlException
    {
        boolean inheritAllParticipants = assignedObjectParticipants.stream()
                .allMatch(participant -> participant.isReplaceChildrenParticipant());

        // inherit participants where needed
        for (AcmParticipant participant : assignedObjectParticipants)
        {
            if (participant.isReplaceChildrenParticipant())
            {
                folder = setParticipantToFolderChildren(folder, getDocumentParticipantFromAssignedObjectParticipant(participant));
            }
        }

        // remove deleted parent participants from children
        for (AcmParticipant rootFolderParticipant : folder.getParticipants())
        {
            boolean existsParentParticipant = assignedObjectParticipants.stream()
                    .anyMatch(participant -> rootFolderParticipant.getParticipantLdapId().equals(participant.getParticipantLdapId())
                            && rootFolderParticipant.getParticipantType()
                                    .equals(getDocumentParticipantFromAssignedObjectParticipant(participant).getParticipantType()));

            if (!existsParentParticipant)
            {
                boolean existsOriginalParentParticipant = originalAssignedObjectParticipants.stream()
                        .anyMatch(participant -> rootFolderParticipant.getParticipantLdapId().equals(participant.getParticipantLdapId())
                                && rootFolderParticipant.getParticipantType()
                                        .equals(getDocumentParticipantFromAssignedObjectParticipant(participant).getParticipantType()));

                // do not delete added participant to folder if not deleted from assignedObject
                if (inheritAllParticipants || existsOriginalParentParticipant)
                {
                    folder = removeParticipantFromFolderAndChildren(folder, rootFolderParticipant.getParticipantLdapId(),
                            rootFolderParticipant.getParticipantType());
                }
            }
        }

        return folder;
    }

    @Transactional
    public AcmContainer setRestrictedFlagRecursively(Boolean restricted, AcmContainer acmContainer)
    {
        if (acmContainer.getFolder() != null)
        {
            AcmFolder savedFolder = setRestrictedFlagRecursively(restricted, acmContainer.getFolder());
            acmContainer.setFolder(savedFolder);
        }
        if (acmContainer.getAttachmentFolder() != null)
        {
            AcmFolder savedFolder = setRestrictedFlagRecursively(restricted, acmContainer.getAttachmentFolder());
            acmContainer.setAttachmentFolder(savedFolder);
        }

        return acmContainer;
    }

    private AcmFolder setRestrictedFlagRecursively(Boolean restricted, AcmFolder folder)
    {
        folder.setRestricted(restricted);
        folder = getFolderDao().save(folder);

        // set restricted flag to child folders
        List<AcmFolder> subfolders = getFolderDao().findSubFolders(folder.getId(), FlushModeType.COMMIT);
        for (AcmFolder subFolder : subfolders)
        {
            AcmFolder updatedSubfolder = setRestrictedFlagRecursively(restricted, subFolder);
            // replace subfolder with updated instance
            subfolders.set(subfolders.indexOf(subFolder), updatedSubfolder);
        }

        // set restricted flag to files in the folder
        getFileDao().findByFolderId(folder.getId(), FlushModeType.COMMIT).forEach(file -> {
            file.setRestricted(restricted);
            getFileDao().save(file);
        });

        return folder;
    }

    private AcmParticipant getDocumentParticipantFromAssignedObjectParticipant(AcmParticipant participant)
    {
        AcmParticipant documentParticipant = new AcmParticipant();
        documentParticipant.setParticipantLdapId(participant.getParticipantLdapId());
        documentParticipant
                .setParticipantType(getDocumentParticipantTypeFromAssignedObjectParticipantType(participant.getParticipantType()));
        return documentParticipant;
    }

    /**
     * Returns the file participant type that is mapped with the given assigned object participant type. If no such
     * mapping is found this method returns null.
     * 
     * @param assignedObjectParticipantType
     *            the assigned object participant type
     * @return the file participant type
     */
    private String getDocumentParticipantTypeFromAssignedObjectParticipantType(String assignedObjectParticipantType)
    {
        if (assignedObjectParticipantType.equals("*"))
        {
            return assignedObjectParticipantType;
        }

        String documentParticipantType = fileParticipantTypes.stream()
                .filter(fileParticipantType -> Arrays.asList(getEcmFileServiceProperties()
                        .getProperty("ecm.documentsParticipantTypes.mappings." + fileParticipantType).split(","))
                        .contains(assignedObjectParticipantType))
                .findFirst().orElse(null);

        if (documentParticipantType == null)
        {
            throw new RuntimeException("No document participant type mapping found for participant type: " + assignedObjectParticipantType
                    + ". Add mapping for new participant type in 'ecmFileService.properties'!");
        }

        return documentParticipantType;
    }

    /**
     * Validates the {@link AcmParticipant}s for files or folders.
     *
     * @param participants
     *            the list of {@link AcmParticipant}s to validate
     * @throws AcmParticipantsException
     *             when the {@link AcmParticipant}s are not valid.
     */
    public void validateFileParticipants(List<AcmParticipant> participants) throws AcmParticipantsException
    {
        // missing participant id
        List<AcmParticipant> missingParticipantLdapIds = participants.stream().filter(participant -> participant.getReceiverLdapId() == null
                && !participant.getParticipantType().equals(ParticipantTypes.ASSIGNEE)).collect(Collectors.toList());

        if (missingParticipantLdapIds.size() > 0)
        {
            String errorMessage = "Missing participant LDAP id!";
            List<String> errorList = missingParticipantLdapIds.stream()
                    .map(participant -> "ParticipantLdapId: " + participant.getParticipantType()).collect(Collectors.toList());
            throw new AcmParticipantsException(errorList, errorMessage);
        }

        // missing participantTypes
        List<AcmParticipant> missingParticipantTypes = participants.stream().filter(participant -> participant.getParticipantType() == null)
                .collect(Collectors.toList());

        if (missingParticipantTypes.size() > 0)
        {
            String errorMessage = "Missing participant type!";
            List<String> errorList = missingParticipantTypes.stream()
                    .map(participant -> "ParticipantLdapId: " + participant.getParticipantLdapId()).collect(Collectors.toList());
            throw new AcmParticipantsException(errorList, errorMessage);
        }

        // multiple assignees
        List<AcmParticipant> assignees = participants.stream()
                .filter(participant -> participant.getParticipantType() == ParticipantTypes.ASSIGNEE).collect(Collectors.toList());
        if (assignees.size() > 1)
        {
            String errorMessage = "Multiple assignees found!";
            List<String> errorList = assignees.stream().map(participant -> "ParticipantLdapId: " + participant.getParticipantLdapId())
                    .collect(Collectors.toList());
            throw new AcmParticipantsException(errorList, errorMessage);
        }

        // multiple owning groups
        List<AcmParticipant> owningGroups = participants.stream()
                .filter(participant -> participant.getParticipantType() == ParticipantTypes.OWNING_GROUP).collect(Collectors.toList());
        if (owningGroups.size() > 1)
        {
            String errorMessage = "Multiple owning groups found!";
            List<String> errorList = owningGroups.stream().map(participant -> "ParticipantLdapId: " + participant.getParticipantLdapId())
                    .collect(Collectors.toList());
            throw new AcmParticipantsException(errorList, errorMessage);
        }

        // search for duplicate participants LDAPIds. One participant cannot have different roles for an object
        Set<String> allLdapIds = new HashSet<>();
        Set<String> duplicateParticipantLdapIds = participants.stream().map(participant -> participant.getParticipantLdapId())
                .filter(participantLdapId -> !allLdapIds.add(participantLdapId)).collect(Collectors.toSet());
        if (duplicateParticipantLdapIds.size() > 0)
        {
            String errorMessage = "Participants in multiple roles found!";
            List<String> errorList = duplicateParticipantLdapIds.stream()
                    .map(participantLdapId -> "ParticipantLdapId: " + participantLdapId).collect(Collectors.toList());
            throw new AcmParticipantsException(errorList, errorMessage);
        }
    }

    public EcmFileDao getFileDao()
    {
        return fileDao;
    }

    public void setFileDao(EcmFileDao fileDao)
    {
        this.fileDao = fileDao;
    }

    public AcmParticipantService getParticipantService()
    {
        return participantService;
    }

    public void setParticipantService(AcmParticipantService participantService)
    {
        this.participantService = participantService;
    }

    public AcmFolderDao getFolderDao()
    {
        return folderDao;
    }

    public void setFolderDao(AcmFolderDao folderDao)
    {
        this.folderDao = folderDao;
    }

    public Properties getEcmFileServiceProperties()
    {
        return ecmFileServiceProperties;
    }

    public void setEcmFileServiceProperties(Properties ecmFileServiceProperties)
    {
        this.ecmFileServiceProperties = ecmFileServiceProperties;
    }
}
