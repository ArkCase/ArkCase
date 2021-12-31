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

import com.armedia.acm.camelcontext.exception.ArkCaseFileRepositoryException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@RequestMapping({ "/api/v1/plugin/ecm", "/api/latest/plugin/ecm" })
public class FileDownloadAPIController
{

    private Logger log = LogManager.getLogger(getClass());

    private EcmFileService ecmFileService;

    // #parentObjectType == 'USER_ORG' applies to uploading profile picture
    @PreAuthorize("hasPermission(#fileId, 'FILE', 'read|group-read|write|group-write') or #parentObjectType == 'USER_ORG'")
    @RequestMapping(value = "/download", method = RequestMethod.GET)
    @ResponseBody
    public void downloadFileById(@RequestParam(value = "inline", required = false, defaultValue = "false") boolean inline,
             @RequestParam(value = "ecmFileId", required = true, defaultValue = "0") Long fileId,
             @RequestParam(value = "acm_email_ticket", required = false, defaultValue = "") String acm_email_ticket,
             @RequestParam(value = "version", required = false, defaultValue = "") String version,
             @RequestParam(value = "parentObjectType", required = false, defaultValue = "") String parentObjectType,
             Authentication authentication,
             HttpSession httpSession, HttpServletResponse response)
            throws IOException, ArkCaseFileRepositoryException, AcmObjectNotFoundException
    {
        log.info("Downloading file by ID '{}' for user '{}'", fileId, authentication.getName());

        EcmFile ecmFile = ecmFileService.findById(fileId);

        if (ecmFile != null)
        {
            getEcmFileService().download(response, inline, ecmFile, version);

            String ipAddress = (String) httpSession.getAttribute("acm_ip_address");
            getEcmFileService().publishEcmFileDownloadedEvent(ipAddress, ecmFile, authentication);
        } else
        {
            getEcmFileService().fileNotFound(fileId);
        }
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
