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
import org.mule.api.MuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;

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
    public CaseFile splitCase(Authentication auth,
                              String ipAddress,
                              SplitCaseOptions splitCaseOptions) throws MuleException, SplitCaseFileException, AcmUserActionFailedException, AcmCreateObjectFailedException, AcmFolderException, AcmObjectNotFoundException {
        CaseFile original = caseFileDao.find(splitCaseOptions.getCaseFileId());
        if (original == null)
            throw new SplitCaseFileException("Case file with id = (" + splitCaseOptions.getCaseFileId() + ") not found");


        CaseFile copyCaseFile = new CaseFile();
        copyCaseFile.setCaseType(original.getCaseType());
        copyCaseFile.setCourtroomName(original.getCourtroomName());
        copyCaseFile.setTitle(original.getTitle());
        copyCaseFile.setDetails(original.getDetails());
        copyCaseFile.setStatus(original.getStatus());


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
                    moveFolder(options, folderMap, attachmentDTO.getId(), containerOfCopy, containerFolderOfCopy);
                    break;
                case "document":
                    moveDocument(options, folderMap, attachmentDTO.getId(), containerOfCopy, containerFolderOfCopy);
                    break;
                default:
                    log.warn("Invalid type({}) for for splitting attachments", attachmentDTO.getType());
                    break;
            }
        }
    }

    private void moveFolder(SplitCaseOptions options,
                            Map<Long, AcmFolder> folderMap,
                            Long folderId,
                            AcmContainer containerOfCopy,
                            AcmFolder rootFolderOfCopy) throws AcmUserActionFailedException, AcmCreateObjectFailedException, AcmObjectNotFoundException, AcmFolderException {
        AcmFolder folderForMoving = acmFolderService.findById(folderId);
        if (options.isPreserveFolderStructure()) {
            //if folder path exists on the copy of case file, than move all sub folders and document, else just move the folder

            //create parent folder structure
            AcmFolder parentFolderOfForMoving = acmFolderService.findById(folderForMoving.getParentFolderId());
            AcmFolder parentFolderOfCopy = createFolderStructure(parentFolderOfForMoving, rootFolderOfCopy, folderMap);

            AcmFolder foundByName = acmFolderService.findByNameAndParent(folderForMoving.getName(), parentFolderOfCopy);
            if (foundByName != null) {
                List<AcmObject> objects = acmFolderService.getFolderChildren(parentFolderOfForMoving.getId());
                for (AcmObject obj : objects) {
                    if (obj instanceof EcmFile) {
                        ecmFileService.moveFile(obj.getId(),
                                containerOfCopy.getContainerObjectId(),
                                containerOfCopy.getContainerObjectType(),
                                foundByName.getId());
                    } else if (obj instanceof AcmFolder) {
                        acmFolderService.moveFolder((AcmFolder) obj,foundByName);
                    } else {
                        log.error("Error: this should not execute.");
                    }
                }

                folderMap.put(folderForMoving.getId(), foundByName);
            } else {
                AcmFolder movedFolder = acmFolderService.moveFolder(folderForMoving, parentFolderOfCopy);
                folderMap.put(folderForMoving.getId(), movedFolder);
            }
        } else {
            AcmFolder movedFolder = acmFolderService.moveFolder(folderForMoving, rootFolderOfCopy);
            folderMap.put(folderForMoving.getId(), movedFolder);
        }
    }

    private void moveDocument(SplitCaseOptions options,
                              Map<Long, AcmFolder> folderMap,
                              Long documentId,
                              AcmContainer containerOfCopy,
                              AcmFolder rootFolderOfCopy) throws AcmUserActionFailedException, AcmObjectNotFoundException, AcmCreateObjectFailedException {
        EcmFile fileForMoving = ecmFileService.findById(documentId);

        if (folderMap.containsKey(fileForMoving.getFolder().getId())) {
            //we have already created that folder
            AcmFolder foundFolder = folderMap.get(fileForMoving.getFolder().getId());
            ecmFileService.moveFile(documentId,
                    containerOfCopy.getContainerObjectId(),
                    containerOfCopy.getContainerObjectType(),
                    foundFolder.getId());
        }

        AcmFolder documentFolder = fileForMoving.getFolder();
        if (documentFolder.getParentFolderId() == null || !options.isPreserveFolderStructure()) {
            //recreate folder structure as on source
            //document is under root folder, no need to create additional folders
            ecmFileService.moveFile(documentId,
                    containerOfCopy.getContainerObjectId(),
                    containerOfCopy.getContainerObjectType(),
                    rootFolderOfCopy.getId());
        } else {
            //create folder structure in saved case file same as in source for the document
            AcmFolder newParentFolder = createFolderStructure(documentFolder, rootFolderOfCopy, folderMap);
            log.debug("created new folder {}", newParentFolder);
            log.debug("moving file with id = {} to that folder", documentId);
            ecmFileService.moveFile(documentId,
                    containerOfCopy.getContainerObjectId(),
                    containerOfCopy.getContainerObjectType(),
                    newParentFolder.getId());
        }
    }

    private AcmFolder createFolderStructure(AcmFolder folder, AcmFolder rootFolder, Map<Long, AcmFolder> folderMap) throws AcmUserActionFailedException, AcmCreateObjectFailedException, AcmObjectNotFoundException {
        if (folderMap.containsKey(folder.getId()))
            return folderMap.get(folder.getId());
        if (folder.getParentFolderId() != null) {
            AcmFolder createdParent = createFolderStructure(acmFolderService.findById(folder.getParentFolderId()), rootFolder, folderMap);
            AcmFolder foundByName = acmFolderService.findByNameAndParent(folder.getName(), createdParent);
            AcmFolder newFolder = foundByName != null ? foundByName : acmFolderService.addNewFolder(createdParent, folder.getName());
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
