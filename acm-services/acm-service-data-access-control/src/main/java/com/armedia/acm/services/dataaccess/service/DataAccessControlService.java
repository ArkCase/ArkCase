package com.armedia.acm.services.dataaccess.service;

import com.armedia.acm.files.propertymanager.PropertyFileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DataAccessControlService {

    private Logger log = LoggerFactory.getLogger(getClass());

    private String propertiesFile;
    private PropertyFileManager propertyFileManager;

    private Map<String, String> properties = new HashMap<>();

    public void initBean()
    {
        try
        {
            properties = getPropertyFileManager().readFromFileAsMap(new File(getPropertiesFile()));
        }
        catch (IOException e)
        {
            log.error("Could not read properties file [{}]", propertiesFile);
        }
    }

    public void  saveProperties(Map<String, String> properties)
    {
        getPropertyFileManager().storeMultiple(properties, getPropertiesFile(), true);
    }

    public Map<String, String> loadProperties()
    {
        return properties;
    }

    public String getPropertiesFile()
    {
        return propertiesFile;
    }

    public void setPropertiesFile(String propertiesFile)
    {
        this.propertiesFile = propertiesFile;
    }

    public PropertyFileManager getPropertyFileManager()
    {
        return propertyFileManager;
    }

    public void setPropertyFileManager(PropertyFileManager propertyFileManager)
    {
        this.propertyFileManager = propertyFileManager;
    }

}
