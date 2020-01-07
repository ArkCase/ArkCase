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

import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.ecm.model.AcmFolderConstants;
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

import javax.persistence.PersistenceException;
import javax.servlet.http.HttpSession;

/**
 * Created by marjan.stefanoski on 22.04.2015.
 */
@Controller
@RequestMapping({ "/api/v1/service/ecm", "/api/latest/service/ecm" })
public class SetActiveFileVersionAPIController
{

    private transient final Logger log = LogManager.getLogger(getClass());

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
