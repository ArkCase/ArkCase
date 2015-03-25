package com.armedia.acm.plugins.ecm.service;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmListObjectsFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.ecm.model.AcmCmisObject;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.services.users.model.AcmUser;

import org.mule.api.MuleException;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

/**
 * Created by armdev on 5/1/14.
 */
public interface EcmFileService
{
    EcmFile upload(
            String fileType,
            String fileCategory,
            InputStream fileContents,
            String fileContentType,
            String fileName,
            Authentication authentication,
            String targetCmisFolderId,
            String parentObjectType,
            Long parentObjectId) throws AcmCreateObjectFailedException, AcmUserActionFailedException;

    /** This method is meant to be called via Frevvo form submissions and any other file upload method aside from the
     * webapp file uploader.
     *
     * @param fileType The application file type: roi, complaint, attachment...
     * @param file The file to be uploaded
     * @param authentication User who has uploaded the file
     * @param targetCmisFolderId ID of the folder where the file should be stored
     * @param parentObjectType Type of the object that contains this file - task, case file, complaint...
     * @param parentObjectId ID  of the parent object
     * @return EcmFile object representing the uploaded file.
     * @throws AcmCreateObjectFailedException
     */
    EcmFile upload(
            String fileType,
            MultipartFile file,
            Authentication authentication,
            String targetCmisFolderId,
            String parentObjectType,
            Long parentObjectId) throws AcmCreateObjectFailedException, AcmUserActionFailedException;
    
    /**
     * 
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
     * 
     * @param id - id of EcmFile
     * @return
     * @throws AcmObjectNotFoundException
     */
    String download(Long id) throws MuleException;

    /**
     * Create a folder in the CMIS repository
     * @param folderPath The path to be created.  If it already exists, the ID of the existing folder is returned.
     * @return CMIS Object ID of the new folder (if it was created), or the existing folder (if the folderPath already
     * existed).  Either way, the object ID represents the folder at the requested folderPath.
     * @throws AcmCreateObjectFailedException If the folder could not be created.
     */
    String createFolder(String folderPath) throws AcmCreateObjectFailedException;

    @Transactional
    AcmContainer getOrCreateContainerFolder(String objectType, Long objectId) throws
            AcmCreateObjectFailedException, AcmUserActionFailedException;

    List<AcmCmisObject> listFolderContents(String folderId, String sortBy, String sortDirection) throws AcmListObjectsFailedException;

    /**
     * Replace all not allowed characters in folder name with underscore
     * 
     * @param name
     * @return
     */
    String buildSafeFolderName(String name);
}
