package com.armedia.acm.plugins.onlyoffice.model.config;

/*-
 * #%L
 * ACM Extra Plugin: OnlyOffice Integration
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

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
