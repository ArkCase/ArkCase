package com.armedia.acm.plugins.ecm.web.api;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.ecm.exception.AcmFolderException;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.AcmFolderConstants;
import com.armedia.acm.plugins.ecm.service.AcmFolderService;
import com.armedia.acm.plugins.ecm.service.FolderEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

/**
 * Created by marjan.stefanoski on 02.04.2015.
 */
@Controller
@RequestMapping({"/api/v1/service/ecm", "/api/latest/service/ecm"})
public class CreateFolderByPathAPIController {

    private AcmFolderService folderService;

    private FolderEventPublisher folderEventPublisher;

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "/createFolderByPath", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AcmFolder addNewFolder(
            @RequestParam("targetObjectType") String targetObjectType,
            @RequestParam("targetObjectId") Long targetObjectId,
            @RequestParam("newPath") String newPath,
            Authentication authentication,
            HttpSession session) throws AcmCreateObjectFailedException, AcmUserActionFailedException, AcmObjectNotFoundException, AcmFolderException {
        /**
         * This API is documented in ark-document-management.raml.  If you update the API, also update the RAML.
         */

        String ipAddress = (String) session.getAttribute(AcmFolderConstants.IP_ADDRESS_ATTRIBUTE);

        if( log.isInfoEnabled() ) {
            log.info("Creating new folder by path" + newPath);
        }

        try {
            AcmFolder newFolder = getFolderService().addNewFolderByPath(targetObjectType, targetObjectId, newPath);
            if( log.isInfoEnabled() ) {
                log.info("Created new folder " + newFolder.getId());
            }
            getFolderEventPublisher().publishFolderCreatedEvent(newFolder,authentication,ipAddress,true);
            return newFolder;
        } catch (  AcmCreateObjectFailedException e) {
            if( log.isErrorEnabled() ){
                log.error("Exception occurred while trying to create a new folder by path",e);
            }
            getFolderEventPublisher().publishFolderCreatedEvent(null,authentication,ipAddress,false);
            throw e;
        } catch (AcmObjectNotFoundException e) {
            if( log.isErrorEnabled() ){
                log.error("Exception occurred while trying to create new folder by path ",e);
            }
            getFolderEventPublisher().publishFolderCreatedEvent(null, authentication, ipAddress, false);
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

