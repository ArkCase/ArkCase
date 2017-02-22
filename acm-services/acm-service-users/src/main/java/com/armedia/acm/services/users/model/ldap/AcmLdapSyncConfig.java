package com.armedia.acm.services.users.model.ldap;

import java.util.Map;


public class AcmLdapSyncConfig extends AcmLdapConfig
{
    private String groupSearchBase;
    private String groupSearchFilter;
    private Map<String, String> roleToGroupMap;
    private String auditUserId = AcmLdapConstants.DEFAULT_AUDIT_USER;
    private String userDomain;
    private String userSearchBase;
    private String userSearchFilter;
    private String allUsersFilter;
    private String allUsersSortingAttribute;
    private String groupSearchFilterForUser;
    private int syncPageSize = 500;
    private String allUsersPageFilter;
    private String groupSearchPageFilter;
    private String groupsSortingAttribute;

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

    public String getAuditUserId()
    {
        return auditUserId;
    }

    public void setAuditUserId(String auditUserId)
    {
        this.auditUserId = auditUserId;
    }

    public String getUserDomain()
    {
        return userDomain;
    }

    public void setUserDomain(String userDomain)
    {
        this.userDomain = userDomain;
    }

    public String getUserSearchBase()
    {
        return userSearchBase;
    }

    public void setUserSearchBase(String userSearchBase)
    {
        this.userSearchBase = userSearchBase;
    }

    public String getUserSearchFilter()
    {
        return userSearchFilter;
    }

    public void setUserSearchFilter(String userSearchFilter)
    {
        this.userSearchFilter = userSearchFilter;
    }

    public String getGroupSearchFilterForUser()
    {
        return groupSearchFilterForUser;
    }

    public void setGroupSearchFilterForUser(String groupSearchFilterForUser)
    {
        this.groupSearchFilterForUser = groupSearchFilterForUser;
    }

    public String getAllUsersFilter()
    {
        return allUsersFilter;
    }

    public void setAllUsersFilter(String allUsersFilter)
    {
        this.allUsersFilter = allUsersFilter;
    }

    public String getAllUsersSortingAttribute()
    {
        return allUsersSortingAttribute;
    }

    public void setAllUsersSortingAttribute(String allUsersSortingAttribute)
    {
        this.allUsersSortingAttribute = allUsersSortingAttribute;
    }

    public int getSyncPageSize()
    {
        return syncPageSize;
    }

    public void setSyncPageSize(int syncPageSize)
    {
        this.syncPageSize = syncPageSize;
    }

    public String getAllUsersPageFilter()
    {
        return allUsersPageFilter;
    }

    public void setAllUsersPageFilter(String allUsersPageFilter)
    {
        this.allUsersPageFilter = allUsersPageFilter;
    }

    public String getGroupSearchPageFilter()
    {
        return groupSearchPageFilter;
    }

    public void setGroupSearchPageFilter(String groupSearchPageFilter)
    {
        this.groupSearchPageFilter = groupSearchPageFilter;
    }

    public String getGroupsSortingAttribute()
    {
        return groupsSortingAttribute;
    }

    public void setGroupsSortingAttribute(String groupsSortingAttribute)
    {
        this.groupsSortingAttribute = groupsSortingAttribute;
    }
}
