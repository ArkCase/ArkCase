package com.armedia.acm.plugins.ecm.web.api;

import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.plugins.ecm.service.FileEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * Created by manoj.dhungana 04/10/2017.
 */

@Controller
@RequestMapping({"/api/v1/service/ecm", "/api/latest/service/ecm"})
public class UpdateFileAPIController
{

    private EcmFileService ecmFileService;
    private FileEventPublisher fileEventPublisher;

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @PreAuthorize("hasPermission(#parentObjectId, #parentObjectType, 'uploadOrReplaceFile')")
    @RequestMapping(value = "/file/update",  method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public EcmFile updateFileType(
            @RequestBody EcmFile file,
            Authentication authentication) throws AcmUserActionFailedException {

        if ( file == null || file.getId() == null) {
            log.error("Invalid incoming file [{}]", file.toString());
            throw new AcmUserActionFailedException(EcmFileConstants.USER_ACTION_UPDATE_FILE, EcmFileConstants.OBJECT_FILE_TYPE, null, "Invalid incoming file", null);
        }else{
            log.debug("Incoming file id to be updated [{}]", file.getId());
            file = getEcmFileService().updateFile(file, authentication);
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

    public FileEventPublisher getFileEventPublisher() {
        return fileEventPublisher;
    }

    public void setFileEventPublisher(FileEventPublisher fileEventPublisher) {
        this.fileEventPublisher = fileEventPublisher;
    }
}
