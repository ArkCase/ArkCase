package com.armedia.acm.plugins.ecm.web.api;

import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.ecm.dao.AcmFolderDao;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.service.AcmFolderService;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by marjan.stefanoski on 06.04.2015.
 */
@Controller
@RequestMapping({"/api/v1/service/ecm", "/api/latest/service/ecm"})
public class RenameFolderAPIController {

    private AcmFolderService folderService;

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "/folder/{objectId}/{newName}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AcmFolder renameFolder(
            @PathVariable("objectId") Long objectId,
            @PathVariable("newName") String newName,
            Authentication authentication,
            HttpSession session) throws AcmUserActionFailedException {

        if( log.isInfoEnabled() ) {
            log.info("Renaming folder, folderId: " + objectId + " with name " + newName);
        }

        try {
            AcmFolder renamedFile = getFolderService().renameFolder(objectId, newName);
            if(log.isInfoEnabled()) {
                log.info("Folder with id: "+objectId+" successfully renamed to: " +newName);
            }
            return renamedFile;
        } catch (AcmUserActionFailedException e) {
            if( log.isErrorEnabled() ){
                log.error("Exception occurred while trying to rename folder with id: " + objectId);
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
