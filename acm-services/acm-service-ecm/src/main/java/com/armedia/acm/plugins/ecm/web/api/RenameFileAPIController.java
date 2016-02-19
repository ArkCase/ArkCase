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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;


/**
 * Created by marjan.stefanoski on 02.04.2015.
 */

@Controller
@RequestMapping({"/api/v1/service/ecm", "/api/latest/service/ecm"})
public class RenameFileAPIController
{

    private EcmFileService fileService;
    private FileEventPublisher fileEventPublisher;

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "/file/{objectId}/{newName}/{extension}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public EcmFile renameFile(
            @PathVariable("objectId") Long objectId,
            @PathVariable("newName") String name,
            @PathVariable("extension") String extension,
            Authentication authentication,
            HttpSession session) throws AcmUserActionFailedException
    {

        String newName = name + "." + extension;

        return getEcmFile(objectId, authentication, session, newName);
    }


    @RequestMapping(value = "/file/{objectId}/rename", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public EcmFile renameFileWithoutExt(
            @PathVariable("objectId") Long objectId,
            @RequestParam("newName") String newName,
            Authentication authentication,
            HttpSession session) throws AcmUserActionFailedException
    {

        return getEcmFile(objectId, authentication, session, newName);
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

    private EcmFile getEcmFile(Long objectId, Authentication authentication, HttpSession session, String newName) throws AcmUserActionFailedException
    {
        if (log.isInfoEnabled())
        {
            log.info("Renaming file, fileId: " + objectId + " with name " + newName);
        }
        String ipAddress = (String) session.getAttribute(EcmFileConstants.IP_ADDRESS_ATTRIBUTE);
        EcmFile source = getFileService().findById(objectId);
        if (source == null)
        {
            throw new AcmUserActionFailedException(EcmFileConstants.USER_ACTION_RENAME_FILE, EcmFileConstants.OBJECT_FILE_TYPE, objectId, "File not found.", null);
        }
        try
        {
            EcmFile renamedFile = getFileService().renameFile(objectId, newName);
            if (log.isInfoEnabled())
            {
                log.info("File with id: " + objectId + " successfully renamed to: " + newName);
            }
            getFileEventPublisher().publishFileRenamedEvent(renamedFile, authentication, ipAddress, true);
            return renamedFile;
        } catch (AcmUserActionFailedException e)
        {
            if (log.isErrorEnabled())
            {
                log.error("Exception occurred while trying to rename file with id: " + objectId);
            }
            getFileEventPublisher().publishFileRenamedEvent(source, authentication, ipAddress, false);
            throw e;
        } catch (AcmObjectNotFoundException e)
        {
            if (log.isErrorEnabled())
            {
                log.debug("File with id: " + objectId + " not found in the DB");
            }
            getFileEventPublisher().publishFileMovedEvent(source, authentication, ipAddress, false);
            throw new AcmUserActionFailedException(EcmFileConstants.USER_ACTION_RENAME_FILE, EcmFileConstants.OBJECT_FILE_TYPE, objectId, "File not found.", e);
        }
    }
}
