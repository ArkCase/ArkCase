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
 * Created by bojan.mickoski on 2/29/2016.
 */

@Controller
@RequestMapping({"/api/v1/service/ecm", "/api/latest/service/ecm"})
public class UnlockFileAPIController {

    private EcmFileService fileService;
    private FileEventPublisher fileEventPublisher;

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "/file/unlock/{fileId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public EcmFile unlockFile(
            @PathVariable("fileId") Long fileId,
            Authentication authentication,
            HttpSession session) throws AcmUserActionFailedException, AcmObjectNotFoundException {
        String ipAddress = (String) session.getAttribute(EcmFileConstants.IP_ADDRESS_ATTRIBUTE);

        try {
            EcmFile file = getFileService().findById(fileId);
            EcmFile unlockFile = getFileService().unlock(file, authentication);

            if (log.isInfoEnabled()) {
                log.info("File with id: " + fileId + " successfully unlock.");
            }

            getFileEventPublisher().publishFileUnLockEvent(unlockFile, authentication, ipAddress, true);

            return unlockFile;
        } catch (AcmObjectNotFoundException e) {
            throw e;
        }
    }

    public EcmFileService getFileService() {
        return fileService;
    }

    public void setFileService(EcmFileService fileService) {
        this.fileService = fileService;
    }

    public FileEventPublisher getFileEventPublisher() {
        return fileEventPublisher;
    }

    public void setFileEventPublisher(FileEventPublisher fileEventPublisher) {
        this.fileEventPublisher = fileEventPublisher;
    }
}
