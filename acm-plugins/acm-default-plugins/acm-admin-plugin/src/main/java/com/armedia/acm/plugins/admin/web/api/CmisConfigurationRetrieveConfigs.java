package com.armedia.acm.plugins.admin.web.api;

import com.armedia.acm.plugins.admin.exception.AcmCmisConfigurationException;
import com.armedia.acm.plugins.admin.service.CmisConfigurationService;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

/**
 * Created by nick.ferguson on 3/22/2017.
 */
@Controller
@RequestMapping({"/api/v1/plugin/admin", "/api/latest/plugin/admin"})
public class CmisConfigurationRetrieveConfigs
{
    private Logger log = LoggerFactory.getLogger(getClass());
    private CmisConfigurationService cmisConfigurationService;

    @RequestMapping(value = "/cmisconfiguration/config", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE
    })
    @ResponseBody
    public String retrieveCmisConfigs() throws IOException, AcmCmisConfigurationException
    {

        try
        {
            List<File> propertiesFiles = cmisConfigurationService.getPropertiesFiles();
            JSONArray cmisJsonArr = new JSONArray();
            for (File propertyFile : propertiesFiles)
            {
                Properties prop = new Properties();
                prop.load(FileUtils.openInputStream(propertyFile));

                // Put all properties into JSON Object
                JSONObject cmisJsonObj = new JSONObject();
                for (String proName : prop.stringPropertyNames())
                {
                    cmisJsonObj.put(proName, prop.getProperty(proName));
                }

                cmisJsonArr.put(cmisJsonObj);
            }

            return cmisJsonArr.toString();

        } catch (Exception e)
        {
            log.error("Can't read CMIS properties file", e);
            throw new AcmCmisConfigurationException("Can't retrieve CMIS properties", e);
        }
    }

    public void setCmisConfigurationService(CmisConfigurationService cmisConfigurationService)
    {
        this.cmisConfigurationService = cmisConfigurationService;
    }
}
