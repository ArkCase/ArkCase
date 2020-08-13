package com.armedia.acm.plugins.ecm.service.impl;

import com.armedia.acm.configuration.service.ConfigurationPropertyService;
import com.armedia.acm.plugins.ecm.model.EcmFileDeDuplicationConfig;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

public class EcmFileDeDuplicationConfigurationServiceImpl {

    private Logger log = LogManager.getLogger(getClass());

    private EcmFileDeDuplicationConfig ecmFileDeDuplicationConfig;

    private ConfigurationPropertyService configurationPropertyService;

    public void writeConfiguration(Map<String, Object> properties)
    {
        configurationPropertyService.updateProperties(properties);
    }

    public EcmFileDeDuplicationConfig getEcmFileDeDuplicationConfig() {
        return ecmFileDeDuplicationConfig;
    }

    public void setEcmFileDeDuplicationConfig(EcmFileDeDuplicationConfig ecmFileDeDuplicationConfig) {
        this.ecmFileDeDuplicationConfig = ecmFileDeDuplicationConfig;
    }

    public ConfigurationPropertyService getConfigurationPropertyService() {
        return configurationPropertyService;
    }

    public void setConfigurationPropertyService(ConfigurationPropertyService configurationPropertyService) {
        this.configurationPropertyService = configurationPropertyService;
    }
}
