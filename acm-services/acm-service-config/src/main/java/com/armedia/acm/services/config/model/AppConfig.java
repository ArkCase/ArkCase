package com.armedia.acm.services.config.model;

import com.armedia.acm.core.AcmApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

public class AppConfig implements AcmConfig, Serializable
{
    private transient final Logger log = LoggerFactory.getLogger(getClass());
    private static final long serialVersionUID = -1L;

    private String configName;
    private AcmApplication acmApplication;


    public String getConfigAsJson() {
        String json = "[]";
        ObjectMapper om = new ObjectMapper();
        try {
            json =  om.writeValueAsString(getAcmApplication());
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

    public AcmApplication getAcmApplication() {
        return acmApplication;
    }

    public void setAcmApplication(AcmApplication acmApplication) {
        this.acmApplication = acmApplication;
    }
}
