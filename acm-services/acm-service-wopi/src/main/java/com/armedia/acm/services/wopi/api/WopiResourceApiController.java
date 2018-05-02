package com.armedia.acm.services.wopi.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping(value = "/api/latest/wopi/")
public class WopiResourceApiController
{
    private Map<String, String> resourceLocations;

    private static final Logger log = LoggerFactory.getLogger(WopiResourceApiController.class);

    @RequestMapping(value = "/resources", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, String> getResourceLocations(Authentication authentication)
    {
        log.debug("Getting wopi resources locations per user [{}]", authentication.getName());
        return resourceLocations;
    }

    public void setResourceLocations(Map<String, String> resourceLocations)
    {
        this.resourceLocations = resourceLocations;
    }
}
