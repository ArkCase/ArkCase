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

import com.armedia.acm.plugins.onlyoffice.model.CallbackResponse;
import com.armedia.acm.plugins.onlyoffice.model.CallbackResponseError;
import com.armedia.acm.plugins.onlyoffice.model.DocumentHistory;
import com.armedia.acm.plugins.onlyoffice.model.callback.CallBackData;
import com.armedia.acm.plugins.onlyoffice.service.CallbackService;
import com.armedia.acm.plugins.onlyoffice.service.ConfigService;
import com.armedia.acm.plugins.onlyoffice.service.DocumentHistoryManager;
import com.armedia.acm.services.dataaccess.service.impl.ArkPermissionEvaluator;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;

@RequestMapping(value = "/api/onlyoffice")
public class OnlyOfficeApiController
{
    private Logger logger = LogManager.getLogger(getClass());
    private CallbackService callbackService;
    private ObjectMapper objectMapper;
    private DocumentHistoryManager documentHistoryManager;
    private ConfigService configService;
    private ArkPermissionEvaluator arkPermissionEvaluator;

    @RequestMapping(value = "/callback", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public @ResponseBody ResponseEntity<CallbackResponse> callbackHandler(@RequestBody String callBackDataString,
            Authentication auth, @RequestHeader(required = false, name = "Authorization") String token, Authentication authentication)
            throws IOException
    {
        logger.info("got Callback [{}]", callBackDataString);
        CallBackData callBackData = objectMapper.readValue(callBackDataString, CallBackData.class);
        Long fileId = Long.valueOf(callBackData.getKey().split("-")[0]);
        // TODO verify callback token and data are matching
        boolean savePermission = arkPermissionEvaluator.hasPermission(authentication, fileId, "FILE",
                "write|group-write");
        if (!savePermission)
        {
            return new ResponseEntity<>(new CallbackResponseError("Not authorized."), HttpStatus.OK);
        }
        CallbackResponse callbackResponse = callbackService.handleCallback(callBackData, auth);
        return new ResponseEntity<>(callbackResponse, HttpStatus.OK);
    }

    @PreAuthorize("hasPermission(#fileId, 'FILE', 'read|group-read')")
    @RequestMapping(value = "/history/{fileId}", method = RequestMethod.GET)
    public @ResponseBody DocumentHistory getDocumentHistory(@PathVariable Long fileId)
    {
        return documentHistoryManager.getDocumentHistory(fileId);
    }

    @PreAuthorize("hasPermission(#fileId, 'FILE', 'read|group-read')")
    @RequestMapping(value = "/history/{fileId}/{version}/changes", method = RequestMethod.GET)
    public ResponseEntity<InputStreamResource> downloadHistoryChanges(@PathVariable Long fileId, @PathVariable String version)
            throws IOException
    {
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

    public void setCallbackService(CallbackService callbackService)
    {
        this.callbackService = callbackService;
    }

    public void setObjectMapper(ObjectMapper objectMapper)
    {
        this.objectMapper = objectMapper;
    }

    public void setDocumentHistoryManager(DocumentHistoryManager documentHistoryManager)
    {
        this.documentHistoryManager = documentHistoryManager;
    }

    public void setConfigService(ConfigService configService)
    {
        this.configService = configService;
    }

    public void setArkPermissionEvaluator(ArkPermissionEvaluator arkPermissionEvaluator)
    {
        this.arkPermissionEvaluator = arkPermissionEvaluator;
    }
}
