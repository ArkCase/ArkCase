package com.armedia.acm.plugins.casefile.service;

import com.armedia.acm.core.AcmObject;
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
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.service.AcmFolderService;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.plugins.objectassociation.model.ObjectAssociation;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.services.participants.model.ParticipantTypes;
import org.mule.api.MuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by nebojsha on 01.06.2015.
 */
public class SplitCaseServiceImpl implements SplitCaseService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private SaveCaseService saveCaseService;
    private CaseFileDao caseFileDao;
    private EcmFileService ecmFileService;
    private AcmFolderService acmFolderService;

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

        // do not set title, so the rules will get us a new title
        // copyCaseFile.setTitle(original.getTitle());
        copyCaseFile.setDetails(original.getDetails());
        copyCaseFile.setStatus(original.getStatus());

        //add assignee to new case
        AcmParticipant participant = new AcmParticipant();
        participant.setParticipantLdapId(auth.getName());
        participant.setParticipantType(ParticipantTypes.ASSIGNEE);

        if (copyCaseFile.getParticipants() != null)
            copyCaseFile.setParticipants(new ArrayList<>());
        copyCaseFile.getParticipants().add(participant);


        ObjectAssociation childObjectCopy = new ObjectAssociation();
        childObjectCopy.setAssociationType("REFERENCE");
        childObjectCopy.setCategory("COPY_FROM");
        childObjectCopy.setTargetId(original.getId());
        childObjectCopy.setTargetType(original.getObjectType());
        copyCaseFile.addChildObject(childObjectCopy);

        copyCaseFile = saveCaseService.saveCase(copyCaseFile, auth, ipAddress);

        ObjectAssociation childObjectOriginal = new ObjectAssociation();
        childObjectOriginal.setAssociationType("REFERENCE");
        childObjectOriginal.setCategory("COPY_TO");
        childObjectOriginal.setTargetId(copyCaseFile.getId());
        childObjectOriginal.setTargetType(copyCaseFile.getObjectType());
        original.addChildObject(childObjectOriginal);
        saveCaseService.saveCase(original, auth, ipAddress);

        splitDocumentsAndFolders(copyCaseFile, splitCaseOptions);
        return copyCaseFile;
    }

    private void splitDocumentsAndFolders(CaseFile saved, SplitCaseOptions options) throws AcmObjectNotFoundException, AcmUserActionFailedException, AcmFolderException, AcmCreateObjectFailedException {
        Map<Long, AcmFolder> folderMap = new HashMap<>();
        for (SplitCaseOptions.AttachmentDTO attachmentDTO : options.getAttachments()) {
            AcmContainer containerOfCopy = saved.getContainer();
            AcmFolder containerFolderOfCopy = containerOfCopy.getFolder();
            switch (attachmentDTO.getType()) {
                case "folder":
                    copyFolder(options, folderMap, attachmentDTO.getId(), containerOfCopy, containerFolderOfCopy);
                    break;
                case "document":
                    copyDocument(options, folderMap, attachmentDTO.getId(), containerOfCopy, containerFolderOfCopy);
                    break;
                default:
                    log.warn("Invalid type({}) for for splitting attachments", attachmentDTO.getType());
                    break;
            }
        }
    }

    private void copyFolder(SplitCaseOptions options,
                            Map<Long, AcmFolder> folderMap,
                            Long folderId,
                            AcmContainer containerOfCopy,
                            AcmFolder rootFolderOfCopy) throws AcmUserActionFailedException, AcmCreateObjectFailedException, AcmObjectNotFoundException, AcmFolderException {

        if (options.isPreserveFolderStructure()) {
            AcmFolder folderForCopying = acmFolderService.findById(folderId);
            String folderPath = acmFolderService.getFolderPath(folderForCopying);

            AcmFolder alreadyHandledFolder = folderMap.get(folderId);
            //check if folder path exists in the copy
            if (alreadyHandledFolder != null || folderForCopying.getParentFolderId() == null || acmFolderService.folderPathExists(folderPath, containerOfCopy)) {
                //copy the folder children (folders documents) into existing folder of the copy case file
                //this should create or return existing path, but path already exists so it should return existing folder

                List<AcmObject> folderChildren = acmFolderService.getFolderChildren(folderForCopying.getId());
                for (AcmObject obj : folderChildren) {
                    if (obj.getObjectType() == null)
                        continue;
                    if ("FILE".equals(obj.getObjectType().toUpperCase())) {
                        copyDocument(options, folderMap, obj.getId(), containerOfCopy, rootFolderOfCopy);
                    } else if ("FOLDER".equals(obj.getObjectType().toUpperCase())) {
                        copyFolder(options, folderMap, obj.getId(), containerOfCopy, rootFolderOfCopy);
                    }
                }
            } else {
                if (folderForCopying.getParentFolderId() == null)
                    return;
                //just copy the folder to new parent
                AcmFolder parentInSource = acmFolderService.findById(folderForCopying.getParentFolderId());
                String folderPathParentInSource = acmFolderService.getFolderPath(parentInSource);

                //this folder will be created if doesn't exits
                AcmFolder createdParentFolderInCopy = acmFolderService.addNewFolderByPath(
                        containerOfCopy.getContainerObjectType(),
                        containerOfCopy.getContainerObjectId(),
                        folderPathParentInSource);
                AcmFolder copiedFolder = acmFolderService.copyFolder(folderForCopying,
                        createdParentFolderInCopy,
                        containerOfCopy.getContainerObjectId(),
                        containerOfCopy.getContainerObjectType());
                folderMap.put(folderForCopying.getId(), copiedFolder);
            }
        }

    }

    private void copyDocument(SplitCaseOptions options,
                              Map<Long, AcmFolder> folderMap,
                              Long documentId,
                              AcmContainer containerOfCopy,
                              AcmFolder rootFolderOfCopy) throws AcmUserActionFailedException, AcmObjectNotFoundException, AcmCreateObjectFailedException {
        EcmFile fileForCopying = ecmFileService.findById(documentId);

        if (folderMap.containsKey(fileForCopying.getFolder().getId())) {
            //we have already created that folder
            AcmFolder foundFolder = folderMap.get(fileForCopying.getFolder().getId());
            ecmFileService.copyFile(documentId,
                    containerOfCopy.getContainerObjectId(),
                    containerOfCopy.getContainerObjectType(),
                    foundFolder.getId());
        }

        AcmFolder documentFolder = fileForCopying.getFolder();
        if (documentFolder.getParentFolderId() == null || !options.isPreserveFolderStructure()) {
            //recreate folder structure as on source
            //document is under root folder, no need to create additional folders
            ecmFileService.copyFile(documentId,
                    containerOfCopy.getContainerObjectId(),
                    containerOfCopy.getContainerObjectType(),
                    rootFolderOfCopy.getId());
        } else {
            //create folder structure in saved case file same as in source for the document
            String folderPath = acmFolderService.getFolderPath(fileForCopying.getFolder());
            log.debug("folder path = '{}' for folder(id={}, name={})", folderPath, fileForCopying.getFolder().getId(), fileForCopying.getFolder().getName());
            try {
                AcmFolder createdFolder = acmFolderService.addNewFolderByPath(
                        containerOfCopy.getContainerObjectType(),
                        containerOfCopy.getContainerObjectId(),
                        folderPath);
                ecmFileService.copyFile(documentId,
                        containerOfCopy.getContainerObjectId(),
                        containerOfCopy.getContainerObjectType(),
                        createdFolder.getId());
                folderMap.put(fileForCopying.getFolder().getId(), createdFolder);
            } catch (Exception e) {
                log.error("Couldn't create folder structure for document with id=" + documentId + " and will not be copied.", e);
            }
        }
    }

    private AcmFolder createFolderStructure(AcmFolder folder, AcmFolder rootFolder, Map<Long, AcmFolder> folderMap) throws AcmUserActionFailedException, AcmCreateObjectFailedException, AcmObjectNotFoundException {
        if (folderMap.containsKey(folder.getId()))
            return folderMap.get(folder.getId());
        if (folder.getParentFolderId() != null) {
            AcmFolder createdParent = createFolderStructure(acmFolderService.findById(folder.getParentFolderId()), rootFolder, folderMap);
            AcmFolder foundByName = acmFolderService.findByNameAndParent(folder.getName(), createdParent);
            AcmFolder newFolder = foundByName != null ? foundByName : acmFolderService.addNewFolder(createdParent.getId(), folder.getName());
            folderMap.put(folder.getId(), newFolder);
            return newFolder;
        } else {
            folderMap.put(folder.getId(), rootFolder);
            return rootFolder;
        }
    }


    public void setSaveCaseService(SaveCaseService saveCaseService) {
        this.saveCaseService = saveCaseService;
    }

    public void setCaseFileDao(CaseFileDao caseFileDao) {
        this.caseFileDao = caseFileDao;
    }

    public void setEcmFileService(EcmFileService ecmFileService) {
        this.ecmFileService = ecmFileService;
    }

    public void setAcmFolderService(AcmFolderService acmFolderService) {
        this.acmFolderService = acmFolderService;
    }
}
