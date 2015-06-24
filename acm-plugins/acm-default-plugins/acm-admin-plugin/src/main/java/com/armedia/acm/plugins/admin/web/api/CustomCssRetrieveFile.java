package com.armedia.acm.plugins.admin.web.api;

import com.armedia.acm.plugins.admin.exception.CustomCssException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by sergey.kolomiets on 6/19/15.
 */
@Controller
@RequestMapping( {"/branding"} )
public class CustomCssRetrieveFile {
    private Logger log = LoggerFactory.getLogger(getClass());
    private CustomCssService customCssService;

    @RequestMapping(value = "/customcss", method = RequestMethod.GET, produces = "text/css")
    @ResponseBody
    public String retrieveFile(
            HttpServletResponse response) throws IOException, CustomCssException {

        try {
            String customCSSFileContent = customCssService.getFile();
            return customCSSFileContent;
        } catch (Exception e) {
            if (log.isErrorEnabled()){
                log.error("Can't get custom CSS file", e);
            }
            throw new CustomCssException("Can't get custom CSS file", e);
        }
    }


    public void setCustomCssService(CustomCssService customCssService) {
        this.customCssService = customCssService;
    }
}
