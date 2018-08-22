package com.armedia.acm.form.config;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Iterator;

public class FormsTypeManagementService
{
    private Logger log = LoggerFactory.getLogger(getClass());
    private String propertiesFileLocation;

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
     * @throws FormsTypeManagementException
     */
    public JSONObject getProperties() throws FormsTypeManagementException
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
     * @throws FormsTypeManagementException
     */
    public JSONObject updateProperties(JSONObject newProps) throws FormsTypeManagementException
    {
        if (newProps == null)
        {
            throw new FormsTypeManagementException("Can't store null into properties file");
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
     * @throws FormsTypeManagementException
     */
    private JSONObject loadPropertiesFile() throws FormsTypeManagementException
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
    private JSONObject updatePropertiesFile(JSONObject newProps) throws FormsTypeManagementException
    {
        try
        {
            File file = FileUtils.getFile(propertiesFileLocation);
            FileUtils.writeStringToFile(file, newProps.toString(), "UTF-8");
        }
        catch (Exception e)
        {
            log.error(String.format("Can't update properties file %s", propertiesFileLocation));
            throw new FormsTypeManagementException("Can't update properties file", e);
        }
        return newProps;
    }

    public void setPropertiesFileLocation(String propertiesFileLocation)
    {
        this.propertiesFileLocation = propertiesFileLocation;
    }
}
