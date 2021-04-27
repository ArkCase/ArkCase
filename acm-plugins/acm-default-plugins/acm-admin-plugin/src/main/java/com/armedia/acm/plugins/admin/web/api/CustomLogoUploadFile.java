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

import com.armedia.acm.configuration.service.FileConfigurationService;
import com.armedia.acm.plugins.admin.exception.AcmCustomLogoException;
import com.armedia.acm.plugins.admin.exception.AcmWorkflowConfigurationException;
import com.armedia.acm.plugins.admin.service.CustomLogoService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

/**
 * Created by sergey.kolomiets on 6/22/15.
 */
@Controller
@RequestMapping({ "/api/v1/plugin/admin", "/api/latest/plugin/admin" })
public class CustomLogoUploadFile
{
    private Logger log = LogManager.getLogger(getClass());

    private CustomLogoService customLogoService;

    private FileConfigurationService fileConfigurationService;

    @RequestMapping(value = "/branding/customlogos", method = RequestMethod.POST)
    @ResponseBody
    public String replaceFile(
            @RequestParam(value = "headerLogo", required = false) MultipartFile headerLogoFile,
            @RequestParam(value = "loginLogo", required = false) MultipartFile loginLogoFile,
            @RequestParam(value = "emailLogo", required = false) MultipartFile emailLogoFile)
            throws AcmWorkflowConfigurationException
    {

        try
        {
            if (headerLogoFile != null && !headerLogoFile.isEmpty())
            {
                if (Objects.equals(headerLogoFile.getContentType(), MediaType.IMAGE_PNG_VALUE))
                {
                    log.debug("Trying to upload file with name {}", headerLogoFile.getOriginalFilename());
                    customLogoService.updateHeaderLogo(new InputStreamResource(headerLogoFile.getInputStream()));
                }
                else
                {
                    throw new AcmCustomLogoException("Only PNG files are supported for logo");
                }
            }

            if (loginLogoFile != null && !loginLogoFile.isEmpty())
            {
                if (Objects.equals(loginLogoFile.getContentType(), MediaType.IMAGE_PNG_VALUE))
                {
                    log.debug("Trying to upload file with name {}", loginLogoFile.getOriginalFilename());
                    customLogoService.updateLoginLogo(new InputStreamResource(loginLogoFile.getInputStream()));
                }
                else
                {
                    throw new AcmCustomLogoException("Only PNG files are supported for logo");
                }
            }

            if (emailLogoFile != null && !emailLogoFile.isEmpty())
            {
                if (Objects.equals(emailLogoFile.getContentType(), MediaType.IMAGE_PNG_VALUE))
                {
                    log.debug("Trying to upload file with name {}", emailLogoFile.getOriginalFilename());
                    customLogoService.updateEmailLogo(new InputStreamResource(emailLogoFile.getInputStream()));
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

    @RequestMapping(value = "/portal/branding/customlogos", method = RequestMethod.POST)
    @ResponseBody
    public String replacePortalFile(
            @RequestParam(value = "headerLogoPortal", required = false) MultipartFile headerLogoFile,
            @RequestParam(value = "loginLogoPortal", required = false) MultipartFile loginLogoFile,
            @RequestParam(value = "bannerPortal", required = false) MultipartFile bannerLogoFile)
            throws AcmWorkflowConfigurationException
    {

        try
        {
            if (headerLogoFile != null && !headerLogoFile.isEmpty())
            {
                if (Objects.equals(headerLogoFile.getContentType(), MediaType.IMAGE_PNG_VALUE))
                {
                    log.debug("Trying to upload file with name {}", headerLogoFile.getOriginalFilename());
                    customLogoService.updateHeaderLogoPortal(new InputStreamResource(headerLogoFile.getInputStream()));
                }
                else
                {
                    throw new AcmCustomLogoException("Only PNG files are supported for portal logo");
                }
            }

            if (loginLogoFile != null && !loginLogoFile.isEmpty())
            {
                if (Objects.equals(loginLogoFile.getContentType(), MediaType.IMAGE_PNG_VALUE))
                {
                    log.debug("Trying to upload file with name {}", loginLogoFile.getOriginalFilename());
                    customLogoService.updateLoginLogoPortal(new InputStreamResource(loginLogoFile.getInputStream()));
                }
                else
                {
                    throw new AcmCustomLogoException("Only PNG files are supported for portal logo");
                }
            }

            if (bannerLogoFile != null && !bannerLogoFile.isEmpty())
            {
                if (Objects.equals(bannerLogoFile.getContentType(), MediaType.IMAGE_PNG_VALUE))
                {
                    log.debug("Trying to upload file with name {}", bannerLogoFile.getOriginalFilename());
                    customLogoService.updateBannerLogoPortal(new InputStreamResource(bannerLogoFile.getInputStream()));
                }
                else
                {
                    throw new AcmCustomLogoException("Only PNG files are supported for portal logo");
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

    public void setFileConfigurationService(FileConfigurationService fileConfigurationService)
    {
        this.fileConfigurationService = fileConfigurationService;
    }
}
