package com.armedia.acm.plugins.admin.web.api;

import com.armedia.acm.plugins.admin.exception.AcmLabelConfigurationException;
import org.apache.commons.io.FileUtils;
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
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

/**
 * Created by sergey.kolomiets on 4/24/15.
 */
@Controller
@RequestMapping( { "/api/v1/plugin/admin", "/api/latest/plugin/admin"} )
public class LabelConfigurationRetrieveAdminResource {
    private Logger log = LoggerFactory.getLogger(getClass());
    private String resourcesFilesLocation;
    @RequestMapping(value = "/labelconfiguration/admin-resource", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE
    })
    @ResponseBody
    public void retrieveResource(
            @RequestParam("lang") String lang,
            @RequestParam("ns") String ns,
            HttpServletResponse response) throws IOException, AcmLabelConfigurationException {

        String fileName = String.format(resourcesFilesLocation, lang, ns);
        try {
            File file = FileUtils.getFile(fileName);

            // Convert Resources json format to array of objects
            String resource = FileUtils.readFileToString(file);

            JSONObject resObject = new JSONObject(resource);

            JSONArray resArray = new JSONArray();

            processNode(resObject, resArray, "");
            response.getOutputStream().print(resArray.toString());
            response.getOutputStream().flush();
        } catch (Exception e) {
            if (log.isErrorEnabled()){
                log.error(String.format("Can't read resource file %s", fileName));
            }
            throw new AcmLabelConfigurationException("Can't read resources info", e);
        }
    }

    private void processNode(JSONObject resObject, JSONArray resArray, String path) {
        Iterator keys = resObject.keys();
        while(keys.hasNext()) {
            String key = (String) keys.next();
            String newPath = path;
            if ((newPath == null)) {
                newPath = "";
            }
            if (path.length() > 0) {
                newPath += ".";
            }
            newPath += key;


            // We've got leaf of tree
            if (resObject.get(key) instanceof JSONObject) {
                JSONObject item = resObject.getJSONObject(key);
                if (item.has("value") && item.has("defaultValue") && item.has("description")) {
                    if ((item.get("value") instanceof String)
                            && (item.get("defaultValue") instanceof String)
                            && (item.get("description") instanceof String)) {


                        // Get path of value and store it into the array
                        JSONObject value = new JSONObject();

                        String valueId = newPath + "." + key;
                        value.put("id", newPath);
                        value.put("value", item.getString("value"));
                        value.put("defaultValue", item.getString("defaultValue"));
                        value.put("description", item.getString("description"));
                        resArray.put(value);
                    }
                } else {
                    processNode(item, resArray, newPath);
                }
            } else {
                // Something is wrong... This is unreachable point
            }
        }
    }

    public void setResourcesFilesLocation(String resourcesFilesLocation) {
        this.resourcesFilesLocation = resourcesFilesLocation;
    }

}