package com.armedia.acm.plugins.ecm.service;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmListObjectsFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.ecm.model.AcmCmisObjectList;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.mule.api.MuleException;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.PersistenceException;
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
     * @param fileType           The application file type: roi, complaint, attachment...
     * @param file               The file to be uploaded
     * @param authentication     User who has uploaded the file
     * @param targetCmisFolderId ID of the folder where the file should be stored
     * @param parentObjectType   Type of the object that contains this file - task, case file, complaint...
     * @param parentObjectId     ID  of the parent object
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
     * @param id - id of EcmFile
     * @return
     * @throws AcmObjectNotFoundException
     */
    String download(Long id) throws MuleException;


    /**
     * @param id - id of EcmFile
     * @return InputStream from the CMIS payload
     * @throws AcmObjectNotFoundException
     * @usage Needed to create attachments for Exchange Web Services (EWS)
     */

    InputStream downloadAsInputStream(Long id) throws MuleException, AcmUserActionFailedException;

    AcmContainer createContainerFolder(String objectType, Long objectId, String cmisRepositoryId) throws AcmCreateObjectFailedException;

    /**
     * Create a folder in the CMIS repository
     *
     * @param folderPath The path to be created.  If it already exists, the ID of the existing folder is returned.
     * @return CMIS Object ID of the new folder (if it was created), or the existing folder (if the folderPath already
     * existed).  Either way, the object ID represents the folder at the requested folderPath.
     * @throws AcmCreateObjectFailedException If the folder could not be created.
     */
    String createFolder(String folderPath) throws AcmCreateObjectFailedException;

    /**
     * Create a folder in the CMIS repository
     *
     * @param folderPath       The path to be created.  If it already exists, the ID of the existing folder is returned.
     * @param cmisRepositoryId CMIS repository identifier
     * @return CMIS Object ID of the new folder (if it was created), or the existing folder (if the folderPath already
     * existed).  Either way, the object ID represents the folder at the requested folderPath.
     * @throws AcmCreateObjectFailedException If the folder could not be created.
     */
    String createFolder(String folderPath, String cmisRepositoryId) throws AcmCreateObjectFailedException;


    @Transactional
    AcmContainer getOrCreateContainer(String objectType, Long objectId) throws
            AcmCreateObjectFailedException, AcmUserActionFailedException;

    @Transactional
    AcmContainer getOrCreateContainer(String objectType, Long objectId, String cmisRepositoryId) throws
            AcmCreateObjectFailedException, AcmUserActionFailedException;

    AcmCmisObjectList allFilesForContainer(Authentication auth,
                                           AcmContainer container)
            throws AcmListObjectsFailedException;

    AcmCmisObjectList listFolderContents(Authentication auth,
                                         AcmContainer container,
                                         String category, String sortBy,
                                         String sortDirection, int startRow, int maxRows) throws AcmListObjectsFailedException;

    AcmCmisObjectList listFlatSearchResults(Authentication auth, AcmContainer container, String category, String sortBy,
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

    EcmFile copyFile(Long fileId, Long targetObjectId, String targetObjectType, Long dstFolderId) throws AcmUserActionFailedException, AcmObjectNotFoundException;

    EcmFile moveFile(Long fileId, Long targetObjectId, String targetObjectType, Long dstFolderId) throws AcmUserActionFailedException, AcmObjectNotFoundException, AcmCreateObjectFailedException;

    EcmFile moveFile(Long fileId, Long targetObjectId, String targetObjectType, AcmFolder folder) throws AcmUserActionFailedException, AcmObjectNotFoundException, AcmCreateObjectFailedException;

    void deleteFile(Long fileId) throws AcmUserActionFailedException, AcmObjectNotFoundException;

    void deleteFile(Long fileId, Boolean allVersions) throws AcmUserActionFailedException, AcmObjectNotFoundException;

    void deleteFile(Long fileId, Long parentId, String parentType) throws AcmUserActionFailedException, AcmObjectNotFoundException;

    EcmFile renameFile(Long fileId, String newFileName) throws AcmUserActionFailedException, AcmObjectNotFoundException;

    EcmFile findById(Long fileId);

    AcmCmisObjectList listAllSubFolderChildren(String category, Authentication auth, AcmContainer container, Long folderId, int startRow, int maxRows, String sortBy, String sortDirection) throws AcmListObjectsFailedException, AcmObjectNotFoundException;

    EcmFile setFilesActiveVersion(Long fileId, String versionTag) throws PersistenceException;

    EcmFile copyFile(Long documentId, AcmFolder targetFolder, AcmContainer targetContainer) throws AcmUserActionFailedException, AcmObjectNotFoundException;

    EcmFile updateFileType(Long fileId, String fileType) throws AcmObjectNotFoundException;

    @Transactional
    EcmFile updateFile(EcmFile file) throws AcmObjectNotFoundException;

    int getTotalPageCount(String parentObjectType, Long parentObjectId, List<String> totalPageCountFileTypes, List<String> totalPageCountMimeTypes, Authentication auth);

    EcmFile updateSecurityField(Long fileId, String securityFieldValue) throws AcmObjectNotFoundException;
}
