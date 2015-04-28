package com.armedia.acm.plugins.admin.web.api;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.plugins.admin.exception.AcmLabelConfigurationException;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLDecoder;

/**
 * Created by sergey.kolomiets on 4/24/15.
 */
@Controller
@RequestMapping( { "/api/v1/plugin/admin", "/api/latest/plugin/admin"} )
public class LabelConfigurationUpdateResource {
    private Logger log = LoggerFactory.getLogger(getClass());
    private String resourcesFilesLocation;

    @RequestMapping(value = "/labelconfiguration/resource", method = RequestMethod.PUT, produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE
    })
    @ResponseBody
    public void updateResource(
            @RequestParam("lang") String lang,
            @RequestParam("ns") String ns,
            @RequestBody String resource,
            HttpServletResponse response, boolean isInline) throws IOException, AcmLabelConfigurationException {

        String decodedResource = URLDecoder.decode(resource, "UTF-8");
        String fileName = String.format(resourcesFilesLocation, lang, ns);
        try {
            File file = FileUtils.getFile(fileName);
            byte[] buffer = decodedResource.getBytes();
            FileUtils.writeByteArrayToFile(file, buffer);
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error(String.format("Can't write resource data in to the file %s", fileName));
            }
            throw new AcmLabelConfigurationException("Update resource error", e);
        }
    }

    public void setResourcesFilesLocation(String resourcesFilesLocation) {
        this.resourcesFilesLocation = resourcesFilesLocation;
    }
}
