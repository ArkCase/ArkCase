package com.armedia.acm.plugins.admin.web.api;

import com.armedia.acm.plugins.admin.exception.AcmCmisConfigurationException;
import com.armedia.acm.plugins.admin.service.CmisConfigurationService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.Map;

/**
 * Created by nick.ferguson on 3/22/2017.
 */
@Controller
@RequestMapping({"/api/v1/plugin/admin", "/api/latest/plugin/admin"})
public class CmisConfigurationUpdateConfig
{
    private Logger log = LoggerFactory.getLogger(getClass());

    private CmisConfigurationService cmisConfigurationService;

    @RequestMapping(value = "/cmisconfiguration/config/{cmisId}", method = RequestMethod.PUT, produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE
    })

    @ResponseBody
    public String updateConfig(
            @RequestBody String resource,
            @PathVariable("cmisId") String cmisId) throws IOException, AcmCmisConfigurationException
    {

        try
        {

            JSONObject cmisObject = new JSONObject(resource);
            if (cmisId == null)
            {
                throw new AcmCmisConfigurationException("CMIS Id is undefined");
            }

            Map<String, Object> props = cmisConfigurationService.getProperties(cmisObject);
            cmisConfigurationService.updateCmisConfig(cmisId, props);

        } catch (Exception e)
        {
            log.error("Can't update CMIS config", e);
            throw new AcmCmisConfigurationException("Update CMIS config error", e);
        }

        return "{}";
    }

    public void setCmisConfigurationService(CmisConfigurationService cmisConfigurationService)
    {
        this.cmisConfigurationService = cmisConfigurationService;
    }
}
