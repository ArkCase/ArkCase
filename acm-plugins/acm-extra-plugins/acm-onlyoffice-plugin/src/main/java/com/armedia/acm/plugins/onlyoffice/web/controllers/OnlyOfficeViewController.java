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

import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.plugins.ecm.service.lock.FileLockType;
import com.armedia.acm.plugins.onlyoffice.model.config.Config;
import com.armedia.acm.plugins.onlyoffice.model.config.DocumentPermissions;
import com.armedia.acm.plugins.onlyoffice.service.ConfigService;
import com.armedia.acm.plugins.onlyoffice.service.JWTSigningService;
import com.armedia.acm.service.objectlock.model.AcmObjectLock;
import com.armedia.acm.service.objectlock.service.AcmObjectLockingManager;
import com.armedia.acm.services.authenticationtoken.service.AuthenticationTokenService;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@RequestMapping(value = "/onlyoffice")
public class OnlyOfficeViewController
{
    private Logger logger = LogManager.getLogger(getClass());
    private ConfigService configService;

    private AuthenticationTokenService authenticationTokenService;
    private AcmObjectLockingManager objectLockingManager;
    private JWTSigningService JWTSigningService;
    private ObjectMapper objectMapper;
    private UserDao userDao;

    @RequestMapping(value = "/editor", method = RequestMethod.GET)
    public ModelAndView editor(
            @RequestParam(name = "file") Long fileId,
            Authentication auth)
    {
        try
        {
            String userId = auth.getName();
            AcmUser user = userDao.findByUserId(userId);

            ModelAndView mav = new ModelAndView("onlyoffice/editor");

            String authTicket = authenticationTokenService.generateAndSaveAuthenticationToken(fileId, user.getMail(), auth);

            Config config = configService.getConfig(fileId, "edit", "en-US", auth, authTicket, user);

            if (userCanLock(config))
            {
                // lock file for onlyoffice processing if document is opened for editing
                AcmObjectLock lock = objectLockingManager.acquireObjectLock(fileId, EcmFileConstants.OBJECT_FILE_TYPE,
                        FileLockType.SHARED_WRITE.name(), null, false, auth.getName());
            }
            String configJsonString = objectMapper.writeValueAsString(config);
            mav.addObject("config", configJsonString);
            mav.addObject("docserviceApiUrl", configService.getDocumentServerUrlApi());
            if (configService.isOutboundSignEnabled())
            {
                mav.addObject("token", JWTSigningService.signJsonPayload(configJsonString));
            }
            mav.addObject("fileId", fileId);
            mav.addObject("ticket", authTicket);
            mav.addObject("arkcaseBaseUrl", configService.getArkcaseBaseUrl());

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

    private boolean userCanLock(Config config)
    {
        DocumentPermissions permissions = config.getDocument().getPermissions();
        if (permissions.isEdit())
        {
            return true;
        }
        else if (!permissions.isEdit() && permissions.isReview())
        {
            return true;
        }
        return false;
    }

    public void setConfigService(ConfigService configService)
    {
        this.configService = configService;
    }

    public void setObjectMapper(ObjectMapper objectMapper)
    {
        this.objectMapper = objectMapper;
    }

    public void setObjectLockingManager(AcmObjectLockingManager objectLockingManager)
    {
        this.objectLockingManager = objectLockingManager;
    }

    public void setJWTSigningService(JWTSigningService JWTSigningService)
    {
        this.JWTSigningService = JWTSigningService;
    }

    public void setAuthenticationTokenService(AuthenticationTokenService authenticationTokenService)
    {
        this.authenticationTokenService = authenticationTokenService;
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }
}
