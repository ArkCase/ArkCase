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

import java.util.List;

/**
 * Document a privilege used by a plugin. Privileges drive the role-based access control mechanism.
 *
 * Each privilege has a name, and a list of application roles that have the privilege. Application roles represent
 * logical user groups. The acm-user-login module is responsible for translating actual LDAP user group names to
 * application roles (via the applicationRoleToUserGroup.properties file).
 *
 * Each privilege is associated with URLs via the AcmPluginUrlPrivilege class.
 */
public class AcmPluginPrivilege
{
    private String privilegeName;
    private List<String> applicationRolesWithPrivilege;

    public String getPrivilegeName()
    {
        return privilegeName;
    }

    public void setPrivilegeName(String privilegeName)
    {
        this.privilegeName = privilegeName;
    }

    public List<String> getApplicationRolesWithPrivilege()
    {
        return applicationRolesWithPrivilege;
    }

    public void setApplicationRolesWithPrivilege(List<String> applicationRolesWithPrivilege)
    {
        this.applicationRolesWithPrivilege = applicationRolesWithPrivilege;
    }
}
