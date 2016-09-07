package com.armedia.acm.plugins.admin.web.api;

import com.armedia.acm.plugins.admin.exception.AcmCustomLogoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;


@Controller
@RequestMapping(value = {"/branding"})
public class CustomLogoRetrieveFile
{
    private Logger log = LoggerFactory.getLogger(getClass());
    private CustomLogoService customLogoService;

    @RequestMapping(value = "/headerlogo.png", method = RequestMethod.GET)
    public void retrieveHeaderLogo(HttpServletResponse response)
    {
        try
        {
            byte[] logo = customLogoService.getHeaderLogo();
            writeImageToResponse(logo, response);
        } catch (AcmCustomLogoException e)
        {
            log.error("Can not get header logo", e);
        }
    }

    @RequestMapping(value = "/loginlogo.png", method = RequestMethod.GET)
    public void retrieveLoginLogo(HttpServletResponse response)
    {
        try
        {
            byte[] logo = customLogoService.getLoginLogo();
            writeImageToResponse(logo, response);
        } catch (AcmCustomLogoException e)
        {
            log.error("Can not get login logo", e);
        }
    }

    private void writeImageToResponse(byte[] image, HttpServletResponse response)
    {
        try
        {
            OutputStream out = response.getOutputStream();
            response.setContentType(MediaType.IMAGE_PNG_VALUE);
            response.setContentLength(image.length);
            //response.setHeader("Cache-control", "public,max-age=86400");
            //response.setHeader("Pragma", "cache");
            out.write(image);
            out.flush();
        } catch (IOException e)
        {
            log.error("IOException", e);
        }
    }

    public void setCustomLogoService(CustomLogoService customLogoService)
    {
        this.customLogoService = customLogoService;
    }
}
