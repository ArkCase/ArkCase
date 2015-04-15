package com.armedia.acm.plugins.ecm.web.api;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmListObjectsFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.ecm.model.AcmCmisObjectList;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.AcmFolderConstants;
import com.armedia.acm.plugins.ecm.service.AcmFolderService;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


/**
 * Created by marjan.stefanoski on 14.04.2015.
 */

@Controller
@RequestMapping({ "/api/v1/service/ecm", "/api/latest/service/ecm" })
public class SubFolderListAPIController {

    private AcmFolderService acmFolderService;
    private EcmFileService fileService;

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @PreAuthorize("hasPermission(#objectId, #objectType, 'read')")
    @RequestMapping(value = "/folder/{objectType}/{objectId}/{folderId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AcmCmisObjectList listFolderChildren(
            @PathVariable("objectType") String objectType,
            @PathVariable("objectId") Long objectId,
            @PathVariable("folderId") Long folderId,
            @RequestParam(value = "s", required = false, defaultValue = "name") String sortBy,
            @RequestParam(value = "dir", required = false, defaultValue = "ASC") String sortDirection,
            @RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "1000") int maxRows,
            @RequestParam(value = "category", required = false) String category,
            Authentication authentication) throws AcmListObjectsFailedException, AcmUserActionFailedException, AcmCreateObjectFailedException {

        if( log.isInfoEnabled() ) {
            log.info("Getting children for the folder with id:  " + folderId);
        }

        AcmContainer container = getFileService().getOrCreateContainer(objectType, objectId);

        // the special category "all" should not be sent to Solr
        category = "all".equals(category) ? null : category;

        AcmCmisObjectList objectList;
        try {
             objectList = getFileService().lsitAllSubFolderChildren(category,authentication,container,folderId);
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

    public EcmFileService getFileService() {
        return fileService;
    }

    public void setFileService(EcmFileService fileService) {
        this.fileService = fileService;
    }
}
