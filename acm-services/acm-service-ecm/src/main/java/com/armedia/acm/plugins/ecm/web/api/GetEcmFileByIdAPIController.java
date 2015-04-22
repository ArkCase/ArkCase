package com.armedia.acm.plugins.ecm.web.api;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.plugins.ecm.model.FileVersionsDTO;
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
 * Created by marjan.stefanoski on 22.04.2015.
 */
@Controller
@RequestMapping({"/api/v1/service/ecm", "/api/latest/service/ecm"})
public class GetEcmFileByIdAPIController {

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    private EcmFileService fileService;

    @RequestMapping(value = "/file/{fileId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public EcmFile getEcmFile(
            @PathVariable("fileId") Long fileId,
            Authentication authentication,
            HttpSession session ) throws AcmUserActionFailedException, AcmObjectNotFoundException {
        if( log.isInfoEnabled() ) {
            log.info("Fetching EcmFile with fileId: " + fileId);
        }
        EcmFile result = getFileService().findById(fileId);
        if (result == null ){
            if(log.isErrorEnabled()){
                log.error("File with fileId: "+ fileId + " does not exists");
            }
            throw new AcmObjectNotFoundException(EcmFileConstants.OBJECT_FILE_TYPE,fileId,"File not found",null);
        }
        return result;
    }

    public EcmFileService getFileService() {
        return fileService;
    }

    public void setFileService(EcmFileService fileService) {
        this.fileService = fileService;
    }
}
