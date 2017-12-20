package com.armedia.acm.plugins.admin.service;

import com.armedia.acm.plugins.admin.exception.AcmCmisConfigurationException;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

/**
 * Created by nick.ferguson on 3/24/2017.
 */
public class CmisConfigurationPropertiesService
{
    private Logger log = LoggerFactory.getLogger(getClass());

    private CmisConfigurationService cmisConfigurationService;

    public JSONArray retrieveProperties() throws AcmCmisConfigurationException
    {
        try
        {
            List<File> propertiesFiles = cmisConfigurationService.getPropertiesFiles();
            JSONArray cmisJsonArr = new JSONArray();
            for (File propertyFile : propertiesFiles)
            {
                try (InputStream propStream = FileUtils.openInputStream(propertyFile))
                {

                    log.debug("Now reading property file: [{}]", propertyFile.getName());

                    Properties prop = new Properties();
                    prop.load(propStream);

                    // Put all properties into JSON Object
                    JSONObject cmisJsonObj = new JSONObject();
                    for (String proName : prop.stringPropertyNames())
                    {
                        log.debug("Reading [{}] with value [{}] from [{}]", proName, prop.getProperty(proName), propertyFile.getName());
                        if(proName.equalsIgnoreCase("cmis.maxIdle")){
                             Integer value = Integer.valueOf(prop.getProperty(proName));
                            cmisJsonObj.put(proName, value);
                        } else if(proName.equalsIgnoreCase("cmis.maxActive")){
                            Integer value = Integer.valueOf(prop.getProperty(proName));
                            cmisJsonObj.put(proName, value);
                        } else if(proName.equalsIgnoreCase("cmis.maxWait")){
                            Integer value = Integer.valueOf(prop.getProperty(proName));
                            cmisJsonObj.put(proName, value);
                        } else if(proName.equalsIgnoreCase("cmis.Count")){
                            Integer value = Integer.valueOf(prop.getProperty(proName));
                            cmisJsonObj.put(proName, value);
                        } else if(proName.equalsIgnoreCase("cmis.reconnectFrequency")) {
                            Integer value = Integer.valueOf(prop.getProperty(proName));
                            cmisJsonObj.put(proName, value);
                        } else if(proName.equalsIgnoreCase("cmis.minEvictionMillis")) {
                            Integer value = Integer.valueOf(prop.getProperty(proName));
                            cmisJsonObj.put(proName, value);
                        } else if(proName.equalsIgnoreCase("cmis.reconnectCount")) {
                            Integer value = Integer.valueOf(prop.getProperty(proName));
                            cmisJsonObj.put(proName, value);
                        } else if(proName.equalsIgnoreCase("cmis.evictionCheckIntervalMillis")) {
                            Integer value = Integer.valueOf(prop.getProperty(proName));
                            cmisJsonObj.put(proName, value);
                        } else {
                            cmisJsonObj.put(proName, prop.getProperty(proName));
                        }
                    }

                    log.debug("Finished reading property file: [{}]", propertyFile.getName());
                    cmisJsonArr.put(cmisJsonObj);
                }
            }

            return cmisJsonArr;

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
