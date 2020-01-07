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
import com.armedia.acm.plugins.admin.service.CustomLogoService;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.OutputStream;

@Controller
@RequestMapping(value = { "/branding" })
public class CustomLogoRetrieveFile
{
    private Logger log = LogManager.getLogger(getClass());
    private CustomLogoService customLogoService;

    @RequestMapping(value = "/headerlogo.png", method = RequestMethod.GET)
    public void retrieveHeaderLogo(HttpServletResponse response)
    {
        try
        {
            byte[] logo = customLogoService.getHeaderLogo();
            writeImageToResponse(logo, response);
        }
        catch (AcmCustomLogoException e)
        {
            log.error("Can not get header logo", e);
        }
    }

    @RequestMapping(value = "/loginlogo.png", method = RequestMethod.GET)
    public void retrieveLoginLogo(HttpServletResponse response)
    {
        try
        {
            byte[] logo = customLogoService.getLoginLogo();
            writeImageToResponse(logo, response);
        }
        catch (AcmCustomLogoException e)
        {
            log.error("Can not get login logo", e);
        }
    }

    @RequestMapping(value = "/emaillogo.png", method = RequestMethod.GET)
    public void retrieveEmailLogo(HttpServletResponse response)
    {
        try
        {
            byte[] logo = customLogoService.getEmailLogo();
            writeImageToResponse(logo, response);
        }
        catch (AcmCustomLogoException e)
        {
            log.error("Can not get email logo", e);
        }
    }

    private void writeImageToResponse(byte[] image, HttpServletResponse response)
    {
        try
        {
            OutputStream out = response.getOutputStream();
            response.setContentType(MediaType.IMAGE_PNG_VALUE);
            response.setContentLength(image.length);
            // response.setHeader("Cache-control", "public,max-age=86400");
            // response.setHeader("Pragma", "cache");
            out.write(image);
            out.flush();
        }
        catch (IOException e)
        {
            log.error("IOException", e);
        }
    }

    public void setCustomLogoService(CustomLogoService customLogoService)
    {
        this.customLogoService = customLogoService;
    }
}
