package com.armedia.acm.plugins.ecm.web.api;

import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.ecm.model.AcmFolderConstants;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.persistence.PersistenceException;
import javax.servlet.http.HttpSession;

/**
 * Created by marjan.stefanoski on 22.04.2015.
 */
@Controller
@RequestMapping({ "/api/v1/service/ecm", "/api/latest/service/ecm" })
public class SetActiveFileVersionAPIController
{

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    private EcmFileService fileService;
    private FileEventPublisher fileEventPublisher;

    @PreAuthorize("hasPermission(#fileId, 'FILE', 'write|group-write')")
    @RequestMapping(value = "/file/{fileId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public EcmFile setFileActiveVersion(@PathVariable("fileId") Long fileId,
            @RequestParam(value = "versionTag", required = true) String versionTag, Authentication authentication, HttpSession session)
            throws AcmUserActionFailedException
    {
        log.info("Version: {}  will be set as active version for file with fileId: {}", versionTag, fileId);

        String ipAddress = (String) session.getAttribute(AcmFolderConstants.IP_ADDRESS_ATTRIBUTE);

        EcmFile source = getFileService().findById(fileId);
        if (source == null)
        {
            log.error("File with fileId: {} not found in the DB", fileId);

            getFileEventPublisher().publishFileActiveVersionSetEvent(source, authentication, ipAddress, false);
            throw new AcmUserActionFailedException(EcmFileConstants.USER_ACTION_SET_FILE_ACTIVE_VERSION, EcmFileConstants.OBJECT_FILE_TYPE,
                    fileId, "File with fileId: " + fileId + " not found in the DB", null);
        }
        EcmFile result;
        try
        {
            result = getFileService().setFilesActiveVersion(fileId, versionTag);
            getFileEventPublisher().publishFileActiveVersionSetEvent(result, authentication, ipAddress, true);
        }
        catch (PersistenceException e)
        {
            log.error("Exception occurred while updating active version on file with fileId: {} with error: {}", fileId, e.getMessage(), e);

            getFileEventPublisher().publishFileActiveVersionSetEvent(source, authentication, ipAddress, false);
            throw new AcmUserActionFailedException(EcmFileConstants.USER_ACTION_SET_FILE_ACTIVE_VERSION, EcmFileConstants.OBJECT_FILE_TYPE,
                    fileId, "Exception occurred while updating active version on file with fileId: " + fileId, e);
        }
        return result;
    }

    public FileEventPublisher getFileEventPublisher()
    {
        return fileEventPublisher;
    }

    public void setFileEventPublisher(FileEventPublisher fileEventPublisher)
    {
        this.fileEventPublisher = fileEventPublisher;
    }

    public EcmFileService getFileService()
    {
        return fileService;
    }

    public void setFileService(EcmFileService fileService)
    {
        this.fileService = fileService;
    }
}
