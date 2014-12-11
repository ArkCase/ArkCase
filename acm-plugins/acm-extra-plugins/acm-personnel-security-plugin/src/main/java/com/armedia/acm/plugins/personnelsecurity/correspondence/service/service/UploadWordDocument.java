package com.armedia.acm.plugins.personnelsecurity.correspondence.service.service;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.correspondence.service.UploadWordDocToAlfresco;
import com.armedia.acm.file.AcmMultipartFile;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;

/**
 * Created by marjan.stefanoski on 11.12.2014.
 */
public class UploadWordDocument {

public ResponseEntity<? extends Object> uploadNewCreatedWordDoc(String objType, String objectId,String ecmFolderId, String fileDiskPath,Authentication auth,HttpServletRequest request) throws AcmCreateObjectFailedException {

    File f = new File(fileDiskPath);
    AcmMultipartFile acmMultipartFile = null;
    try {
        DiskFileItem fileItem = (DiskFileItem) new DiskFileItemFactory().createItem("fileData", "text/plain", true, f.getName());
        MultipartFile file = new CommonsMultipartFile(fileItem);
        acmMultipartFile = new AcmMultipartFile(
                file.getName(),
                file.getOriginalFilename(),
                file.getContentType(),
                file.isEmpty(),
                file.getSize(),
                file.getBytes(),
                file.getInputStream(),
                true);

    } catch (Exception e){

    }
    UploadWordDocToAlfresco uploadWordDocToAlfresco = new UploadWordDocToAlfresco();

    return uploadWordDocToAlfresco.upload(objType, objectId, ecmFolderId, acmMultipartFile, auth, request);

}
}
