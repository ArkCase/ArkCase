package com.armedia.acm.plugins.admin.web.api;

import com.armedia.acm.plugins.admin.exception.AcmLabelConfigurationException;
import org.apache.commons.io.FileUtils;
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

/**
 * Created by sergey.kolomiets on 4/24/15.
 */
@Controller
@RequestMapping( { "/api/v1/plugin/admin", "/api/latest/plugin/admin"} )
public class LabelConfigurationUpdateSettings {
    private Logger log = LoggerFactory.getLogger(getClass());
    private String settingsFileLocation;

    @RequestMapping(value = "/labelconfiguration/settings", method = RequestMethod.PUT, produces = {
            MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.TEXT_PLAIN_VALUE
    })
    @ResponseBody
    public void updateSettings(
            @RequestBody String settings) throws IOException, AcmLabelConfigurationException {

        try {
            File file = FileUtils.getFile(settingsFileLocation);
            byte[] buffer = settings.getBytes();
            FileUtils.writeByteArrayToFile(file, buffer);
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error(String.format("Can't write settings data in to the file %s", settingsFileLocation));
            }
            throw new AcmLabelConfigurationException("Update settings error", e);
        }
    }

    public void setSettingsFileLocation(String settingsFileLocation) {
        this.settingsFileLocation = settingsFileLocation;
    }
}
