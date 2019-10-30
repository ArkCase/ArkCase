package com.armedia.acm.plugins.ecm.service.impl;

import com.antkorwin.xsync.XSync;

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

import com.armedia.acm.auth.ExternalAuthenticationUtils;
import com.armedia.acm.core.exceptions.AcmParticipantsException;
import com.armedia.acm.plugins.ecm.dao.AcmFolderDao;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.AcmFolderParticipantChangedEvent;
import com.armedia.acm.plugins.ecm.model.ChangedParticipantConstants;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileConfig;
import com.armedia.acm.plugins.ecm.service.AcmFolderService;
import com.armedia.acm.plugins.ecm.utils.EcmFileParticipantServiceHelper;
import com.armedia.acm.services.participants.model.AcmAssignedObject;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.services.participants.service.AcmParticipantService;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by bojan.milenkoski on 06.10.2017
 */
public class EcmFileParticipantService implements ApplicationEventPublisherAware
{
    private static List<String> fileParticipantTypes = Arrays.asList("*", "group-write", "group-read", "group-no-access", "write", "read",
            "no-access");
    private transient final Logger log = LogManager.getLogger(getClass());
    private EcmFileDao fileDao;
    private AcmFolderDao folderDao;
    private AcmFolderService folderService;
    private AcmParticipantService participantService;
    private EcmFileParticipantServiceHelper fileParticipantServiceHelper;
    private EcmFileConfig ecmFileConfig;
    private ApplicationEventPublisher applicationEventPublisher;
    private ExternalAuthenticationUtils externalAuthenticationUtils;
    private XSync<String> xSync;

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
     * files in the folder and subfolders. Does not persist the folder instance. Persists the subfolders and files
     * participants.
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
        setFolderParticipants(folder, folder.getParentFolder().getParticipants(), folder.getRestricted());

        // modify the instance to trigger the Solr transformers
        folder.setModified(new Date());
    }

    private void setParticipantToFolderAndChildren(AcmFolder folder, AcmParticipant participant, boolean restricted)
    {
        if (StringUtils.isEmpty(participant.getParticipantLdapId()))
        {
            // we don't use participants without ldapId for files/folders as we do for AssignedObject (Assignee)
            return;
        }

        folder.setRestricted(restricted);

        getFileParticipantServiceHelper().setParticipantToFolder(folder, participant);

        // modify the instance to trigger the Solr transformers
        folder.setModified(new Date());

        getFileParticipantServiceHelper().setParticipantToFolderChildren(folder, participant, restricted);
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
            if (participants.stream()
                    .noneMatch(
                            existingParticipant -> existingParticipant.getParticipantLdapId().equals(participant.getParticipantLdapId())))
            {
                participants.add(participant);
            }
        });

        return participants;
    }

    /**
     * Inherits participants and restricted flag from assigned object to files and folders recursively. Maps the
     * assigned object participant types to file participant types using the mapping in 'ecmFileService.properties'.
     * 
     * @param assignedObjectParticipants
     *            the assigned object participants to set on the files and folders
     * @param originalAssignedObjectParticipants
     *            the original assigned object participants to calculate the changes between the two. Used for deleted
     *            participants.
     * @param acmContainer
     *            the container linked to the assigned object
     * @param restricted
     *            the restricted flag to set recursively
     */
    @Transactional(rollbackFor = Exception.class)
    @Async("fileParticipantsThreadPoolTaskExecutor")
    public void inheritParticipantsFromAssignedObject(List<AcmParticipant> assignedObjectParticipants,
            List<AcmParticipant> originalAssignedObjectParticipants, AcmContainer acmContainer, boolean restricted)
    {
        if (acmContainer == null)
        {
            log.warn("Null container passed in " + getClass().getName() + ".inheritParticipantsFromAssignedObject()");
            return;
        }

        xSync.execute("CONTAINER" + acmContainer.getId(), () -> {
            log.debug("Setting participants for container [{}]", acmContainer.getId());

            if (acmContainer.getFolder() != null)
            {
                inheritParticipantsFromAssignedObject(assignedObjectParticipants,
                        originalAssignedObjectParticipants, acmContainer.getFolder(), restricted);
            }
            if (acmContainer.getAttachmentFolder() != null
                    && (acmContainer.getFolder() == null
                            || (acmContainer.getAttachmentFolder() != acmContainer.getFolder()
                                    && acmContainer.getAttachmentFolder().getId() != null &&
                                    !acmContainer.getAttachmentFolder().getId().equals(acmContainer.getFolder().getId()))))
            {
                inheritParticipantsFromAssignedObject(assignedObjectParticipants,
                        originalAssignedObjectParticipants, acmContainer.getAttachmentFolder(), restricted);
            }
        });
    }

    private void inheritParticipantsFromAssignedObject(List<AcmParticipant> assignedObjectParticipants,
            List<AcmParticipant> originalAssignedObjectParticipants, AcmFolder folder, boolean restricted)
    {
        // filter participants without participantLdapId
        assignedObjectParticipants = assignedObjectParticipants.stream().filter(
                participant -> participant.getParticipantLdapId() != null && participant.getParticipantLdapId().trim().length() > 0)
                .collect(Collectors.toList());

        boolean inheritAllParticipants = assignedObjectParticipants.stream()
                .allMatch(AcmParticipant::isReplaceChildrenParticipant);

        boolean inheritNoParticipants = assignedObjectParticipants.stream()
                .noneMatch(AcmParticipant::isReplaceChildrenParticipant);

        if (inheritAllParticipants || inheritNoParticipants)
        {
            List<AcmParticipant> fileParticipants = assignedObjectParticipants.stream()
                    .map(this::getDocumentParticipantFromAssignedObjectParticipant)
                    .collect(Collectors.toList());
            setFolderParticipants(folder, fileParticipants, restricted);
        }
        else
        {

            // inherit participants where needed
            assignedObjectParticipants.stream().filter(AcmParticipant::isReplaceChildrenParticipant).forEach(
                    participant -> setParticipantToFolderAndChildren(folder,
                            getDocumentParticipantFromAssignedObjectParticipant(participant), restricted));

            List<AcmParticipant> fileParticipants = assignedObjectParticipants.stream()
                    .map(this::getDocumentParticipantFromAssignedObjectParticipant)
                    .collect(Collectors.toList());
            // check Participants which should be deleted. Compare assignParticipants with the participants of folder
            List<AcmParticipant> existDeletedParticipants = checkDifferentParticipant(fileParticipants, folder.getParticipants());

            if (existDeletedParticipants.size() > 0)
            {
                setFolderParticipants(folder, fileParticipants, restricted);
                getFileParticipantServiceHelper().removeDeletedParticipantFromFolderChild(folder, existDeletedParticipants);
            }

        }
    }

    /**
     * Check wether there is any participant should be deleted
     * 
     * @param assignedObjectParticipants
     * @return
     */
    private List<AcmParticipant> checkDifferentParticipant(List<AcmParticipant> assignedObjectParticipants,
            List<AcmParticipant> orignialObjectParticipants)
    {
        List<AcmParticipant> deletedParticipantList = new ArrayList<>();

        for (AcmParticipant originalParticipant : orignialObjectParticipants)
        {
            boolean found = false;
            for (AcmParticipant assignedObjectParticipant : assignedObjectParticipants)
            {

                if (assignedObjectParticipant.getParticipantLdapId().equals(originalParticipant.getParticipantLdapId()) &&
                        assignedObjectParticipant.getParticipantType().equals(originalParticipant.getParticipantType()))
                {
                    found = true;
                    break;
                }
            }
            if (!found)
            {
                // if the orginal participant is not in the assignedParticipant list
                // add it to the delete list
                deletedParticipantList.add(originalParticipant);
            }
        }

        return deletedParticipantList;
    }

    private AcmParticipant getDocumentParticipantFromAssignedObjectParticipant(AcmParticipant participant)
    {
        AcmParticipant documentParticipant = new AcmParticipant();
        documentParticipant.setParticipantLdapId(participant.getParticipantLdapId());
        documentParticipant
                .setParticipantType(getDocumentParticipantTypeFromAssignedObjectParticipantType(participant.getParticipantType()));
        documentParticipant.setReplaceChildrenParticipant(participant.isReplaceChildrenParticipant());
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

        return fileParticipantTypes.stream()
                .filter(fileParticipantType -> ecmFileConfig.getFileParticipant(fileParticipantType) != null)
                .filter(fileParticipantType -> Arrays.asList(ecmFileConfig.getFileParticipant(fileParticipantType).split(","))
                        .contains(assignedObjectParticipantType))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "No document participant type mapping found for participant type: " + assignedObjectParticipantType
                                + ". Add mapping for new participant type in 'ecmFileService.properties'!"));
    }

    /**
     * Sets the given participants to a FILE. The changed file is persisted in this method.
     * 
     * @param objectId
     *            the {@link AcmAssignedObject#getId()} of the object to set participants on
     * @param participants
     *            a list of participants to set on the object
     * @return the participants set on the object including all participants added by Drools rules
     * 
     * @throws AcmParticipantsException
     *             when the participants list is not valid
     */
    @Transactional(rollbackFor = Exception.class)
    public List<AcmParticipant> setFileParticipants(Long objectId, List<AcmParticipant> participants)
            throws AcmParticipantsException
    {
        validateFileParticipants(participants);

        EcmFile file = getFileDao().find(objectId);
        setFileParticipants(file, participants);
        // modify the instance to trigger the Solr transformers
        file.setModified(new Date());
        EcmFile savedFile = getFileDao().save(file);
        // make sure the session is flushed so that the Drools rules have been run
        getFileDao().getEm().flush();

        return savedFile.getParticipants();
    }

    /**
     * Sets the given participants to a FOLDER. When setting participants to the FOLDER, all participants that have
     * replaceChildrenParticipant set to true are inherited to subfolder and files recursively. All changed files and
     * folders are persisted in this method. The changed folder and any subfolder and file changed are persisted in this
     * method.
     * 
     * @param objectId
     *            the {@link AcmAssignedObject#getId()} of the object to set participants on
     * @param participants
     *            a list of participants to set on the object
     * @return the participants set on the object including all participants added by Drools rules
     * 
     * @throws AcmParticipantsException
     *             when the participants list is not valid
     */
    @Transactional(rollbackFor = Exception.class)
    public List<AcmParticipant> setFolderParticipants(Long objectId, List<AcmParticipant> participants)
            throws AcmParticipantsException
    {
        validateFileParticipants(participants);

        AcmFolder folder = getFolderService().findById(objectId);
        getFolderDao().getEm().detach(folder);
        setFolderParticipants(folder, participants, folder.getRestricted());
        // modify the instance to trigger the Solr transformers
        folder.setModified(new Date());
        AcmFolder savedFolder = getFolderService().saveFolder(folder);

        return savedFolder.getParticipants();
    }

    private void setFileParticipants(EcmFile file, List<AcmParticipant> participants)
    {
        for (AcmParticipant participant : participants)
        {
            getFileParticipantServiceHelper().setParticipantToFile(file, participant);
        }

        // copy file participants to a new list because the file.getParticipants() list will be modified in the loop and
        // will cause ConcurrentModificationException
        List<AcmParticipant> fileParticipants = new ArrayList<>(file.getParticipants());

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
        return participants.stream().noneMatch(participant -> participant.getParticipantLdapId().equals(ldapId));
    }

    private void setFolderParticipants(AcmFolder folder, List<AcmParticipant> participants, boolean restricted)
    {
        for (AcmParticipant participant : participants)
        {
            getFileParticipantServiceHelper().setParticipantToFolder(folder, participant);
        }

        // copy folder participants to a new list because the folder.getParticipants() list will be modified in
        // removeParticipantFromFolderAndChildren() method and will cause ConcurrentModificationException
        List<AcmParticipant> folderParticipants = new ArrayList<>(folder.getParticipants());

        // remove deleted participants
        for (AcmParticipant existingParticipant : folderParticipants)
        {
            if (containsParticipantWithLdapId(participants, existingParticipant.getParticipantLdapId()))
            {
                // remove participant from current folder
                boolean removed = folder.getParticipants()
                        .removeIf(participant -> participant.getParticipantLdapId().equals(existingParticipant.getParticipantLdapId())
                                && participant.getParticipantType().equals(existingParticipant.getParticipantType()));
                if (removed)
                {
                    AcmParticipant changedParticipant = AcmParticipant.createRulesTestParticipant(existingParticipant);
                    AcmFolderParticipantChangedEvent folderParticipantChangedEvent = new AcmFolderParticipantChangedEvent(folder);
                    String ldapId = getExternalAuthenticationUtils()
                            .getEcmServiceUserIdByParticipantLdapId(changedParticipant.getParticipantLdapId());
                    if (ldapId != null)
                    {
                        changedParticipant.setParticipantLdapId(ldapId);
                        folderParticipantChangedEvent.setChangedParticipant(changedParticipant);
                        folderParticipantChangedEvent.setChangeType(ChangedParticipantConstants.REMOVED);
                        getApplicationEventPublisher().publishEvent(folderParticipantChangedEvent);
                    }
                }
            }
        }

        folder.setRestricted(restricted);

        // modify the instance to trigger the Solr transformers
        folder.setModified(new Date());

        getFileParticipantServiceHelper().setParticipantsToFolderChildren(folder, participants, restricted);
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
        List<String> duplicateParticipantLdapIdsErrors = participants.stream().map(AcmParticipant::getParticipantLdapId)
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

    public AcmFolderService getFolderService()
    {
        return folderService;
    }

    public void setFolderService(AcmFolderService folderService)
    {
        this.folderService = folderService;
    }

    public EcmFileParticipantServiceHelper getFileParticipantServiceHelper()
    {
        return fileParticipantServiceHelper;
    }

    public void setFileParticipantServiceHelper(EcmFileParticipantServiceHelper fileParticipantServiceHelper)
    {
        this.fileParticipantServiceHelper = fileParticipantServiceHelper;
    }

    public AcmFolderDao getFolderDao()
    {
        return folderDao;
    }

    public void setFolderDao(AcmFolderDao folderDao)
    {
        this.folderDao = folderDao;
    }

    public EcmFileConfig getEcmFileConfig()
    {
        return ecmFileConfig;
    }

    public void setEcmFileConfig(EcmFileConfig ecmFileConfig)
    {
        this.ecmFileConfig = ecmFileConfig;
    }

    public ApplicationEventPublisher getApplicationEventPublisher()
    {
        return applicationEventPublisher;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public ExternalAuthenticationUtils getExternalAuthenticationUtils()
    {
        return externalAuthenticationUtils;
    }

    public void setExternalAuthenticationUtils(ExternalAuthenticationUtils externalAuthenticationUtils)
    {
        this.externalAuthenticationUtils = externalAuthenticationUtils;
    }

    public XSync<String> getxSync()
    {
        return xSync;
    }

    public void setxSync(XSync<String> xSync)
    {
        this.xSync = xSync;
    }
}
