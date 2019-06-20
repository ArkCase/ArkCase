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
import com.armedia.acm.plugins.ecm.model.AcmDeletedFolderDto;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.AcmFolderConstants;
import com.armedia.acm.plugins.ecm.model.DeleteFolderInfo;
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

@Controller
@RequestMapping({ "/api/v1/service/ecm", "/api/latest/service/ecm" })
public class DeleteFolderAPIController
{

    private transient final Logger log = LogManager.getLogger(getClass());
    private AcmFolderService folderService;
    private FolderEventPublisher folderEventPublisher;

    @PreAuthorize("hasPermission(#folderId, 'FOLDER', 'write|group-write')")
    @RequestMapping(value = "/folder/{folderId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AcmDeletedFolderDto deleteFolder(@PathVariable("folderId") Long folderId, Authentication authentication, HttpSession session)
            throws AcmUserActionFailedException
    {

        log.info("Folder with id: [{}] will be deleted", folderId);

        String ipAddress = (String) session.getAttribute(AcmFolderConstants.IP_ADDRESS_ATTRIBUTE);

        AcmFolder source = getFolderService().findById(folderId);

        try
        {
            getFolderService().deleteFolderTreeSafe(folderId, authentication);
            log.info("Folder with id: [{}] successfully deleted", folderId);
            getFolderEventPublisher().publishFolderDeletedEvent(source, authentication, ipAddress, false);
            return prepareResult(AcmFolderConstants.SUCCESS_FOLDER_DELETE_MSG, folderId);
        }
        catch (AcmObjectNotFoundException e)
        {
            log.debug("Folder with id: [{}] not found in the DB", folderId);
            return prepareResult(AcmFolderConstants.SUCCESS_FOLDER_DELETE_MSG, folderId);
        }
        catch (AcmUserActionFailedException e)
        {
            log.error("Exception occurred while trying to delete folder with id: [{}]", folderId, e);
            getFolderEventPublisher().publishFolderDeletedEvent(source, authentication, ipAddress, false);
            throw e;
        }
    }

    @RequestMapping(value = "/folder/{folderId}/count", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public DeleteFolderInfo folderDocumentCountList(@PathVariable("folderId") Long folderId) throws AcmObjectNotFoundException
    {
        return getFolderService().getFolderToDeleteInfo(folderId);
    }

    private AcmDeletedFolderDto prepareResult(String msg, Long folderId)
    {
        AcmDeletedFolderDto result = new AcmDeletedFolderDto();
        result.setDeletedFolderId(Long.toString(folderId));
        result.setMessage(msg);
        return result;
    }

    public FolderEventPublisher getFolderEventPublisher()
    {
        return folderEventPublisher;
    }

    public void setFolderEventPublisher(FolderEventPublisher folderEventPublisher)
    {
        this.folderEventPublisher = folderEventPublisher;
    }

    public AcmFolderService getFolderService()
    {
        return folderService;
    }

    public void setFolderService(AcmFolderService folderService)
    {
        this.folderService = folderService;
    }
}
