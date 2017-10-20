package com.armedia.acm.plugins.ecm.service.impl;

import com.armedia.acm.core.AcmObject;
import com.armedia.acm.core.exceptions.AcmAccessControlException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.ecm.dao.AcmFolderDao;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.plugins.ecm.service.AcmFolderService;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.services.participants.service.AcmParticipantService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * Created by bojan.milenkoski on 06.10.2017
 */
public class EcmFileParticipantService
{
    private AcmFolderService folderService;
    private AcmFolderDao folderDao;
    private EcmFileDao fileDao;
    private AcmParticipantService participantService;
    private Properties ecmFileServiceProperties;

    private static List<String> fileParticipantTypes = Arrays.asList("group-write", "group-read", "group-no-access", "write", "read",
            "no-access");

    public void setFileParticipantsFromParentFolder(EcmFile file)
    {
        if (file.getFolder() == null)
        {
            throw new RuntimeException("File doesn't have parent folder!");
        }

        // Use the participants service to avoid execution of Drools Assignment rules
        file.getParticipants().forEach(participant -> getParticipantService().removeParticipant(participant.getId()));
        file.getParticipants().clear();

        // set participants from parent folder
        file.getFolder().getParticipants().forEach(folderParticipant -> {
            AcmParticipant participant = new AcmParticipant();
            participant.setParticipantType(folderParticipant.getParticipantType());
            participant.setParticipantLdapId(folderParticipant.getParticipantLdapId());
            file.getParticipants().add(participant);
        });

        getFileDao().save(file);
    }

    public void setFolderParticipantsFromParentFolder(AcmFolder folder) throws AcmAccessControlException
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
        folder.getParentFolder().getParticipants().forEach(folderParticipant -> {
            AcmParticipant participant = new AcmParticipant();
            participant.setParticipantType(folderParticipant.getParticipantType());
            participant.setParticipantLdapId(folderParticipant.getParticipantLdapId());
            folder.getParticipants().add(participant);
        });
        getFolderDao().save(folder);

        List<AcmObject> childObjects = null;
        try
        {
            childObjects = getFolderService().getFolderChildren(folder.getId());
        }
        catch (AcmUserActionFailedException | AcmObjectNotFoundException e)
        {
            throw new AcmAccessControlException(Arrays.asList(e.getMessage()), e.getMessage());
        }

        // set participants to child folders
        List<AcmFolder> childFolders = childObjects.stream().filter(AcmFolder.class::isInstance).map(AcmFolder.class::cast)
                .collect(Collectors.toList());
        for (AcmFolder childFolder : childFolders)
        {
            setFolderParticipantsFromParentFolder(childFolder);
        }

        // set participants to files in the folder
        List<EcmFile> files = childObjects.stream().filter(EcmFile.class::isInstance).map(EcmFile.class::cast).collect(Collectors.toList());
        files.forEach(file -> setFileParticipantsFromParentFolder(file));
    }

    public void setParticipantToFolderChildren(AcmFolder folder, AcmParticipant participant) throws AcmAccessControlException
    {
        List<AcmObject> childObjects = null;
        try
        {
            childObjects = getFolderService().getFolderChildren(folder.getId());
        }
        catch (AcmUserActionFailedException | AcmObjectNotFoundException e)
        {
            throw new AcmAccessControlException(Arrays.asList(e.getMessage()), e.getMessage());
        }

        // set participant to child folders
        List<AcmFolder> childFolders = childObjects.stream().filter(AcmFolder.class::isInstance).map(AcmFolder.class::cast)
                .collect(Collectors.toList());
        for (AcmFolder childFolder : childFolders)
        {
            setParticipantToFolderChildren(childFolder, participant);
        }

        // set participant to current folder
        AcmParticipant existingParticipant = getParticipantService().getParticipantByParticipantTypeAndObjectTypeAndId(
                participant.getParticipantLdapId(), participant.getParticipantType(), EcmFileConstants.OBJECT_FOLDER_TYPE, folder.getId());
        // skip existing participant type for one user
        if (existingParticipant == null)
        {
            getParticipantService().saveParticipant(participant.getParticipantLdapId(), participant.getParticipantType(), folder.getId(),
                    EcmFileConstants.OBJECT_FOLDER_TYPE);
        }

        // set participant to files in folder
        List<EcmFile> files = childObjects.stream().filter(EcmFile.class::isInstance).map(EcmFile.class::cast).collect(Collectors.toList());
        for (EcmFile file : files)
        {
            AcmParticipant existingFileParticipant = getParticipantService().getParticipantByParticipantTypeAndObjectTypeAndId(
                    participant.getParticipantLdapId(), participant.getParticipantType(), EcmFileConstants.OBJECT_FILE_TYPE, file.getId());
            // skip existing participant type for one user
            if (existingFileParticipant == null)
            {
                getParticipantService().saveParticipant(participant.getParticipantLdapId(), participant.getParticipantType(), file.getId(),
                        EcmFileConstants.OBJECT_FILE_TYPE);
            }
        }
    }

    public void removeParticipantFromFolderAndChildren(AcmFolder folder, String participantLdapId, String participantType)
            throws AcmAccessControlException
    {
        List<AcmObject> childObjects = null;
        try
        {
            childObjects = getFolderService().getFolderChildren(folder.getId());
        }
        catch (AcmUserActionFailedException | AcmObjectNotFoundException e)
        {
            throw new AcmAccessControlException(Arrays.asList(e.getMessage()), e.getMessage());
        }

        // remove participant to child folders
        List<AcmFolder> childFolders = childObjects.stream().filter(AcmFolder.class::isInstance).map(AcmFolder.class::cast)
                .collect(Collectors.toList());
        for (AcmFolder childFolder : childFolders)
        {
            removeParticipantFromFolderAndChildren(childFolder, participantLdapId, participantType);
        }

        // remove participant from current folder
        getParticipantService().removeParticipant(participantLdapId, participantType, EcmFileConstants.OBJECT_FOLDER_TYPE, folder.getId());

        // remove participants from files in folder
        List<EcmFile> files = childObjects.stream().filter(EcmFile.class::isInstance).map(EcmFile.class::cast).collect(Collectors.toList());
        files.forEach(file -> getParticipantService().removeParticipant(participantLdapId, participantType,
                EcmFileConstants.OBJECT_FILE_TYPE, file.getId()));
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

            // skip duplicate participant type for one user
            if (!participants.stream()
                    .anyMatch(existingParticipant -> existingParticipant.getParticipantLdapId().equals(participant.getParticipantLdapId())
                            && existingParticipant.getParticipantType().equals(participant.getParticipantType())))
            {
                participants.add(participant);
            }
        });

        return participants;
    }

    public void inheritParticipantsFromAssignedObject(List<AcmParticipant> assignedObjectParticipants,
            List<AcmParticipant> originalAssignedObjectParticipants, AcmFolder rootFolder) throws AcmAccessControlException
    {
        // sometimes we call this method for attachmentsFolder which could be null
        if (rootFolder == null)
        {
            return;
        }

        boolean inheritAllParticipants = assignedObjectParticipants.stream()
                .allMatch(participant -> participant.isReplaceChildrenParticipant());

        // inherit participants where needed
        for (AcmParticipant participant : assignedObjectParticipants)
        {
            if (participant.isReplaceChildrenParticipant())
            {
                setParticipantToFolderChildren(rootFolder, getDocumentParticipantFromAssignedObjectParticipant(participant));
            }
        }

        // remove deleted parent participants from children
        for (AcmParticipant rootFolderParticipant : rootFolder.getParticipants())
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
                    removeParticipantFromFolderAndChildren(rootFolder, rootFolderParticipant.getParticipantLdapId(),
                            rootFolderParticipant.getParticipantType());
                }
            }
        }
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
        return fileParticipantTypes.stream()
                .filter(fileParticipantType -> Arrays.asList(getEcmFileServiceProperties()
                        .getProperty("ecm.documentsParticipantTypes.mappings." + fileParticipantType).split(","))
                        .contains(assignedObjectParticipantType))
                .findFirst().orElse(null);
    }

    public AcmFolderService getFolderService()
    {
        return folderService;
    }

    public void setFolderService(AcmFolderService folderService)
    {
        this.folderService = folderService;
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
