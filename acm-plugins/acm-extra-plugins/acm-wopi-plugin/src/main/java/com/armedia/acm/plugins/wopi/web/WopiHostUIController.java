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

import com.armedia.acm.core.model.ApplicationConfig;
import com.armedia.acm.plugins.wopi.model.WopiConfig;
import com.armedia.acm.services.authenticationtoken.service.AuthenticationTokenService;
import com.armedia.acm.services.users.model.AcmUser;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/plugin/office")
public class WopiHostUIController
{
    private static final Logger log = LogManager.getLogger(WopiHostUIController.class);

    private WopiConfig wopiConfig;
    private AuthenticationTokenService tokenService;
    private ApplicationConfig applicationConfig;

    @Value("${tokenExpiration.wopiLinks}")
    private Long tokenExpiry;

    @PreAuthorize("hasPermission(#fileId, 'FILE', 'read|group-read|write|group-write')")
    @RequestMapping(method = RequestMethod.GET, value = "/{fileId}")
    public ModelAndView getWopiHostPage(Authentication authentication, @PathVariable Long fileId, HttpSession session)
    {
        log.info("Opening file with id [{}] per user [{}]", fileId, authentication.getName());
         AcmUser user = (AcmUser) session.getAttribute("acm_user");
        String accessToken = tokenService.getUncachedTokenForAuthentication(authentication);

        tokenService.addTokenToRelativeAndGenericPaths(generateWopiRelativePaths(accessToken, fileId),
                generateWopiGenericPaths(fileId), accessToken, tokenExpiry, user.getMail());

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
        String accessToken = tokenService.getUncachedTokenForAuthentication(authentication);
        tokenService.addTokenToRelativeAndGenericPaths(generateWopiRelativePaths(accessToken, fileId),
                generateWopiGenericPaths(fileId), accessToken, tokenExpiry, user.getMail());

        ModelAndView model = new ModelAndView();
        model.setViewName("wopi-host");
        model.addObject("url", wopiConfig.getWopiHostValidationUrl(fileId, accessToken));
        return model;
    }

    private List<String> generateWopiGenericPaths(Long fileId)
    {
        String wopiFileGenericPath = applicationConfig.getBaseUrl() + "/api/latest/plugin/wopi/files/" + fileId+ "?";
        String wopiFileContentsGenericPath = applicationConfig.getBaseUrl() + "/api/latest/plugin/wopi/files/" + fileId+ "/contents";
        String wopiFileLockGenericPath = applicationConfig.getBaseUrl() + "/api/latest/plugin/wopi/files/" + fileId+ "/lock";
        String wopiFileRenameGenericPath = applicationConfig.getBaseUrl() + "/api/latest/plugin/wopi/files/" + fileId+ "/rename";
        String wopiUsersGenericPath = applicationConfig.getBaseUrl() + "/api/latest/plugin/wopi/users/resource/" + fileId + "?";
        return Arrays.asList(wopiFileGenericPath, wopiUsersGenericPath, wopiFileContentsGenericPath, wopiFileLockGenericPath, wopiFileRenameGenericPath);
    }

    private List<String> generateWopiRelativePaths(String accessToken, Long fileId)
    {
        String relativePathUsers = applicationConfig.getBaseUrl() + "/api/latest/plugin/wopi/users?ecmFileId=" + fileId + "&acm_wopi_ticket=" + accessToken;
        return Arrays.asList(relativePathUsers);
    }

    public void setWopiConfig(WopiConfig wopiConfig)
    {
        this.wopiConfig = wopiConfig;
    }

    public void setTokenService(AuthenticationTokenService tokenService)
    {
        this.tokenService = tokenService;
    }

    public void setApplicationConfig(ApplicationConfig applicationConfig)
    {
        this.applicationConfig = applicationConfig;
    }
}
