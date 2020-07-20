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

public interface AcmLdapConstants
{
    int RETRY_ATTEMPTS = 3;
    long RETRY_TIMEOUT = 500L;
    String DEFAULT_OPEN_LDAP_DIRECTORY_NAME = "openldap";
    String DEFAULT_OPEN_ACTIVE_DIRECTORY_NAME = "activedirectory";
    String DEFAULT_AUDIT_USER = "LDAP-SYNC";
    String LDAP_FULL_NAME_ATTR = "fullName";
    String LDAP_FIRST_NAME_ATTR = "firstName";
    String LDAP_LAST_NAME_ATTR = "lastName";
    String LDAP_USER_ID_ATTR = "userId";
    String LDAP_MAIL_ATTR = "mail";
    String LDAP_MEMBER_ATTR = "member";
    String LDAP_PASSWORD_ATTR = "password";
    String LDAP_UNICODE_PASSWORD_ATTR = "unicodePassword";
    String LDAP_UID_NUMBER_ATTR = "uidNumber";
    String LDAP_GID_NUMBER_ATTR = "gidNumber";
    String LDAP_HOME_DIRECTORY_ATTR = "homeDirectory";
    String LDAP_OBJECT_CLASS_ATTR = "objectClass";
    String LDAP_CN_ATTR = "cn";
    String LDAP_GIVEN_NAME = "givenName";
    String LDAP_SN = "sn";
    String LDAP_UID = "uid";
    String LDAP_USER_ACCOUNT_CONTROL = "userAccountControl";
    String LDAP_USER_PASSWORD_ATTR = "userPassword";
    String SHADOW_WARNING_ATTR = "shadowWarning";
    String SHADOW_LAST_CHANGE_ATTR = "shadowLastChange";
    String SHADOW_MAX_ATTR = "shadowMax";
    String LDAP_ID_ATTR = "id";
    String LDAP_SAMA_ACCOUNT_NAME_ATTR = "sAMAccountName";
    String LDAP_USER_PRINCIPAL_NAME_ATTR = "userPrincipalName";
    String LDAP_UNICODE_PWD_ATTR = "unicodePwd";
    String LDAP_USER_OBJECT_CLASS_VALUE = "top,person,inetOrgPerson,organizationalPerson,posixAccount,uacPerson,shadowAccount";
    String LDAP_GROUP_OBJECT_CLASS_VALUE = "top,groupOfNames,sortableGroupofnames";
    String ACTIVE_DIRECTORY_USER_OBJECT_CLASS_VALUE = "top,person,user,organizationalPerson";
    String ACTIVE_DIRECTORY_GROUP_OBJECT_CLASS_VALUE = "top,group";
    String DC_DELETED = "DC=Deleted";
}
