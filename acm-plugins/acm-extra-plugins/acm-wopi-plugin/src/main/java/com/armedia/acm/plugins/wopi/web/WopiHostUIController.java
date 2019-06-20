package com.armedia.acm.plugins.wopi.web;

/*-
 * #%L
 * ACM Service: Wopi service
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

import com.armedia.acm.plugins.wopi.model.WopiConfig;
import com.armedia.acm.services.authenticationtoken.service.AuthenticationTokenService;
import com.armedia.acm.services.users.model.AcmUser;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/plugin/office")
public class WopiHostUIController
{
    private static final Logger log = LogManager.getLogger(WopiHostUIController.class);

    private WopiConfig wopiConfig;
    private AuthenticationTokenService tokenService;

    @PreAuthorize("hasPermission(#fileId, 'FILE', 'read|group-read|write|group-write')")
    @RequestMapping(method = RequestMethod.GET, value = "/{fileId}")
    public ModelAndView getWopiHostPage(Authentication authentication, @PathVariable Long fileId, HttpSession session)
    {
        log.info("Opening file with id [{}] per user [{}]", fileId, authentication.getName());

        AcmUser user = (AcmUser) session.getAttribute("acm_user");
        String accessToken = tokenService.generateAndSaveAuthenticationToken(fileId, user.getMail(), authentication);

        ModelAndView model = new ModelAndView();
        model.setViewName("wopi-host");
        model.addObject("url", wopiConfig.getWopiHostUrl(fileId, accessToken));
        return model;
    }

    @PreAuthorize("hasPermission(#fileId, 'FILE', 'read|group-read|write|group-write')")
    @RequestMapping(method = RequestMethod.GET, value = "/testapp/{fileId}")
    public ModelAndView getWopiValidationAppPage(Authentication authentication, @PathVariable Long fileId, HttpSession session)
    {
        log.info("Opening wopi validation app for file with id [{}] per user [{}]", fileId, authentication.getName());

        AcmUser user = (AcmUser) session.getAttribute("acm_user");
        String accessToken = tokenService.generateAndSaveAuthenticationToken(fileId, user.getMail(), authentication);

        ModelAndView model = new ModelAndView();
        model.setViewName("wopi-host");
        model.addObject("url", wopiConfig.getWopiHostValidationUrl(fileId, accessToken));
        return model;
    }

    public void setWopiConfig(WopiConfig wopiConfig)
    {
        this.wopiConfig = wopiConfig;
    }

    public void setTokenService(AuthenticationTokenService tokenService)
    {
        this.tokenService = tokenService;
    }
}
