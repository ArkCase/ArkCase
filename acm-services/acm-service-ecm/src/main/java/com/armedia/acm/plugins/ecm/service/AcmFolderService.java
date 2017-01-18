package com.armedia.acm.plugins.ecm.service;

import com.armedia.acm.core.AcmObject;
import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.ecm.exception.AcmFolderException;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import org.json.JSONArray;
import org.mule.api.MuleException;

import java.util.List;

/**
 * Created by marjan.stefanoski on 03.04.2015.
 */
public interface AcmFolderService {

    AcmFolder addNewFolder(Long parentFolderId, String folderName) throws AcmCreateObjectFailedException, AcmUserActionFailedException, AcmObjectNotFoundException;

    AcmFolder addNewFolder(Long parentFolderId, String folderName, Long parentId, String parentType) throws AcmCreateObjectFailedException, AcmUserActionFailedException, AcmObjectNotFoundException;

    AcmFolder addNewFolder(AcmFolder parentFolder, String folderName) throws AcmCreateObjectFailedException, AcmUserActionFailedException, AcmObjectNotFoundException;

    AcmFolder addNewFolderByPath(String targetObjectType, Long targetObjectId, String newPath) throws AcmCreateObjectFailedException, AcmUserActionFailedException, AcmObjectNotFoundException, AcmFolderException;

    String findFolderPath(String cmisFolderObjectId) throws MuleException, AcmUserActionFailedException;

    AcmFolder renameFolder(Long folderId, String newFolderName) throws AcmUserActionFailedException, AcmObjectNotFoundException, AcmFolderException;

    AcmFolder moveFolder(AcmFolder folderForMoving, AcmFolder dstFolder) throws AcmObjectNotFoundException, AcmUserActionFailedException, AcmFolderException;

    AcmFolder copyFolder(AcmFolder toBeCopied, AcmFolder dstFolder, Long targetObjectId, String targetObjectType) throws AcmUserActionFailedException, AcmObjectNotFoundException, AcmCreateObjectFailedException, AcmFolderException;

    void deleteFolderIfEmpty(Long folderId) throws AcmUserActionFailedException, AcmObjectNotFoundException;

    AcmFolder findById(Long folderId);

    List<AcmObject> getFolderChildren(Long folderId) throws AcmUserActionFailedException, AcmObjectNotFoundException;

    AcmFolder moveRootFolder(AcmFolder folderForMoving, AcmFolder dstFolder) throws AcmObjectNotFoundException, AcmUserActionFailedException, AcmFolderException;

    AcmFolder copyFolder(Long folderToBeCopiedId, Long copyDstFolderId, Long targetObjectId, String targetObjectType) throws AcmUserActionFailedException, AcmObjectNotFoundException, AcmCreateObjectFailedException, AcmFolderException;

    AcmFolder findByNameAndParent(String name, AcmFolder parent);

    void addFolderStructure(AcmContainer container, AcmFolder parentFolder, JSONArray folderStructure) throws AcmCreateObjectFailedException, AcmUserActionFailedException, AcmObjectNotFoundException;

    String getFolderPath(AcmFolder folder) throws AcmObjectNotFoundException;

    boolean folderPathExists(String folderPath, AcmContainer container) throws AcmFolderException;

    void copyFolderStructure(Long folderId,
                             AcmContainer containerOfCopy,
                             AcmFolder rootFolderOfCopy) throws AcmUserActionFailedException, AcmCreateObjectFailedException, AcmObjectNotFoundException, AcmFolderException;

    void copyDocumentStructure(Long documentId,
                               AcmContainer containerOfCopy,
                               AcmFolder rootFolderOfCopy) throws AcmUserActionFailedException, AcmObjectNotFoundException, AcmCreateObjectFailedException;

    AcmContainer findContainerByFolderId(Long folderId) throws AcmObjectNotFoundException;
}
