package com.armedia.acm.plugins.admin.web.api;

import com.armedia.acm.plugins.admin.exception.AcmLabelConfigurationException;
import com.armedia.acm.plugins.admin.exception.AcmLabelManagementException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Iterator;

/**
 * Created by sergey on 2/14/16.
 */
@Controller
@RequestMapping( { "/api/v1/plugin/admin", "/api/latest/plugin/admin"} )
public class LabelManagementUpdateResource {
    private Logger log = LoggerFactory.getLogger(getClass());
    private LabelManagementService labelManagementService;

    @RequestMapping(value = "/labelmanagement/admin-resource", method = RequestMethod.PUT, produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE
    })
    @ResponseBody
    public void updateResource(
            @RequestParam("lang") String lang,
            @RequestParam("ns") String ns,
            @RequestBody String resource,
            HttpServletResponse response) throws IOException, AcmLabelConfigurationException, AcmLabelManagementException {

        try {
            JSONObject value = new JSONObject(resource);
            JSONObject updatedRes = labelManagementService.updateCustomResource(ns, lang, value);
            JSONArray jsonResourceArray = new JSONArray();
            // Convert json object to the array
            Iterator<String> keys = updatedRes.keys();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                JSONObject node = (JSONObject)updatedRes.get(key);
                node.put("id", key);
                jsonResourceArray.put(node);
            }
            response.getOutputStream().print(jsonResourceArray.toString());
            response.getOutputStream().flush();

        } catch (Exception e){
            String errMsg = String.format("Can't update resource %s:%s", lang, ns);
            if (log.isErrorEnabled()) {
                log.error(String.format(errMsg, e));
            }
            throw new AcmLabelManagementException(errMsg, e);
        }
    }


    public void setLabelManagementService(LabelManagementService labelManagementService) {
        this.labelManagementService = labelManagementService;
    }
}
