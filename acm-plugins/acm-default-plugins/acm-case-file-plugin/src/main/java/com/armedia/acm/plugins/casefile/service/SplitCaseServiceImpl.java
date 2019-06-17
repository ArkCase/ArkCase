package com.armedia.acm.plugins.casefile.service;

/*-
 * #%L
 * ACM Default Plugin: Case File
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
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by nebojsha on 01.06.2015.
 */
public class SplitCaseServiceImpl implements SplitCaseService
{

    private final Logger log = LogManager.getLogger(getClass());

    private SaveCaseService saveCaseService;
    private CaseFileDao caseFileDao;
    private AcmFolderService acmFolderService;
    private Set<String> typesToCopy = new HashSet<>();
    private AcmTaskService acmTaskService;
    private SplitCaseFileBusinessRule splitCaseFileBusinessRule;

    @Override
    @Transactional
    public CaseFile splitCase(Authentication auth,
            String ipAddress,
            SplitCaseOptions splitCaseOptions) throws PipelineProcessException, SplitCaseFileException, AcmUserActionFailedException,
            AcmCreateObjectFailedException, AcmFolderException, AcmObjectNotFoundException
    {
        CaseFile original = caseFileDao.find(splitCaseOptions.getCaseFileId());
        if (original == null)
            throw new SplitCaseFileException("Case file with id = (" + splitCaseOptions.getCaseFileId() + ") not found");

        CaseFile copyCaseFile = new CaseFile();

        Map<String, CaseFile> caseFiles = new HashMap<>();
        caseFiles.put("source", original);
        caseFiles.put("copy", copyCaseFile);
        getSplitCaseFileBusinessRule().applyRules(caseFiles);

        // add assignee to new case
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
        childObjectCopy.setTargetTitle(original.getTitle());
        childObjectCopy.setTargetName(original.getCaseNumber());
        copyCaseFile.addChildObject(childObjectCopy);

        if (typesToCopy.contains("people"))
            copyPeople(original, copyCaseFile);

        copyCaseFile.getParticipants().forEach(copyCaseFileParticipant -> copyCaseFileParticipant.setReplaceChildrenParticipant(true));
        copyCaseFile = saveCaseService.saveCase(copyCaseFile, auth, ipAddress);

        ObjectAssociation childObjectOriginal = new ObjectAssociation();
        childObjectOriginal.setAssociationType("REFERENCE");
        childObjectOriginal.setCategory("COPY_TO");
        childObjectOriginal.setTargetId(copyCaseFile.getId());
        childObjectOriginal.setTargetType(copyCaseFile.getObjectType());
        childObjectOriginal.setTargetTitle(copyCaseFile.getTitle());
        childObjectOriginal.setTargetName(copyCaseFile.getCaseNumber());
        original.addChildObject(childObjectOriginal);
        saveCaseService.saveCase(original, auth, ipAddress);

        if (typesToCopy.contains("tasks"))
        {
            try
            {
                copyTasks(original, copyCaseFile, auth, ipAddress);
            }
            catch (AcmTaskException e)
            {
                log.error("Couldn't copy tasks.", e);
            }
        }
        copyDocumentsAndFolders(copyCaseFile, splitCaseOptions);
        return copyCaseFile;
    }

    private void copyParticipants(CaseFile original, CaseFile copyCaseFile, Authentication auth)
    {
        // all participants are copied and assigned as followers, except current user is exist is not copied
        if (original.getParticipants() == null || original.getParticipants().isEmpty())
            return;
        if (copyCaseFile.getParticipants() == null)
            copyCaseFile.setParticipants(new ArrayList<>());
        for (AcmParticipant participant : original.getParticipants())
        {
            if (participant.getParticipantLdapId().equals(auth.getName()))
                continue;
            if ((ParticipantTypes.ASSIGNEE.equals(participant.getParticipantType())
                    || ParticipantTypes.FOLLOWER.equals(participant.getParticipantType()))
                    && !StringUtils.isEmpty(participant.getParticipantLdapId()))
            {
                AcmParticipant copyParticipant = new AcmParticipant();
                copyParticipant.setParticipantType(ParticipantTypes.FOLLOWER);
                copyParticipant.setParticipantLdapId(participant.getParticipantLdapId());
                copyCaseFile.getParticipants().add(copyParticipant);
            }
        }
    }

    private void copyPeople(CaseFile original, CaseFile copyCaseFile)
    {
        if (original.getPersonAssociations() == null || original.getPersonAssociations().isEmpty())
            return;
        if (copyCaseFile.getPersonAssociations() == null)
            copyCaseFile.setPersonAssociations(new ArrayList<>());
        for (PersonAssociation person : original.getPersonAssociations())
        {
            PersonAssociation copyPerson = new PersonAssociation();
            copyPerson.setPersonType(person.getPersonType());
            copyPerson.setPerson(person.getPerson());
            copyPerson.setNotes(person.getNotes());
            copyPerson.setPersonDescription(person.getPersonDescription());
            copyPerson.setTags(person.getTags());
            copyCaseFile.getPersonAssociations().add(copyPerson);
        }
    }

    private void copyDocumentsAndFolders(CaseFile saved, SplitCaseOptions options)
            throws AcmObjectNotFoundException, AcmUserActionFailedException, AcmFolderException, AcmCreateObjectFailedException
    {
        for (SplitCaseOptions.AttachmentDTO attachmentDTO : options.getAttachments())
        {
            AcmContainer containerOfCopy = saved.getContainer();
            AcmFolder containerFolderOfCopy = containerOfCopy.getFolder();
            switch (attachmentDTO.getType())
            {
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

    private void copyTasks(CaseFile original, CaseFile copyCaseFile, Authentication auth, String ipAddress) throws AcmTaskException,
            AcmCreateObjectFailedException, AcmUserActionFailedException, AcmObjectNotFoundException, AcmFolderException
    {
        acmTaskService.copyTasks(original.getId(), original.getObjectType(), copyCaseFile.getId(), copyCaseFile.getObjectType(),
                copyCaseFile.getTitle(), auth, ipAddress);
    }

    public void setSaveCaseService(SaveCaseService saveCaseService)
    {
        this.saveCaseService = saveCaseService;
    }

    public void setCaseFileDao(CaseFileDao caseFileDao)
    {
        this.caseFileDao = caseFileDao;
    }

    public void setAcmFolderService(AcmFolderService acmFolderService)
    {
        this.acmFolderService = acmFolderService;
    }

    public void setTypesToCopy(String typesToCopyStr)
    {
        if (!StringUtils.isEmpty(typesToCopyStr))
            this.typesToCopy.addAll(Arrays.asList(typesToCopyStr.trim().replaceAll(",[\\s]*", ",").split(",")));
    }

    public void setAcmTaskService(AcmTaskService acmTaskService)
    {
        this.acmTaskService = acmTaskService;
    }

    public SplitCaseFileBusinessRule getSplitCaseFileBusinessRule()
    {
        return splitCaseFileBusinessRule;
    }

    public void setSplitCaseFileBusinessRule(SplitCaseFileBusinessRule splitCaseFileBusinessRule)
    {
        this.splitCaseFileBusinessRule = splitCaseFileBusinessRule;
    }
}
