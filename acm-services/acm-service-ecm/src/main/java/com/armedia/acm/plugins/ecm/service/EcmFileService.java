package com.armedia.acm.plugins.ecm.service;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmListObjectsFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.plugins.ecm.model.AcmCmisObject;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import org.mule.api.MuleException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

/**
 * Created by armdev on 5/1/14.
 */
public interface EcmFileService
{

    /**
     * This method is meant to be called from the web application and is tailored for the jQuery file upload control.
     *
     * @param fileType The application file type: roi, complaint, attachment...
     * @param file The file to be uploaded
     * @param acceptHeader either application/json (from non-IE browsers) or text/plain (for IE)
     * @param contextPath Used to construct file URL to be displayed by the jQuery file upload control after file has
     *                    been uploaded
     * @param authentication User who has uploaded the file
     * @param targetCmisFolderId ID of the folder where the file should be stored
     * @param parentObjectType Type of the object that contains this file - task, case file, complaint...
     * @param parentObjectId ID  of the parent object
     * @param parentObjectName Name of the parent object
     * @return Spring response entity; mime type of application/json for non-IE browsers, or text/plain for IE
     * @throws AcmCreateObjectFailedException
     */
    ResponseEntity<? extends Object> upload(
            String fileType,
            MultipartFile file,
            String acceptHeader,
            String contextPath,
            Authentication authentication,
            String targetCmisFolderId,
            String parentObjectType,
            Long parentObjectId,
            String parentObjectName) throws AcmCreateObjectFailedException;

    EcmFile upload(
            String fileType,
            InputStream fileContents,
            String fileContentType,
            String fileName,
            Authentication authentication,
            String targetCmisFolderId,
            String parentObjectType,
            Long parentObjectId,
            String parentObjectName) throws AcmCreateObjectFailedException;

    EcmFile upload(
            String fileType,
            String fileCategory,
            InputStream fileContents,
            String fileContentType,
            String fileName,
            Authentication authentication,
            String targetCmisFolderId,
            String parentObjectType,
            Long parentObjectId,
            String parentObjectName) throws AcmCreateObjectFailedException;

    /** This method is meant to be called via Frevvo form submissions and any other file upload method aside from the
     * webapp file uploader.
     *
     * @param fileType The application file type: roi, complaint, attachment...
     * @param file The file to be uploaded
     * @param authentication User who has uploaded the file
     * @param targetCmisFolderId ID of the folder where the file should be stored
     * @param parentObjectType Type of the object that contains this file - task, case file, complaint...
     * @param parentObjectId ID  of the parent object
     * @param parentObjectName Name of the parent object
     * @return EcmFile object representing the uploaded file.
     * @throws AcmCreateObjectFailedException
     */
    EcmFile upload(
            String fileType,
            MultipartFile file,
            Authentication authentication,
            String targetCmisFolderId,
            String parentObjectType,
            Long parentObjectId,
            String parentObjectName) throws AcmCreateObjectFailedException;
    
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

    List<AcmCmisObject> listFolderContents(String folderId, String sortBy, String sortDirection) throws AcmListObjectsFailedException;
}
