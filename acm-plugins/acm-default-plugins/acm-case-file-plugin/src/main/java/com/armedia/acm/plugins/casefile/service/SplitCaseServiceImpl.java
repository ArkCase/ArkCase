package com.armedia.acm.plugins.casefile.service;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.exceptions.SplitCaseFileException;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.model.SplitCaseOptions;
import com.armedia.acm.plugins.ecm.exception.AcmFolderException;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.service.AcmFolderService;
import com.armedia.acm.plugins.objectassociation.model.ObjectAssociation;
import com.armedia.acm.plugins.person.model.PersonAssociation;
import com.armedia.acm.plugins.task.exception.AcmTaskException;
import com.armedia.acm.plugins.task.service.AcmTaskService;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.services.participants.model.ParticipantTypes;
import org.apache.commons.lang.StringUtils;
import org.mule.api.MuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by nebojsha on 01.06.2015.
 */
public class SplitCaseServiceImpl implements SplitCaseService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private SaveCaseService saveCaseService;
    private CaseFileDao caseFileDao;
    private AcmFolderService acmFolderService;
    private Set<String> typesToCopy = new HashSet<>();
    private AcmTaskService acmTaskService;

    @Override
    @Transactional
    public CaseFile splitCase(Authentication auth,
                              String ipAddress,
                              SplitCaseOptions splitCaseOptions) throws MuleException, SplitCaseFileException, AcmUserActionFailedException, AcmCreateObjectFailedException, AcmFolderException, AcmObjectNotFoundException {
        CaseFile original = caseFileDao.find(splitCaseOptions.getCaseFileId());
        if (original == null)
            throw new SplitCaseFileException("Case file with id = (" + splitCaseOptions.getCaseFileId() + ") not found");


        CaseFile copyCaseFile = new CaseFile();
        copyCaseFile.setCaseType(original.getCaseType());
        copyCaseFile.setCourtroomName(original.getCourtroomName());
        copyCaseFile.setNextCourtDate(original.getNextCourtDate());
        copyCaseFile.setResponsibleOrganization(original.getResponsibleOrganization());

        copyCaseFile.setDetails(original.getDetails());
        copyCaseFile.setStatus(original.getStatus());

        //add assignee to new case
        AcmParticipant participant = new AcmParticipant();
        participant.setParticipantLdapId(auth.getName());
        participant.setParticipantType(ParticipantTypes.ASSIGNEE);

        if (copyCaseFile.getParticipants() != null)
            copyCaseFile.setParticipants(new ArrayList<>());
        copyCaseFile.getParticipants().add(participant);

        if (typesToCopy.contains("participants"))
            copyParticipants(original, copyCaseFile, auth);


        ObjectAssociation childObjectCopy = new ObjectAssociation();
        childObjectCopy.setAssociationType("REFERENCE");
        childObjectCopy.setCategory("COPY_FROM");
        childObjectCopy.setTargetId(original.getId());
        childObjectCopy.setTargetType(original.getObjectType());
        childObjectCopy.setTargetName(original.getCaseNumber());
        copyCaseFile.addChildObject(childObjectCopy);


        if (typesToCopy.contains("people"))
            copyPeople(original, copyCaseFile);

        copyCaseFile = saveCaseService.saveCase(copyCaseFile, auth, ipAddress);

        ObjectAssociation childObjectOriginal = new ObjectAssociation();
        childObjectOriginal.setAssociationType("REFERENCE");
        childObjectOriginal.setCategory("COPY_TO");
        childObjectOriginal.setTargetId(copyCaseFile.getId());
        childObjectOriginal.setTargetType(copyCaseFile.getObjectType());
        childObjectOriginal.setTargetName(copyCaseFile.getCaseNumber());
        original.addChildObject(childObjectOriginal);
        saveCaseService.saveCase(original, auth, ipAddress);

        if (typesToCopy.contains("tasks")) {
            try {
                copyTasks(original, copyCaseFile, auth, ipAddress);
            } catch (AcmTaskException e) {
                log.error("Couldn't copy tasks.", e);
            }
        }
        copyDocumentsAndFolders(copyCaseFile, splitCaseOptions);
        return copyCaseFile;
    }

    private void copyParticipants(CaseFile original, CaseFile copyCaseFile, Authentication auth) {
        //all participants are copied and assigned as followers, except current user is exist is not copied
        if (original.getParticipants() == null || original.getParticipants().isEmpty())
            return;
        if (copyCaseFile.getParticipants() == null)
            copyCaseFile.setParticipants(new ArrayList<>());
        for (AcmParticipant participant : original.getParticipants()) {
            if (participant.getParticipantLdapId().equals(auth.getName()))
                continue;
            if (ParticipantTypes.ASSIGNEE.equals(participant.getParticipantType())
                    || ParticipantTypes.FOLLOWER.equals(participant.getParticipantType())) {
                AcmParticipant copyParticipant = new AcmParticipant();
                copyParticipant.setParticipantType(ParticipantTypes.FOLLOWER);
                copyParticipant.setParticipantLdapId(participant.getParticipantLdapId());
                copyCaseFile.getParticipants().add(copyParticipant);
            }
        }
    }

    private void copyPeople(CaseFile original, CaseFile copyCaseFile) {
        if (original.getPersonAssociations() == null || original.getPersonAssociations().isEmpty())
            return;
        if (copyCaseFile.getPersonAssociations() == null)
            copyCaseFile.setPersonAssociations(new ArrayList<>());
        for (PersonAssociation person : original.getPersonAssociations()) {
            PersonAssociation copyPerson = new PersonAssociation();
            copyPerson.setPersonType(person.getPersonType());
            copyPerson.setPerson(person.getPerson());
            copyPerson.setNotes(person.getNotes());
            copyPerson.setPersonDescription(person.getPersonDescription());
            copyPerson.setTags(person.getTags());
            copyCaseFile.getPersonAssociations().add(copyPerson);
        }
    }

    private void copyDocumentsAndFolders(CaseFile saved, SplitCaseOptions options) throws AcmObjectNotFoundException, AcmUserActionFailedException, AcmFolderException, AcmCreateObjectFailedException {
        for (SplitCaseOptions.AttachmentDTO attachmentDTO : options.getAttachments()) {
            AcmContainer containerOfCopy = saved.getContainer();
            AcmFolder containerFolderOfCopy = containerOfCopy.getFolder();
            switch (attachmentDTO.getType()) {
                case "folder":
                    acmFolderService.copyFolderStructure(attachmentDTO.getId(), containerOfCopy, containerFolderOfCopy);
                    break;
                case "document":
                    acmFolderService.copyDocumentStructure(attachmentDTO.getId(), containerOfCopy, containerFolderOfCopy);
                    break;
                default:
                    log.warn("Invalid type({}) for for splitting attachments", attachmentDTO.getType());
                    break;
            }
        }
    }


    private void copyTasks(CaseFile original, CaseFile copyCaseFile, Authentication auth, String ipAddress) throws AcmTaskException, AcmCreateObjectFailedException, AcmUserActionFailedException, AcmObjectNotFoundException, AcmFolderException {
        acmTaskService.copyTasks(original.getId(),
                original.getObjectType(),
                copyCaseFile.getId(),
                copyCaseFile.getObjectType(),
                copyCaseFile.getTitle(),
                auth,
                ipAddress);
    }

    public void setSaveCaseService(SaveCaseService saveCaseService) {
        this.saveCaseService = saveCaseService;
    }

    public void setCaseFileDao(CaseFileDao caseFileDao) {
        this.caseFileDao = caseFileDao;
    }

    public void setAcmFolderService(AcmFolderService acmFolderService) {
        this.acmFolderService = acmFolderService;
    }

    public void setTypesToCopy(String typesToCopyStr) {
        if (!StringUtils.isEmpty(typesToCopyStr))
            this.typesToCopy.addAll(Arrays.asList(typesToCopyStr.trim().replaceAll(",[\\s]*", ",").split(",")));
    }

    public void setAcmTaskService(AcmTaskService acmTaskService) {
        this.acmTaskService = acmTaskService;
    }
}
