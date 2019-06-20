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
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.service.AcmFolderService;
import com.armedia.acm.plugins.ecm.service.FolderEventPublisher;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping({ "/api/v1/service/ecm", "/api/latest/service/ecm" })
public class RenameFolderAPIController
{

    private transient final Logger log = LogManager.getLogger(getClass());
    private AcmFolderService folderService;
    private FolderEventPublisher folderEventPublisher;

    @PreAuthorize("hasPermission(#objectId, 'FOLDER', 'write|group-write')")
    @RequestMapping(value = "/folder/{objectId}/{newName}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AcmFolder renameFolder(@PathVariable("objectId") Long objectId, @PathVariable("newName") String newName)
            throws AcmUserActionFailedException, AcmObjectNotFoundException, AcmFolderException
    {
        log.info("Renaming folder, folderId: {} with name: {}", objectId, newName);
        AcmFolder source = getFolderService().findById(objectId);
        try
        {
            AcmFolder renamedFolder = getFolderService().renameFolder(objectId, newName);
            log.info("Folder with id: {} successfully renamed to: {}", objectId, newName);
            AcmContainer container = getFolderService().findContainerByFolderId(renamedFolder.getId());

            getFolderEventPublisher().publishFolderRenamedEvent(renamedFolder, true, container.getContainerObjectType(),
                    container.getContainerObjectId());
            return renamedFolder;
        }
        catch (AcmUserActionFailedException | AcmFolderException | AcmObjectNotFoundException e)
        {
            log.error("Exception occurred while trying to rename folder with id: {}", objectId);
            getFolderEventPublisher().publishFolderRenamedEvent(source, false, null, null);
            throw e;
        }
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
