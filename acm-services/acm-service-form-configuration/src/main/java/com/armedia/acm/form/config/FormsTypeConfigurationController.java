package com.armedia.acm.form.config;

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

import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping({ "/api/v1/service/forms/type", "/api/latest/service/forms/type" })
public class FormsTypeConfigurationController
{
    private Logger log = LoggerFactory.getLogger(getClass());
    private FormsTypeManagementService formsTypeManagementService;

    @RequestMapping(method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE
    })
    @ResponseBody
    public String retrieveProperties(HttpServletResponse response) throws FormsTypeManagementException
    {
        try
        {
            return formsTypeManagementService.getProperties().toString();
        }
        catch (Exception e)
        {
            String msg = "Can't retrieve application properties";
            log.error(msg, e);
            throw new FormsTypeManagementException(msg, e);
        }
    }

    @RequestMapping(value = "/{propertyName}", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE })
    @ResponseBody
    public String retrieveProperty(@PathVariable("propertyName") String propertyName, HttpServletResponse response)
            throws FormsTypeManagementException
    {
        try
        {
            return formsTypeManagementService.getProperty(propertyName).toString();
        }
        catch (Exception e)
        {
            String msg = "Can't retrieve application property";
            log.error(msg, e);
            throw new FormsTypeManagementException(msg, e);
        }
    }

    @RequestMapping(method = RequestMethod.PUT, produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE })
    @ResponseBody
    public String updateProperty(@RequestBody String resource, HttpServletResponse response)
            throws FormsTypeManagementException
    {
        try
        {
            JSONObject newProps = new JSONObject(resource);
            return formsTypeManagementService.updateProperties(newProps).toString();
        }
        catch (Exception e)
        {
            String msg = "Can't retrieve application property";
            log.error(msg, e);
            throw new FormsTypeManagementException(msg, e);
        }
    }

    public void setFormsTypeManagementService(FormsTypeManagementService formsTypeManagementService)
    {
        this.formsTypeManagementService = formsTypeManagementService;
    }

}
