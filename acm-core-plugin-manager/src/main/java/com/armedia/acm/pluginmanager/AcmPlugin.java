package com.armedia.acm.pluginmanager;


public class AcmPlugin
{
    private String pluginName;
    private boolean navigatorTab;
    private String navigatorTabName;
    private String navigatorViewName;

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

    public String getNavigatorViewName()
    {
        return navigatorViewName;
    }

    public void setNavigatorViewName(String navigatorViewName)
    {
        this.navigatorViewName = navigatorViewName;
    }
}
