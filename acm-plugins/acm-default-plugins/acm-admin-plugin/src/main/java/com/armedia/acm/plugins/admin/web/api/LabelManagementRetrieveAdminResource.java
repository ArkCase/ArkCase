package com.armedia.acm.plugins.admin.web.api;

import com.armedia.acm.plugins.admin.exception.AcmLabelManagementException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Iterator;

/**
 * Created by sergey on 2/14/16.
 */
@Controller
@RequestMapping( { "/api/v1/plugin/admin", "/api/latest/plugin/admin"} )
public class LabelManagementRetrieveAdminResource {
    private Logger log = LoggerFactory.getLogger(getClass());
    private LabelManagementService labelManagementService;

    @RequestMapping(value = "/labelmanagement/admin-resource", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE
    })
    @ResponseBody
    public void retrieveResource(
            @RequestParam("lang") String lang,
            @RequestParam("ns") String ns,
            HttpServletResponse response) throws IOException, AcmLabelManagementException {

        try {
            JSONObject jsonResource = labelManagementService.getAdminResource(ns, lang);
            JSONArray jsonResourceArray = new JSONArray();
            // Convert json object to the array
            Iterator<String> keys = jsonResource.keys();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                JSONObject node = (JSONObject)jsonResource.get(key);
                node.put("id", key);
                jsonResourceArray.put(node);
            }
            response.getOutputStream().print(jsonResourceArray.toString());
            response.getOutputStream().flush();
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Can't retrieve admin resource", e);
            }
            throw new AcmLabelManagementException("Can't retrieve admin resource", e);
        }
    }

    public void setLabelManagementService(LabelManagementService labelManagementService) {
        this.labelManagementService = labelManagementService;
    }

}