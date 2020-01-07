package com.armedia.acm.plugins.admin.service;

/*-
 * #%L
 * ACM Default Plugin: admin
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import com.armedia.acm.plugins.admin.exception.AcmCmisConfigurationException;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

/**
 * Created by nick.ferguson on 3/24/2017.
 */
public class CmisConfigurationPropertiesService
{
    private Logger log = LogManager.getLogger(getClass());

    private CmisConfigurationService cmisConfigurationService;

    private List<String> propertyNamesForIntegerValues;

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

                        log.debug("Reading [{}] with value [{}] from [{}]", proName, prop.getProperty(proName), propertyFile.getName());
                        if (isPropertyNameForIntegerValue(proName))
                        {
                            Integer value = Integer.valueOf(prop.getProperty(proName));
                            cmisJsonObj.put(proName, value);
                        }
                        else
                        {
                            cmisJsonObj.put(proName, prop.getProperty(proName));
                        }
                    }

                    log.debug("Finished reading property file: [{}]", propertyFile.getName());
                    cmisJsonArr.put(cmisJsonObj);
                }
            }

            return cmisJsonArr;

        }
        catch (Exception e)
        {
            log.error("Can't read CMIS properties file", e);
            throw new AcmCmisConfigurationException("Can't retrieve CMIS properties", e);
        }
    }

    public void setCmisConfigurationService(CmisConfigurationService cmisConfigurationService)
    {
        this.cmisConfigurationService = cmisConfigurationService;
    }

    public void setPropertyNamesForIntegerValues(List<String> propertyNamesForIntegerValues)
    {
        this.propertyNamesForIntegerValues = propertyNamesForIntegerValues;
    }

    private boolean isPropertyNameForIntegerValue(String propertyName)
    {
        return propertyNamesForIntegerValues != null
                && propertyNamesForIntegerValues.stream().filter(item -> item.equalsIgnoreCase(propertyName)).findFirst().isPresent();
    }
}
