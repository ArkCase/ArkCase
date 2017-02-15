package com.armedia.acm.plugins.admin.web.api;


public interface LdapConfigurationProperties
{
    String LDAP_PROP_ID = "ldapConfig.id";
    String LDAP_PROP_BASE = "ldapConfig.base";
    String LDAP_PROP_AUTH_USER_DN = "ldapConfig.authUserDn";
    String LDAP_PROP_AUTH_USER_PASSWORD = "ldapConfig.authUserPassword";
    String LDAP_PROP_USER_SEARCH_BASE = "ldapConfig.userSearchBase";
    String LDAP_PROP_USER_SEARCH_FILTER = "ldapConfig.userSearchFilter";
    String LDAP_PROP_GROUP_SEARCH_BASE = "ldapConfig.groupSearchBase";
    String LDAP_PROP_GROUP_SEARCH_FILTER = "ldapConfig.groupSearchFilter";
    String LDAP_PROP_GROUP_SEARCH_PAGE_FILTER = "ldapConfig.groupSearchPageFilter";
    String LDAP_PROP_GROUPS_SORT_ATTRIBUTE = "ldapConfig.groupsSortingAttribute";
    String LDAP_PROP_ALL_USERS_FILTER = "ldapConfig.allUsersFilter";
    String LDAP_PROP_ALL_USERS_PAGE_FILTER = "ldapConfig.allUsersPageFilter";
    String LDAP_PROP_ALL_USERS_SORT_ATTRIBUTE = "ldapConfig.allUsersSortingAttribute";
    String LDAP_PROP_GROUP_SEARCH_FILTER_FOR_USER = "ldapConfig.groupSearchFilterForUser";
    String LDAP_PROP_LDAP_URL = "ldapConfig.ldapUrl";
    String LDAP_PROP_USER_ID_ATTR_NAME = "ldapConfig.userIdAttributeName";
    String LDAP_PROP_USER_DOMAIN = "ldapConfig.userDomain";
    String LDAP_PROP_ENABLE_EDITING_LDAP_USERS = "ldapConfig.enableEditingLdapUsers";
    String LDAP_PROP_SYNC_PAGE_SIZE = "ldapConfig.syncPageSize";
}
