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
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.service.EcmFileService;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by riste.tutureski on 9/14/2015.
 */

@Controller
@RequestMapping({ "/api/v1/service/ecm", "/api/latest/service/ecm" })
public class UpdateFileTypeAPIController
{
    private transient final Logger log = LogManager.getLogger(getClass());
    private EcmFileService ecmFileService;

    @PreAuthorize("hasPermission(#fileId, 'FILE', 'write|group-write')")
    @RequestMapping(value = "/file/{fileId}/type/{fileType}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public EcmFile updateFileType(@PathVariable("fileId") Long fileId, @PathVariable("fileType") String fileType,
            Authentication authentication, HttpSession session) throws AcmObjectNotFoundException
    {

        log.debug("Updating file type to '{}'", fileType);

        EcmFile file = updateFileType(fileId, fileType);

        if (file == null)
        {
            throw new AcmObjectNotFoundException("EcmFile", fileId, "Cannot update file type.");
        }

        return file;
    }

    @RequestMapping(value = "/file/bulk/type/{fileType}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<EcmFile> bulkUpdateFileType(@RequestBody List<Long> fileIds, @PathVariable("fileType") String fileType,
            Authentication authentication, HttpSession session)
    {

        log.debug("Updating file type to '{}' for multiple files.", fileType);

        List<EcmFile> files = new ArrayList<>();

        if (fileIds != null)
        {
            files = fileIds.stream().map(fileId -> updateFileType(fileId, fileType)).filter(file -> file != null)
                    .collect(Collectors.toList());
        }

        return files;
    }

    private EcmFile updateFileType(Long fileId, String fileType)
    {
        EcmFile file = null;

        try
        {
            file = getEcmFileService().updateFileType(fileId, fileType);
        }
        catch (AcmObjectNotFoundException e)
        {
            log.error("Error wile updating file type: {}", e.getMessage(), e);
        }

        return file;
    }

    public EcmFileService getEcmFileService()
    {
        return ecmFileService;
    }

    public void setEcmFileService(EcmFileService ecmFileService)
    {
        this.ecmFileService = ecmFileService;
    }
}
