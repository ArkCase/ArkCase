package com.armedia.acm.plugins.admin.web.api;

import com.armedia.acm.plugins.admin.exception.AcmLinkFormsWorkflowException;
import org.activiti.engine.impl.util.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sergey.kolomiets  on 6/15/15.
 */
@Controller
@RequestMapping( { "/api/v1/plugin/admin", "/api/latest/plugin/admin"} )
public class LinkFormsWorkflowsUpdateConfiguration {
    private Logger log = LoggerFactory.getLogger(getClass());

    private LinkFormsWorkflowsService linkFormsWorkflowsService;

    @RequestMapping(value = "/linkformsworkflows/configuration", method = RequestMethod.PUT, produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE
    })
    @ResponseBody
    public String updateConfiguration(
            @RequestBody String resource) throws IOException, AcmLinkFormsWorkflowException {

        try {

            JSONArray cellsArray = new JSONArray(resource);

            // Convert cells JSON into array of arrays
            List<List<String>> values = new ArrayList();
            for(int rowNum = 0; rowNum < cellsArray.length(); rowNum++) {
                JSONArray row = cellsArray.getJSONArray(rowNum);
                List<String> valuesRow = new ArrayList();
                for (int colNum = 0; colNum < row.length(); colNum++) {
                    valuesRow.add(row.getString(colNum));
                }
                values.add(valuesRow);
            }

            linkFormsWorkflowsService.updateConfiguration(values);

            return "{}";
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Can't update Link Forms Workflows Configuration", e);
            }
            throw new AcmLinkFormsWorkflowException("Can't update Link Forms Workflows Configuration", e);
        }
    }

    public void setLinkFormsWorkflowsService(LinkFormsWorkflowsService linkFormsWorkflowsService) {
        this.linkFormsWorkflowsService = linkFormsWorkflowsService;
    }
}