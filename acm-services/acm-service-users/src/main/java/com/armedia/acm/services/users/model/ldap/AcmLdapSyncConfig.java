package com.armedia.acm.services.users.model.ldap;

import org.apache.commons.lang3.StringUtils;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AcmLdapSyncConfig extends AcmLdapConfig
{
    private String groupSearchBase;
    private String groupSearchFilter;
    private String auditUserId = AcmLdapConstants.DEFAULT_AUDIT_USER;
    private String userDomain;
    private String userSearchBase;
    private String userSearchFilter;
    private String allUsersFilter;
    private String allChangedUsersFilter;
    private String allUsersSortingAttribute;
    private String groupSearchFilterForUser;
    private int syncPageSize = 500;
    private String allUsersPageFilter;
    private String allChangedUsersPageFilter;
    private String groupSearchPageFilter;
    private String changedGroupSearchPageFilter;
    private String changedGroupSearchFilter;
    private String groupsSortingAttribute;
    private String[] userSyncAttributes;
    private Map<String, String> roleToGroupMap;

    public Map<String, String> getRoleToGroupMap()
    {
        return roleToGroupMap;
    }

    public Map<String, List<String>> getGroupToRolesMap()
    {
        // generate all value-key pairs from the original map and then group the keys by these values
        return getRoleToGroupsMap().entrySet().stream()
                .flatMap(entry -> entry.getValue().stream()
                        .map(it -> new AbstractMap.SimpleEntry<>(it, entry.getKey())))
                .collect(Collectors.groupingBy(AbstractMap.SimpleEntry::getKey,
                        Collectors.mapping(AbstractMap.SimpleEntry::getValue, Collectors.toList())));
    }

    public Map<String, Set<String>> getRoleToGroupsMap()
    {
        Function<String, Set<String>> groupsStringToSet = s -> {
            String[] groupsPerRole = s.split(",");
            return Arrays.stream(groupsPerRole)
                    .filter(StringUtils::isNotEmpty)
                    .map(String::toUpperCase)
                    .collect(Collectors.toSet());
        };

        return roleToGroupMap.entrySet()
                .stream()
                .filter(entry -> StringUtils.isNotEmpty(entry.getKey()))
                .filter(entry -> StringUtils.isNotEmpty(entry.getValue()))
                .collect(
                        Collectors.toMap(entry -> entry.getKey().toUpperCase(),
                                entry -> groupsStringToSet.apply(entry.getValue()))
                );
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

    public String getAllChangedUsersFilter()
    {
        return allChangedUsersFilter;
    }

    public void setAllChangedUsersFilter(String allChangedUsersFilter)
    {
        this.allChangedUsersFilter = allChangedUsersFilter;
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

    public String getAllChangedUsersPageFilter()
    {
        return allChangedUsersPageFilter;
    }

    public void setAllChangedUsersPageFilter(String allChangedUsersPageFilter)
    {
        this.allChangedUsersPageFilter = allChangedUsersPageFilter;
    }

    public String getGroupSearchPageFilter()
    {
        return groupSearchPageFilter;
    }

    public void setGroupSearchPageFilter(String groupSearchPageFilter)
    {
        this.groupSearchPageFilter = groupSearchPageFilter;
    }

    public String getChangedGroupSearchPageFilter()
    {
        return changedGroupSearchPageFilter;
    }

    public void setChangedGroupSearchPageFilter(String changedGroupSearchPageFilter)
    {
        this.changedGroupSearchPageFilter = changedGroupSearchPageFilter;
    }

    public String getChangedGroupSearchFilter()
    {
        return changedGroupSearchFilter;
    }

    public void setChangedGroupSearchFilter(String changedGroupSearchFilter)
    {
        this.changedGroupSearchFilter = changedGroupSearchFilter;
    }

    public String getGroupsSortingAttribute()
    {
        return groupsSortingAttribute;
    }

    public void setGroupsSortingAttribute(String groupsSortingAttribute)
    {
        this.groupsSortingAttribute = groupsSortingAttribute;
    }

    public String[] getUserSyncAttributes()
    {
        return userSyncAttributes;
    }

    public void setUserSyncAttributes(String[] userSyncAttributes)
    {
        this.userSyncAttributes = userSyncAttributes;
    }
}
