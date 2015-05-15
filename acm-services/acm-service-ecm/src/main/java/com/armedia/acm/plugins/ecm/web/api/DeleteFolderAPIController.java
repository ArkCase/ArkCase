package com.armedia.acm.plugins.ecm.web.api;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.ecm.model.AcmDeletedFolderDto;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.AcmFolderConstants;
import com.armedia.acm.plugins.ecm.model.event.AcmFolderPersistenceEvent;
import com.armedia.acm.plugins.ecm.service.AcmFolderService;
import com.armedia.acm.plugins.ecm.service.FolderEventPublisher;
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
 * Created by marjan.stefanoski on 10.04.2015.
 */

@Controller
@RequestMapping({"/api/v1/service/ecm", "/api/latest/service/ecm"})
public class DeleteFolderAPIController {

    private AcmFolderService folderService;
    private FolderEventPublisher folderEventPublisher;


    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "/folder/{folderId}",method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AcmDeletedFolderDto deleteFolder(
            @PathVariable("folderId") Long folderId,
            Authentication authentication,
            HttpSession session) throws AcmUserActionFailedException {

        if(log.isInfoEnabled()) {
            log.info("Folder with id: "+folderId+" will be deleted");
        }

        String ipAddress = (String) session.getAttribute(AcmFolderConstants.IP_ADDRESS_ATTRIBUTE);

        AcmFolder source = getFolderService().findById(folderId);

        try {
            getFolderService().deleteFolderIfEmpty(folderId);
            if(log.isInfoEnabled()) {
                log.info("Folder with id: "+folderId+" successfully deleted");
            }
            getFolderEventPublisher().publishFolderDeletedEvent(source,authentication,ipAddress,false);
            return prepareResult(AcmFolderConstants.SUCCESS_FOLDER_DELETE_MSG,folderId);
        } catch (AcmObjectNotFoundException e) {
            if( log.isErrorEnabled() ){
                log.debug("Folder with id: " + folderId + " not found in the DB");
            }
            return prepareResult(AcmFolderConstants.SUCCESS_FOLDER_DELETE_MSG,folderId);
        } catch ( AcmUserActionFailedException e ) {
            if( log.isErrorEnabled() ){
                log.error("Exception occurred while trying to delete folder with id: " + folderId);
            }
            getFolderEventPublisher().publishFolderDeletedEvent(source,authentication,ipAddress,false);
            throw e;
        }
    }

    private AcmDeletedFolderDto prepareResult(String msg, Long folderId){
        AcmDeletedFolderDto result = new AcmDeletedFolderDto();
        result.setDeletedFolderId(Long.toString(folderId));
        result.setMessage(msg);
        return result;
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
