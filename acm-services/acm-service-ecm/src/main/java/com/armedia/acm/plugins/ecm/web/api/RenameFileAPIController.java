package com.armedia.acm.plugins.ecm.web.api;

/*-
 * #%L
 * ACM Service: Enterprise Content Management
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.plugins.ecm.service.FileEventPublisher;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
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
@RequestMapping({ "/api/v1/service/ecm", "/api/latest/service/ecm" })
public class RenameFileAPIController
{

    private transient final Logger log = LogManager.getLogger(getClass());
    private EcmFileService fileService;
    private FileEventPublisher fileEventPublisher;

    @PreAuthorize("hasPermission(#objectId, 'FILE', 'write|group-write')")
    @RequestMapping(value = "/file/{objectId}/{newName}/{extension}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public EcmFile renameFile(@PathVariable("objectId") Long objectId, @PathVariable("newName") String name,
            @PathVariable("extension") String extension, Authentication authentication, HttpSession session)
            throws AcmUserActionFailedException
    {

        String newName = name + "." + extension;

        return getEcmFile(objectId, authentication, session, newName);
    }

    @PreAuthorize("hasPermission(#objectId, 'FILE', 'write|group-write')")
    @RequestMapping(value = "/file/{objectId}/rename", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public EcmFile renameFileWithoutExt(@PathVariable("objectId") Long objectId, @RequestParam("newName") String newName,
            Authentication authentication, HttpSession session) throws AcmUserActionFailedException
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

    private EcmFile getEcmFile(Long objectId, Authentication authentication, HttpSession session, String newName)
            throws AcmUserActionFailedException
    {
        if (log.isInfoEnabled())
        {
            log.info("Renaming file, fileId: " + objectId + " with name " + newName);
        }
        String ipAddress = (String) session.getAttribute(EcmFileConstants.IP_ADDRESS_ATTRIBUTE);
        EcmFile source = getFileService().findById(objectId);
        if (source == null)
        {
            throw new AcmUserActionFailedException(EcmFileConstants.USER_ACTION_RENAME_FILE, EcmFileConstants.OBJECT_FILE_TYPE, objectId,
                    "File not found.", null);
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
        }
        catch (AcmUserActionFailedException e)
        {
            if (log.isErrorEnabled())
            {
                log.error("Exception occurred while trying to rename file with id: " + objectId);
            }
            getFileEventPublisher().publishFileRenamedEvent(source, authentication, ipAddress, false);
            throw e;
        }
        catch (AcmObjectNotFoundException e)
        {
            if (log.isErrorEnabled())
            {
                log.debug("File with id: " + objectId + " not found in the DB");
            }
            getFileEventPublisher().publishFileMovedEvent(source, authentication, ipAddress, false);
            throw new AcmUserActionFailedException(EcmFileConstants.USER_ACTION_RENAME_FILE, EcmFileConstants.OBJECT_FILE_TYPE, objectId,
                    "File not found.", e);
        }
    }
}
