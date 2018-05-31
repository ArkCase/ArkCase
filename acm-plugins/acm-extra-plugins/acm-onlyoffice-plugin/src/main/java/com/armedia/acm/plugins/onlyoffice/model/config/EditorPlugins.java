package com.armedia.acm.plugins.onlyoffice.model.config;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class EditorPlugins
{
    /**
     * Defines the array of the identifiers (as entered in config.json) for the plugins, which will automatically start
     * when the editor opens, and the order the plugins will run one-by-one.
     */
    private List<String> autostart;
    /**
     * Defines the array of absolute URLs to the plugin configuration files (config.json), which is defined relatively
     * to the url path.
     */
    private List<String> pluginsData;
    /**
     * Defines the absolute URL to the directory where the plugins are stored. Deprecated since version 4.3, please use
     * the absolute URLs in pluginsData field.
     */
    private String url;

    public List<String> getAutostart()
    {
        return autostart;
    }

    public void setAutostart(List<String> autostart)
    {
        this.autostart = autostart;
    }

    public List<String> getPluginsData()
    {
        return pluginsData;
    }

    public void setPluginsData(List<String> pluginsData)
    {
        this.pluginsData = pluginsData;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }
}
