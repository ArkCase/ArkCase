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
import com.armedia.acm.plugins.onlyoffice.model.DocumentHistory;
import com.armedia.acm.plugins.onlyoffice.model.callback.CallBackData;
import com.armedia.acm.plugins.onlyoffice.service.CallbackService;
import com.armedia.acm.plugins.onlyoffice.service.ConfigService;
import com.armedia.acm.plugins.onlyoffice.service.DocumentHistoryManager;
import com.armedia.acm.service.objectlock.model.AcmObjectLock;
import com.armedia.acm.service.objectlock.service.AcmObjectLockingManager;
import com.armedia.acm.services.authenticationtoken.service.AuthenticationTokenService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;

@RequestMapping(value = "/onlyoffice")
public class OnlyOfficeController
{
    private Logger logger = LoggerFactory.getLogger(getClass());
    private ConfigService configService;
    private CallbackService callbackService;
    private DocumentHistoryManager documentHistoryManager;
    private AuthenticationTokenService authenticationTokenService;
    private AcmObjectLockingManager objectLockingManager;
    private ObjectMapper objectMapper;

    @RequestMapping(value = "/editor", method = RequestMethod.GET)
    public ModelAndView editor(
            @RequestParam(name = "file") Long fileId,
            Authentication auth)
    {
        try
        {
            ModelAndView mav = new ModelAndView("onlyoffice/editor");
            // lock file for onlyoffice processing
            AcmObjectLock lock = objectLockingManager.acquireObjectLock(fileId, EcmFileConstants.OBJECT_FILE_TYPE,
                    FileLockType.SHARED_WRITE.name(), null, false, auth.getName());
            String authTicket = authenticationTokenService.getTokenForAuthentication(auth);

            mav.addObject("config", objectMapper.writeValueAsString(configService.getConfig(fileId, auth)));
            mav.addObject("docserviceApiUrl", configService.getDocumentServerUrlApi());
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

    @RequestMapping(value = "/callback", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public @ResponseBody CallbackResponse callbackHandler(@RequestBody CallBackData callBackData,
            Authentication auth, HttpServletRequest request)
    {
        logger.info("got Callback [{}]", callBackData);
        return callbackService.handleCallback(callBackData, auth);
    }

    @RequestMapping(value = "/history/{fileId}", method = RequestMethod.GET)
    public @ResponseBody DocumentHistory getDocumentHistory(@PathVariable Long fileId)
    {
        return documentHistoryManager.getDocumentHistory(fileId);
    }

    @RequestMapping(value = "/history/{key}/changes", method = RequestMethod.GET)
    public ResponseEntity<InputStreamResource> downloadHistoryChanges(@PathVariable String key)
            throws IOException
    {
        String[] keySplit = key.split("-");
        Long fileId = Long.valueOf(keySplit[0]);
        String version = keySplit[1];
        File file = documentHistoryManager.getHistoryChangesFile(fileId, version);

        HttpHeaders respHeaders = new HttpHeaders();
        respHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        respHeaders.setContentLength(file.length());
        // must have this header, because is called from iframe
        URL url = new URL(configService.getDocumentServerUrlApi());
        respHeaders.setAccessControlAllowOrigin(url.getProtocol() + "://" + url.getHost());

        InputStreamResource isr = new InputStreamResource(new FileInputStream(file));
        return new ResponseEntity<>(isr, respHeaders, HttpStatus.OK);
    }

    public void setConfigService(ConfigService configService)
    {
        this.configService = configService;
    }

    public void setCallbackService(CallbackService callbackService)
    {
        this.callbackService = callbackService;
    }

    public void setObjectMapper(ObjectMapper objectMapper)
    {
        this.objectMapper = objectMapper;
    }

    public void setObjectLockingManager(AcmObjectLockingManager objectLockingManager)
    {
        this.objectLockingManager = objectLockingManager;
    }

    public void setDocumentHistoryManager(DocumentHistoryManager documentHistoryManager)
    {
        this.documentHistoryManager = documentHistoryManager;
    }

    public void setAuthenticationTokenService(AuthenticationTokenService authenticationTokenService)
    {
        this.authenticationTokenService = authenticationTokenService;
    }
}
