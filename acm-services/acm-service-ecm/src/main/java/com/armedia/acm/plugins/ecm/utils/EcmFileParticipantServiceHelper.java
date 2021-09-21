package com.armedia.acm.plugins.ecm.utils;

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

import com.armedia.acm.auth.AcmAuthentication;
import com.armedia.acm.auth.ExternalAuthenticationUtils;
import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.plugins.ecm.dao.AcmFolderDao;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.AcmFolderParticipantChangedEvent;
import com.armedia.acm.plugins.ecm.model.ChangedParticipantConstants;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.plugins.ecm.model.EcmFileParticipantChangedEvent;
import com.armedia.acm.services.dataaccess.service.impl.ArkPermissionEvaluator;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.web.api.MDCConstants;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slf4j.MDC;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.FlushModeType;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by bojan.milenkoski on 11.01.2018
 */
public class EcmFileParticipantServiceHelper implements ApplicationEventPublisherAware
{
    private Logger log = LogManager.getLogger(getClass());

    private EcmFileDao fileDao;
    private AcmFolderDao folderDao;
    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;
    private ApplicationEventPublisher applicationEventPublisher;
    private ExternalAuthenticationUtils externalAuthenticationUtils;
    private ArkPermissionEvaluator arkPermissionEvaluator;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void setParticipantsToFolderChildren(AcmFolder folder, List<AcmParticipant> participants, boolean restricted, String auditEntityUserId)
    {
        log.trace("Setting participants for folder children [{}-{}]", folder.getId(), folder.getName());

        getAuditPropertyEntityAdapter().setUserId(auditEntityUserId);
        setParticipantsToFolderChildrenRecursively(folder, participants, restricted, auditEntityUserId);
    }

    private void setParticipantsToFolderChildrenRecursively(AcmFolder folder, List<AcmParticipant> participants, boolean restricted, String auditEntityUserId)
    {

        for (AcmParticipant participant : participants)
        {
            log.trace("Setting participant [{}] (recursively: {}) to folders [{}-{}] children ", participant.getParticipantLdapId(),
                    participant.isReplaceChildrenParticipant(), folder.getId(), folder.getName());
            if (participant.isReplaceChildrenParticipant())
            {
                setParticipantToFolderChildren(folder, participant, restricted, auditEntityUserId);
            }
        }

        boolean inheritAllParticipants = participants.stream()
                .allMatch(participant -> participant.isReplaceChildrenParticipant());

        boolean inheritNoParticipants = participants.stream()
                .allMatch(participant -> !participant.isReplaceChildrenParticipant());

        // flush the session before removing participants
        getFolderDao().getEm().flush();

        // remove deleted participants
        if ((inheritAllParticipants || inheritNoParticipants) && folder.getId() != null)
        {
            List<AcmFolder> subfolders = getFolderDao().findSubFolders(folder.getId(), FlushModeType.COMMIT);
            for (AcmFolder subFolder : subfolders)
            {
                // copy folder participants to a new list because the folder.getParticipants() list will be
                // modified in removeParticipantFromFolderAndChildren() method and will cause
                // ConcurrentModificationException
                List<AcmParticipant> folderParticipants = subFolder.getParticipants().stream().collect(Collectors.toList());

                for (AcmParticipant existingParticipant : folderParticipants)
                {
                    if (!containsParticipantWithLdapId(participants, existingParticipant.getParticipantLdapId()))
                    {
                        removeParticipantFromFolderAndChildren(subFolder,
                                existingParticipant.getParticipantLdapId(), existingParticipant.getParticipantType());
                    }
                }
            }

            // set participant to files in folder
            List<EcmFile> files = getFileDao().findByFolderId(folder.getId(), FlushModeType.COMMIT);
            for (EcmFile file : files)
            {
                boolean fileParticipantsChanged = false;

                // copy folder participants to a new list because the folder.getParticipants() list will be
                // modified in removeParticipantFromFolderAndChildren() method and will cause
                // ConcurrentModificationException
                List<AcmParticipant> fileParticipants = file.getParticipants().stream().collect(Collectors.toList());

                for (AcmParticipant existingParticipant : fileParticipants)
                {
                    if (!containsParticipantWithLdapId(participants, existingParticipant.getParticipantLdapId()))
                    {
                        file.getParticipants().removeIf(
                                participant -> participant.getParticipantLdapId().equals(existingParticipant.getParticipantLdapId())
                                        && participant.getParticipantType().equals(existingParticipant.getParticipantType()));
                        fileParticipantsChanged = true;
                    }
                }

                if (fileParticipantsChanged)
                {
                    // modify the instance to trigger the Solr transformers
                    file.setModified(new Date());
                    getFileDao().save(file);
                }
            }
        }
    }

    /**
     * @param folder
     * @param deletedParticipants
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void removeDeletedParticipantFromFolderChild(AcmFolder folder, List<AcmParticipant> deletedParticipants)
    {
        if (folder.getId() != null)
        {
            for (AcmParticipant deletdParticipant : deletedParticipants)
            {
                removeParticipantFromFolderAndChildren(folder, deletdParticipant.getParticipantLdapId(),
                        deletdParticipant.getParticipantType());
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void setParticipantToFolderChildren(AcmFolder folder, AcmParticipant participant, boolean restricted, String auditEntityUserId)
    {
        if (!folder.isLink()) {
            log.trace("Setting participant [{}] with privilege [{}] for folder children [{}-{}]", participant.getParticipantLdapId(),
                    participant.getParticipantType(), folder.getId(), folder.getName());
            getAuditPropertyEntityAdapter().setUserId(auditEntityUserId);
            setParticipantToFolderChildrenRecursively(folder, participant, restricted, auditEntityUserId);
        }
    }

    private void setParticipantToFolderChildrenRecursively(AcmFolder folder, AcmParticipant participant, boolean restricted, String auditEntityUserId)
    {
        getAuditPropertyEntityAdapter().setUserId(auditEntityUserId);

        // set participant to child folders
        List<AcmFolder> subfolders = getFolderDao().findSubFolders(folder.getId(), FlushModeType.COMMIT);
        log.trace("Setting participant [{}] (recursive: {}) to {} subfolders of [{}-{}]", participant.getParticipantLdapId(),
                participant.isReplaceChildrenParticipant(), subfolders.size(), folder.getId(), folder.getName());
        if (subfolders != null)
        {
            for (AcmFolder subFolder : subfolders)
            {
                log.trace("Setting participant [{}] (recursive: {}) to subfolder [{}-{}] of [{}-{}]", participant.getParticipantLdapId(),
                        participant.isReplaceChildrenParticipant(), subFolder.getId(), subFolder.getName(), folder.getId(),
                        folder.getName());
                subFolder.setRestricted(restricted);

                setParticipantToFolder(subFolder, participant);

                // modify the instance to trigger the Solr transformers
                subFolder.setModified(new Date());

                setParticipantToFolderChildren(subFolder, participant, restricted, auditEntityUserId);
            }
        }

        if (folder.getId() != null)
        {
            // set participant to files in folder
            List<EcmFile> files = getFileDao().findByFolderId(folder.getId(), FlushModeType.COMMIT);
            for (EcmFile file : files)
            {
                file.setRestricted(restricted);

                setParticipantToFile(file, participant);

                // modify the instance to trigger the Solr transformers
                file.setModified(new Date());
                getFileDao().save(file);
            }
        }
    }

    private void removeParticipantFromFolderAndChildren(AcmFolder folder, String participantLdapId, String participantType)
    {
        log.trace("Removing [{}] privilege to folder [{}-{}] and children for participant [{}]", participantType, folder.getId(),
                folder.getName(), participantLdapId);

        // remove participant from current folder
        boolean removed = folder.getParticipants().removeIf(participant -> participant.getParticipantLdapId().equals(participantLdapId)
                && participant.getParticipantType().equals(participantType));

        AcmFolderParticipantChangedEvent acmFolderParticipantChangedEvent = new AcmFolderParticipantChangedEvent(folder);
        if (removed)
        {
            AcmParticipant participant = new AcmParticipant();
            String ldapId = getExternalAuthenticationUtils().getEcmServiceUserIdByParticipantLdapId(participantLdapId);
            if (ldapId != null)
            {
                participant.setParticipantLdapId(ldapId);
                participant.setParticipantType(participantType);
                participant.setObjectType(EcmFileConstants.OBJECT_FOLDER_TYPE);
                participant.setObjectId(folder.getId());

                acmFolderParticipantChangedEvent.setChangedParticipant(participant);
                acmFolderParticipantChangedEvent.setChangeType(ChangedParticipantConstants.REMOVED);
                getApplicationEventPublisher().publishEvent(acmFolderParticipantChangedEvent);
            }
        }

        // modify the instance to trigger the Solr transformers
        folder.setModified(new Date());

        // remove participant from child folders
        List<AcmFolder> subfolders = folder.getChildrenFolders();
        if (subfolders != null)
        {
            subfolders.forEach(subfolder -> removeParticipantFromFolderAndChildren(subfolder, participantLdapId, participantType));
        }

        if (folder.getId() != null)
        {
            // remove participants from files in folder
            getFileDao().findByFolderId(folder.getId(), FlushModeType.COMMIT)
                    .forEach(file -> {
                        boolean removedFileParticipant = file.getParticipants()
                                .removeIf(participant -> participant.getParticipantLdapId().equals(participantLdapId)
                                        && participant.getParticipantType().equals(participantType));

                        if (removedFileParticipant)
                        {
                            EcmFileParticipantChangedEvent ecmFileParticipantChangedEvent = new EcmFileParticipantChangedEvent(file);
                            AcmParticipant participant = new AcmParticipant();

                            String ldapId = getExternalAuthenticationUtils().getEcmServiceUserIdByParticipantLdapId(participantLdapId);
                            if (ldapId != null)
                            {
                                participant.setParticipantLdapId(ldapId);
                                participant.setParticipantType(participantType);
                                participant.setObjectType(EcmFileConstants.OBJECT_FILE_TYPE);
                                participant.setObjectId(file.getFileId());

                                ecmFileParticipantChangedEvent.setChangedParticipant(participant);
                                ecmFileParticipantChangedEvent.setChangeType(ChangedParticipantConstants.REMOVED);
                                getApplicationEventPublisher().publishEvent(ecmFileParticipantChangedEvent);
                            }
                        }
                        // modify the instance to trigger the Solr transformers
                        file.setModified(new Date());
                        getFileDao().save(file);
                    });
        }
    }

    /**
     * First checks if the participant already exists for the file.
     * If not - add it, else - check the participant type and change it if not same.
     *
     * @param file
     * @param participant
     */
    public void setParticipantToFile(EcmFile file, AcmParticipant participant)
    {
        log.trace("Setting [{}] privilege to file [{}-{}] for participant [{}]", participant.getParticipantType(), file.getId(),
                file.getFileName(), participant.getParticipantLdapId());
        EcmFileParticipantChangedEvent ecmFileParticipantChangedEvent = new EcmFileParticipantChangedEvent(file);
        boolean publishChangedEvent = false;
        Optional<AcmParticipant> existingFileParticipant = file.getParticipants().stream()
                .filter(existingParticipant -> existingParticipant.getParticipantLdapId().equals(participant.getParticipantLdapId()))
                .findFirst();

        // for files and folders only one AcmParticipant per user is allowed
        if (existingFileParticipant.isPresent())
        {
            // change the role of the existing participant if needed
            if (!existingFileParticipant.get().getParticipantType().equals(participant.getParticipantType()))
            {
                AcmParticipant changedParticipant = AcmParticipant.createRulesTestParticipant(participant);
                String ldapId = getExternalAuthenticationUtils()
                        .getEcmServiceUserIdByParticipantLdapId(changedParticipant.getParticipantLdapId());
                if (ldapId != null)
                {
                    changedParticipant.setParticipantLdapId(ldapId);
                    ecmFileParticipantChangedEvent.setChangedParticipant(changedParticipant);
                    publishChangedEvent = true;
                }

                AcmParticipant oldParticipant = AcmParticipant.createRulesTestParticipant(existingFileParticipant.get());
                String ldapIdOldParticipant = getExternalAuthenticationUtils()
                        .getEcmServiceUserIdByParticipantLdapId(oldParticipant.getParticipantLdapId());
                if (ldapIdOldParticipant != null)
                {
                    oldParticipant.setParticipantLdapId(ldapIdOldParticipant);
                    ecmFileParticipantChangedEvent.setOldParticipant(oldParticipant);
                    ecmFileParticipantChangedEvent.setChangeType(ChangedParticipantConstants.CHANGED);

                    existingFileParticipant.get().setParticipantType(participant.getParticipantType());
                    publishChangedEvent = true;
                }
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

            AcmParticipant changedParticipant = AcmParticipant.createRulesTestParticipant(newParticipant);
            String ldapId = getExternalAuthenticationUtils()
                    .getEcmServiceUserIdByParticipantLdapId(changedParticipant.getParticipantLdapId());
            if (ldapId != null)
            {
                changedParticipant.setParticipantLdapId(ldapId);
                ecmFileParticipantChangedEvent.setChangedParticipant(changedParticipant);
                ecmFileParticipantChangedEvent.setChangeType(ChangedParticipantConstants.ADDED);
                publishChangedEvent = true;
            }
        }
        if (publishChangedEvent)
        {
            getApplicationEventPublisher().publishEvent(ecmFileParticipantChangedEvent);
        }
    }

    /**
     * First checks if the participant already exists for the folder.
     * If not - add it, else - check the participant type and change it if not same.
     * 
     * @param folder
     * @param participant
     */
    public void setParticipantToFolder(AcmFolder folder, AcmParticipant participant)
    {
        log.trace("Setting [{}] privilege to folder [{}-{}] for participant [{}]", participant.getParticipantType(), folder.getId(),
                folder.getName(), participant.getParticipantLdapId());
        AcmFolderParticipantChangedEvent folderParticipantChangedEvent = new AcmFolderParticipantChangedEvent(folder);
        boolean publishChangedEvent = false;
        // set participant to current folder
        Optional<AcmParticipant> existingFolderParticipant = folder.getParticipants().stream()
                .filter(existingParticipant -> existingParticipant.getParticipantLdapId().equals(participant.getParticipantLdapId()))
                .findFirst();

        // for files and folders only one AcmParticipant per user is allowed
        if (existingFolderParticipant.isPresent())
        {
            // change the role of the existing participant if needed
            if (!existingFolderParticipant.get().getParticipantType().equals(participant.getParticipantType()))
            {
                AcmParticipant changedParticipant = AcmParticipant.createRulesTestParticipant(participant);
                String ldapId = getExternalAuthenticationUtils()
                        .getEcmServiceUserIdByParticipantLdapId(changedParticipant.getParticipantLdapId());
                if (ldapId != null)
                {
                    changedParticipant.setParticipantLdapId(ldapId);
                    folderParticipantChangedEvent.setChangedParticipant(changedParticipant);
                    publishChangedEvent = true;
                }

                AcmParticipant oldParticipant = AcmParticipant.createRulesTestParticipant(existingFolderParticipant.get());
                String ldapIdOldParticipant = getExternalAuthenticationUtils()
                        .getEcmServiceUserIdByParticipantLdapId(oldParticipant.getParticipantLdapId());
                if (ldapIdOldParticipant != null)
                {
                    oldParticipant.setParticipantLdapId(ldapIdOldParticipant);
                    folderParticipantChangedEvent.setOldParticipant(oldParticipant);
                    folderParticipantChangedEvent.setChangeType(ChangedParticipantConstants.CHANGED);

                    existingFolderParticipant.get().setParticipantType(participant.getParticipantType());
                    publishChangedEvent = true;
                }
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

            AcmParticipant changedParticipant = AcmParticipant.createRulesTestParticipant(newParticipant);
            String ldapId = getExternalAuthenticationUtils()
                    .getEcmServiceUserIdByParticipantLdapId(changedParticipant.getParticipantLdapId());
            if (ldapId != null)
            {
                changedParticipant.setParticipantLdapId(ldapId);
                folderParticipantChangedEvent.setChangedParticipant(changedParticipant);
                folderParticipantChangedEvent.setChangeType(ChangedParticipantConstants.ADDED);
                publishChangedEvent = true;
            }
        }
        if (publishChangedEvent)
        {
            getApplicationEventPublisher().publishEvent(folderParticipantChangedEvent);
        }
    }

    /**
     * Sets participant to a folder without sending event to the Content Repository (Alfresco).
     * 
     * First checks if the participant already exists for the folder.
     * If not - add it, else - check the participant type and change it if not same.
     *
     * @param folder
     * @param participantLdapId
     * @param participantType
     */
    public void setParticipantToFolderWithoutSendingEvent(AcmFolder folder, String participantLdapId, String participantType)
    {
        // set participant to current folder
        Optional<AcmParticipant> existingFolderParticipants = folder.getParticipants().stream()
                .filter(existingParticipant -> existingParticipant.getParticipantLdapId().equals(participantLdapId))
                .findFirst();

        // for files and folders only one AcmParticipant per user is allowed
        if (existingFolderParticipants.isPresent())
        {
            // change the role of the existing participant if needed
            if (!existingFolderParticipants.get().getParticipantType().equals(participantType))
            {
                existingFolderParticipants.get().setParticipantType(participantType);
            }
        }
        else
        {
            AcmParticipant newParticipant = new AcmParticipant();
            newParticipant.setParticipantType(participantType);
            newParticipant.setParticipantLdapId(participantLdapId);
            newParticipant.setObjectType(EcmFileConstants.OBJECT_FOLDER_TYPE);
            newParticipant.setObjectId(folder.getId());
            folder.getParticipants().add(newParticipant);
        }

        // modify the instance to trigger the Solr transformers
        folder.setModified(new Date());

        getFolderDao().save(folder);
    }

    /**
     * Sets participant to a file without sending event to the Content Repository (Alfresco).
     * 
     * First checks if the participant already exists for the file.
     * If not - add it, else - check the participant type and change it if not same.
     *
     * @param file
     * @param participantLdapId
     * @param participantType
     */
    public void setParticipantToFileWithoutSendingEvent(EcmFile file, String participantLdapId, String participantType)
    {
        Optional<AcmParticipant> existingFileParticipants = file.getParticipants().stream()
                .filter(existingParticipant -> existingParticipant.getParticipantLdapId().equals(participantLdapId))
                .findFirst();

        // for files and folders only one AcmParticipant per user is allowed
        if (existingFileParticipants.isPresent())
        {
            // change the role of the existing participant if needed
            if (!existingFileParticipants.get().getParticipantType().equals(participantType))
            {
                existingFileParticipants.get().setParticipantType(participantType);
            }
        }
        else
        {
            AcmParticipant newParticipant = new AcmParticipant();
            newParticipant.setParticipantType(participantType);
            newParticipant.setParticipantLdapId(participantLdapId);
            newParticipant.setObjectType(EcmFileConstants.OBJECT_FILE_TYPE);
            newParticipant.setObjectId(file.getId());
            file.getParticipants().add(newParticipant);
        }

        // modify the instance to trigger the Solr transformers
        file.setModified(new Date());

        getFileDao().save(file);
    }

    private boolean containsParticipantWithLdapId(List<AcmParticipant> participants, String ldapId)
    {
        return participants.stream()
                .filter(participant -> participant.getParticipantLdapId().equals(ldapId))
                .count() > 0;
    }

    public EcmFileDao getFileDao()
    {
        return fileDao;
    }

    public void setFileDao(EcmFileDao fileDao)
    {
        this.fileDao = fileDao;
    }

    public AuditPropertyEntityAdapter getAuditPropertyEntityAdapter()
    {
        return auditPropertyEntityAdapter;
    }

    public void setAuditPropertyEntityAdapter(AuditPropertyEntityAdapter auditPropertyEntityAdapter)
    {
        this.auditPropertyEntityAdapter = auditPropertyEntityAdapter;
    }

    public AcmFolderDao getFolderDao()
    {
        return folderDao;
    }

    public void setFolderDao(AcmFolderDao folderDao)
    {
        this.folderDao = folderDao;
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

    public ArkPermissionEvaluator getArkPermissionEvaluator() {
        return arkPermissionEvaluator;
    }

    public void setArkPermissionEvaluator(ArkPermissionEvaluator arkPermissionEvaluator) {
        this.arkPermissionEvaluator = arkPermissionEvaluator;
    }
}
