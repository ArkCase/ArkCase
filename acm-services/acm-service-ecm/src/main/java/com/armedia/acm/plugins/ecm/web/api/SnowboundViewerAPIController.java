package com.armedia.acm.plugins.ecm.web.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;

@Controller
@RequestMapping("/api/latest/viewer")
public class SnowboundViewerAPIController
{
     private Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void decryptURL(HttpServletRequest request) throws UnsupportedEncodingException
    {
        String queryString = request.getQueryString();
        log.debug("Decrypted query string: {}", queryString);
    }
}
