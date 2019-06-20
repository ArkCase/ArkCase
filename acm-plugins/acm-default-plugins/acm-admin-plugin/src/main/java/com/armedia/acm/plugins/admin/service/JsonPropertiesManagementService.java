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

import com.armedia.acm.plugins.admin.exception.AcmPropertiesManagementException;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.File;
import java.util.Iterator;

/**
 * Created by sergey on 4/13/16.
 */
public class JsonPropertiesManagementService
{
    private Logger log = LogManager.getLogger(getClass());
    private String propertiesFileLocation;

    /**
     * Get property value
     *
     * @param propertyName
     * @return
     * @throws AcmPropertiesManagementException
     */
    public JSONObject getProperty(String propertyName) throws AcmPropertiesManagementException
    {
        JSONObject result = new JSONObject();
        JSONObject props = loadPropertiesFile();
        if (props != null && props.has(propertyName))
        {
            result.put(propertyName, props.get(propertyName));
        }
        return result;
    }

    /**
     * Get all properties. If file is missed then return empty JSON object
     *
     * @return
     * @throws AcmPropertiesManagementException
     */
    public JSONObject getProperties() throws AcmPropertiesManagementException
    {
        JSONObject props = loadPropertiesFile();
        if (props == null)
        {
            props = new JSONObject();
        }
        return props;
    }

    /**
     * Update property
     *
     * @param newProps
     * @return
     * @throws AcmPropertiesManagementException
     */
    public JSONObject updateProperties(JSONObject newProps) throws AcmPropertiesManagementException
    {
        if (newProps == null)
        {
            throw new AcmPropertiesManagementException("Can't store null into properties file");
        }
        JSONObject props = loadPropertiesFile();
        if (props == null)
        {
            props = newProps;
        }
        else
        {
            Iterator<String> keys = newProps.keys();
            while (keys.hasNext())
            {
                String key = keys.next();
                props.put(key, newProps.get(key));
            }
        }

        updatePropertiesFile(props);
        return props;

    }

    /**
     * Load application properties file
     *
     * @return
     * @throws AcmPropertiesManagementException
     */
    private JSONObject loadPropertiesFile() throws AcmPropertiesManagementException
    {
        try
        {
            File file = FileUtils.getFile(propertiesFileLocation);
            String resource = FileUtils.readFileToString(file, "UTF-8");
            return new JSONObject(resource);

        }
        catch (Exception e)
        {
            log.warn(String.format("Can't read application properties file %s", propertiesFileLocation));
            return null;
        }
    }

    /**
     * Update application properties file
     *
     * @param newProps
     * @return
     */
    private JSONObject updatePropertiesFile(JSONObject newProps) throws AcmPropertiesManagementException
    {
        try
        {
            File file = FileUtils.getFile(propertiesFileLocation);
            FileUtils.writeStringToFile(file, newProps.toString(), "UTF-8");
        }
        catch (Exception e)
        {
            log.error(String.format("Can't update properties file %s", propertiesFileLocation));
            throw new AcmPropertiesManagementException("Can't update properties file", e);
        }
        return newProps;
    }

    public void setPropertiesFileLocation(String propertiesFileLocation)
    {
        this.propertiesFileLocation = propertiesFileLocation;
    }
}
