package com.armedia.acm.plugins.ecm.web.api;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.ecm.model.AcmCmisObjectList;
import com.armedia.acm.plugins.ecm.model.AcmFolderConstants;
import com.armedia.acm.plugins.ecm.service.AcmFolderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * Created by marjan.stefanoski on 14.04.2015.
 */

@Controller
@RequestMapping({ "/api/v1/service/ecm", "/api/latest/service/ecm" })
public class SubFolderListAPIController {

    private AcmFolderService acmFolderService;

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "/folder/{objectType}/{objectId}/{folderId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AcmCmisObjectList listFolderChildren(
            @PathVariable("objectType") String objectType,
            @PathVariable("objectId") Long objectId,
            @PathVariable("folderId") Long folderId,
            Authentication authentication) throws AcmUserActionFailedException {

        if( log.isInfoEnabled() ) {
            log.info("Getting children for the folder with id:  " + folderId);
        }
        AcmCmisObjectList objectList;
        try {
             objectList = getAcmFolderService().getFolderChildren(objectType,objectId,folderId);
            if( log.isInfoEnabled() ) {
                log.info("Children of the folder with id: " + folderId + " retrieved successfully");
            }
        } catch ( AcmObjectNotFoundException e ) {
            if ( log.isErrorEnabled() ) {
                log.debug("Folder with id: " + folderId + " not found in the DB");
            }
            throw new AcmUserActionFailedException(AcmFolderConstants.USER_ACTION_LIST_FOLDER, AcmFolderConstants.OBJECT_FOLDER_TYPE,folderId,"Folder not found.",e);
        }
        return objectList;
    }

    public AcmFolderService getAcmFolderService() {
        return acmFolderService;
    }

    public void setAcmFolderService(AcmFolderService acmFolderService) {
        this.acmFolderService = acmFolderService;
    }
}
