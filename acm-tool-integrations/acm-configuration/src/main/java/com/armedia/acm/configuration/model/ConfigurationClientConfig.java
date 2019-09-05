package com.armedia.acm.configuration.model;

import org.springframework.beans.factory.annotation.Value;

public class ConfigurationClientConfig
{

    @Value("${application.name.default}")
    private String defaultApplicationName;

    @Value("${application.name.active}")
    private String activeApplicationName;

    @Value("${configuration.server.update.file.path}")
    private String updateFilePath;

    @Value("${application.profile}")
    private String activeProfile;

    @Value("${configuration.server.url}")
    private String configurationUrl;

    private String updatePropertiesEndpoint;

    public String getDefaultApplicationName()
    {
        return defaultApplicationName;
    }

    public void setDefaultApplicationName(String defaultApplicationName)
    {
        this.defaultApplicationName = defaultApplicationName;
    }

    public String getActiveApplicationName()
    {
        return activeApplicationName;
    }

    public void setActiveApplicationName(String activeApplicationName)
    {
        this.activeApplicationName = activeApplicationName;
    }

    public String getUpdateFilePath()
    {
        return updateFilePath;
    }

    public void setUpdateFilePath(String updateFilePath)
    {
        this.updateFilePath = updateFilePath;
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

    public String getUpdateFilePropertiesEndpoint()
    {
        return String.format("%s%s", this.configurationUrl, this.updateFilePath);
    }
}
