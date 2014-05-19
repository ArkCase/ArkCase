package com.armedia.acm.pluginmanager.model;


import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AcmPlugin implements Serializable
{
    private static final long serialVersionUID = -364262047493069587L;

    /**
     * Internal name.
     */
    private String pluginName;

    /**
     * Whether this plugin is a navigator tab.
     */
    private boolean navigatorTab;

    /**
     * Text to appear on the navigator tab.
     */
    private String navigatorTabName;

    /**
     * URL to invoke when the navigator tab is clicked.
     */
    private String homeUrl;

    /**
     * Whether the navigator tab should be enabled.  If false, the tab will not appear.
     */
    private boolean enabled;

    /**
     * List of plugin-specific properties.
     */
    private Map<String, Object> pluginProperties = new HashMap<>();

    /**
     * Privileges supported by the plugin.  Each plugin may have its own set of unique privileges.
     */
    private List<AcmPluginPrivilege> privileges;

    /**
     * Privilege required to see the navigator tab.  For the navigator tab to appear, this plugin must be a
     * navigator plugin (navigatorTab); it must be enabled (enabled); and the user must have this privilege.
     */
    private AcmPluginPrivilege navigatorTabPrivilegeRequired;

    /**
     * The privilege required to execute each URL exported by this plugin.  Each Spring MVC request mapping needs an
     * entry in this list, to define the privilege required to call that URL.
     */
    private List<AcmPluginUrlPrivilege> urlPrivileges;

    /**
     * Image name of the image to appear on the navigator tab.
     */
    private String pluginImage;

    public AcmPluginPrivilege getNavigatorTabPrivilegeRequired()
    {
        return navigatorTabPrivilegeRequired;
    }

    public void setNavigatorTabPrivilegeRequired(AcmPluginPrivilege navigatorTabPrivilegeRequired)
    {
        this.navigatorTabPrivilegeRequired = navigatorTabPrivilegeRequired;
    }

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
