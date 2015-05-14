package com.armedia.acm.services.config.model;

import java.io.Serializable;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertyConfig implements AcmConfig, Serializable
{
    private transient final Logger log = LoggerFactory.getLogger(getClass());
    private static final long serialVersionUID = -1L;

    private String configName;
    private Map<Object, Object> properties;

    public String getConfigAsJson() {
        String json = "{}";
        ObjectMapper om = new ObjectMapper();
        try {
            json =  om.writeValueAsString(getProperties());
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
        return json;
    }
    public String getConfigName() {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    public Map<Object, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<Object, Object> properties) {
        this.properties = properties;
    }
}
