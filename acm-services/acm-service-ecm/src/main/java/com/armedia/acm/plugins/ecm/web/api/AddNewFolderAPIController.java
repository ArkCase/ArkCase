package com.armedia.acm.plugins.ecm.web.api;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Created by marjan.stefanoski on 02.04.2015.
 */

@RequestMapping({"/api/v1/service/ecm", "/api/latest/service/ecm"})
public class AddNewFolderAPIController {

    private EcmFileService ecmFileService;

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping( method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AcmFolder addNewFolder(
            @RequestParam(value = "parentFolderPath", required = true ) String parentFolderPath,
            @RequestParam(value = "folderName", required = true ) String folderName,
            Authentication authentication) throws AcmCreateObjectFailedException {

        return getEcmFileService().addNewFolder(parentFolderPath, folderName);
    }

    public EcmFileService getEcmFileService() {
        return ecmFileService;
    }

    public void setEcmFileService(EcmFileService ecmFileService) {
        this.ecmFileService = ecmFileService;
    }
}
