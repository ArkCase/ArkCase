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
import com.armedia.acm.plugins.ecm.exception.AcmFolderException;
import com.armedia.acm.plugins.ecm.exception.LinkAlreadyExistException;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.AcmFolderConstants;
import com.armedia.acm.plugins.ecm.model.FolderDTO;
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
 * Created by martin.jovanovski on 20.02.2020.
 */
@Controller
@RequestMapping({ "/api/v1/service/ecm", "/api/latest/service/ecm" })
public class CopyFolderAsLinkAPIController
{

    private transient final Logger log = LogManager.getLogger(getClass());

    private AcmFolderService folderService;
    private FolderEventPublisher folderEventPublisher;

    @PreAuthorize("hasPermission(#folderId, 'FOLDER', 'read|group-read|write|group-write') and hasPermission(#dstFolderId, 'FOLDER', 'write|group-write')")
    @RequestMapping(value = "/folder/copyAsLink/{folderId}/{dstFolderId}/{targetObjectType}/{targetObjectId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public FolderDTO copyFolderAsLink(@PathVariable("folderId") Long folderId, @PathVariable("dstFolderId") Long dstFolderId,
            @PathVariable("targetObjectType") String targetObjectType, @PathVariable("targetObjectId") Long targetObjectId,
            Authentication authentication, HttpSession session) throws AcmUserActionFailedException, LinkAlreadyExistException
    {

        log.debug("Folder with id: {} will be a link into folder with id: {}", folderId, dstFolderId);

        String ipAddress = (String) session.getAttribute(AcmFolderConstants.IP_ADDRESS_ATTRIBUTE);
        AcmFolder source = getFolderService().findById(folderId);
        AcmFolder dstFolder = getFolderService().findById(dstFolderId);

        try
        {

            log.debug("A Link for folder with id: {} was successfully created to the location with id: {}", folderId, dstFolderId);

            AcmFolder folder = getFolderService().copyFolderAsLink(source, dstFolder, targetObjectId, targetObjectType);
            getFolderEventPublisher().publishFolderCopiedAsLinkEvent(source, authentication, ipAddress, true);
            FolderDTO folderDTO = new FolderDTO();
            folderDTO.setOriginalFolderId(folderId);
            folderDTO.setNewFolder(folder);
            folderDTO.setLink(folder.isLink());
            return folderDTO;
        }
        catch (AcmObjectNotFoundException e)
        {

            log.error("Exception occurred while trying to copy folder with id: {} to the location with id: {}" + e.getMessage(), folderId,
                    dstFolderId, e);

            getFolderEventPublisher().publishFolderCopiedAsLinkEvent(source, authentication, ipAddress, false);
            throw new AcmUserActionFailedException(AcmFolderConstants.USER_ACTION_COPY_FOLDER_AS_LINK,
                    AcmFolderConstants.OBJECT_FOLDER_TYPE,
                    source.getId(), "Exception occurred while trying to copy folder " + e.getMessage(), e);
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
