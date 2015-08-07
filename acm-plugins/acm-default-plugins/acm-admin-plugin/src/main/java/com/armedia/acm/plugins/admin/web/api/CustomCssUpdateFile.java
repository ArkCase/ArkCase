package com.armedia.acm.plugins.admin.web.api;

import com.armedia.acm.plugins.admin.exception.AcmCustomCssException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

/**
 * Created by sergey.kolomiets on 6/19/15.
 */
@Controller
@RequestMapping( { "/api/v1/plugin/admin", "/api/latest/plugin/admin"} )
public class CustomCssUpdateFile {
    private Logger log = LoggerFactory.getLogger(getClass());

    CustomCssService customCssService;

    @RequestMapping(value = "/branding/customcss", method = RequestMethod.PUT, produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE
    })
    @ResponseBody
    public String updateFile(
            @RequestBody String resource) throws IOException, AcmCustomCssException {

        try {
            customCssService.updateFile(resource);
            return "{}";
        } catch (Exception e) {
            if (log.isErrorEnabled()){
                log.error("Can't update custom CSS file", e);
            }
            throw new AcmCustomCssException("Can't update custom CSS file", e);
        }
    }


    public void setCustomCssService(CustomCssService customCssService) {
        this.customCssService = customCssService;
    }
}
