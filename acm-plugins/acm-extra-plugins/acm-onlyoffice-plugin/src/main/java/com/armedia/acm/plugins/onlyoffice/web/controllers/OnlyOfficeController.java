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
import com.armedia.acm.plugins.onlyoffice.model.CallbackResponse;
import com.armedia.acm.plugins.onlyoffice.model.callback.CallBackData;
import com.armedia.acm.plugins.onlyoffice.service.CallbackService;
import com.armedia.acm.plugins.onlyoffice.service.ConfigService;
import com.armedia.acm.plugins.onlyoffice.service.JWTSigningService;
import com.armedia.acm.service.objectlock.model.AcmObjectLock;
import com.armedia.acm.service.objectlock.service.AcmObjectLockingManager;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

@RequestMapping(value = "/onlyoffice")
public class OnlyOfficeController {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private ConfigService configService;
    private CallbackService callbackService;

    private AcmObjectLockingManager objectLockingManager;
    private JWTSigningService JWTSigningService;
    private ObjectMapper objectMapper;

    @RequestMapping(value = "/editor", method = RequestMethod.GET)
    public ModelAndView editor(
            @RequestParam(name = "file") Long fileId,
            Authentication auth) {
        try {
            ModelAndView mav = new ModelAndView("onlyoffice/editor");
            // lock file for onlyoffice processing
            AcmObjectLock lock = objectLockingManager.acquireObjectLock(fileId, EcmFileConstants.OBJECT_FILE_TYPE,
                    FileLockType.SHARED_WRITE.name(), null, false, auth.getName());

            String configJsonString = objectMapper.writeValueAsString(configService.getConfig(fileId, "edit", "en-US", auth));
            mav.addObject("config", configJsonString);
            mav.addObject("docserviceApiUrl", configService.getDocumentServerUrlApi());
            if (configService.isOutboundSignEnabled()) {
                mav.addObject("token", JWTSigningService.signJsonPayload(configJsonString));
            }
            return mav;
        } catch (Exception e) {
            logger.error("Error executing onlyoffice editor: {}", e.getMessage(), e);
            ModelAndView modelAndView = new ModelAndView("onlyoffice/error");
            modelAndView.addObject("errorMessage", e.getMessage());
            return modelAndView;
        }
    }

    @RequestMapping(value = "/callback", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public @ResponseBody
    CallbackResponse callbackHandler(@RequestBody String callBackDataString,
                                     Authentication auth, @RequestHeader(required = false, name = "Authorization") String token) throws IOException {
        logger.info("got Callback [{}]", callBackDataString);
        CallBackData callBackData = objectMapper.readValue(callBackDataString, CallBackData.class);


        return callbackService.handleCallback(callBackData, auth);
    }

    public void setConfigService(ConfigService configService) {
        this.configService = configService;
    }

    public void setCallbackService(CallbackService callbackService) {
        this.callbackService = callbackService;
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void setObjectLockingManager(AcmObjectLockingManager objectLockingManager) {
        this.objectLockingManager = objectLockingManager;
    }

    public void setJWTSigningService(JWTSigningService JWTSigningService) {
        this.JWTSigningService = JWTSigningService;
    }
}
