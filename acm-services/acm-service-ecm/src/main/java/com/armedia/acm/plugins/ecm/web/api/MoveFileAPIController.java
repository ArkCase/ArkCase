package com.armedia.acm.plugins.ecm.web.api;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.plugins.ecm.model.MoveCopyFileDto;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by marjan.stefanoski on 06.04.2015.
 */
@Controller
@RequestMapping({"/api/v1/service/ecm", "/api/latest/service/ecm"})
public class MoveFileAPIController {

    private EcmFileService fileService;

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "/move", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public EcmFile moveFile(
            @RequestParam MoveCopyFileDto in,
            Authentication authentication
    ) throws AcmUserActionFailedException {

        if(log.isInfoEnabled()) {
            log.info("File with id: "+in.getId()+" will be moved to the location "+in.getPath());
        }

        try {
            EcmFile movedFile = getFileService().moveFile(in.getId(),in.getPath());
            if(log.isInfoEnabled()) {
                log.info("File with id: "+in.getId()+" successfully moved to the location "+in.getPath());
            }
            return movedFile;
        } catch (AcmUserActionFailedException e) {
            if( log.isErrorEnabled() ){
                log.error("Exception occurred while trying to move file with id: " + in.getId() +" to the location "+ in.getPath());
            }
            throw e;
        } catch ( AcmObjectNotFoundException e ) {
            if (log.isErrorEnabled()) {
                log.debug("File with id: " + in.getId() + " not found in the DB");
            }
            throw new AcmUserActionFailedException(EcmFileConstants.USER_ACTION_MOVE_FILE,EcmFileConstants.OBJECT_FILE_TYPE,in.getId(),"File not found.",e);
        }
    }

    public EcmFileService getFileService() {
        return fileService;
    }

    public void setFileService(EcmFileService fileService) {
        this.fileService = fileService;
    }
}
