package com.armedia.acm.plugins.casefile.service;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.exceptions.AcmCaseFileNotFound;
import com.armedia.acm.plugins.casefile.exceptions.MergeCaseFilesException;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.model.MergeCaseOptions;
import com.armedia.acm.plugins.ecm.dao.AcmContainerDao;
import com.armedia.acm.plugins.ecm.dao.AcmFolderDao;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.exception.AcmFolderException;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.service.AcmFolderService;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.plugins.objectassociation.model.ObjectAssociation;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.services.participants.model.ParticipantTypes;
import com.armedia.acm.services.participants.service.AcmParticipantService;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MergeCaseServiceImpl implements MergeCaseService
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    private SaveCaseService saveCaseService;
    private CaseFileDao caseFileDao;
    private AcmFolderDao acmFolderDao;
    private EcmFileService ecmFileService;
    private AcmFolderService acmFolderService;
    private AcmContainerDao acmContainerDao;
    private EcmFileDao ecmFileDao;
    private List<String> excludeDocumentTypesList;
    private AcmParticipantService acmParticipantService;

    @Override
    @Transactional
    public CaseFile mergeCases(Authentication auth, String ipAddress, MergeCaseOptions mergeCaseOptions)
            throws PipelineProcessException, MergeCaseFilesException, AcmUserActionFailedException, AcmCreateObjectFailedException
    {

        CaseFile source = caseFileDao.find(mergeCaseOptions.getSourceCaseFileId());
        if (source == null)
            throw new AcmCaseFileNotFound("Source Case File with id = " + mergeCaseOptions.getSourceCaseFileId() + " not found");
        if (hasBeenMerged(source))
            throw new MergeCaseFilesException("Source is already merged");
        CaseFile target = caseFileDao.find(mergeCaseOptions.getTargetCaseFileId());
        if (target == null)
            throw new AcmCaseFileNotFound("Target Case File with id = " + mergeCaseOptions.getTargetCaseFileId() + " not found");
        log.info("Going to merge {} into {}", source.getCaseNumber(), target.getCaseNumber());
        // merge details
        // String sourceDetails = StringUtils.isEmpty(source.getDetails()) ? "" : String.format(MERGE_TEXT_SEPPARATOR,
        // source.getTitle(), source.getCaseNumber()) + source.getDetails();
        // target.setDetails(!StringUtils.isEmpty(target.getDetails()) ?
        // target.getDetails() + sourceDetails :
        // sourceDetails);

        // merge folders and documents
        mergeFoldersAndDocuments(source, target);

        ObjectAssociation childObjectSource = new ObjectAssociation();
        childObjectSource.setAssociationType("REFERENCE");
        childObjectSource.setCategory("MERGED_TO");
        childObjectSource.setTargetId(target.getId());
        childObjectSource.setTargetType(target.getObjectType());
        childObjectSource.setTargetTitle(target.getTitle());
        childObjectSource.setTargetName(target.getCaseNumber());
        source.addChildObject(childObjectSource);

        ObjectAssociation childObjectTarget = new ObjectAssociation();
        childObjectTarget.setAssociationType("REFERENCE");
        childObjectTarget.setCategory("MERGED_FROM");
        childObjectTarget.setTargetId(source.getId());
        childObjectTarget.setTargetType(source.getObjectType());
        childObjectTarget.setTargetTitle(source.getTitle());
        childObjectTarget.setTargetName(source.getCaseNumber());
        target.addChildObject(childObjectTarget);

        source.setStatus("CLOSED");

        // set current user as assignee
        handleParticipants(auth, target);

        saveCaseService.saveCase(source, auth, ipAddress);
        target = saveCaseService.saveCase(target, auth, ipAddress);

        return target;
    }

    private void handleParticipants(Authentication auth, CaseFile target) throws MergeCaseFilesException
    {
        // 1. if current user is already assignee do nothing
        // 2. change case file assignee into follower
        // 2.1. if current user is follower, change is into assignee
        // 2.2. if current user is not participant, than add it as assignee

        // set assignee as follower if exists
        if (target.getParticipants() != null)
        {
            AcmParticipant foundAssignee = null;
            for (AcmParticipant ap : target.getParticipants())
            {
                if (ParticipantTypes.ASSIGNEE.equals(ap.getParticipantType()))
                {
                    try
                    {
                        foundAssignee = ap;
                        break;
                    } catch (Exception e)
                    {
                        throw new MergeCaseFilesException("Unable to change role on " + ap.toString() + " into follower.", e);
                    }
                }
            }
            if (foundAssignee != null)
            {
                if (foundAssignee.getParticipantLdapId().equals(auth.getName()))
                    return;
                foundAssignee.setParticipantType(ParticipantTypes.FOLLOWER);
                AcmParticipant addedAssignee = acmParticipantService.saveParticipant(auth.getName(), ParticipantTypes.ASSIGNEE,
                        target.getId(), target.getObjectType());
                target.getParticipants().add(addedAssignee);
            } else
            {
                AcmParticipant addedAssignee = acmParticipantService.saveParticipant(auth.getName(), ParticipantTypes.ASSIGNEE,
                        target.getId(), target.getObjectType());
                target.getParticipants().add(addedAssignee);
            }
        } else
        {
            // there are no participants in target case file, just add current user as assignee
            AcmParticipant addedAssignee = acmParticipantService.saveParticipant(auth.getName(), ParticipantTypes.ASSIGNEE, target.getId(),
                    target.getObjectType());
            List<AcmParticipant> participants = new ArrayList<>();
            participants.add(addedAssignee);
            target.setParticipants(participants);
        }
    }

    private boolean hasBeenMerged(CaseFile source)
    {
        // if folder has parent, that means that has been merged
        return source.getContainer().getFolder().getParentFolder() != null;
    }

    private void mergeFoldersAndDocuments(CaseFile source, CaseFile target) throws MergeCaseFilesException
    {
        try
        {
            // remove source ROOT folder from acm_container from db, when source will be saved new ROOT folder will be
            // created
            AcmFolder sourceRootFolder = source.getContainer().getFolder();

            // move source ROOT folder into target ROOT folder
            sourceRootFolder.setName(String.format("%s(%s)", source.getTitle(), source.getCaseNumber()));
            acmFolderService.moveRootFolder(sourceRootFolder, target.getContainer().getFolder());

            // change source case file documents with target's container id
            long documentsUpdated = ecmFileDao.changeContainer(source.getContainer(), target.getContainer(), excludeDocumentTypesList);
            log.info("moved {} documents  from container id={} to container id={}", documentsUpdated, source.getContainer().getId(),
                    target.getContainer().getId());

        } catch (AcmFolderException | AcmUserActionFailedException | AcmObjectNotFoundException e)
        {
            throw new MergeCaseFilesException("Error merging case files. Exception in moving documents and folders.", e);
        }
    }

    public void setSaveCaseService(SaveCaseService saveCaseService)
    {
        this.saveCaseService = saveCaseService;
    }

    public void setCaseFileDao(CaseFileDao caseFileDao)
    {
        this.caseFileDao = caseFileDao;
    }

    public void setAcmFolderDao(AcmFolderDao acmFolderDao)
    {
        this.acmFolderDao = acmFolderDao;
    }

    public void setEcmFileService(EcmFileService ecmFileService)
    {
        this.ecmFileService = ecmFileService;
    }

    public void setAcmFolderService(AcmFolderService acmFolderService)
    {
        this.acmFolderService = acmFolderService;
    }

    public void setAcmContainerDao(AcmContainerDao acmContainerDao)
    {
        this.acmContainerDao = acmContainerDao;
    }

    public void setEcmFileDao(EcmFileDao ecmFileDao)
    {
        this.ecmFileDao = ecmFileDao;
    }

    public void setAcmParticipantService(AcmParticipantService acmParticipantService)
    {
        this.acmParticipantService = acmParticipantService;
    }

    public void setExcludeDocumentTypes(String excludeDocumentTypes)
    {
        this.excludeDocumentTypesList = !StringUtils.isEmpty(excludeDocumentTypes)
                ? Arrays.asList(excludeDocumentTypes.trim().replaceAll(",[\\s]*", ",").split(",")) : new ArrayList<>();
    }
}
