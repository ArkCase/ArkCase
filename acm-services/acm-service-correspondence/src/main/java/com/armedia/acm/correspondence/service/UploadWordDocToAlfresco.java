package com.armedia.acm.correspondence.service;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.InputStream;

/**
 * Created by marjan.stefanoski on 11.12.2014.
 */
public class UploadWordDocToAlfresco {

    private EcmFileService ecmFileService;
    private Logger log = LoggerFactory.getLogger(getClass());

    private final  String uploadFileType = "correspondence";
    public ResponseEntity<? extends Object> upload(String objType, String objectId, String ecmFolderId, MultipartFile file, Authentication auth, HttpServletRequest request) throws AcmCreateObjectFailedException {

        String objectType = "WORD_DOC";
        String contextPath = request.getServletContext().getContextPath();
        try {
            return getEcmFileService().upload(uploadFileType, file, "Accept: */*", contextPath, auth,
                    ecmFolderId, objectType, null, file.getName());
        } catch (AcmCreateObjectFailedException e) {
            if(log.isErrorEnabled()){
                log.error("The upload of the new generated correspondence word document for Object Type " + objType +" with ObjectID " +objectId+" failed!");
            }
            throw e;
        }
    }

    public EcmFileService getEcmFileService() {
        return ecmFileService;
    }

    public void setEcmFileService(EcmFileService ecmFileService) {
        this.ecmFileService = ecmFileService;
    }
}
