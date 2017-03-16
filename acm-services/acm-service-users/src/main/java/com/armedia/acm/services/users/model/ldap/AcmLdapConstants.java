package com.armedia.acm.services.users.model.ldap;

/**
 * Created by armdev on 4/15/15.
 */
public interface AcmLdapConstants
{
    int RETRY_ATTEMPTS = 3;
    long RETRY_TIMEOUT = 500L;
    String DEFAULT_AUDIT_USER = "LDAP-SYNC";
    String GROUP_OBJECT_TYPE = "LDAP_GROUP";
    String LDAP_USER_FULL_NAME_ATTR = "fullName";
    String LDAP_USER_FIRST_NAME_ATTR = "firstName";
    String LDAP_USER_LAST_NAME_ATTR = "lastName";
    String LDAP_USER_USER_ID_ATTR = "userId";
    String LDAP_USER_MAIL_ATTR = "mail";
    String LDAP_USER_MEMBER_OF_ATTR = "memberOf";
    String LDAP_USER_ACCOUNT_CONTROL_ATTR = "userAccountControl";
    String LDAP_USER_PASSWORD_ATTR = "password";
    String LDAP_USER_UID_NUMBER_ATTR = "uidNumber";
    String LDAP_USER_GID_NUMBER_ATTR = "gidNumber";
    String LDAP_USER_HOME_DIRECTORY_ATTR = "homeDirectory";
    String LDAP_USER_OBJECT_CLASS_ATTR = "objectClass";
    String LDAP_USER_SHADOW_WARNING_ATTR = "shadowWarning";
    String LDAP_USER_SHADOW_LAST_CHANGE_ATTR = "userAccountControl";
    String LDAP_USER_SHADOW_MAX_ATTR = "userAccountControl";
}
