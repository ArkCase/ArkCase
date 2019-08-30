package com.armedia.acm.configuration.model;

import org.springframework.beans.factory.annotation.Value;

public class ConfigurationClientConfig
{

    @Value("${application.name}")
    private String applicationName;

    @Value("${configuration.server.update.file.path}")
    private String updatePath;

    @Value("${application.profile}")
    private String activeProfile;

    @Value("${configuration.server.url}")
    private String configurationUrl;

    private String updatePropertiesEndpoint;

    public String getApplicationName()
    {
        return applicationName;
    }

    public void setApplicationName(String applicationName)
    {
        this.applicationName = applicationName;
    }

    public String getUpdatePath()
    {
        return updatePath;
    }

    public void setUpdatePath(String updatePath)
    {
        this.updatePath = updatePath;
    }

    public String getActiveProfile()
    {
        return activeProfile;
    }

    public void setActiveProfile(String activeProfile)
    {
        this.activeProfile = activeProfile;
    }

    public String getConfigurationUrl()
    {
        return configurationUrl;
    }

    public void setConfigurationUrl(String configurationUrl)
    {
        this.configurationUrl = configurationUrl;
    }

    public String getUpdatePropertiesEndpoint()
    {
        return String.format("%s%s", this.configurationUrl, this.updatePath);
    }
}
