package com.armedia.acm.plugins.ecm.web.api;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.plugins.ecm.service.FileEventPublisher;
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
 * Created by riste.tutureski on 9/14/2015.
 */

@Controller
@RequestMapping({"/api/v1/service/ecm", "/api/latest/service/ecm"})
public class UpdateFileTypeAPIController
{
    private EcmFileService ecmFileService;

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "/file/{fileId}/type/{fileType}",  method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public EcmFile updateFileType(
            @PathVariable("fileId") Long fileId,
            @PathVariable("fileType") String fileType,
            Authentication authentication,
            HttpSession session) throws AcmObjectNotFoundException {


        log.debug("Updating file type to '" + fileType + "'");

        EcmFile file = null;

        try
        {
            file = getEcmFileService().updateFileType(fileId, fileType);
        }
        catch (AcmObjectNotFoundException e)
        {
            throw e;
        }

        return file;
    }

    public EcmFileService getEcmFileService()
    {
        return ecmFileService;
    }

    public void setEcmFileService(EcmFileService ecmFileService)
    {
        this.ecmFileService = ecmFileService;
    }
}
