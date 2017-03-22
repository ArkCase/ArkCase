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
    String LDAP_FULL_NAME_ATTR = "fullName";
    String LDAP_FIRST_NAME_ATTR = "firstName";
    String LDAP_LAST_NAME_ATTR = "lastName";
    String LDAP_USER_ID_ATTR = "userId";
    String LDAP_MAIL_ATTR = "mail";
    String LDAP_MEMBER_OF_ATTR = "memberOf";
    String LDAP_MEMBER_ATTR = "member";
    String LDAP_ACCOUNT_CONTROL_ATTR = "userAccountControl";
    String LDAP_PASSWORD_ATTR = "password";
    String LDAP_UID_NUMBER_ATTR = "uidNumber";
    String LDAP_GID_NUMBER_ATTR = "gidNumber";
    String LDAP_HOME_DIRECTORY_ATTR = "homeDirectory";
    String LDAP_OBJECT_CLASS_ATTR = "objectClass";
    String LDAP_SHADOW_WARNING_ATTR = "shadowWarning";
    String LDAP_SHADOW_LAST_CHANGE_ATTR = "userAccountControl";
    String LDAP_SHADOW_MAX_ATTR = "userAccountControl";
}
