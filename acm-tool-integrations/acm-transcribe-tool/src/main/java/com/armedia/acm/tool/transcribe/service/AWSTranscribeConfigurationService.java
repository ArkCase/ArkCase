package com.armedia.acm.tool.transcribe.service;

import com.armedia.acm.configuration.service.ConfigurationPropertyService;
import com.armedia.acm.tool.transcribe.model.AWSTranscribeConfiguration;

/**
 * Created by Vladimir Cherepnalkovski
 */
public class AWSTranscribeConfigurationService
{
    private AWSTranscribeConfiguration AWSTranscribeConfig;

    private ConfigurationPropertyService configurationPropertyService;

    public void saveAWSProperties(AWSTranscribeConfiguration AWSTranscribeConfig)
    {
        configurationPropertyService.updateProperties(AWSTranscribeConfig);
    }

    public AWSTranscribeConfiguration loadAWSProperties()
    {
        return AWSTranscribeConfig;
    }

    public AWSTranscribeConfiguration getAWSTranscribeConfig()
    {
        return AWSTranscribeConfig;
    }

    public void setAWSTranscribeConfig(AWSTranscribeConfiguration AWSTranscribeConfig)
    {
        this.AWSTranscribeConfig = AWSTranscribeConfig;
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
