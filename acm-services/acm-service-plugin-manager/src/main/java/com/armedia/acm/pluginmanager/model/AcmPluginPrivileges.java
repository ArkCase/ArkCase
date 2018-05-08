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
import java.util.List;

public class AcmPluginPrivileges implements Serializable
{

    private static final long serialVersionUID = 5330357171041311491L;
    private String pluginName;
    private List<AcmPluginPrivilege> privileges;
    private List<AcmPluginUrlPrivilege> urlPrivileges;

    public String getPluginName()
    {
        return pluginName;
    }

    public void setPluginName(String pluginName)
    {
        this.pluginName = pluginName;
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
