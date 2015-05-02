package com.armedia.acm.plugins.ecm.service;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.ecm.exception.AcmFolderException;
import com.armedia.acm.plugins.ecm.model.AcmCmisObjectList;
import com.armedia.acm.plugins.ecm.model.AcmFolder;

/**
 * Created by marjan.stefanoski on 03.04.2015.
 */
public interface AcmFolderService {

    AcmFolder addNewFolder(Long parentFolderId, String folderName) throws AcmCreateObjectFailedException, AcmUserActionFailedException;

    AcmFolder renameFolder(Long folderId, String newFolderName) throws AcmUserActionFailedException;

    AcmFolder moveFolder(AcmFolder folderForMoving, AcmFolder dstFolder) throws AcmObjectNotFoundException, AcmUserActionFailedException, AcmFolderException;

    void deleteFolderIfEmpty(Long folderId) throws AcmUserActionFailedException, AcmObjectNotFoundException;

    AcmFolder findById(Long folderId);

    AcmCmisObjectList getFolderChildren(String objectType,Long objectId, Long folderId) throws AcmUserActionFailedException, AcmObjectNotFoundException;

    AcmFolder copyFolder(Long folderToBeCopiedId, Long copyDstFolderId, Long targetObjectId, String targetObjectType) throws AcmUserActionFailedException, AcmObjectNotFoundException, AcmCreateObjectFailedException, AcmFolderException;



}
