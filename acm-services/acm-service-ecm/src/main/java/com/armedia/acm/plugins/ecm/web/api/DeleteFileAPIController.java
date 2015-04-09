package com.armedia.acm.plugins.ecm.web.api;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.plugins.ecm.service.FileEventPublisher;
import org.activiti.engine.impl.util.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by marjan.stefanoski on 06.04.2015.
 */
@Controller
@RequestMapping({"/api/v1/service/ecm", "/api/latest/service/ecm"})
public class DeleteFileAPIController {

    private EcmFileService fileService;
    private FileEventPublisher fileEventPublisher;

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "/id/{fileId}",method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public String deleteFile(
            @PathVariable("fileId") Long objectId,
            Authentication authentication
    ) throws AcmUserActionFailedException {

        if(log.isInfoEnabled()) {
            log.info("File with id: "+objectId+" will be deleted");
        }
        EcmFile source = getFileService().findById(objectId);
        try {
            getFileService().deleteFile(objectId);
            if(log.isInfoEnabled()) {
                log.info("File with id: "+objectId+" successfully deleted");
            }
            getFileEventPublisher().publishFileDeletedEvent(source,authentication,true);
            return prepareJsonReturnMsg( EcmFileConstants.SUCCESS_DELETE_MSG,objectId, source.getFileName() );
        } catch ( AcmUserActionFailedException e ) {
            if( log.isErrorEnabled() ){
                log.error("Exception occurred while trying to delete file with id: " + objectId);
            }
            getFileEventPublisher().publishFileDeletedEvent(source,authentication,false);
            throw e;
        } catch ( AcmObjectNotFoundException e ) {
            if( log.isErrorEnabled() ){
                log.debug("File with id: " + objectId + " not found in the DB");
            }
            return prepareJsonReturnMsg(EcmFileConstants.SUCCESS_DELETE_MSG,objectId);
        }
    }

    private String prepareJsonReturnMsg(String msg, Long fileId, String fileName) {
        JSONObject objectToReturnJSON = new JSONObject();
        objectToReturnJSON.put("deletedFileId", fileId);
        objectToReturnJSON.put("name",fileName);
        objectToReturnJSON.put("Message", msg);
        String objectToReturn;
        objectToReturn = objectToReturnJSON.toString();
        return objectToReturn;
    }
    private String prepareJsonReturnMsg(String msg, Long fileId) {
        JSONObject objectToReturnJSON = new JSONObject();
        objectToReturnJSON.put("deletedFileId", fileId);
        objectToReturnJSON.put("Message", msg);
        String objectToReturn;
        objectToReturn = objectToReturnJSON.toString();
        return objectToReturn;
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
