package com.armedia.acm.plugins.admin.web.api;

import com.armedia.acm.plugins.admin.exception.AcmCmisConfigurationException;
import com.armedia.acm.plugins.admin.model.CmisConfigurationConstants;
import com.armedia.acm.plugins.admin.service.CmisConfigurationService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by nick.ferguson on 3/22/2017.
 */
@Controller
@RequestMapping({"/api/v1/plugin/admin", "/api/latest/plugin/admin"})
public class CmisConfigurationCreateConfig
{
    private Logger log = LoggerFactory.getLogger(getClass());
    private CmisConfigurationService cmisConfigurationService;

    @RequestMapping(value = "/cmisconfiguration/config", method = RequestMethod.POST, produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE
    })

    @ResponseBody
    public String createDirectory(
            @RequestBody String resource) throws IOException, AcmCmisConfigurationException
    {
        try
        {
            JSONObject newCmisObject = new JSONObject(resource);
            String id = newCmisObject.getString(CmisConfigurationConstants.CMIS_ID);

            if (id == null)
            {
                throw new AcmCmisConfigurationException("ID is undefined");
            }

            HashMap<String, Object> props = cmisConfigurationService.getProperties(newCmisObject);

            // Create CMIS Configuration
            cmisConfigurationService.createCmisConfig(id, props);

            return newCmisObject.toString();
        } catch (Exception e)
        {
            log.error("Can't create CMIS config", e);
            throw new AcmCmisConfigurationException("Create CMIS config error", e);
        }
    }

    public void setCmisConfigurationService(CmisConfigurationService cmisConfigurationService)
    {
        this.cmisConfigurationService = cmisConfigurationService;
    }
}
