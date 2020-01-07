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

import com.armedia.acm.plugins.ecm.model.DeleteFileResult;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.service.EcmFileService;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.util.List;

@Controller
@RequestMapping({ "/api/v1/plugin/ecm/temp", "/api/latest/plugin/ecm/temp" })
public class UploadTempFilesAPIController
{
    private transient final Logger log = LogManager.getLogger(getClass());
    private EcmFileService fileFolderService;

    @RequestMapping(value = "upload", method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    public List<EcmFile> uploadTempFiles(HttpServletRequest request, Authentication authentication, HttpSession session)
    {
        log.debug("Uploading files to tmp directory by user {}", authentication.getName());

        // Obtains the files from the HTTP POST request multipart body
        MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
        MultiValueMap<String, MultipartFile> files = multipartHttpServletRequest.getMultiFileMap();

        // Writes the uploaded files to a temporary directory
        return getFileFolderService().saveFilesToTempDirectory(files);
    }

    @RequestMapping(value = "delete", method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    public DeleteFileResult deleteTempFile(@RequestParam(value = "fileName") String fileName, HttpServletRequest request,
            Authentication authentication, HttpSession session)
    {
        log.debug("Deleting temp file {} by {}", fileName, authentication.getName());
        DeleteFileResult deleteFileResult = new DeleteFileResult();

        // Removes the temporary attachment file from the file system
        boolean fileDeleted = getFileFolderService().deleteTempFile(fileName);
        if (!fileDeleted)
        {
            deleteFileResult.setDeletedFileName(fileName);
            deleteFileResult.setSuccess(false);
            log.warn("The temp file {} was not deleted. The server file system will require manual cleanup.", fileName);
        }
        else
        {
            deleteFileResult.setDeletedFileName(fileName);
            deleteFileResult.setSuccess(true);
        }

        return deleteFileResult;
    }

    public EcmFileService getFileFolderService()
    {
        return fileFolderService;
    }

    public void setFileFolderService(EcmFileService fileFolderService)
    {
        this.fileFolderService = fileFolderService;
    }
}
