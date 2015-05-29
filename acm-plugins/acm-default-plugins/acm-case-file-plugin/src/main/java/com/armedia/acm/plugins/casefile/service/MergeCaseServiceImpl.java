package com.armedia.acm.plugins.casefile.service;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmListObjectsFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.exceptions.AcmCaseFileNotFound;
import com.armedia.acm.plugins.casefile.exceptions.MergeCaseFilesException;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.model.MergeCaseOptions;
import com.armedia.acm.plugins.ecm.dao.AcmFolderDao;
import com.armedia.acm.plugins.ecm.model.AcmCmisObject;
import com.armedia.acm.plugins.ecm.model.AcmCmisObjectList;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.service.AcmFolderService;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import org.apache.commons.lang.StringUtils;
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

    @Override
    @Transactional
    public CaseFile mergeCases(Authentication auth, String ipAddress, MergeCaseOptions mergeCaseOptions) throws MuleException, MergeCaseFilesException, AcmUserActionFailedException, AcmCreateObjectFailedException {

        CaseFile source = caseFileDao.find(mergeCaseOptions.getSourceCaseFileId());
        if (source == null)
            throw new AcmCaseFileNotFound("Source Case File with id = " + mergeCaseOptions.getSourceCaseFileId() + " not found");
        CaseFile target = caseFileDao.find(mergeCaseOptions.getTargetCaseFileId());
        if (target == null)
            throw new AcmCaseFileNotFound("Target Case File with id = " + mergeCaseOptions.getTargetCaseFileId() + " not found");

        //merge details
        String sourceDetails = StringUtils.isEmpty(source.getDetails()) ? "" : String.format(MERGE_TEXT_SEPPARATOR, source.getTitle(), source.getCaseNumber()) + source.getDetails();
        target.setDetails(!StringUtils.isEmpty(target.getDetails()) ?
                target.getDetails() + sourceDetails :
                sourceDetails);

        //merge folders and documents
        AcmFolder targetFolder = acmFolderService.addNewFolder(target.getContainer().getFolder().getId(), String.format("%s(%s)", source.getTitle(), source.getCaseNumber()));

        //move content from source ROOT folder into targetFolder
        try {
            AcmCmisObjectList acmCmisObjectList = ecmFileService.listFolderContents(auth,
                    source.getContainer(), null, "name", "ASC", 0, 10000);
            for (AcmCmisObject cmisObject : acmCmisObjectList.getChildren()) {
                if ("folder".equals(cmisObject.getObjectType())) {
                    //change the parent id
                    AcmFolder folderToBeMoved = acmFolderDao.find(cmisObject.getObjectId());
                    folderToBeMoved.setParentFolderId(targetFolder.getId());
                    acmFolderDao.save(folderToBeMoved);
                } else if ("file".equals(cmisObject.getObjectType())) {
                    ecmFileService.moveFile(cmisObject.getObjectId(),
                            target.getContainer().getContainerObjectId(),
                            target.getContainer().getContainerObjectType(),
                            targetFolder.getId());
                }
            }
        } catch (AcmObjectNotFoundException | AcmUserActionFailedException | AcmListObjectsFailedException | AcmCreateObjectFailedException e) {
            throw new MergeCaseFilesException("Error merging case files. Exception in moving documents and folders.", e);
        }


        //set source that is merged
        source.setMergedTo(target);

        saveCaseService.saveCase(source, auth, ipAddress);
        saveCaseService.saveCase(target, auth, ipAddress);

        return target;
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
}
