package com.armedia.acm.plugins.admin.service;

import com.armedia.acm.configuration.service.ConfigurationPropertyService;
import com.armedia.acm.plugins.admin.model.OutgoingIncomingEmailConversionConfig;

import java.util.Map;

public class OutgoingIncomingEmailConversionConfigurationService
{
    private OutgoingIncomingEmailConversionConfig outgoingIncomingEmailConversionConfig;
    private ConfigurationPropertyService configurationPropertyService;

    public Map<String, Object> getOutgoingIncomingEmailConversionConfiguration()
    {
        return getConfigurationPropertyService().getProperties(outgoingIncomingEmailConversionConfig);
    }

    public void saveOutgoingIncomingEmailConversionConfiguration(OutgoingIncomingEmailConversionConfig outgoingIncomingEmailConversionConfig)
    {
        getConfigurationPropertyService().updateProperties(outgoingIncomingEmailConversionConfig);
    }


    public OutgoingIncomingEmailConversionConfig getOutgoingIncomingEmailConversionConfig()
    {
        return outgoingIncomingEmailConversionConfig;
    }

    public void setOutgoingIncomingEmailConversionConfig(OutgoingIncomingEmailConversionConfig outgoingIncomingEmailConversionConfig)
    {
        this.outgoingIncomingEmailConversionConfig = outgoingIncomingEmailConversionConfig;
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
