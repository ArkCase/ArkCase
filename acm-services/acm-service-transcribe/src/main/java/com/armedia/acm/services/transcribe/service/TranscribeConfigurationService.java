package com.armedia.acm.services.transcribe.service;

import com.armedia.acm.configuration.service.ConfigurationPropertyService;
import com.armedia.acm.services.mediaengine.model.MediaEngineConfiguration;
import com.armedia.acm.services.transcribe.model.TranscribeConfiguration;

/**
 * Created by Vladimir Cherepnalkovski
 */
public class TranscribeConfigurationService
{
    private TranscribeConfiguration transcribeConfig;

    private ConfigurationPropertyService configurationPropertyService;

    public void saveProperties(MediaEngineConfiguration transcribeConfiguration)
    {
        configurationPropertyService.updateProperties(transcribeConfiguration);
    }

    public TranscribeConfiguration loadProperties()
    {
        return transcribeConfig;
    }

    public TranscribeConfiguration getTranscribeConfig()
    {
        return transcribeConfig;
    }

    public void setTranscribeConfig(TranscribeConfiguration transcribeConfig)
    {
        this.transcribeConfig = transcribeConfig;
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
