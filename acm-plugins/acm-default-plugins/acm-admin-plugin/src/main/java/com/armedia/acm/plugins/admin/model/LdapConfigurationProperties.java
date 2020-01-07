package com.armedia.acm.plugins.admin.model;

/*-
 * #%L
 * ACM Default Plugin: admin
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

public interface LdapConfigurationProperties
{
    String LDAP_PROP_ID = "ldapConfig.id";
    String LDAP_PROP_DIRECTORY_TYPE = "ldapConfig.directoryType";
    String LDAP_PROP_BASE = "ldapConfig.base";
    String LDAP_PROP_AUTH_USER_DN = "ldapConfig.authUserDn";
    String LDAP_PROP_AUTH_USER_PASSWORD = "ldapConfig.authUserPassword";
    String LDAP_PROP_USER_SEARCH_BASE = "ldapConfig.userSearchBase";
    String LDAP_PROP_USER_SEARCH_FILTER = "ldapConfig.userSearchFilter";
    String LDAP_PROP_GROUP_SEARCH_BASE = "ldapConfig.groupSearchBase";
    String LDAP_PROP_GROUP_SEARCH_FILTER = "ldapConfig.groupSearchFilter";
    String LDAP_PROP_CHANGED_GROUP_SEARCH_FILTER = "ldapConfig.changedGroupSearchFilter";
    String LDAP_PROP_GROUP_SEARCH_PAGE_FILTER = "ldapConfig.groupSearchPageFilter";
    String LDAP_PROP_CHANGED_GROUP_SEARCH_PAGE_FILTER = "ldapConfig.changedGroupSearchPageFilter";
    String LDAP_PROP_GROUPS_SORT_ATTRIBUTE = "ldapConfig.groupsSortingAttribute";
    String LDAP_PROP_ALL_USERS_FILTER = "ldapConfig.allUsersFilter";
    String LDAP_PROP_ALL_CHANGED_USERS_FILTER = "ldapConfig.allChangedUsersFilter";
    String LDAP_PROP_ALL_USERS_PAGE_FILTER = "ldapConfig.allUsersPageFilter";
    String LDAP_PROP_ALL_CHANGED_USERS_PAGE_FILTER = "ldapConfig.allChangedUsersPageFilter";
    String LDAP_PROP_ALL_USERS_SORT_ATTRIBUTE = "ldapConfig.allUsersSortingAttribute";
    String LDAP_PROP_GROUP_SEARCH_FILTER_FOR_USER = "ldapConfig.groupSearchFilterForUser";
    String LDAP_PROP_LDAP_URL = "ldapConfig.ldapUrl";
    String LDAP_PROP_USER_ID_ATTR_NAME = "ldapConfig.userIdAttributeName";
    String LDAP_PROP_USER_DOMAIN = "ldapConfig.userDomain";
    String LDAP_PROP_USER_PREFIX = "ldapConfig.userPrefix";
    String LDAP_PROP_GROUP_PREFIX = "ldapConfig.groupPrefix";
    String LDAP_PROP_USER_CONTROL_GROUP = "ldapConfig.userControlGroup";
    String LDAP_PROP_GROUP_CONTROL_GROUP = "ldapConfig.groupControlGroup";
    String LDAP_PROP_ENABLE_EDITING_LDAP_USERS = "ldapConfig.enableEditingLdapUsers";
    String LDAP_PROP_SYNC_ENABLED = "ldapConfig.syncEnabled";
    String LDAP_PROP_SYNC_PAGE_SIZE = "ldapConfig.syncPageSize";
    String LDAP_PROP_ADD_USER_TEMPLATE = "ldapConfig.addUserTemplate";
    String LDAP_PROP_ADD_GROUP_TEMPLATE = "ldapConfig.addGroupTemplate";
    String LDAP_FULL_SYNC_CRON = "ldapConfig.fullSyncCron";
    String LDAP_PARTIAL_SYNC_CRON = "ldapConfig.partialSyncCron";
}
