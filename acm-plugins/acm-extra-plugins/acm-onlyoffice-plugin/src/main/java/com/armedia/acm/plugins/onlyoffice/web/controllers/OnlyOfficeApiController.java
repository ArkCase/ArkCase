package com.armedia.acm.plugins.onlyoffice.web.controllers;

import com.armedia.acm.plugins.onlyoffice.model.CallbackResponse;
import com.armedia.acm.plugins.onlyoffice.model.DocumentHistory;
import com.armedia.acm.plugins.onlyoffice.model.callback.CallBackData;
import com.armedia.acm.plugins.onlyoffice.service.CallbackService;
import com.armedia.acm.plugins.onlyoffice.service.ConfigService;
import com.armedia.acm.plugins.onlyoffice.service.DocumentHistoryManager;
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
    private Logger logger = LoggerFactory.getLogger(getClass());
    private CallbackService callbackService;
    private ObjectMapper objectMapper;
    private DocumentHistoryManager documentHistoryManager;
    private ConfigService configService;

    @RequestMapping(value = "/callback", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public @ResponseBody CallbackResponse callbackHandler(@RequestBody String callBackDataString,
            Authentication auth, @RequestHeader(required = false, name = "Authorization") String token) throws IOException
    {
        logger.info("got Callback [{}]", callBackDataString);
        CallBackData callBackData = objectMapper.readValue(callBackDataString, CallBackData.class);
        // TODO verify callback
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
}
