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

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
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
public class AddNewFolderAPIController
{

    private transient final Logger log = LogManager.getLogger(getClass());
    private AcmFolderService folderService;
    private FolderEventPublisher folderEventPublisher;

    @PreAuthorize("hasPermission(#parentFolderId, 'FOLDER', 'write|group-write')")
    @RequestMapping(value = "/folder/{parentFolderId}/{newFolderName}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AcmFolder addNewFolder(@PathVariable("parentFolderId") Long parentFolderId, @PathVariable("newFolderName") String newFolderName)
            throws AcmCreateObjectFailedException, AcmUserActionFailedException, AcmObjectNotFoundException
    {
        /**
         * This API is documented in ark-document-management.raml. If you update the API, also update the RAML.
         */

        log.info("Creating new folder into: {} with name: {}", parentFolderId, newFolderName);
        try
        {
            AcmContainer container = getFolderService().findContainerByFolderId(parentFolderId);
            AcmFolder newFolder = getFolderService().addNewFolder(parentFolderId, newFolderName, container.getContainerObjectId(),
                    container.getContainerObjectType());

            log.info("Created new folder: {} with name: {}", newFolder.getId(), newFolderName);
            getFolderEventPublisher().publishFolderCreatedEvent(newFolder, true, container.getContainerObjectType(),
                    container.getContainerObjectId());
            return newFolder;
        }
        catch (AcmCreateObjectFailedException | AcmObjectNotFoundException e)
        {
            log.error("Exception occurred while trying to create new folder: {} ", newFolderName, e);
            // create mock source to audit the event
            AcmFolder mockFolder = new AcmFolder();
            mockFolder.setName(newFolderName);
            getFolderEventPublisher().publishFolderCreatedEvent(mockFolder, false, null, null);
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
