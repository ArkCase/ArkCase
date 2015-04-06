package com.armedia.acm.plugins.ecm.web.api;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.service.AcmFolderService;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Created by marjan.stefanoski on 02.04.2015.
 */

@RequestMapping({"/api/v1/service/ecm", "/api/latest/service/ecm"})
public class AddNewFolderAPIController {

    private AcmFolderService folderService;

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping( method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AcmFolder addNewFolder(
            @RequestParam(value = "parentFolderPath", required = true ) String parentFolderPath,
            @RequestParam(value = "folderName", required = true ) String folderName,
            Authentication authentication) throws AcmCreateObjectFailedException {

        if( log.isInfoEnabled() ) {
            log.info("Creating new folder into  " + parentFolderPath + " with name " + folderName);
        }

        try {
            AcmFolder newFolder =   getFolderService().addNewFolder(parentFolderPath, folderName);
            if( log.isInfoEnabled() ){
                log.info("Created new folder " + newFolder.getId() + "with name: " + folderName);
            }
            return newFolder;
        } catch ( AcmCreateObjectFailedException e) {
            if( log.isErrorEnabled() ){
                log.error("Exception occurred while trying to create new folder "+folderName,e);
            }
            throw e;
        }
    }

    public AcmFolderService getFolderService() {
        return folderService;
    }

    public void setFolderService(AcmFolderService folderService) {
        this.folderService = folderService;
    }
}

