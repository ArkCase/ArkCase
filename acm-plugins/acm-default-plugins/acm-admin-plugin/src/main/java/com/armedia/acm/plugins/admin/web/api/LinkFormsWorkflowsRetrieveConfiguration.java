package com.armedia.acm.plugins.admin.web.api;

import com.armedia.acm.plugins.admin.exception.AcmLinkFormsWorkflowException;
import com.armedia.acm.plugins.admin.service.LinkFormsWorkflowsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

/**
 * Created by sergey.kolomiets  on 6/15/15.
 */
@Controller
@RequestMapping({"/api/v1/plugin/admin", "/api/latest/plugin/admin"})
public class LinkFormsWorkflowsRetrieveConfiguration
{
    private Logger log = LoggerFactory.getLogger(getClass());

    private LinkFormsWorkflowsService linkFormsWorkflowsService;

    @RequestMapping(value = "/linkformsworkflows/configuration", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE
    })
    @ResponseBody
    public String retrieveConfiguration() throws IOException, AcmLinkFormsWorkflowException
    {

        try
        {
            return linkFormsWorkflowsService.retrieveConfigurationAsJson().toString();
        } catch (Exception e)
        {
            log.error("Can't retrieve Link Forms Workflows Configuration", e);
            throw new AcmLinkFormsWorkflowException("Can't retrieve Link Forms Workflows Configuration", e);
        }
    }

    public void setLinkFormsWorkflowsService(LinkFormsWorkflowsService linkFormsWorkflowsService)
    {
        this.linkFormsWorkflowsService = linkFormsWorkflowsService;
    }
}