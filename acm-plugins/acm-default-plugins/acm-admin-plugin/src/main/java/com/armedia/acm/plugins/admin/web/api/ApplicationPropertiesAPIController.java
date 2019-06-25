package com.armedia.acm.plugins.admin.web.api;

import com.armedia.acm.plugins.admin.exception.AcmPropertiesManagementException;
import com.armedia.acm.plugins.admin.model.ApplicationProperties;
import com.armedia.acm.plugins.admin.service.ApplicationPropertiesManagementService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping({ "/api/v1/plugin/admin/app-properties", "/api/latest/plugin/admin/app-properties" })
public class ApplicationPropertiesAPIController
{

    private transient final Logger log = LogManager.getLogger(getClass());

    private ApplicationPropertiesManagementService applicationPropertiesManagementService;

    @RequestMapping(method = RequestMethod.PUT)
    @ResponseBody
    public void updateApplicationPropertiesConfig(@RequestBody ApplicationProperties applicationProperties)
    {
        applicationPropertiesManagementService.writeConfiguration(applicationProperties);
    }

    @RequestMapping(value = "/{propertyName}", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE
    })
    @ResponseBody
    public String retrieveProperty(
            @PathVariable("propertyName") String propertyName) throws AcmPropertiesManagementException
    {

        try
        {
            return applicationPropertiesManagementService.readProperty(propertyName).toString();
        }
        catch (Exception e)
        {
            String msg = "Can't retrieve application property";
            log.error(msg, e);
            throw new AcmPropertiesManagementException(msg, e);
        }
    }

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ApplicationProperties getApplicationPropertiesConfig()
    {
        return applicationPropertiesManagementService.readConfiguration();
    }

    public void setApplicationPropertiesManagementService(ApplicationPropertiesManagementService applicationPropertiesManagementService)
    {
        this.applicationPropertiesManagementService = applicationPropertiesManagementService;
    }
}
