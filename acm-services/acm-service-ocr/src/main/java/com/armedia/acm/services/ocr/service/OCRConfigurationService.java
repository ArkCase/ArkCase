package com.armedia.acm.services.ocr.service;

import com.armedia.acm.configuration.service.ConfigurationPropertyService;
import com.armedia.acm.plugins.ecm.model.EcmFileConfig;
import com.armedia.acm.services.mediaengine.model.MediaEngineConfiguration;
import com.armedia.acm.services.ocr.model.OCRConfiguration;

/**
 * Created by Vladimir Cherepnalkovski
 */
public class OCRConfigurationService
{
    private OCRConfiguration ocrConfig;
    private EcmFileConfig ecmFileConfig;

    private ConfigurationPropertyService configurationPropertyService;

    public void saveProperties(MediaEngineConfiguration ocrConfig)
    {
        configurationPropertyService.updateProperties(ocrConfig);

        ecmFileConfig.setSnowboundEnableOcr(ocrConfig.isEnabled());
        configurationPropertyService.updateProperties(ecmFileConfig);
    }

    public OCRConfiguration loadProperties()
    {
        return ocrConfig;
    }

    public OCRConfiguration getOcrConfig()
    {
        return ocrConfig;
    }

    public void setOcrConfig(OCRConfiguration ocrConfig)
    {
        this.ocrConfig = ocrConfig;
    }

    public ConfigurationPropertyService getConfigurationPropertyService()
    {
        return configurationPropertyService;
    }

    public void setConfigurationPropertyService(ConfigurationPropertyService configurationPropertyService)
    {
        this.configurationPropertyService = configurationPropertyService;
    }

    public EcmFileConfig getEcmFileConfig()
    {
        return ecmFileConfig;
    }

    public void setEcmFileConfig(EcmFileConfig ecmFileConfig)
    {
        this.ecmFileConfig = ecmFileConfig;
    }
}
