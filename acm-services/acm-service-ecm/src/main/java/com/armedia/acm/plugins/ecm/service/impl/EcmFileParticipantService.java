package com.armedia.acm.plugins.ecm.service.impl;

import com.armedia.acm.core.exceptions.AcmParticipantsException;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.plugins.ecm.service.AcmFolderService;
import com.armedia.acm.services.participants.model.AcmAssignedObject;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.services.participants.service.AcmParticipantService;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.FlushModeType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
    private transient final Logger log = LoggerFactory.getLogger(getClass());

    private EcmFileDao fileDao;
    private AcmFolderService folderService;
    private AcmParticipantService participantService;
    private Properties ecmFileServiceProperties;

    private static List<String> fileParticipantTypes = Arrays.asList("group-write", "group-read", "group-no-access", "write", "read",
            "no-access");

    /**
     * Sets the file's participants from the parent folder's participants and persists the file instance with the
     * assigned participants.
     * 
     * @param file
     *            the file to set parent folder' participants
     * @return update file instance with the participants saved
     */
    @Transactional(rollbackFor = Exception.class)
    public EcmFile setFileParticipantsFromParentFolder(EcmFile file)
    {
        if (file.getFolder() == null)
        {
            throw new IllegalStateException("File doesn't have parent folder!");
        }

        setFileParticipants(file, file.getFolder().getParticipants());

        // modify the instance to trigger the Solr transformers
        file.setModified(new Date());
        return getFileDao().save(file);
    }

    /**
     * Sets the folder participants from the parent folder's participants. Also sets the participants recursively to
     * files in the folder and subfolders. Does not persist the folder instance, including the subfolders instances.
     * Persists the file's participants because the AcmFolder does not keep a list of files in the folder, so the
     * clients would not get these changes.
     * 
     * @param folder
     *            the folder to set parent folder' participants
     */
    @Transactional(rollbackFor = Exception.class)
    public void setFolderParticipantsFromParentFolder(AcmFolder folder)
    {
        if (folder.getParentFolder() == null)
        {
            throw new IllegalStateException("Folder doesn't have parent folder!");
        }

        folder.getParentFolder().getParticipants().forEach(participant -> participant.setReplaceChildrenParticipant(true));
        setFolderParticipants(folder, folder.getParentFolder().getParticipants());

        // modify the instance to trigger the Solr transformers
        folder.setModified(new Date());
    }

    @Transactional(rollbackFor = Exception.class)
    private void setParticipantToFolderChildren(AcmFolder folder, AcmParticipant participant)
    {
        if (StringUtils.isEmpty(participant.getParticipantLdapId()))
        {
            // we don't use participants without ldapId for files/folders as we do for AssignedObject (Assignee)
            return;
        }

        // set participant to child folders
        List<AcmFolder> subfolders = folder.getChildrenFolders();
        if (subfolders != null)
        {
            for (AcmFolder subFolder : subfolders)
            {
                setParticipantToFolderChildren(subFolder, participant);
            }
        }

        setParticipantToFolder(folder, participant);

        if (folder.getId() != null)
        {
            // set participant to files in folder
            List<EcmFile> files = getFileDao().findByFolderId(folder.getId(), FlushModeType.COMMIT);
            for (EcmFile file : files)
            {
                setParticipantToFile(file, participant);

                // modify the instance to trigger the Solr transformers
                file.setModified(new Date());
                getFileDao().save(file);
            }
        }

        // modify the instance to trigger the Solr transformers
        folder.setModified(new Date());
    }

    private void setParticipantToFolder(AcmFolder folder, AcmParticipant participant)
    {
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
                existingFolderParticipants.get().setParticipantType(participant.getParticipantType());
            }
        }
        else
        {
            AcmParticipant newParticipant = new AcmParticipant();
            newParticipant.setParticipantType(participant.getParticipantType());
            newParticipant.setParticipantLdapId(participant.getParticipantLdapId());
            newParticipant.setObjectType(EcmFileConstants.OBJECT_FOLDER_TYPE);
            newParticipant.setObjectId(folder.getId());
            folder.getParticipants().add(newParticipant);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    private void removeParticipantFromFolderAndChildren(AcmFolder folder, String participantLdapId, String participantType)
    {
        // remove participant from child folders
        List<AcmFolder> subfolders = folder.getChildrenFolders();
        if (subfolders != null)
        {
            for (AcmFolder subFolder : subfolders)
            {
                removeParticipantFromFolderAndChildren(subFolder, participantLdapId, participantType);
            }
        }

        // remove participant from current folder
        folder.getParticipants().removeIf(participant -> participant.getParticipantLdapId().equals(participantLdapId)
                && participant.getParticipantType().equals(participantType));

        if (folder.getId() != null)
        {
            // remove participants from files in folder
            getFileDao().findByFolderId(folder.getId(), FlushModeType.COMMIT)
                    .forEach(file -> {
                        file.getParticipants().removeIf(participant -> participant.getParticipantLdapId().equals(participantLdapId)
                                && participant.getParticipantType().equals(participantType));
                        // modify the instance to trigger the Solr transformers
                        file.setModified(new Date());
                        getFileDao().save(file);
                    });
        }

        // modify the instance to trigger the Solr transformers
        folder.setModified(new Date());
    }

    /**
     * Returns a list of folder participants inherited from {@link AcmAssignedObject}'s participants. The folder
     * participants types are mapped to AcmAssignedObject's participant types in ecmFileService.properties file. If one
     * participant LdapId has multiple participant roles for an AcmAssignedObject, then one random role is mapped to the
     * unique file participant.
     * 
     * @param objectType
     *            the {@link AcmAssignedObject#getObjectType(){
     * @param objectId
     *            the {@link AcmAssignedObject#getId()}
     * @return a list of participants mapped as a folder participants
     */
    public List<AcmParticipant> getFolderParticipantsFromParentAssignedObject(String objectType, Long objectId)
    {
        List<AcmParticipant> participants = getParticipantService().getParticipantsFromParentObject(objectType, objectId);
        return getFolderParticipantsFromAssignedObject(participants);
    }

    /**
     * Returns a list of folder participants inherited from {@link AcmAssignedObject}'s participants. The folder
     * participants types are mapped to AcmAssignedObject's participant types in ecmFileService.properties file. If one
     * participant LdapId has multiple participant roles for an AssignedObject, then one random role is mapped to the
     * unique file participant.
     * 
     * @param assignedObjectParticipants
     *            list of participants set on an AssignedObject
     * @return a list of participants mapped as a folder participants
     */
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
            List<AcmParticipant> originalAssignedObjectParticipants, AcmContainer acmContainer)
    {
        if (acmContainer == null)
        {
            log.warn("Null container passed in " + getClass().getName() + ".inheritParticipantsFromAssignedObject()");
            return;
        }

        if (acmContainer.getFolder() != null)
        {
            inheritParticipantsFromAssignedObject(assignedObjectParticipants,
                    originalAssignedObjectParticipants, acmContainer.getFolder());

        }
        if (acmContainer.getAttachmentFolder() != null
                && !acmContainer.getAttachmentFolder().getId().equals(acmContainer.getFolder().getId()))
        {
            inheritParticipantsFromAssignedObject(assignedObjectParticipants,
                    originalAssignedObjectParticipants, acmContainer.getAttachmentFolder());
        }

        // make sure the changes made are flushed and not overridden later
        getFileDao().getEm().flush();
    }

    private void inheritParticipantsFromAssignedObject(List<AcmParticipant> assignedObjectParticipants,
            List<AcmParticipant> originalAssignedObjectParticipants, AcmFolder folder)
    {
        boolean inheritAllParticipants = assignedObjectParticipants.stream()
                .allMatch(participant -> participant.isReplaceChildrenParticipant());

        // inherit participants where needed
        assignedObjectParticipants.stream().filter(participant -> participant.isReplaceChildrenParticipant()).forEach(
                participant -> setParticipantToFolderChildren(folder, getDocumentParticipantFromAssignedObjectParticipant(participant)));

        // remove deleted parent participants from folder and children

        // copy folder participants to a new list because the folder.getParticipants() list will be modified in
        // removeParticipantFromFolderAndChildren() method and will cause ConcurrentModificationException
        List<AcmParticipant> folderParticipants = folder.getParticipants().stream().collect(Collectors.toList());
        for (AcmParticipant folderParticipant : folderParticipants)
        {
            boolean existsParentParticipant = assignedObjectParticipants.stream()
                    .anyMatch(participant -> folderParticipant.getParticipantLdapId().equals(participant.getParticipantLdapId())
                            && folderParticipant.getParticipantType()
                                    .equals(getDocumentParticipantFromAssignedObjectParticipant(participant).getParticipantType()));

            if (!existsParentParticipant)
            {
                boolean existsOriginalParentParticipant = originalAssignedObjectParticipants.stream()
                        .anyMatch(participant -> folderParticipant.getParticipantLdapId().equals(participant.getParticipantLdapId())
                                && folderParticipant.getParticipantType()
                                        .equals(getDocumentParticipantFromAssignedObjectParticipant(participant).getParticipantType()));

                // do not delete added participant to folder if not deleted from assignedObject
                if (inheritAllParticipants || existsOriginalParentParticipant)
                {
                    removeParticipantFromFolderAndChildren(folder, folderParticipant.getParticipantLdapId(),
                            folderParticipant.getParticipantType());
                }
            }
        }

        // modify the instance to trigger the Solr transformers
        folder.setModified(new Date());
    }

    @Transactional(rollbackFor = Exception.class)
    public void setRestrictedFlagRecursively(Boolean restricted, AcmContainer acmContainer)
    {
        if (acmContainer == null)
        {
            log.warn("Null container passed in " + getClass().getName() + ".setRestrictedFlagRecursively()");
            return;
        }

        if (acmContainer.getFolder() != null)
        {
            setRestrictedFlagRecursively(restricted, acmContainer.getFolder());
        }
        if (acmContainer.getAttachmentFolder() != null && !acmContainer.getAttachmentFolder().equals(acmContainer.getFolder()))
        {
            setRestrictedFlagRecursively(restricted, acmContainer.getAttachmentFolder());
        }
    }

    private void setRestrictedFlagRecursively(Boolean restricted, AcmFolder folder)
    {
        // usually this method is called after inheritParticipantsFromAssignedObject
        // we'll make sure that any changes done are flushed to the database
        getFileDao().getEm().flush();

        folder.setRestricted(restricted);

        // set restricted flag to child folders
        List<AcmFolder> subfolders = folder.getChildrenFolders();
        if (subfolders != null)
        {
            subfolders.forEach(subfolder -> setRestrictedFlagRecursively(restricted, subfolder));
        }

        if (folder.getId() != null)
        {
            // set restricted flag to files in the folder
            getFileDao().findByFolderId(folder.getId(), FlushModeType.COMMIT).forEach(file -> {
                file.setRestricted(restricted);
                // modify the instance to trigger the Solr transformers
                file.setModified(new Date());
                getFileDao().save(file);
            });
        }

        // modify the instance to trigger the Solr transformers
        folder.setModified(new Date());
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
            throw new IllegalStateException(
                    "No document participant type mapping found for participant type: " + assignedObjectParticipantType
                            + ". Add mapping for new participant type in 'ecmFileService.properties'!");
        }

        return documentParticipantType;
    }

    /**
     * Sets the given participants to a FILE or FOLDER. When setting participants to FOLDER, all participants that have
     * replaceChildrenParticipant set to true are inherited to subfolder and files recursively. All changed files and
     * folders are persisted in this method.
     * 
     * @param objectId
     *            the {@link AcmAssignedObject#getId()} of the object to set participants on
     * @param objectType
     *            the {@link AcmAssignedObject#getObjectType()} of the object to set participants on
     * @param participants
     *            a list of participants to set on the object
     * @return the participants set on the object including all participants added by Drools rules
     * 
     * @throws AcmParticipantsException
     *             when the participants list is not valid
     */
    @Transactional(rollbackFor = Exception.class)
    public List<AcmParticipant> setFileFolderParticipants(Long objectId, String objectType, List<AcmParticipant> participants)
            throws AcmParticipantsException
    {
        validateFileParticipants(participants);

        List<AcmParticipant> participantsToReturn = new ArrayList<>();
        if (objectType.equals(EcmFileConstants.OBJECT_FOLDER_TYPE))
        {
            AcmFolder folder = getFolderService().findById(objectId);
            setFolderParticipants(folder, participants);
            // modify the instance to trigger the Solr transformers
            folder.setModified(new Date());
            AcmFolder savedFolder = getFolderService().saveFolder(folder);
            // make sure the session is flushed so that the Drools rules have been run
            getFileDao().getEm().flush();
            participantsToReturn = savedFolder.getParticipants();
        }
        else if (objectType.equals(EcmFileConstants.OBJECT_FILE_TYPE))
        {
            EcmFile file = getFileDao().find(objectId);
            setFileParticipants(file, participants);
            // modify the instance to trigger the Solr transformers
            file.setModified(new Date());
            EcmFile savedFile = getFileDao().save(file);
            // make sure the session is flushed so that the Drools rules have been run
            getFileDao().getEm().flush();
            participantsToReturn = savedFile.getParticipants();
        }
        else
        {
            throw new AcmParticipantsException(Arrays.asList(""),
                    "The called method cannot be executed on objectType {" + objectType + "}!");
        }

        return participantsToReturn;
    }

    private void setFileParticipants(EcmFile file, List<AcmParticipant> participants)
    {
        for (AcmParticipant participant : participants)
        {
            Optional<AcmParticipant> returnedParticipant = file.getParticipants().stream()
                    .filter(existingParticipant -> existingParticipant.getParticipantLdapId()
                            .equals(participant.getParticipantLdapId()))
                    .findFirst();

            if (returnedParticipant.isPresent())
            {
                returnedParticipant.get().setParticipantType(participant.getParticipantType());
            }
            else
            {
                setParticipantToFile(file, participant);
            }
        }

        // copy file participants to a new list because the file.getParticipants() list will be modified in the loop and
        // will cause ConcurrentModificationException
        List<AcmParticipant> fileParticipants = file.getParticipants().stream().collect(Collectors.toList());

        // remove deleted participants
        for (AcmParticipant existingParticipant : fileParticipants)
        {
            if (containsParticipantWithLdapId(participants, existingParticipant.getParticipantLdapId()))
            {
                file.getParticipants()
                        .removeIf(participant -> participant.getParticipantLdapId().equals(existingParticipant.getParticipantLdapId())
                                && participant.getParticipantType().equals(existingParticipant.getParticipantType()));
            }
        }
    }

    private boolean containsParticipantWithLdapId(List<AcmParticipant> participants, String ldapId)
    {
        return participants.stream()
                .filter(participant -> participant.getParticipantLdapId().equals(ldapId))
                .count() == 0;
    }

    private void setParticipantToFile(EcmFile file, AcmParticipant participant)
    {
        // set participant to current folder
        Optional<AcmParticipant> existingFolderParticipants = file.getParticipants().stream()
                .filter(existingParticipant -> existingParticipant.getParticipantLdapId().equals(participant.getParticipantLdapId()))
                .findFirst();

        // for files and folders only one AcmParticipant per user is allowed
        if (existingFolderParticipants.isPresent())
        {
            // change the role of the existing participant if needed
            if (!existingFolderParticipants.get().getParticipantType().equals(participant.getParticipantType()))
            {
                existingFolderParticipants.get().setParticipantType(participant.getParticipantType());
            }
        }
        else
        {
            AcmParticipant newParticipant = new AcmParticipant();
            newParticipant.setParticipantType(participant.getParticipantType());
            newParticipant.setParticipantLdapId(participant.getParticipantLdapId());
            newParticipant.setObjectType(EcmFileConstants.OBJECT_FILE_TYPE);
            newParticipant.setObjectId(file.getId());
            file.getParticipants().add(newParticipant);
        }
    }

    private void setFolderParticipants(AcmFolder folder, List<AcmParticipant> participants)
    {
        for (AcmParticipant participant : participants)
        {
            if (participant.isReplaceChildrenParticipant())
            {
                setParticipantToFolderChildren(folder, participant);
            }
            else
            {
                setParticipantToFolder(folder, participant);
            }
        }

        // copy folder participants to a new list because the folder.getParticipants() list will be modified in
        // removeParticipantFromFolderAndChildren() method and will cause ConcurrentModificationException
        List<AcmParticipant> folderParticipants = folder.getParticipants().stream().collect(Collectors.toList());

        // remove deleted participants
        for (AcmParticipant existingParticipant : folderParticipants)
        {
            if (containsParticipantWithLdapId(participants, existingParticipant.getParticipantLdapId()))
            {
                removeParticipantFromFolderAndChildren(folder,
                        existingParticipant.getParticipantLdapId(), existingParticipant.getParticipantType());
            }
        }
    }

    /**
     * Validates the {@link AcmParticipant}s for files or folders.
     *
     * @param participants
     *            the list of {@link AcmParticipant}s to validate
     * @throws AcmParticipantsException
     *             when the {@link AcmParticipant}s are not valid.
     */
    private void validateFileParticipants(List<AcmParticipant> participants) throws AcmParticipantsException
    {
        // missing participant id
        List<String> missingParticipantLdapIdsErrors = participants.stream()
                .filter(participant -> participant.getReceiverLdapId() == null)
                .map(participant -> "No LDAP id for participant type: " + participant.getParticipantType()).collect(Collectors.toList());

        if (missingParticipantLdapIdsErrors.size() > 0)
        {
            String errorMessage = "Missing participant LDAP id!";
            throw new AcmParticipantsException(missingParticipantLdapIdsErrors, errorMessage);
        }

        // missing participantTypes
        List<String> missingParticipantTypesErrors = participants.stream().filter(participant -> participant.getParticipantType() == null)
                .map(participant -> "No participant type for LDAP id: " + participant.getParticipantLdapId()).collect(Collectors.toList());

        if (missingParticipantTypesErrors.size() > 0)
        {
            String errorMessage = "Missing participant type!";
            throw new AcmParticipantsException(missingParticipantTypesErrors, errorMessage);
        }

        // search for duplicate participants LDAPIds. One participant cannot have different roles for an object
        Set<String> allLdapIds = new HashSet<>();
        List<String> duplicateParticipantLdapIdsErrors = participants.stream().map(participant -> participant.getParticipantLdapId())
                .filter(participantLdapId -> !allLdapIds.add(participantLdapId))
                .map(participantLdapId -> "Participant LDAP Id in multiple roles: " + participantLdapId).collect(Collectors.toList());
        if (duplicateParticipantLdapIdsErrors.size() > 0)
        {
            String errorMessage = "Participants in multiple roles found!";
            throw new AcmParticipantsException(duplicateParticipantLdapIdsErrors, errorMessage);
        }

        // check participant types
        List<String> invalidParticipantTypesErrors = participants.stream()
                .filter(participant -> !fileParticipantTypes.contains(participant.getParticipantType()))
                .map(participant -> "Invalid participant type: '" + participant.getParticipantLdapId() + "' for LDAP id: "
                        + participant.getParticipantLdapId())
                .collect(Collectors.toList());

        if (invalidParticipantTypesErrors.size() > 0)
        {
            String errorMessage = "Invalid file participant type!";
            throw new AcmParticipantsException(invalidParticipantTypesErrors, errorMessage);
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

    public Properties getEcmFileServiceProperties()
    {
        return ecmFileServiceProperties;
    }

    public void setEcmFileServiceProperties(Properties ecmFileServiceProperties)
    {
        this.ecmFileServiceProperties = ecmFileServiceProperties;
    }

    public AcmFolderService getFolderService()
    {
        return folderService;
    }

    public void setFolderService(AcmFolderService folderService)
    {
        this.folderService = folderService;
    }
}
