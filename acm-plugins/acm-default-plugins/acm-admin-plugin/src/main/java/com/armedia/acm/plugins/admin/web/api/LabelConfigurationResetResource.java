package com.armedia.acm.plugins.admin.web.api;

import com.armedia.acm.plugins.admin.exception.AcmLabelConfigurationException;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
            @RequestBody String resource) throws IOException, AcmLabelConfigurationException {

        List<String> namespaces = new ArrayList<>();
        List<String> langs = new ArrayList<>();

        try {
            JSONObject resourceObject = new JSONObject(resource);
            JSONArray nsJsonArray = resourceObject.getJSONArray("ns");
            for (int i = 0; i < nsJsonArray.length(); i++) {
                namespaces.add(nsJsonArray.getString(i));
            }
            JSONArray langJsonArray = resourceObject.getJSONArray("lng");
            for (int i = 0; i < langJsonArray.length(); i++) {
                langs.add(langJsonArray.getString(i));
            }
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error(String.format("Wrong reset parameter '%s' ", resource));
            }
            throw new AcmLabelConfigurationException("Reset resource error", e);
        }

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
                    FileUtils.writeStringToFile(file, adminResourceObj.toString(4), "UTF-8");

                    // Generate and save resource file.
                    generateResourceNode(adminResourceObj);

                    // Save generated resource
                    String resourceFileName = String.format(resourcesFilesLocation, langIter, nsIter);
                    File resourceFile = FileUtils.getFile(resourceFileName);
                    FileUtils.writeStringToFile(resourceFile, adminResourceObj.toString(4), "UTF-8");
                } catch (Exception e) {
                    if (log.isErrorEnabled()) {
                        log.error(String.format("Can't reset values of resource %s:%s ", langIter, nsIter));
                    }
                    throw new AcmLabelConfigurationException("Reset resource error", e);
                }
            }
        }
    }


    private void resetValues(JSONObject resObject) throws AcmLabelConfigurationException {
        Iterator keys = resObject.keys();
        while(keys.hasNext()) {
            String key = (String) keys.next();

            // We've got leaf of tree
            if (resObject.get(key) instanceof JSONObject) {
                JSONObject item = resObject.getJSONObject(key);
                boolean isResource = item.has("value") && item.has("defaultValue") && item.has("description");
                if (isResource) {
                    boolean isResourceValid = (item.get("value") instanceof String)
                            && (item.get("defaultValue") instanceof String)
                            && (item.get("description") instanceof String);
                    if (isResourceValid) {
                        item.put("value", item.getString("defaultValue"));
                    } else {
                        if (log.isErrorEnabled()) {
                            log.error(String.format("Resource file format is broken"));
                        }
                    }
                } else {
                    resetValues(item);
                }
            } else {
                if (log.isErrorEnabled()) {
                    log.error(String.format("Resource file format is broken"));
                }
                throw new AcmLabelConfigurationException("Resource file format is broken");
            }
        }
    }

    private void generateResourceNode(JSONObject resObject) throws AcmLabelConfigurationException {
        Iterator keys = resObject.keys();
        while(keys.hasNext()) {
            String key = (String) keys.next();

            // We've got leaf of tree
            if (resObject.get(key) instanceof JSONObject) {
                JSONObject item = resObject.getJSONObject(key);
                boolean isResource = item.has("value") && item.has("defaultValue") && item.has("description");
                if (isResource) {
                    boolean isResourceValid = (item.get("value") instanceof String)
                            && (item.get("defaultValue") instanceof String)
                            && (item.get("description") instanceof String);
                    if (isResourceValid) {
                        // Get path of value and store it into the array
                        String value = item.getString("value");
                        resObject.put(key, value);
                    } else {
                        if (log.isErrorEnabled()) {
                            log.error(String.format("Resource file format is broken"));
                        }
                        throw new AcmLabelConfigurationException("Resource file format is broken");
                    }
                } else {
                    generateResourceNode(item);
                }
            } else {
                if (log.isErrorEnabled()) {
                    log.error(String.format("Resource file format is broken"));
                }
                throw new AcmLabelConfigurationException("Resource file format is broken");
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
