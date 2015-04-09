package com.armedia.acm.plugins.ecm.web.api;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;

import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.service.AcmFolderService;
import com.armedia.acm.plugins.ecm.service.FolderEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * Created by marjan.stefanoski on 02.04.2015.
 */
@Controller
@RequestMapping({"/api/v1/service/ecm", "/api/latest/service/ecm"})
public class AddNewFolderAPIController {

    private AcmFolderService folderService;

    private FolderEventPublisher folderEventPublisher;

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "/folder/{parentFolderId}/{newFolderName}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AcmFolder addNewFolder(
            @PathVariable("parentFolderId")Long parentFolderId,
            @PathVariable("newFolderName") String newFolderName,
            Authentication authentication) throws AcmCreateObjectFailedException, AcmUserActionFailedException {

        if( log.isInfoEnabled() ) {
            log.info("Creating new folder into  " + parentFolderId + " with name " + newFolderName);
        }

        try {
            AcmFolder newFolder = getFolderService().addNewFolder(parentFolderId, newFolderName);
            if( log.isInfoEnabled() ) {
                log.info("Created new folder " + newFolder.getId() + "with name: " + newFolderName);
            }
            getFolderEventPublisher().publishFolderCreatedEvent(newFolder,authentication,true);
            return newFolder;
        } catch ( AcmCreateObjectFailedException e) {
            if( log.isErrorEnabled() ){
                log.error("Exception occurred while trying to create new folder "+ newFolderName,e);
            }
            getFolderEventPublisher().publishFolderCreatedEvent(null,authentication,false);
            throw e;
        }
    }

    public FolderEventPublisher getFolderEventPublisher() {
        return folderEventPublisher;
    }

    public void setFolderEventPublisher(FolderEventPublisher folderEventPublisher) {
        this.folderEventPublisher = folderEventPublisher;
    }

    public AcmFolderService getFolderService() {
        return folderService;
    }

    public void setFolderService(AcmFolderService folderService) {
        this.folderService = folderService;
    }
}

