package com.armedia.acm.plugins.admin.web.api;

import com.armedia.acm.plugins.admin.exception.AcmPropertiesManagementException;
import com.armedia.acm.plugins.admin.service.JsonPropertiesManagementService;

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
 * Created by sergey on 4/13/16.
 */

@Controller
@RequestMapping({ "/api/v1/plugin/admin", "/api/latest/plugin/admin" })
public class AngularPropertiesManagementRetrieveProperties
{
    private Logger log = LoggerFactory.getLogger(getClass());
    private JsonPropertiesManagementService jsonPropertiesManagementService;

    @RequestMapping(value = "/app-properties", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE
    })
    @ResponseBody
    public String retrieveProperties(
            HttpServletResponse response) throws IOException, AcmPropertiesManagementException
    {

        try
        {
            return jsonPropertiesManagementService.getProperties().toString();
        }
        catch (Exception e)
        {
            String msg = "Can't retrieve application properties";
            log.error(msg, e);
            throw new AcmPropertiesManagementException(msg, e);
        }
    }

    public void setJsonPropertiesManagementService(JsonPropertiesManagementService jsonPropertiesManagementService)
    {
        this.jsonPropertiesManagementService = jsonPropertiesManagementService;
    }
}
