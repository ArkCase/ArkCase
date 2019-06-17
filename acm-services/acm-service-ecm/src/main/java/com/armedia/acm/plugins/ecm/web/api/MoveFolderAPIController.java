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
import com.armedia.acm.plugins.ecm.exception.AcmFolderException;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.AcmFolderConstants;
import com.armedia.acm.plugins.ecm.service.AcmFolderService;
import com.armedia.acm.plugins.ecm.service.FolderEventPublisher;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by marjan.stefanoski on 20.04.2015.
 */
@Controller
@RequestMapping({ "/api/v1/service/ecm", "/api/latest/service/ecm" })
public class MoveFolderAPIController
{

    private transient final Logger log = LogManager.getLogger(getClass());
    private AcmFolderService folderService;
    private FolderEventPublisher folderEventPublisher;

    @PreAuthorize("hasPermission(#folderToMoveId, 'FOLDER', 'read|group-read|write|group-write') and hasPermission(#dstFolderId, 'FOLDER', 'write|group-write')")
    @RequestMapping(value = "/folder/move/{folderToMoveId}/{dstFolderId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AcmFolder moveFolder(@PathVariable("folderToMoveId") Long folderToMoveId, @PathVariable("dstFolderId") Long dstFolderId,
            Authentication authentication, HttpSession session) throws AcmUserActionFailedException
    {
        if (log.isInfoEnabled())
        {
            log.info("Folder with id: " + folderToMoveId + " will be moved to the location with id: " + dstFolderId);
        }
        String ipAddress = (String) session.getAttribute(AcmFolderConstants.IP_ADDRESS_ATTRIBUTE);
        AcmFolder sourceFolderToBeMoved = getFolderService().findById(folderToMoveId);
        AcmFolder sourceDstFolder = getFolderService().findById(dstFolderId);
        try
        {
            AcmFolder movedFolder = getFolderService().moveFolder(sourceFolderToBeMoved, sourceDstFolder);
            if (log.isInfoEnabled())
            {
                log.info("Folder with id: " + folderToMoveId + " successfully moved to the location with id: " + dstFolderId);
            }
            getFolderEventPublisher().publishFolderMovedEvent(movedFolder, authentication, ipAddress, true);
            getFolderEventPublisher().publishFolderCreatedEvent(sourceDstFolder, authentication, ipAddress, true);
            return movedFolder;
        }
        catch (AcmUserActionFailedException e)
        {
            if (log.isErrorEnabled())
            {
                log.error("Exception occurred while trying to move folder with id: " + folderToMoveId + " to the location with id: "
                        + dstFolderId);
            }
            getFolderEventPublisher().publishFolderMovedEvent(sourceFolderToBeMoved, authentication, ipAddress, false);
            throw e;
        }
        catch (AcmObjectNotFoundException e)
        {
            if (log.isErrorEnabled())
            {
                log.debug("Folder with id: " + e.getObjectId() + " not found in the DB");
            }
            getFolderEventPublisher().publishFolderMovedEvent(sourceFolderToBeMoved, authentication, ipAddress, false);
            throw new AcmUserActionFailedException(AcmFolderConstants.USER_ACTION_MOVE_FOLDER, AcmFolderConstants.OBJECT_FOLDER_TYPE,
                    folderToMoveId, e.getMessage(), e);
        }
        catch (AcmFolderException e)
        {
            if (log.isErrorEnabled())
            {
                log.error("Exception occurred while trying to move folder with id: " + folderToMoveId + " to the location with id:"
                        + dstFolderId);
            }
            getFolderEventPublisher().publishFolderMovedEvent(sourceFolderToBeMoved, authentication, ipAddress, false);
            throw new AcmUserActionFailedException(AcmFolderConstants.USER_ACTION_MOVE_FOLDER, AcmFolderConstants.OBJECT_FOLDER_TYPE,
                    folderToMoveId, "Exception occurred while trying to move the folder.", e);
        }
    }

    public AcmFolderService getFolderService()
    {
        return folderService;
    }

    public void setFolderService(AcmFolderService folderService)
    {
        this.folderService = folderService;
    }

    public FolderEventPublisher getFolderEventPublisher()
    {
        return folderEventPublisher;
    }

    public void setFolderEventPublisher(FolderEventPublisher folderEventPublisher)
    {
        this.folderEventPublisher = folderEventPublisher;
    }
}
