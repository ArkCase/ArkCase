package com.armedia.acm.plugins.ecm.web.api;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.ecm.model.AcmFolderConstants;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.plugins.ecm.model.EcmFileVersion;
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

import javax.persistence.PersistenceException;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Created by marjan.stefanoski on 22.04.2015.
 */
@Controller
@RequestMapping({"/api/v1/service/ecm", "/api/latest/service/ecm"})
public class SetActiveFileVersionAPIController {

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    private EcmFileService fileService;
    private FileEventPublisher fileEventPublisher;

    @RequestMapping(value = "/file/{fileId}/{versionTag}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public EcmFile setFileActiveVersion(
            @PathVariable("fileId") Long fileId,
            @PathVariable("versionTag") String versionTag,
            Authentication authentication,
            HttpSession session ) throws AcmUserActionFailedException {
        if( log.isInfoEnabled() ) {
            log.info("Version: " + versionTag+" will be set as active version for file with fileId: " + fileId);
        }

        String ipAddress = (String) session.getAttribute(AcmFolderConstants.IP_ADDRESS_ATTRIBUTE);

        EcmFile source = getFileService().findById( fileId );
        if ( source == null ) {
            if (log.isErrorEnabled()){
                log.error("File with fileId: "+fileId+" not found in the DB");
            }
            getFileEventPublisher().publishFileActiveVersionSetEvent(source,authentication,ipAddress,false);
            throw new AcmUserActionFailedException(EcmFileConstants.USER_ACTION_SET_FILE_ACTIVE_VERSION,EcmFileConstants.OBJECT_FILE_TYPE,fileId,"File with fileId: "+fileId+" not found in the DB",null);
        }
        EcmFile result;
        try {
            result = getFileService().setFilesActiveVersion(fileId,versionTag);
            getFileEventPublisher().publishFileActiveVersionSetEvent(result,authentication,ipAddress,true);
        } catch (PersistenceException e) {
            if (log.isErrorEnabled()){
                log.error("Exception occurred while updating active version on file with fileId: "+fileId+" "+e.getMessage(),e);
            }
            getFileEventPublisher().publishFileActiveVersionSetEvent(source,authentication,ipAddress,false);
            throw new AcmUserActionFailedException(EcmFileConstants.USER_ACTION_SET_FILE_ACTIVE_VERSION,EcmFileConstants.OBJECT_FILE_TYPE,fileId,"Exception occurred while updating active version on file with fileId: "+fileId,e);
        }
        return result;
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
