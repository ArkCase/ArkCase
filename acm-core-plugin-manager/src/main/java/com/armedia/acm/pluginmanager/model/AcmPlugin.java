package com.armedia.acm.pluginmanager.model;


import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AcmPlugin implements Serializable
{
    private static final long serialVersionUID = -364262047493069587L;

    private String pluginName;
    private boolean navigatorTab;
    private String navigatorTabName;
    private String homeUrl;
    private boolean enabled;
    private Map<String, Object> pluginProperties = new HashMap<>();
    private List<AcmPluginPrivilege> privileges;
    private List<AcmPluginUrlPrivilege> urlPrivileges;
    private String pluginImage;



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

    public Map<String, Object> getPluginProperties()
    {
        return pluginProperties;
    }

    public void setPluginProperties(Map<String, Object> pluginProperties)
    {
        this.pluginProperties = pluginProperties;
    }

    public List<AcmPluginPrivilege> getPrivileges()
    {
        return privileges;
    }

    public void setPrivileges(List<AcmPluginPrivilege> privileges)
    {
        this.privileges = privileges;
    }

    public String getPluginImage() {
        return pluginImage;
    }

    public void setPluginImage(String pluginImage) {
        this.pluginImage = pluginImage;
    }

    public List<AcmPluginUrlPrivilege> getUrlPrivileges()
    {
        return urlPrivileges;
    }

    public void setUrlPrivileges(List<AcmPluginUrlPrivilege> urlPrivileges)
    {
        this.urlPrivileges = urlPrivileges;
    }
}
