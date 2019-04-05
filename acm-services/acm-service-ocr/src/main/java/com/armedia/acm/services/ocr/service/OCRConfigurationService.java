package com.armedia.acm.services.ocr.service;

import com.armedia.acm.configuration.service.ConfigurationPropertyService;
import com.armedia.acm.services.mediaengine.model.MediaEngineConfiguration;
import com.armedia.acm.services.ocr.model.OCRConfiguration;

/**
 * Created by Vladimir Cherepnalkovski
 */
public class OCRConfigurationService
{
    private OCRConfiguration ocrConfig;

    private ConfigurationPropertyService configurationPropertyService;

    public void saveProperties(MediaEngineConfiguration ocrConfig)
    {
        configurationPropertyService.updateProperties(ocrConfig);
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
}
