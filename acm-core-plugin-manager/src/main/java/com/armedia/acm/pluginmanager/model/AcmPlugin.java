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
     * List of plugin-specific properties.
     */
    private Map<String, Object> pluginProperties = new HashMap<>();

    /**
     * Privileges supported by the plugin.  Each plugin may have its own set of unique privileges.
     */
    private List<AcmPluginPrivilege> privileges;

    /**
     * The privilege required to execute each URL exported by this plugin.  Each Spring MVC request mapping needs an
     * entry in this list, to define the privilege required to call that URL.
     */
    private List<AcmPluginUrlPrivilege> urlPrivileges;


    public String getPluginName()
    {
        return pluginName;
    }

    public void setPluginName(String pluginName)
    {
        this.pluginName = pluginName;
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

    public List<AcmPluginUrlPrivilege> getUrlPrivileges()
    {
        return urlPrivileges;
    }

    public void setUrlPrivileges(List<AcmPluginUrlPrivilege> urlPrivileges)
    {
        this.urlPrivileges = urlPrivileges;
    }
}
