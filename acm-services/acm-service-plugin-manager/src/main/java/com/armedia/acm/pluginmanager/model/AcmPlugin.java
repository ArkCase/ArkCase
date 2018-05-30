package com.armedia.acm.pluginmanager.model;

/*-
 * #%L
 * ACM Service: Plugin Manager
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
     * Privileges supported by the plugin. Each plugin may have its own set of unique privileges.
     */
    private List<AcmPluginPrivilege> privileges;

    /**
     * ObjectTypes supported by the plugin. Each plugin may support one or more Object Types
     */
    private List<String> suportedObjectTypesNames;

    /**
     * The privilege required to execute each URL exported by this plugin. Each Spring MVC request mapping needs an
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

    public List<String> getSuportedObjectTypesNames()
    {
        return suportedObjectTypesNames;
    }

    public void setSuportedObjectTypesNames(List<String> suportedObjectTypesNames)
    {
        this.suportedObjectTypesNames = suportedObjectTypesNames;
    }
}
