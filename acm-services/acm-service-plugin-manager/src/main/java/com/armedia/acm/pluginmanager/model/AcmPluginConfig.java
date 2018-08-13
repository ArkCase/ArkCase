package com.armedia.acm.pluginmanager.model;

public class AcmPluginConfig
{
    private String pluginName;

    private boolean isEnabled;

    public AcmPluginConfig(String pluginName, boolean isEnabled)
    {
        this.pluginName = pluginName;
        this.isEnabled = isEnabled;
    }

    public String getPluginName()
    {
        return pluginName;
    }

    public boolean isEnabled()
    {
        return isEnabled;
    }
}
