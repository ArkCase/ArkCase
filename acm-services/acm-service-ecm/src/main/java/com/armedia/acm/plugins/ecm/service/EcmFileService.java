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

import com.armedia.acm.camelcontext.exception.ArkCaseFileRepositoryException;
import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmListObjectsFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.ecm.exception.EcmFileLinkException;
import com.armedia.acm.plugins.ecm.exception.LinkAlreadyExistException;
import com.armedia.acm.plugins.ecm.model.AcmCmisObjectList;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.LinkTargetFileDTO;
import com.armedia.acm.plugins.ecm.model.RecycleBinItem;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.persistence.PersistenceException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by armdev on 5/1/14.
 */
public interface EcmFileService
{
    CmisObject findObjectByPath(String path) throws Exception;

    CmisObject findObjectById(String cmisRepositoryId, String cmisId) throws Exception;

    EcmFile upload(
            String originalFileName,
            String fileType,
            String fileCategory,
            InputStream fileContents,
            String fileContentType,
            String fileName,
            Authentication authentication,
            String targetCmisFolderId,
            String parentObjectType,
            Long parentObjectId) throws AcmCreateObjectFailedException, AcmUserActionFailedException;

    EcmFile upload(
            String originalFileName,
            String fileType,
            String fileCategory,
            InputStream fileContents,
            String fileContentType,
            String fileName,
            Authentication authentication,
            String targetCmisFolderId,
            String parentObjectType,
            Long parentObjectId,
            String cmisRepositoryId) throws AcmCreateObjectFailedException, AcmUserActionFailedException;

    @Transactional
    EcmFile upload(String originalFileName, String fileType, String fileCategory, InputStream fileContents,
            String fileContentType, String fileName, Authentication authentication,
            String targetCmisFolderId, String parentObjectType, Long parentObjectId,
            String cmisRepositoryId, Document existingCmisDocument)
            throws AcmCreateObjectFailedException, AcmUserActionFailedException;

    /**
     * Service method allowing for extensions to subtype the EcmFIle.
     *
     * @param authentication
     * @param parentObjectType
     * @param parentObjectId
     * @param targetCmisFolderId
     * @param arkcaseFileName
     * @param fileContents
     * @param metadata
     * @return
     * @throws AcmCreateObjectFailedException
     * @throws AcmUserActionFailedException
     */
    @Transactional
    EcmFile upload(Authentication authentication, String parentObjectType, Long parentObjectId,
            String targetCmisFolderId, String arkcaseFileName, InputStream fileContents, EcmFile metadata)
            throws AcmCreateObjectFailedException, AcmUserActionFailedException;

    /**
     * Special method for ECM file sync, where the ECM repository already has the file we want to load into Alfresco.
     *
     * @param authentication
     * @param parentObjectType
     * @param parentObjectId
     * @param targetCmisFolderId
     * @param arkcaseFileName
     * @param fileContents
     * @param metadata
     * @param existingCmisDocument
     * @return
     * @throws AcmCreateObjectFailedException
     * @throws AcmUserActionFailedException
     */
    @Transactional
    EcmFile upload(Authentication authentication, String parentObjectType, Long parentObjectId,
            String targetCmisFolderId, String arkcaseFileName, InputStream fileContents, EcmFile metadata,
            Document existingCmisDocument)
            throws AcmCreateObjectFailedException, AcmUserActionFailedException;

    /**
     * This method is meant to be called via Frevvo form submissions and any other file upload method aside from the
     * webapp file uploader.
     *
     * @param fileType
     *            The application file type: roi, complaint, attachment...
     * @param file
     *            The file to be uploaded
     * @param authentication
     *            User who has uploaded the file
     * @param targetCmisFolderId
     *            ID of the folder where the file should be stored
     * @param parentObjectType
     *            Type of the object that contains this file - task, case file, complaint...
     * @param parentObjectId
     *            ID of the parent object
     * @return EcmFile object representing the uploaded file.
     * @throws AcmCreateObjectFailedException
     */
    EcmFile upload(
            String arkcaseFileName,
            String fileType,
            MultipartFile file,
            Authentication authentication,
            String targetCmisFolderId,
            String parentObjectType,
            Long parentObjectId) throws AcmCreateObjectFailedException, AcmUserActionFailedException;

    EcmFile upload(
            Authentication authentication,
            MultipartFile file,
            String targetCmisFolderId,
            String parentObjectType,
            Long parentObjectId,
            EcmFile metadata) throws AcmCreateObjectFailedException, AcmUserActionFailedException;

    EcmFile upload(
            String arkcaseFileName,
            String fileType,
            String fileLang,
            MultipartFile file,
            Authentication authentication,
            String targetCmisFolderId,
            String parentObjectType,
            Long parentObjectId) throws AcmCreateObjectFailedException, AcmUserActionFailedException;

    /**
     * @param ecmFile
     * @param file
     * @param authentication
     * @return
     * @throws AcmCreateObjectFailedException
     */
    EcmFile update(
            EcmFile ecmFile,
            MultipartFile file,
            Authentication authentication) throws AcmCreateObjectFailedException;

    /**
     * @param ecmFile
     * @param inputStream
     * @param authentication
     * @return
     * @throws AcmCreateObjectFailedException
     */
    EcmFile update(
            EcmFile ecmFile,
            InputStream inputStream,
            Authentication authentication) throws AcmCreateObjectFailedException;

    /**
     * Returns the file with the given Id and acquires a WRITE lock.
     *
     * @param id
     *            the file id
     * @return the file contents as String
     *
     * @throws AcmUserActionFailedException
     */
    String checkout(Long id) throws AcmUserActionFailedException;

    /**
     * @param id
     *            - id of EcmFile
     * @return
     * @throws AcmUserActionFailedException
     */
    String download(Long id) throws AcmUserActionFailedException;

    /**
     * @param id
     *            - id of EcmFile
     * @return InputStream from the CMIS payload
     * @throws AcmUserActionFailedException
     * @usage Needed to create attachments for Exchange Web Services (EWS)
     */

    InputStream downloadAsInputStream(Long id) throws AcmUserActionFailedException;

    InputStream downloadAsInputStream(EcmFile ecmFile) throws AcmUserActionFailedException;

    InputStream downloadAsInputStream(Long id, String version) throws AcmUserActionFailedException;

    AcmContainer createContainerFolder(String objectType, Long objectId, String cmisRepositoryId) throws AcmCreateObjectFailedException;

    /**
     * Create a folder in the CMIS repository
     *
     * @param folderPath
     *            The path to be created. If it already exists, the ID of the existing folder is returned.
     * @return CMIS Object ID of the new folder (if it was created), or the existing folder (if the folderPath already
     *         existed). Either way, the object ID represents the folder at the requested folderPath.
     * @throws AcmCreateObjectFailedException
     *             If the folder could not be created.
     */
    String createFolder(String folderPath) throws AcmCreateObjectFailedException;

    /**
     * Create a folder in the CMIS repository
     *
     * @param folderPath
     *            The path to be created. If it already exists, the ID of the existing folder is returned.
     * @param cmisRepositoryId
     *            CMIS repository identifier
     * @return CMIS Object ID of the new folder (if it was created), or the existing folder (if the folderPath already
     *         existed). Either way, the object ID represents the folder at the requested folderPath.
     * @throws AcmCreateObjectFailedException
     *             If the folder could not be created.
     */
    String createFolder(String folderPath, String cmisRepositoryId) throws AcmCreateObjectFailedException;

    @Transactional
    AcmContainer getOrCreateContainer(String objectType, Long objectId) throws AcmCreateObjectFailedException, AcmUserActionFailedException;

    @Transactional
    AcmContainer getOrCreateContainer(String objectType, Long objectId, String cmisRepositoryId)
            throws AcmCreateObjectFailedException, AcmUserActionFailedException;

    AcmCmisObjectList allFilesForContainer(Authentication auth,
            AcmContainer container)
            throws AcmListObjectsFailedException;

    AcmCmisObjectList listFolderContents(Authentication auth,
            AcmContainer container,
            String category, String sortBy,
            String sortDirection, int startRow, int maxRows) throws AcmListObjectsFailedException;

    AcmCmisObjectList listFlatSearchResults(Authentication auth, AcmContainer container, String category, String sortBy,
            String sortDirection, int startRow, int maxRows, String searchFilter) throws AcmListObjectsFailedException;

    AcmCmisObjectList listFlatSearchResultsAdvanced(Authentication auth, AcmContainer container, String category, String sortBy,
            String sortDirection, int startRow, int maxRows, String searchFilter) throws AcmListObjectsFailedException;

    AcmCmisObjectList listFileFolderByCategory(Authentication auth,
            AcmContainer container,
            String sortBy,
            String sortDirection,
            int startRow,
            int maxRows,
            String category) throws AcmListObjectsFailedException;

    void declareFileAsRecord(Long fileId, Authentication authentication)
            throws AcmObjectNotFoundException;

    void declareFolderAsRecord(Long folderId, Authentication authentication, String parentObjectType, Long parentObjectId)
            throws AcmObjectNotFoundException, AcmListObjectsFailedException, AcmCreateObjectFailedException, AcmUserActionFailedException;

    AcmCmisObjectList allFilesForFolder(Authentication auth,
            AcmContainer container, Long folderId)
            throws AcmListObjectsFailedException;

    EcmFile copyFile(Long fileId, Long targetObjectId, String targetObjectType, Long dstFolderId)
            throws AcmUserActionFailedException, AcmObjectNotFoundException;

    EcmFile moveFile(Long fileId, Long targetObjectId, String targetObjectType, Long dstFolderId)
            throws AcmUserActionFailedException, AcmObjectNotFoundException, AcmCreateObjectFailedException, LinkAlreadyExistException;

    EcmFile moveFile(Long fileId, Long targetObjectId, String targetObjectType, AcmFolder folder)
            throws AcmUserActionFailedException, AcmObjectNotFoundException, AcmCreateObjectFailedException;

    EcmFile moveFileLink(Long fileId, Long targetObjectId, String targetObjectType, AcmFolder folder)
            throws AcmUserActionFailedException, AcmObjectNotFoundException, AcmCreateObjectFailedException, LinkAlreadyExistException;

    @Retryable(maxAttempts = 3, value = Exception.class, backoff = @Backoff(delay = 500))
    EcmFile moveFileInArkcase(EcmFile file, AcmFolder targetParentFolder, String targetObjectType)
            throws AcmUserActionFailedException, AcmCreateObjectFailedException;

    @Retryable(maxAttempts = 3, value = Exception.class, backoff = @Backoff(delay = 500))
    void deleteFileFromArkcase(Long fileId);

    void deleteFile(Long fileId) throws AcmUserActionFailedException, AcmObjectNotFoundException;

    RecycleBinItem putFileIntoRecycleBin(Long fileId, Authentication authentication, HttpSession session)
            throws AcmUserActionFailedException, AcmObjectNotFoundException, AcmCreateObjectFailedException;

    void deleteFile(Long fileId, Boolean allVersions) throws AcmUserActionFailedException, AcmObjectNotFoundException;

    void deleteCmisObject(CmisObject cmisObject, String cmisRepositoryId) throws Exception;

    void deleteFilePermanently(Long fileId, Long recycleBinId) throws AcmUserActionFailedException, AcmObjectNotFoundException;

    void deleteFile(Long fileId, Long parentId, String parentType) throws AcmUserActionFailedException, AcmObjectNotFoundException;

    @Retryable(maxAttempts = 3, value = Exception.class, backoff = @Backoff(delay = 500))
    void deleteFileInArkcase(EcmFile file) throws AcmUserActionFailedException, AcmObjectNotFoundException;

    void deleteFileLinks(EcmFile file);

    EcmFile renameFile(Long fileId, String newFileName) throws AcmUserActionFailedException, AcmObjectNotFoundException;

    EcmFile renameFileInArkcase(EcmFile file, String newFileName);

    EcmFile findById(Long fileId);

    List<EcmFile> findByIds(List<Long> ids);

    AcmCmisObjectList listAllSubFolderChildren(String category, Authentication auth, AcmContainer container, Long folderId, int startRow,
            int maxRows, String sortBy, String sortDirection) throws AcmListObjectsFailedException, AcmObjectNotFoundException;

    EcmFile setFilesActiveVersion(Long fileId, String versionTag) throws PersistenceException;

    /**
     * Get the record from the cmis repository and re-upload it as a new file in the specified folder
     * 
     * @param fileId
     *            original record ID
     * @param folderId
     *            destination folder ID
     * @param targetObjectType
     *            container type
     * @param targetObjectId
     *            container ID
     * @param authentication
     *            authenticaton object
     * @return
     * @throws AcmObjectNotFoundException
     * @throws AcmUserActionFailedException
     */
    EcmFile copyRecord(Long fileId, Long folderId, String targetObjectType, Long targetObjectId, Authentication authentication)
            throws AcmObjectNotFoundException, AcmUserActionFailedException;

    EcmFile copyFile(Long documentId, AcmFolder targetFolder, AcmContainer targetContainer)
            throws AcmUserActionFailedException, AcmObjectNotFoundException;

    @Retryable(maxAttempts = 3, value = Exception.class, backoff = @Backoff(delay = 500))
    EcmFile copyFileInArkcase(EcmFile originalFile, String copiedFileNodeId, AcmFolder targetFolder)
            throws AcmUserActionFailedException;

    EcmFile updateFileType(Long fileId, String fileType) throws AcmObjectNotFoundException;

    List<EcmFile> findFileByContainerAndFileType(Long containerId, String fileType);

    EcmFile findOldestFileByContainerAndFileType(Long containerId, String fileType);

    @Transactional
    EcmFile updateFile(EcmFile file) throws AcmObjectNotFoundException;

    int getTotalPageCount(String parentObjectType, Long parentObjectId, List<String> totalPageCountFileTypes,
            List<String> totalPageCountMimeTypes, Authentication auth);

    EcmFile updateSecurityField(Long fileId, String securityFieldValue) throws AcmObjectNotFoundException;

    /**
     * Save files from request to temp directory
     *
     * @param files
     *            multipart-files to save
     * @return List<EcmFile> which includes unique filename id for saved files in temp directory
     */
    List<EcmFile> saveFilesToTempDirectory(MultiValueMap<String, MultipartFile> files);

    /**
     * Delete temp files uploaded earlier, named 'uniqueFileName'
     *
     * @param uniqueFileName
     *            name of file in temp directory to save
     * @return whether the delete was successful
     */
    boolean deleteTempFile(String uniqueFileName);

    File convertFile(String fileKey, String version, String fileExtension, String fileName, String mimeType, EcmFile ecmFile)
            throws IOException;

    void removeLockAndSendMessage(Long objectId, String message);

    String uploadFileChunk(MultipartHttpServletRequest request, String fileName, String uniqueArkCaseHashFileIdentifier);

    EcmFile copyFileAsLink(Long fileId, Long targetObjectId, String targetObjectType, Long dstFolderId)
            throws AcmUserActionFailedException, AcmObjectNotFoundException, LinkAlreadyExistException;

    EcmFile copyFileAsLink(Long fileId, AcmFolder targetFolder, AcmContainer targetContainer)
            throws AcmUserActionFailedException, AcmObjectNotFoundException, LinkAlreadyExistException;

    List<EcmFile> uploadFiles(Authentication authentication, String parentObjectType, Long parentObjectId, String fileType,
            String folderCmisId, MultipartHttpServletRequest request, HttpSession session)
            throws AcmUserActionFailedException, AcmCreateObjectFailedException, IOException;

    List<EcmFile> uploadFiles(Authentication authentication, String parentObjectType, Long parentObjectId, String fileType,
            String fileLang, String folderCmisId, MultipartHttpServletRequest request, HttpSession session)
            throws AcmUserActionFailedException, AcmCreateObjectFailedException, IOException;

    List<EcmFile> getFileLinks(Long fileId) throws AcmObjectNotFoundException;

    @Transactional
    void updateFileLinks(EcmFile file) throws AcmObjectNotFoundException;

    void updateLinkTargetFile(EcmFile file) throws EcmFileLinkException;

    String addDateInPath(String folderPath, Boolean flag) throws AcmCreateObjectFailedException;

    LinkTargetFileDTO getLinkTargetFileInfo(EcmFile ecmFile) throws EcmFileLinkException;

    void checkAndSetDuplicatesByHash(EcmFile ecmFile);

    List<EcmFile> findFilesByFolder(Long folderId);

    List<EcmFile> getFileDuplicates(Long fileId) throws AcmObjectNotFoundException;

    void download(HttpServletResponse response, boolean isInline, EcmFile ecmFile, String version)
            throws IOException, ArkCaseFileRepositoryException, AcmObjectNotFoundException;

    void fileNotFound(Long fileId) throws AcmObjectNotFoundException;

    void publishEcmFileDownloadedEvent(String ipAddress, EcmFile ecmFile, Authentication auth);
}
