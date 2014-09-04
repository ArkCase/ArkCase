package com.armedia.acm.services.users.model.ldap;

import java.util.Map;

/**
 * Created by armdev on 5/28/14.
 */
public class AcmLdapSyncConfig extends AcmLdapConfig
{
    private String groupSearchBase;
    private String groupSearchFilter;
    private Map<String, String> roleToGroupMap;

    public Map<String, String> getRoleToGroupMap()
    {
        return roleToGroupMap;
    }

    public void setRoleToGroupMap(Map<String, String> roleToGroupMap)
    {
        this.roleToGroupMap = roleToGroupMap;
    }

    public String getGroupSearchBase()
    {
        return groupSearchBase;
    }

    public void setGroupSearchBase(String groupSearchBase)
    {
        this.groupSearchBase = groupSearchBase;
    }

    public String getGroupSearchFilter()
    {
        return groupSearchFilter;
    }

    public void setGroupSearchFilter(String groupSearchFilter)
    {
        this.groupSearchFilter = groupSearchFilter;
    }
}
