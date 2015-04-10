package com.armedia.acm.plugins.ecm.service;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.ecm.model.AcmFolder;

/**
 * Created by marjan.stefanoski on 03.04.2015.
 */
public interface AcmFolderService {

    AcmFolder addNewFolder(Long parentFolderId, String folderName) throws AcmCreateObjectFailedException, AcmUserActionFailedException;

    AcmFolder renameFolder(Long folderId, String newFolderName) throws AcmUserActionFailedException;

    void deleteFolderIfEmpty(Long folderId) throws AcmUserActionFailedException, AcmObjectNotFoundException;

    AcmFolder findById(Long folderId);

}
