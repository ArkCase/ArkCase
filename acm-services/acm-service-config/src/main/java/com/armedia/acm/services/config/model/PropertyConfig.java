package com.armedia.acm.services.config.model;

import com.armedia.acm.crypto.properties.AcmEncryptablePropertyUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class PropertyConfig implements AcmConfig, Serializable, InitializingBean
{
    private transient final Logger log = LoggerFactory.getLogger(getClass());
    private static final long serialVersionUID = -1L;

    private String configName;
    private Map<Object, Object> properties = new HashMap<>();
    private String configDescription;
    private AcmEncryptablePropertyUtils encryptablePropertyUtils;

    public String getConfigAsJson()
    {
        String json = "{}";
        ObjectMapper om = new ObjectMapper();
        try
        {
            json = om.writeValueAsString(getProperties());
        } catch (JsonProcessingException e)
        {
            log.error(e.getMessage());
        }
        return json;
    }

    public String getConfigName()
    {
        return configName;
    }

    public void setConfigName(String configName)
    {
        this.configName = configName;
    }

    public Map<Object, Object> getProperties()
    {
        return properties;
    }

    public void setProperties(Map<Object, Object> properties)
    {
        this.properties = properties;
    }

    @Override
    public String getConfigDescription()
    {
        return configDescription;
    }

    public void setConfigDescription(String configDescription)
    {
        this.configDescription = configDescription;
    }

    public AcmEncryptablePropertyUtils getEncryptablePropertyUtils()
    {
        return encryptablePropertyUtils;
    }

    public void setEncryptablePropertyUtils(AcmEncryptablePropertyUtils encryptablePropertyUtils)
    {
        this.encryptablePropertyUtils = encryptablePropertyUtils;
    }

    @Override
    public void afterPropertiesSet() throws Exception
    {
        log.debug("config name: {}", getConfigName());
        getEncryptablePropertyUtils().decryptProperties(properties);
    }
}
