package com.armedia.acm.plugins.ecm.web.api;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.plugins.ecm.model.MoveCopyFileDto;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.plugins.ecm.service.FileEventPublisher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by marjan.stefanoski on 06.04.2015.
 */
@Controller
@RequestMapping({ "/api/v1/service/ecm", "/api/latest/service/ecm" })
public class MoveFileAPIController
{

    private EcmFileService fileService;
    private FileEventPublisher fileEventPublisher;

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "/moveToAnotherContainer/{targetObjectType}/{targetObjectId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public EcmFile moveFile(
            @RequestBody MoveCopyFileDto in,
            @PathVariable("targetObjectType") String targetObjectType,
            @PathVariable("targetObjectId") Long targetObjectId,
            Authentication authentication,
            HttpSession session) throws AcmUserActionFailedException
    {

        if (log.isInfoEnabled())
        {
            log.info("File with id: " + in.getId() + " will be moved to the location with id: " + in.getFolderId());
        }
        String ipAddress = (String) session.getAttribute(EcmFileConstants.IP_ADDRESS_ATTRIBUTE);
        EcmFile source = getFileService().findById(in.getId());
        try
        {
            EcmFile movedFile = getFileService().moveFile(in.getId(), targetObjectId, targetObjectType, in.getFolderId());
            if (log.isInfoEnabled())
            {
                log.info("File with id: " + in.getId() + " successfully moved to the location with id: " + in.getFolderId());
            }
            getFileEventPublisher().publishFileMovedEvent(movedFile, authentication, ipAddress, true);
            return movedFile;
        }
        catch (AcmUserActionFailedException e)
        {
            if (log.isErrorEnabled())
            {
                log.error("Exception occurred while trying to move file with id: " + in.getId() + " to the location with id: "
                        + in.getFolderId());
            }
            getFileEventPublisher().publishFileMovedEvent(source, authentication, ipAddress, false);
            throw e;
        }
        catch (AcmObjectNotFoundException e)
        {
            if (log.isErrorEnabled())
            {
                log.debug("File with id: " + in.getId() + " not found in the DB");
            }
            getFileEventPublisher().publishFileMovedEvent(source, authentication, ipAddress, false);
            throw new AcmUserActionFailedException(EcmFileConstants.USER_ACTION_MOVE_FILE, EcmFileConstants.OBJECT_FILE_TYPE, in.getId(),
                    "File not found.", e);
        }
        catch (AcmCreateObjectFailedException e)
        {
            if (log.isErrorEnabled())
            {
                log.error("Exception occurred while trying to move file with id: " + in.getId() + " to the location with id:"
                        + in.getFolderId());
            }
            getFileEventPublisher().publishFileMovedEvent(source, authentication, ipAddress, false);
            throw new AcmUserActionFailedException(EcmFileConstants.USER_ACTION_MOVE_FILE, EcmFileConstants.OBJECT_FILE_TYPE, in.getId(),
                    "Exception occurred while trying to move the file.", e);
        }
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
