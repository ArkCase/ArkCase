package com.armedia.acm.plugins.admin.web.api;

import com.armedia.acm.plugins.admin.exception.AcmCmisConfigurationException;
import com.armedia.acm.plugins.admin.service.CmisConfigurationPropertiesService;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;


/**
 * Created by nick.ferguson on 3/22/2017.
 */
@Controller
@RequestMapping({"/api/v1/plugin/admin", "/api/latest/plugin/admin"})
public class CmisConfigurationRetrieveConfigs
{
    private Logger log = LoggerFactory.getLogger(getClass());

    private CmisConfigurationPropertiesService cmisConfigurationPropertiesService;

    @RequestMapping(value = "/cmisconfiguration/config", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE
    })
    @ResponseBody
    public String retrieveCmisConfigs() throws IOException, AcmCmisConfigurationException
    {
        log.debug("Retrieving CMIS Properties");

        JSONArray cmisPropertiesArray = cmisConfigurationPropertiesService.retrieveProperties();
        return cmisPropertiesArray.toString();
    }

    public void setCmisConfigurationPropertiesService(CmisConfigurationPropertiesService cmisConfigurationPropertiesService)
    {
        this.cmisConfigurationPropertiesService = cmisConfigurationPropertiesService;
    }
}
