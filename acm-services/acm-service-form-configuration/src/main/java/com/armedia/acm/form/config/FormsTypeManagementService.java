package com.armedia.acm.form.config;

/*-
 * #%L
 * ACM Service: Form Configuration
 * %%
 * Copyright (C) 2014 - 2020 ArkCase LLC
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

import com.armedia.acm.configuration.service.ConfigurationPropertyService;
import com.armedia.acm.objectonverter.ObjectConverter;
import org.json.JSONObject;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.Iterator;
import java.util.Map;

public class FormsTypeManagementService
{
    private Logger log = LogManager.getLogger(getClass());
    private FormsTypeConfig formsTypeConfig;
    private ObjectConverter objectConverter;
    private ConfigurationPropertyService configurationPropertyService;

    /**
     * Get property value
     *
     * @param propertyName
     * @return
     * @throws FormsTypeManagementException
     */
    public JSONObject getProperty(String propertyName) throws FormsTypeManagementException
    {
        JSONObject result = new JSONObject();
        JSONObject props = loadPropertiesConfig();
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
     * @throws FormsTypeManagementException
     */
    public JSONObject getProperties() throws FormsTypeManagementException
    {
        JSONObject props = loadPropertiesConfig();
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
     * @throws FormsTypeManagementException
     */
    public JSONObject updateProperties(JSONObject newProps) throws FormsTypeManagementException
    {
        if (newProps == null)
        {
            throw new FormsTypeManagementException("Can't store null into properties file");
        }
        JSONObject props = loadPropertiesConfig();
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

        updatePropertiesConfig(props);
        return props;
    }

    /**
     * Load application properties
     *
     * @return
     * @throws FormsTypeManagementException
     */
    private JSONObject loadPropertiesConfig() throws FormsTypeManagementException
    {
        try
        {
            String resource = objectConverter.getJsonMarshaller().marshal(formsTypeConfig.getFormsTypeProps());
            return new JSONObject(resource);
        }
        catch (Exception e)
        {
            log.warn(String.format("Can't read application properties [%s]", "formsTypeConfiguration"));
            return null;
        }
    }

    /**
     * Update application properties
     *
     * @param props
     * @return
     */
    private JSONObject updatePropertiesConfig(JSONObject props) throws FormsTypeManagementException
    {
        try
        {
            FormsTypeConfig config = new FormsTypeConfig();
            config.setFormsTypeProps(objectConverter.getJsonUnmarshaller().unmarshall(props.toString(), Map.class));
            configurationPropertyService.updateProperties(config);
        }
        catch (Exception e)
        {
            log.error(String.format("Can't update application properties [%s]", "formsTypeConfiguration"));
            throw new FormsTypeManagementException("Can't update application properties", e);
        }
        return props;
    }

    public void setFormsTypeConfig(FormsTypeConfig formsTypeConfig)
    {
        this.formsTypeConfig = formsTypeConfig;
    }

    public void setObjectConverter(ObjectConverter objectConverter)
    {
        this.objectConverter = objectConverter;
    }

    public void setConfigurationPropertyService(ConfigurationPropertyService configurationPropertyService)
    {
        this.configurationPropertyService = configurationPropertyService;
    }
}
