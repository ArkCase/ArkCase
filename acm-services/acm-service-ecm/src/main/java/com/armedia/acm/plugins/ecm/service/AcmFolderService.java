package com.armedia.acm.plugins.ecm.service;

/*-
 * #%L
 * ACM Service: Enterprise Content Management
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

import com.armedia.acm.core.AcmObject;
import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.ecm.exception.AcmFolderException;
import com.armedia.acm.plugins.ecm.exception.LinkAlreadyExistException;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.DeleteFolderInfo;
import com.armedia.acm.plugins.ecm.model.EcmFile;

import org.json.JSONArray;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.core.Authentication;

import java.util.Date;
import java.util.List;

public interface AcmFolderService
{

    AcmFolder addNewFolder(Long parentFolderId, String folderName)
            throws AcmCreateObjectFailedException, AcmUserActionFailedException, AcmObjectNotFoundException;

    AcmFolder addNewFolder(Long parentFolderId, String folderName, Long parentId, String parentType)
            throws AcmCreateObjectFailedException, AcmUserActionFailedException, AcmObjectNotFoundException;

    AcmFolder addNewFolder(AcmFolder parentFolder, String folderName)
            throws AcmCreateObjectFailedException, AcmUserActionFailedException, AcmObjectNotFoundException;

    AcmFolder addNewFolderByPath(String targetObjectType, Long targetObjectId, String newPath)
            throws AcmCreateObjectFailedException, AcmUserActionFailedException, AcmObjectNotFoundException, AcmFolderException;

    String findFolderPath(String cmisFolderObjectId) throws AcmUserActionFailedException;

    AcmFolder renameFolder(Long folderId, String newFolderName)
            throws AcmUserActionFailedException, AcmObjectNotFoundException, AcmFolderException;

    List<EcmFile> getFilesInFolderAndSubfolders(Long folderId);

    AcmFolder moveFolder(AcmFolder folderForMoving, AcmFolder dstFolder)
            throws AcmObjectNotFoundException, AcmUserActionFailedException, AcmFolderException;

    @Retryable(maxAttempts = 3, value = Exception.class, backoff = @Backoff(delay = 500))
    AcmFolder moveFolderInArkcase(AcmFolder folderForMoving, AcmFolder dstFolder)
            throws AcmObjectNotFoundException, AcmUserActionFailedException, AcmFolderException;

    AcmFolder copyFolder(AcmFolder toBeCopied, AcmFolder dstFolder, Long targetObjectId, String targetObjectType)
            throws AcmUserActionFailedException, AcmObjectNotFoundException, AcmCreateObjectFailedException, AcmFolderException;

    void deleteFolderTreeSafe(Long folderId, Authentication authentication) throws AcmUserActionFailedException, AcmObjectNotFoundException;

    void deleteFolderTree(Long folderId, Authentication authentication) throws AcmUserActionFailedException, AcmObjectNotFoundException;

    List<AcmFolder> getFolderLinks(Long folderId) throws AcmObjectNotFoundException;

    void deleteFolderLinks(AcmFolder folder);

    void deleteFolderContent(AcmFolder folder, String user);

    void deleteContainerSafe(AcmContainer container, Authentication authentication)
            throws AcmUserActionFailedException, AcmObjectNotFoundException;

    void deleteContainer(Long containerId, Authentication authentication) throws AcmUserActionFailedException, AcmObjectNotFoundException;

    void deleteContainerAndContent(AcmContainer container, String username);

    AcmFolder findById(Long folderId);

    List<AcmObject> getFolderChildren(Long folderId) throws AcmUserActionFailedException, AcmObjectNotFoundException;

    AcmFolder moveRootFolder(AcmFolder folderForMoving, AcmFolder dstFolder)
            throws AcmObjectNotFoundException, AcmUserActionFailedException, AcmFolderException;

    AcmFolder copyFolder(Long folderToBeCopiedId, Long copyDstFolderId, Long targetObjectId, String targetObjectType)
            throws AcmUserActionFailedException, AcmObjectNotFoundException, AcmCreateObjectFailedException, AcmFolderException;

    AcmFolder findByNameAndParent(String name, AcmFolder parent);

    void addFolderStructure(AcmContainer container, AcmFolder parentFolder, JSONArray folderStructure)
            throws AcmCreateObjectFailedException, AcmUserActionFailedException, AcmObjectNotFoundException;

    String getFolderPath(AcmFolder folder) throws AcmObjectNotFoundException;

    boolean folderPathExists(String folderPath, AcmContainer container) throws AcmFolderException;

    void copyFolderStructure(Long folderId,
            AcmContainer containerOfCopy,
            AcmFolder rootFolderOfCopy)
            throws AcmUserActionFailedException, AcmCreateObjectFailedException, AcmObjectNotFoundException, AcmFolderException;

    void copyDocumentStructure(Long documentId,
            AcmContainer containerOfCopy,
            AcmFolder rootFolderOfCopy) throws AcmUserActionFailedException, AcmObjectNotFoundException, AcmCreateObjectFailedException;

    AcmContainer findContainerByFolderId(Long folderId) throws AcmObjectNotFoundException;

    AcmContainer findContainerByFolderIdTransactionIndependent(Long folderId) throws AcmObjectNotFoundException;

    List<AcmFolder> findModifiedSince(Date lastModified, int start, int pageSize);

    /**
     * retrieves root folder
     *
     * @param parentObjectId
     * @param parentObjectType
     * @return AcmFolder root folder for given objectId, objectType
     */
    AcmFolder getRootFolder(Long parentObjectId, String parentObjectType) throws AcmObjectNotFoundException;

    String getCmisRepositoryId(AcmFolder folder);

    DeleteFolderInfo getFolderToDeleteInfo(Long folderId) throws AcmObjectNotFoundException;

    void putFolderIntoRecycleBin(Long folderId)
            throws AcmObjectNotFoundException, AcmUserActionFailedException, AcmFolderException, AcmCreateObjectFailedException;

    AcmFolder removeLinksFromFilesInFolder(AcmFolder folder);

    AcmFolder saveFolder(AcmFolder folder);

    AcmFolder copyFolderAsLink(AcmFolder toBeCopied, AcmFolder dstFolder, Long targetObjectId, String targetObjectType)
            throws AcmObjectNotFoundException, LinkAlreadyExistException;

    AcmFolder copyFolderAsLink(AcmFolder originalFolder, AcmFolder copyDstFolder, Long targetObjectId, String targetObjectType,
            String newFolderName) throws AcmObjectNotFoundException;

    @Retryable(maxAttempts = 3, value = Exception.class, backoff = @Backoff(delay = 500))
    AcmFolder createFolder(AcmFolder targetParentFolder, String cmisFolderId, String folderName)
            throws AcmFolderException, AcmUserActionFailedException;

    @Retryable(maxAttempts = 3, value = Exception.class, backoff = @Backoff(delay = 500))
    void recordMetadataOfExistingFolderChildren(AcmFolder parentFolder, String userId)
            throws AcmObjectNotFoundException, AcmUserActionFailedException;

    void removeLockAndSendMessage(Long objectId, String message);
}
