package com.armedia.acm.plugins.admin.web.api;

/*-
 * #%L
 * ACM Default Plugin: admin
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

import com.armedia.acm.plugins.admin.exception.AcmCustomLogoException;
import com.armedia.acm.plugins.admin.exception.AcmWorkflowConfigurationException;
import com.armedia.acm.plugins.admin.service.CustomLogoService;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Created by sergey.kolomiets on 6/22/15.
 */
@Controller
@RequestMapping({ "/api/v1/plugin/admin", "/api/latest/plugin/admin" })
public class CustomLogoUploadFile
{
    private Logger log = LogManager.getLogger(getClass());

    private CustomLogoService customLogoService;

    @RequestMapping(value = "/branding/customlogos", method = RequestMethod.POST)
    @ResponseBody
    public String replaceFile(
            @RequestParam(value = "headerLogo", required = false) MultipartFile headerLogoFile,
            @RequestParam(value = "loginLogo", required = false) MultipartFile loginLogoFile,
            @RequestParam(value = "emailLogo", required = false) MultipartFile emailLogoFile)
            throws IOException, AcmWorkflowConfigurationException
    {

        try
        {
            if (headerLogoFile != null && !headerLogoFile.isEmpty())
            {
                if (headerLogoFile.getContentType().equals(MediaType.IMAGE_PNG_VALUE))
                {
                    customLogoService.updateHeaderLogo(headerLogoFile);
                }
                else
                {
                    throw new AcmCustomLogoException("Only PNG files are supported for logo");
                }
            }

            if (loginLogoFile != null && !loginLogoFile.isEmpty())
            {
                if (loginLogoFile.getContentType().equals(MediaType.IMAGE_PNG_VALUE))
                {
                    customLogoService.updateLoginLogo(loginLogoFile);
                }
                else
                {
                    throw new AcmCustomLogoException("Only PNG files are supported for logo");
                }
            }

            if (emailLogoFile != null && !emailLogoFile.isEmpty())
            {
                if (emailLogoFile.getContentType().equals(MediaType.IMAGE_PNG_VALUE))
                {
                    customLogoService.updateEmailLogo(emailLogoFile);
                }
                else
                {
                    throw new AcmCustomLogoException("Only PNG files are supported for logo");
                }
            }

            return "{}";
        }
        catch (Exception e)
        {
            log.error("Can't update logos", e);
            throw new AcmWorkflowConfigurationException("Can't update logos. " + e.getLocalizedMessage(), e);
        }
    }

    public void setCustomLogoService(CustomLogoService customLogoService)
    {
        this.customLogoService = customLogoService;
    }
}
