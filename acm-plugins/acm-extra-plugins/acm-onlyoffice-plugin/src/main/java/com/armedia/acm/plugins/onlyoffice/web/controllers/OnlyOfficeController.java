package com.armedia.acm.plugins.onlyoffice.web.controllers;

import com.armedia.acm.plugins.onlyoffice.helpers.ConfigManager;
import com.armedia.acm.plugins.onlyoffice.model.CallBackData;
import com.armedia.acm.plugins.onlyoffice.model.CallbackResponse;
import com.armedia.acm.plugins.onlyoffice.service.OnlyOfficeService;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;

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
    private OnlyOfficeService onlyOfficeService;

    @RequestMapping(value = "/editor")
    public ModelAndView editor(
            @RequestParam(name = "file") Long fileId,
            @RequestParam(name = "mode") String mode,
            Authentication auth)
    {
        try
        {
            ModelAndView mav = new ModelAndView("onlyoffice/editor");
            // FIXME use already defined bean object mappper instead of creating new
            ObjectMapper om = new ObjectMapper();
            om.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            mav.addObject("config", om.writeValueAsString(onlyOfficeService.getConfig(fileId, auth)));
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
    public @ResponseBody CallbackResponse callbackHandler(@RequestBody CallBackData callBackData)
    {
        logger.info("got Callback [{}]", callBackData);
        return new CallbackResponse();
    }

    public void setOnlyOfficeService(OnlyOfficeService onlyOfficeService)
    {
        this.onlyOfficeService = onlyOfficeService;
    }
}
