package com.armedia.acm.pluginmanager;


import java.io.Serializable;

public class AcmPlugin implements Serializable
{
    private static final long serialVersionUID = -364262047493069587L;

    private String pluginName;
    private boolean navigatorTab;
    private String navigatorTabName;
    private String navigatorImage;
    private String homeUrl;
    private boolean enabled;



    public String getPluginName()
    {
        return pluginName;
    }

    public void setPluginName(String pluginName)
    {
        this.pluginName = pluginName;
    }

    public boolean isNavigatorTab()
    {
        return navigatorTab;
    }

    public void setNavigatorTab(boolean navigatorTab)
    {
        this.navigatorTab = navigatorTab;
    }

    public String getNavigatorTabName()
    {
        return navigatorTabName;
    }

    public void setNavigatorTabName(String navigatorTabName)
    {
        this.navigatorTabName = navigatorTabName;
    }

    public String getHomeUrl()
    {
        return homeUrl;
    }

    public void setHomeUrl(String homeUrl)
    {
        this.homeUrl = homeUrl;
    }

    public boolean isEnabled()
    {
        return enabled;
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    public String getNavigatorImage() {
        return navigatorImage;
    }

    public void setNavigatorImage(String navigatorImage) {
        this.navigatorImage = navigatorImage;
    }
}
