package com.armedia.acm.pluginmanager.model;

import java.util.List;

/**
 * Document a privilege used by a plugin.  Privileges drive the role-based access control mechanism.
 *
 * Each privilege has a name, and a list of application roles that have the privilege.  Application roles represent
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
