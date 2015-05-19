package com.armedia.acm.plugins.admin.web.api;

import com.armedia.acm.plugins.admin.exception.AcmLabelConfigurationException;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

/**
 * Created by sergey.kolomiets on 4/24/15.
 */
@Controller
@RequestMapping( { "/api/v1/plugin/admin", "/api/latest/plugin/admin"} )
public class LabelConfigurationResetResource {
    private Logger log = LoggerFactory.getLogger(getClass());
    private String resourcesFilesLocation;
    private String adminResourcesFilesLocation;

    @RequestMapping(value = "/labelconfiguration/admin-resource/reset", method = RequestMethod.POST, produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE
    })
    @ResponseBody
    public void updateResource(
            @RequestParam("lang") String lang,
            @RequestParam("ns") String ns,
            @RequestBody String resource,
            HttpServletResponse response, boolean isInline) throws IOException, AcmLabelConfigurationException {



        String[] namespaces = ns.split("\\,");
        String[] langs = lang.split("\\,");

        // Go through the files and reset values
        for (String langIter: langs) {
            for(String nsIter : namespaces) {

                try {
                    String adminResourceFileName = String.format(adminResourcesFilesLocation, langIter, nsIter);
                    // Load resources file and convert it to the JSONObject
                    File file = FileUtils.getFile(adminResourceFileName);
                    byte[] buffer = FileUtils.readFileToByteArray(file);

                    //String resourceFile = new String(buffer);
                    JSONObject adminResourceObj = new JSONObject(new String(buffer));

                    resetValues(adminResourceObj);
                    FileUtils.writeStringToFile(file, adminResourceObj.toString(4));

                    // Generate and save resource file.
                    generateResourceNode(adminResourceObj);

                    // Save generated resource
                    String resourceFileName = String.format(resourcesFilesLocation, lang, ns);
                    File resourceFile = FileUtils.getFile(resourceFileName);
                    FileUtils.writeStringToFile(resourceFile, adminResourceObj.toString(4));
                } catch (Exception e) {
                    if (log.isErrorEnabled()) {
                        log.error(String.format("Can't reset values of resource %s:%s ", langIter, nsIter));
                    }
                    throw new AcmLabelConfigurationException("Reset resource error", e);
                }


            }
        }
    }


    private void resetValues(JSONObject resObject) {
        Iterator keys = resObject.keys();
        while(keys.hasNext()) {
            String key = (String) keys.next();

            // We've got leaf of tree
            if (resObject.get(key) instanceof JSONObject) {
                JSONObject item = resObject.getJSONObject(key);
                if (item.has("value") && item.has("defaultValue") && item.has("description")) {
                    if ((item.get("value") instanceof String)
                            && (item.get("defaultValue") instanceof String)
                            && (item.get("description") instanceof String)) {

                        item.put("value", item.getString("defaultValue"));
                    }
                } else {
                    resetValues(item);
                }
            } else {
                // Something is wrong... This is unreachable point
            }
        }
    }

    private void generateResourceNode(JSONObject resObject) {
        Iterator keys = resObject.keys();
        while(keys.hasNext()) {
            String key = (String) keys.next();

            // We've got leaf of tree
            if (resObject.get(key) instanceof JSONObject) {
                JSONObject item = resObject.getJSONObject(key);
                if (item.has("value") && item.has("defaultValue") && item.has("description")) {
                    if ((item.get("value") instanceof String)
                            && (item.get("defaultValue") instanceof String)
                            && (item.get("description") instanceof String)) {



                        // Get path of value and store it into the array
                        String value = item.getString("value");
                        resObject.put(key, value);
                    }
                } else {
                    generateResourceNode(item);
                }
            } else {
                // Something is wrong... This is unreachable point
            }
        }
    }


    public void setResourcesFilesLocation(String resourcesFilesLocation) {
        this.resourcesFilesLocation = resourcesFilesLocation;
    }


    public void setAdminResourcesFilesLocation(String adminResourcesFilesLocation) {
        this.adminResourcesFilesLocation = adminResourcesFilesLocation;
    }
}
