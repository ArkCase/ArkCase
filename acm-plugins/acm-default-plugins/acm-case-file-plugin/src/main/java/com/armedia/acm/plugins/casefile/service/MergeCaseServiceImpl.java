package com.armedia.acm.plugins.casefile.service;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.exceptions.AcmCaseFileNotFound;
import com.armedia.acm.plugins.casefile.exceptions.MergeCaseFilesException;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.model.CaseFileConstants;
import com.armedia.acm.plugins.casefile.model.MergeCaseOptions;
import com.armedia.acm.plugins.ecm.dao.AcmContainerDao;
import com.armedia.acm.plugins.ecm.dao.AcmFolderDao;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.exception.AcmFolderException;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.service.AcmFolderService;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.plugins.objectassociation.model.ObjectAssociation;
import org.mule.api.MuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

public class MergeCaseServiceImpl implements MergeCaseService {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private SaveCaseService saveCaseService;
    private CaseFileDao caseFileDao;
    private AcmFolderDao acmFolderDao;
    private EcmFileService ecmFileService;
    private AcmFolderService acmFolderService;
    private AcmContainerDao acmContainerDao;
    private EcmFileDao ecmFileDao;

    @Override
    @Transactional
    public CaseFile mergeCases(Authentication auth, String ipAddress, MergeCaseOptions mergeCaseOptions) throws MuleException, MergeCaseFilesException, AcmUserActionFailedException, AcmCreateObjectFailedException {

        CaseFile source = caseFileDao.find(mergeCaseOptions.getSourceCaseFileId());
        if (source == null)
            throw new AcmCaseFileNotFound("Source Case File with id = " + mergeCaseOptions.getSourceCaseFileId() + " not found");
        if (source.getContainer().getFolder().getParentFolderId() != null)
            throw new MergeCaseFilesException("Source is already merged");
        CaseFile target = caseFileDao.find(mergeCaseOptions.getTargetCaseFileId());
        if (target == null)
            throw new AcmCaseFileNotFound("Target Case File with id = " + mergeCaseOptions.getTargetCaseFileId() + " not found");
        log.info("Going to merge {} into {}", source.getCaseNumber(), target.getCaseNumber());
        //merge details
        //        String sourceDetails = StringUtils.isEmpty(source.getDetails()) ? "" : String.format(MERGE_TEXT_SEPPARATOR, source.getTitle(), source.getCaseNumber()) + source.getDetails();
        //        target.setDetails(!StringUtils.isEmpty(target.getDetails()) ?
        //                target.getDetails() + sourceDetails :
        //                sourceDetails);

        //merge folders and documents
        mergeFoldersAndDocuments(source, target);

        ObjectAssociation childObjectSource = new ObjectAssociation();
        childObjectSource.setAssociationType("REFERENCE");
        childObjectSource.setCategory("MERGED_TO");
        childObjectSource.setTargetId(target.getId());
        childObjectSource.setTargetType(source.getObjectType());
        source.addChildObject(childObjectSource);

        ObjectAssociation childObjectTarget = new ObjectAssociation();
        childObjectTarget.setAssociationType("REFERENCE");
        childObjectTarget.setCategory("MERGED_FROM");
        childObjectTarget.setTargetId(source.getId());
        childObjectTarget.setTargetType(target.getObjectType());
        target.addChildObject(childObjectTarget);

        source.setStatus("CLOSED");
        saveCaseService.saveCase(source, auth, ipAddress);
        saveCaseService.saveCase(target, auth, ipAddress);

        return target;
    }

    private void mergeFoldersAndDocuments(CaseFile source, CaseFile target) throws MergeCaseFilesException {
        try {
            //remove source ROOT folder from acm_container from db, when source will be saved new ROOT folder will be created
            AcmFolder sourceRootFolder = source.getContainer().getFolder();

            //move source ROOT folder into target ROOT folder
            sourceRootFolder.setName(String.format("%s(%s)", source.getTitle(), source.getCaseNumber()));
            acmFolderService.moveRootFolder(sourceRootFolder, target.getContainer().getFolder());

            //change source case file documents with target's container id
            long documentsUpdated = ecmFileDao.changeContainer(source.getContainer(), target.getContainer());
            log.info("updated {} documents with new container id=", documentsUpdated, target.getContainer().getId());

        } catch (AcmFolderException | AcmUserActionFailedException | AcmObjectNotFoundException e) {
            throw new MergeCaseFilesException("Error merging case files. Exception in moving documents and folders.", e);
        }
    }


    public void setSaveCaseService(SaveCaseService saveCaseService) {
        this.saveCaseService = saveCaseService;
    }

    public void setCaseFileDao(CaseFileDao caseFileDao) {
        this.caseFileDao = caseFileDao;
    }

    public void setAcmFolderDao(AcmFolderDao acmFolderDao) {
        this.acmFolderDao = acmFolderDao;
    }

    public void setEcmFileService(EcmFileService ecmFileService) {
        this.ecmFileService = ecmFileService;
    }

    public void setAcmFolderService(AcmFolderService acmFolderService) {
        this.acmFolderService = acmFolderService;
    }

    public void setAcmContainerDao(AcmContainerDao acmContainerDao) {
        this.acmContainerDao = acmContainerDao;
    }

    public void setEcmFileDao(EcmFileDao ecmFileDao) {
        this.ecmFileDao = ecmFileDao;
    }
}
