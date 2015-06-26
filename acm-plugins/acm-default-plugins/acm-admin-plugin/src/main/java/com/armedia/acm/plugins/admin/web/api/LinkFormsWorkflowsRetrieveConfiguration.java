package com.armedia.acm.plugins.admin.web.api;

import com.armedia.acm.plugins.admin.exception.AcmLinkFormsWorkflowException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by sergey.kolomiets  on 6/15/15.
 */
@Controller
@RequestMapping( { "/api/v1/plugin/admin", "/api/latest/plugin/admin"} )
public class LinkFormsWorkflowsRetrieveConfiguration  {
    private Logger log = LoggerFactory.getLogger(getClass());

    private LinkFormsWorkflowsService linkFormsWorkflowsService;

    @RequestMapping(value = "/linkformsworkflows/configuration", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE
    })
    @ResponseBody
    public String retrieveConfiguration(
            @RequestBody String resource,
            HttpServletResponse response) throws IOException, AcmLinkFormsWorkflowException {

        try {
            return linkFormsWorkflowsService.retrieveConfigurationAsJson().toString();
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Can't retrieve Link Forms Workflows Configuration", e);
            }
            throw new AcmLinkFormsWorkflowException("Can't retrieve Link Forms Workflows Configuration", e);
        }
    }

    public void setLinkFormsWorkflowsService(LinkFormsWorkflowsService linkFormsWorkflowsService) {
        this.linkFormsWorkflowsService = linkFormsWorkflowsService;
    }
}