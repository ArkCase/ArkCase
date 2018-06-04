package com.armedia.acm.plugins.onlyoffice.web.controllers;

import com.armedia.acm.plugins.onlyoffice.model.CallbackResponse;
import com.armedia.acm.plugins.onlyoffice.model.callback.CallBackData;
import com.armedia.acm.plugins.onlyoffice.service.CallbackService;
import com.armedia.acm.plugins.onlyoffice.service.ConfigService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@RequestMapping(value = "/onlyoffice")
public class OnlyOfficeController
{
    private Logger logger = LoggerFactory.getLogger(getClass());
    private ConfigService configService;
    private CallbackService callbackService;

    private ObjectMapper objectMapper;

    @RequestMapping(value = "/editor", method = RequestMethod.GET)
    public ModelAndView editor(
            @RequestParam(name = "file") Long fileId,
            Authentication auth)
    {
        try
        {
            ModelAndView mav = new ModelAndView("onlyoffice/editor");
            // FIXME use already defined bean object mappper instead of creating new

            mav.addObject("config", objectMapper.writeValueAsString(configService.getConfig(fileId, auth)));
            mav.addObject("docserviceApiUrl", configService.getDocumentServerUrlApi());
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
}
