package com.armedia.acm.plugins.admin.web.api;

import com.armedia.acm.plugins.admin.exception.AcmLabelConfigurationException;
import org.apache.commons.io.FileUtils;
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

/**
 * Created by sergey.kolomiets on 4/24/15.
 */
@Controller
@RequestMapping( { "/api/v1/plugin/admin", "/api/latest/plugin/admin"} )
public class LabelConfigurationRetrieveResource {
    private Logger log = LoggerFactory.getLogger(getClass());
    private String resourcesFilesLocation;

    @RequestMapping(value = "/labelconfiguration/resource", method = RequestMethod.GET, produces = {
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
            FileUtils.copyFile(file, response.getOutputStream());
            response.getOutputStream().flush();
        } catch (Exception e) {
            if (log.isErrorEnabled()){
                log.error(String.format("Can't read resource file %s", fileName));
            }
            throw new AcmLabelConfigurationException("Can't read resources info", e);
        }
    }

    public void setResourcesFilesLocation(String resourcesFilesLocation) {
        this.resourcesFilesLocation = resourcesFilesLocation;
    }

}