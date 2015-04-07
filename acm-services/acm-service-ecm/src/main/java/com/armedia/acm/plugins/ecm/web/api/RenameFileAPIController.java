package com.armedia.acm.plugins.ecm.web.api;

import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.ecm.model.*;
import com.armedia.acm.plugins.ecm.service.AcmFolderService;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.plugins.ecm.service.FileEventPublisher;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import org.activiti.engine.impl.util.json.JSONObject;
import org.activiti.engine.impl.util.json.JSONString;
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
public class RenameFileAPIController {

    private EcmFileService fileService;
    private FileEventPublisher fileEventPublisher;

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "/file/{objectId}/{newName}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public EcmFile renameFile(
            @PathVariable("objectId") Long objectId,
            @PathVariable("newName") String newName,
            Authentication authentication) throws AcmUserActionFailedException  {

        if( log.isInfoEnabled() ) {
            log.info("Renaming file, fileId: " + objectId + " with name " + newName);
        }
        EcmFile source = getFileService().findById(objectId);
        try {
            EcmFile renamedFile = getFileService().renameFile(objectId, newName);
            if(log.isInfoEnabled()) {
                log.info("File with id: "+objectId+" successfully renamed to: " +newName);
            }
            getFileEventPublisher().publishFileRenamedEvent(renamedFile,authentication,true);
            return renamedFile;
        } catch (AcmUserActionFailedException e) {
            if( log.isErrorEnabled() ){
                log.error("Exception occurred while trying to rename file with id: " + objectId);
            }
            getFileEventPublisher().publishFileRenamedEvent(source,authentication,false);
            throw e;
        }
    }

    public FileEventPublisher getFileEventPublisher() {
        return fileEventPublisher;
    }

    public void setFileEventPublisher(FileEventPublisher fileEventPublisher) {
        this.fileEventPublisher = fileEventPublisher;
    }

    public EcmFileService getFileService() {
        return fileService;
    }

    public void setFileService(EcmFileService fileService) {
        this.fileService = fileService;
    }
}
