package com.armedia.acm.services.users.model.ldap;

/*-
 * #%L
 * ACM Service: Users
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

public class AcmLdapSyncConfig extends AcmLdapConfig
{
    private String groupSearchBase;
    private String groupSearchFilter;
    private String auditUserId = AcmLdapConstants.DEFAULT_AUDIT_USER;
    private String userDomain;
    private String userPrefix;
    private String groupPrefix;
    private String userControlGroup;
    private String groupControlGroup;
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
    private String partialSyncCron;
    private String fullSyncCron;
    private Boolean syncEnabled;
    private String status;

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

    public String getUserPrefix()
    {
        return userPrefix;
    }

    public void setUserPrefix(String userPrefix)
    {
        this.userPrefix = userPrefix;
    }

    public String getGroupPrefix()
    {
        return groupPrefix;
    }

    public void setGroupPrefix(String groupPrefix)
    {
        this.groupPrefix = groupPrefix;
    }

    public String getUserControlGroup()
    {
        return userControlGroup;
    }

    public void setUserControlGroup(String userControlGroup)
    {
        this.userControlGroup = userControlGroup;
    }

    public String getGroupControlGroup()
    {
        return groupControlGroup;
    }

    public void setGroupControlGroup(String groupControlGroup)
    {
        this.groupControlGroup = groupControlGroup;
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

    public String getPartialSyncCron()
    {
        return partialSyncCron;
    }

    public void setPartialSyncCron(String partialSyncCron)
    {
        this.partialSyncCron = partialSyncCron;
    }

    public String getFullSyncCron()
    {
        return fullSyncCron;
    }

    public void setFullSyncCron(String fullSyncCron)
    {
        this.fullSyncCron = fullSyncCron;
    }

    public Boolean getSyncEnabled()
    {
        return syncEnabled;
    }

    public void setSyncEnabled(Boolean syncEnabled)
    {
        this.syncEnabled = syncEnabled;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }
}
