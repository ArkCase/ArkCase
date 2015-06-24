package com.armedia.acm.plugins.admin.web.api;

import com.armedia.acm.plugins.admin.exception.CustomCssException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
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
public class CustomLogoRetrieveFile {
    private Logger log = LoggerFactory.getLogger(getClass());
    private CustomLogoService customLogoService;

    @RequestMapping(value = "/headerlogo", method = RequestMethod.GET, produces = MediaType.IMAGE_PNG_VALUE)
    @ResponseBody
    public byte[] retrieveHeaderLogo(
            HttpServletResponse response) throws IOException, CustomCssException {

        try {
            byte[] logo = customLogoService.getHeaderLogo();
            return logo;
        } catch (Exception e) {
            if (log.isErrorEnabled()){
                log.error("Can't header logo", e);
            }
            throw new CustomCssException("Can't get header logo", e);
        }
    }

    @RequestMapping(value = "/loginlogo", method = RequestMethod.GET, produces = MediaType.IMAGE_PNG_VALUE)
    @ResponseBody
    public byte[] retrieveLoginLogo(
            HttpServletResponse response) throws IOException, CustomCssException {

        try {
            byte[] logo = customLogoService.getLoginLogo();
            return logo;
        } catch (Exception e) {
            if (log.isErrorEnabled()){
                log.error("Can't header logo", e);
            }
            throw new CustomCssException("Can't get header logo", e);
        }
    }


    public void setCustomLogoService(CustomLogoService customLogoService) {
        this.customLogoService = customLogoService;
    }
}
