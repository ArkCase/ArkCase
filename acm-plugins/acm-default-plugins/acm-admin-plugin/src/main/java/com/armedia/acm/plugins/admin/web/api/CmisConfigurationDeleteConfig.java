package com.armedia.acm.plugins.admin.web.api;

import com.armedia.acm.plugins.admin.exception.AcmCmisConfigurationException;
import com.armedia.acm.plugins.admin.service.CmisConfigurationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

/**
 * Created by nick.ferguson on 3/22/2017.
 */
@Controller
@RequestMapping({"/api/v1/plugin/admin", "/api/latest/plugin/admin"})
public class CmisConfigurationDeleteConfig
{
    private Logger log = LoggerFactory.getLogger(getClass());
    private CmisConfigurationService cmisConfigurationService;

    @RequestMapping(value = "/cmisconfiguration/config/{cmisId}", method = RequestMethod.DELETE, produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE
    })
    @ResponseBody
    public String deleteConfig(
            @PathVariable("cmisId") String cmisId) throws IOException, AcmCmisConfigurationException
    {
        try
        {
            if (cmisId == null)
            {
                log.debug("CMIS ID is undefined");
                throw new AcmCmisConfigurationException("Config Id is undefined");
            }
            log.debug("Attempting to delete CMIS Configuration with ID: " + cmisId);

            cmisConfigurationService.deleteCmisConfig(cmisId);
            log.debug("CMIS Config with ID '{}' deleted.", cmisId);
            return cmisId;

        } catch (Exception e)
        {
            log.error("Can't delete CMIS config", e);
            throw new AcmCmisConfigurationException("Delete CMIS config error", e);
        }
    }

    public void setCmisConfigurationService(CmisConfigurationService cmisConfigurationService)
    {
        this.cmisConfigurationService = cmisConfigurationService;
    }
}
