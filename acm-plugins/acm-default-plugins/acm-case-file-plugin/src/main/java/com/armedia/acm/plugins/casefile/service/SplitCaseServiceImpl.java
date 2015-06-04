package com.armedia.acm.plugins.casefile.service;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.data.AcmEntity;
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

import java.lang.reflect.Field;
import java.util.HashMap;
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
        CaseFile toBeSplitCaseFile = caseFileDao.find(splitCaseOptions.getCaseFileId());
        if (toBeSplitCaseFile == null)
            throw new SplitCaseFileException("Case file with id = (" + splitCaseOptions.getCaseFileId() + ") not found");


        //clean all created,creator,modified, modifier in sub objects as well
        try {
            cleanRecursivelyAcmEntity(toBeSplitCaseFile);
        } catch (IllegalAccessException e) {
            log.error("Clean AcmEntity failed", e);
            throw new SplitCaseFileException("clean AcmEntity failed", e);
        }

        toBeSplitCaseFile.setId(null);
        toBeSplitCaseFile.setCaseNumber(null);

        //clean container
        toBeSplitCaseFile.setContainer(null);

        toBeSplitCaseFile.setParticipants(null);

        CaseFile originalCaseFile = caseFileDao.find(splitCaseOptions.getCaseFileId());


        ObjectAssociation childObjectCopy = new ObjectAssociation();
        childObjectCopy.setAssociationType("REFERENCE");
        childObjectCopy.setCategory("COPY_FROM");
        childObjectCopy.setTargetId(originalCaseFile.getId());
        childObjectCopy.setTargetType(originalCaseFile.getObjectType());
        toBeSplitCaseFile.addChildObject(childObjectCopy);

        CaseFile copyCaseFile = saveCaseService.saveCase(toBeSplitCaseFile, auth, ipAddress);
        //copyCaseFile = caseFileDao.save(copyCaseFile);

        ObjectAssociation childObjectOriginal = new ObjectAssociation();
        childObjectOriginal.setAssociationType("REFERENCE");
        childObjectOriginal.setCategory("COPY_TO");
        childObjectOriginal.setTargetId(copyCaseFile.getId());
        childObjectOriginal.setTargetType(copyCaseFile.getObjectType());
        originalCaseFile.addChildObject(childObjectOriginal);
        saveCaseService.saveCase(originalCaseFile, auth, ipAddress);

        splitDocumentsAndFolders(copyCaseFile, splitCaseOptions);
        return copyCaseFile;
    }

    private void splitDocumentsAndFolders(CaseFile saved, SplitCaseOptions options) throws AcmObjectNotFoundException, AcmUserActionFailedException, AcmFolderException, AcmCreateObjectFailedException {
        Map<Long, AcmFolder> folderMap = new HashMap<>();
        for (SplitCaseOptions.AttachmentDTO attachmentDTO : options.getAttachments()) {
            AcmContainer savedContainer = saved.getContainer();
            AcmFolder savedContainerFolder = savedContainer.getFolder();
            switch (attachmentDTO.getType()) {
                case "folder":
                    AcmFolder folderForMoving = acmFolderService.findById(attachmentDTO.getId());
                    AcmFolder movedFolder = acmFolderService.moveFolder(folderForMoving, savedContainerFolder);
                    folderMap.put(folderForMoving.getId(), movedFolder);
                    break;
                case "document":
                    EcmFile fileForMoving = ecmFileService.findById(attachmentDTO.getId());

                    if (folderMap.containsKey(fileForMoving.getFolder().getId())) {
                        //we have already created that folder
                        AcmFolder foundFolder = folderMap.get(fileForMoving.getFolder().getId());
                        ecmFileService.moveFile(attachmentDTO.getId(),
                                savedContainer.getContainerObjectId(),
                                savedContainer.getContainerObjectType(),
                                foundFolder.getId());
                    }

                    AcmFolder documentFolder = fileForMoving.getFolder();
                    if (documentFolder.getParentFolderId() == null || !options.isPreserveFolderStructure()) {
                        //recreate folder structure as on source
                        //document is under root folder, no need to create additional folders
                        ecmFileService.moveFile(attachmentDTO.getId(),
                                savedContainer.getContainerObjectId(),
                                savedContainer.getContainerObjectType(),
                                savedContainerFolder.getId());
                    } else {
                        //create folder structure in saved case file same as in source for the document
                        AcmFolder newParentFolder = createFolderStructure(documentFolder, savedContainerFolder, folderMap);
                        log.debug("created new folder {}", newParentFolder);
                        log.debug("moving file with id = {} to that folder", attachmentDTO.getId());
                        ecmFileService.moveFile(attachmentDTO.getId(),
                                savedContainer.getContainerObjectId(),
                                savedContainer.getContainerObjectType(),
                                newParentFolder.getId());
                    }

                    break;
                default:
                    log.warn("Invalid type({}) for for splitting attachments", attachmentDTO.getType());
                    break;
            }
        }
    }

    private AcmFolder createFolderStructure(AcmFolder folder, AcmFolder rootFolder, Map<Long, AcmFolder> folderMap) throws AcmUserActionFailedException, AcmCreateObjectFailedException, AcmObjectNotFoundException {
        if (folderMap.containsKey(folder.getId()))
            return folderMap.get(folder.getId());
        if (folder.getParentFolderId() != null) {
            AcmFolder createdParent = createFolderStructure(acmFolderService.findById(folder.getParentFolderId()), rootFolder, folderMap);
            AcmFolder newFolder = acmFolderService.addNewFolder(createdParent, folder.getName());
            folderMap.put(folder.getId(), newFolder);
            return newFolder;
        } else {
            folderMap.put(folder.getId(), rootFolder);
            return rootFolder;
        }
    }


    private void cleanRecursivelyAcmEntity(AcmEntity entity) throws IllegalAccessException {
        entity.setCreated(null);
        entity.setCreator(null);
        entity.setModified(null);
        entity.setModifier(null);
        for (Field filed : entity.getClass().getFields()) {
            if (filed.getType().isAssignableFrom(AcmEntity.class)) {
                cleanRecursivelyAcmEntity((AcmEntity) filed.get(entity));
            }
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
