package gov.foia.service;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.armedia.acm.core.AcmObject;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.service.AcmFolderService;
import com.armedia.acm.plugins.ecm.service.EcmFileService;

import gov.foia.model.FOIAFile;

public class PublicFlagService {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private EcmFileService ecmFileService;
    private EcmFileDao ecmFileDao;
    private AcmFolderService acmFolderService;


    public void updatePublicFlagForFiles(Set<Long> fileIds, Set<Long> folderIds, boolean publicFlag) throws AcmUserActionFailedException, AcmObjectNotFoundException {

        for(Long folderId : folderIds)
        {
            getChildrenFilesForFolderRecursive(folderId, fileIds);
        }

        for(Long fileId : fileIds)
        {
            log.info("Updating public flag for file with id [{}] with status [{}]", fileId, publicFlag);

            FOIAFile ecmFile = (FOIAFile) getEcmFileDao().find(fileId);
            ecmFile.setPublicFlag(publicFlag);

            getEcmFileService().updateFile(ecmFile);
        }
    }

    private void getChildrenFilesForFolderRecursive(Long folderId, Set<Long> childrenList) throws AcmObjectNotFoundException, AcmUserActionFailedException {

        for(AcmObject child : getAcmFolderService().getFolderChildren(folderId))
        {
            if("FILE".equals(child.getObjectType()))
            {
                childrenList.add(child.getId());
            }
            else if("FOLDER".equals(child.getObjectType()))
            {
                getChildrenFilesForFolderRecursive(child.getId(), childrenList);
            }
        }
    }

    public EcmFileService getEcmFileService() {
        return ecmFileService;
    }

    public void setEcmFileService(EcmFileService ecmFileService) {
        this.ecmFileService = ecmFileService;
    }

    public EcmFileDao getEcmFileDao() {
        return ecmFileDao;
    }

    public void setEcmFileDao(EcmFileDao ecmFileDao) {
        this.ecmFileDao = ecmFileDao;
    }

    public AcmFolderService getAcmFolderService() {
        return acmFolderService;
    }

    public void setAcmFolderService(AcmFolderService acmFolderService) {
        this.acmFolderService = acmFolderService;
    }
}
