package com.armedia.acm.plugins.admin.web.api;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.plugins.admin.exception.AcmLabelConfigurationException;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.zip.JSONzip;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLDecoder;
import java.util.Iterator;

/**
 * Created by sergey.kolomiets on 4/24/15.
 */
@Controller
@RequestMapping( { "/api/v1/plugin/admin", "/api/latest/plugin/admin"} )
public class LabelConfigurationUpdateResource {
    private Logger log = LoggerFactory.getLogger(getClass());
    private String resourcesFilesLocation;
    private String adminResourcesFilesLocation;

    @RequestMapping(value = "/labelconfiguration/admin-resource", method = RequestMethod.PUT, produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE
    })
    @ResponseBody
    public void updateResource(
            @RequestParam("lang") String lang,
            @RequestParam("ns") String ns,
            @RequestBody String resource,
            HttpServletResponse response, boolean isInline) throws IOException, AcmLabelConfigurationException {

        String fileName = String.format(adminResourcesFilesLocation, lang, ns);

        try {
            JSONObject newResObject = new JSONObject(resource);
            if (newResObject.has("id")) {
                String[] namespaces = newResObject.getString("id").split("\\.");

                // Load resources file and convert it to the JSONObject
                File file = FileUtils.getFile(fileName);
                byte[] buffer = FileUtils.readFileToByteArray(file);

                //String resourceFile = new String(buffer);
                JSONObject adminResourceObj = new JSONObject(new String(buffer));

                // Find item by id
                int level = 0;
                boolean itemFound = false;
                JSONObject currentNode = adminResourceObj;
                while (!itemFound) {
                    Iterator keys = currentNode.keys();
                    boolean keyFound = false;
                    while (keys.hasNext()) {
                        String key = (String) keys.next();
                        if (key.equals(namespaces[level])) {
                            if ((level + 1) == namespaces.length) {
                                currentNode.getJSONObject(key).put("value", newResObject.getString("value"));
                                currentNode.getJSONObject(key).put("description", newResObject.getString("description"));
                                itemFound = true;
                            } else {
                                currentNode = currentNode.getJSONObject(key);
                            }
                            keyFound = true;
                            level++;
                            break;
                        }
                    }
                    if (!keyFound) {
                        throw new AcmLabelConfigurationException("Incorrect resource format");
                    }
                }

                FileUtils.writeStringToFile(file, adminResourceObj.toString(4));

                // Generate and save resource file.
                generateResourceNode(adminResourceObj);

                // Save generated resource
                String resourceFileName = String.format(resourcesFilesLocation, lang, ns);
                File resourceFile = FileUtils.getFile(resourceFileName);
                FileUtils.writeStringToFile(resourceFile, adminResourceObj.toString(4));
            }
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error(String.format("Can't write resource data in to the file %s", fileName));
            }
            throw new AcmLabelConfigurationException("Update resource error", e);
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
