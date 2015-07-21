package com.armedia.acm.plugins.admin.web.api;

import com.armedia.acm.plugins.admin.exception.AcmCustomLogoException;
import com.armedia.acm.plugins.admin.exception.AcmWorkflowConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by sergey.kolomiets  on 6/22/15.
 */
@Controller
@RequestMapping( { "/api/v1/plugin/admin", "/api/latest/plugin/admin"} )
public class CustomLogoUploadFile {
    private Logger log = LoggerFactory.getLogger(getClass());

    private CustomLogoService customLogoService;

    @RequestMapping(
            value = "/branding/customlogos",
            method = RequestMethod.POST
    )
    @ResponseBody
    public String replaceFile(
            @RequestParam(value = "headerLogo", required = false) MultipartFile headerLogoFile,
            @RequestParam(value = "loginLogo", required = false) MultipartFile loginLogoFile)
            throws IOException, AcmWorkflowConfigurationException
    {

        try {
            if (headerLogoFile != null && !headerLogoFile.isEmpty()) {
                if (headerLogoFile.getContentType().equals(MediaType.IMAGE_PNG_VALUE)) {
                    InputStream headerLogoStream = headerLogoFile.getInputStream();
                    customLogoService.updateHeaderLogo(headerLogoStream);
                } else {
                    throw new AcmCustomLogoException("Only PNG files are supported for logo");
                }
            }

            if (loginLogoFile != null &&  !loginLogoFile.isEmpty()) {
                if (loginLogoFile.getContentType().equals(MediaType.IMAGE_PNG_VALUE)) {
                    InputStream loginLogoStream = loginLogoFile.getInputStream();
                    customLogoService.updateLoginLogo(loginLogoStream);
                } else {
                    throw new AcmCustomLogoException("Only PNG files are supported for logo");
                }
            }

            return "{}";
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Can't update logos", e);
            }
            throw new AcmWorkflowConfigurationException("Can't update logos. " + e.getLocalizedMessage(), e);
        }
    }

    public void setCustomLogoService(CustomLogoService customLogoService) {
        this.customLogoService = customLogoService;
    }
}
