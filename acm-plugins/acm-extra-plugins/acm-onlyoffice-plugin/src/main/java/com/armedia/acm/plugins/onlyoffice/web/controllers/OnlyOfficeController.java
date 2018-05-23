/*
 * (c) Copyright Ascensio System Limited 2010-2017
 * The MIT License (MIT)
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.armedia.acm.plugins.onlyoffice.web.controllers;

/*-
 * #%L
 * ACM Extra Plugin: OnlyOffice Integration
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

import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.plugins.onlyoffice.helpers.ConfigManager;
import com.armedia.acm.plugins.onlyoffice.model.CallBackData;
import com.armedia.acm.services.authenticationtoken.service.AuthenticationTokenService;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

@RequestMapping(value = "/onlyoffice")
public class OnlyOfficeController
{
    private Logger logger = LoggerFactory.getLogger(getClass());
    private EcmFileService ecmFileService;
    private UserDao userDao;
    private AuthenticationTokenService authenticationTokenService;

    @RequestMapping(value = "/editor")
    public ModelAndView editor(
            @RequestParam(name = "file") Long fileId,
            @RequestParam(name = "mode") String mode,
            Authentication auth)
    {
        try
        {
            Boolean desktopMode = !"embedded".equals(mode);
            EcmFile ecmFile = ecmFileService.findById(fileId);
            AcmUser user = userDao.findByUserId(auth.getName());
            ModelAndView mav = new ModelAndView("onlyoffice/editor");
            mav.addObject("fileInfo", ecmFile);
            mav.addObject("mode", mode);
            mav.addObject("user", user);
            mav.addObject("token", authenticationTokenService.getTokenForAuthentication(auth));
            mav.addObject("type", desktopMode ? "desktop" : "embedded");
            mav.addObject("docserviceApiUrl", ConfigManager.getProperty("files.docservice.url.api"));
            return mav;
        }
        catch (Exception e)
        {
            logger.error("Error executing onlyoffice editor: {}", e.getMessage(), e);
            ModelAndView modelAndView = new ModelAndView("onlyoffice/error");
            modelAndView.addObject("errorMessage", e.getMessage());
            return modelAndView;
        }
    }

    @RequestMapping(value = "/callback", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public @ResponseBody String callbackHandler(@RequestBody CallBackData callBackData)
    {
        logger.info("got Callback [{}]", callBackData);
        return "{\"error\":0}";
    }

    public void setEcmFileService(EcmFileService ecmFileService)
    {
        this.ecmFileService = ecmFileService;
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    public void setAuthenticationTokenService(AuthenticationTokenService authenticationTokenService)
    {
        this.authenticationTokenService = authenticationTokenService;
    }
}
