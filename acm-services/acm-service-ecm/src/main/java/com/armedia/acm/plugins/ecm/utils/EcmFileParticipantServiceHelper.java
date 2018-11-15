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

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.plugins.ecm.dao.AcmFolderDao;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.web.api.MDCConstants;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Async;
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
public class EcmFileParticipantServiceHelper
{
    private EcmFileDao fileDao;
    private AcmFolderDao folderDao;
    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Async("fileParticipantsThreadPoolTaskExecutor")
    public void setParticipantsToFolderChildren(AcmFolder folder, List<AcmParticipant> participants, boolean restricted)
    {
        setAuditPropertyEntityAdapterUserId();

        for (AcmParticipant participant : participants)
        {
            if (participant.isReplaceChildrenParticipant())
            {
                setParticipantToFolderChildren(folder, participant, restricted);
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
    @Async("fileParticipantsThreadPoolTaskExecutor")
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
    @Async("fileParticipantsThreadPoolTaskExecutor")
    public void setParticipantToFolderChildren(AcmFolder folder, AcmParticipant participant, boolean restricted)
    {
        setAuditPropertyEntityAdapterUserId();

        // set participant to child folders
        List<AcmFolder> subfolders = getFolderDao().findSubFolders(folder.getId(), FlushModeType.COMMIT);
        if (subfolders != null)
        {
            for (AcmFolder subFolder : subfolders)
            {
                subFolder.setRestricted(restricted);

                setParticipantToFolder(subFolder, participant);

                // modify the instance to trigger the Solr transformers
                subFolder.setModified(new Date());

                setParticipantToFolderChildren(subFolder, participant, restricted);
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
        setAuditPropertyEntityAdapterUserId();

        // remove participant from current folder
        folder.getParticipants().removeIf(participant -> participant.getParticipantLdapId().equals(participantLdapId)
                && participant.getParticipantType().equals(participantType));

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
                        file.getParticipants().removeIf(participant -> participant.getParticipantLdapId().equals(participantLdapId)
                                && participant.getParticipantType().equals(participantType));
                        // modify the instance to trigger the Solr transformers
                        file.setModified(new Date());
                        getFileDao().save(file);
                    });
        }
    }

    // when we execute Async methods the userId might be null, because it is a {@link ThreadLocal} variable.
    private void setAuditPropertyEntityAdapterUserId()
    {
        if (getAuditPropertyEntityAdapter().getUserId() == null)
        {
            getAuditPropertyEntityAdapter().setUserId(MDC.get(MDCConstants.EVENT_MDC_REQUEST_USER_ID_KEY));
        }
    }

    public void setParticipantToFile(EcmFile file, AcmParticipant participant)
    {
        Optional<AcmParticipant> existingFileParticipants = file.getParticipants().stream()
                .filter(existingParticipant -> existingParticipant.getParticipantLdapId().equals(participant.getParticipantLdapId()))
                .findFirst();

        // for files and folders only one AcmParticipant per user is allowed
        if (existingFileParticipants.isPresent())
        {
            // change the role of the existing participant if needed
            if (!existingFileParticipants.get().getParticipantType().equals(participant.getParticipantType()))
            {
                existingFileParticipants.get().setParticipantType(participant.getParticipantType());
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

    public void setParticipantToFolder(AcmFolder folder, AcmParticipant participant)
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
}
