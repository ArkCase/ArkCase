package com.armedia.acm.services.users.service.ldap;

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

import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.group.AcmGroup;
import com.armedia.acm.services.users.model.ldap.AcmLdapConstants;
import com.armedia.acm.services.users.model.ldap.AcmLdapSyncConfig;
import com.armedia.acm.services.users.model.ldap.MapperUtils;
import com.armedia.acm.spring.SpringContextHolder;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;

import java.util.Map;

/**
 * Creates LDAP context for the new entries with the specified attributes
 */
public class LdapEntryTransformer
{
    private AcmLdapConfiguration ldapConfiguration;

    private SpringContextHolder acmContextHolder;

    private Logger log = LogManager.getLogger(getClass());

    public DirContextAdapter createContextForNewUserEntry(String directoryName, AcmUser user, String userPassword, String baseDC)
    {
        DirContextAdapter context = new DirContextAdapter(MapperUtils.stripBaseFromDn(user.getDistinguishedName(), baseDC));

        AcmLdapUserSyncConfig config = acmContextHolder.getAllBeansOfType(AcmLdapUserSyncConfig.class)
                .get(String.format("%s_userSync", directoryName));
        AcmLdapSyncConfig syncConfig = acmContextHolder.getAllBeansOfType(AcmLdapSyncConfig.class)
                .get(String.format("%s_sync", directoryName));

        Map<String, String> userAttributes = config.getAttributes();
        long timestamp = System.currentTimeMillis();
        String userId = StringUtils.substringBeforeLast(user.getUserId(), "@");

        for (Map.Entry<String, String> attributeEntry : userAttributes.entrySet())
        {
            String attr = attributeEntry.getKey();
            String value = attributeEntry.getValue();

            String key = (String) ldapConfiguration.getAttributes().get("ldapAddUserValuesTransformations").get(attr);
            if (key != null)
            {
                if (key.equals(AcmLdapConstants.LDAP_OBJECT_CLASS_ATTR))
                {
                    String[] classes = value.split(",");
                    context.setAttributeValues(attr, classes);
                }
                else if (key.equals(AcmLdapConstants.LDAP_USER_ID_ATTR))
                {
                    context.setAttributeValue(attr, userId);
                }
                else if (key.equals(AcmLdapConstants.LDAP_FIRST_NAME_ATTR))
                {
                    context.setAttributeValue(attr, user.getFirstName());
                }
                else if (key.equals(AcmLdapConstants.LDAP_LAST_NAME_ATTR))
                {
                    context.setAttributeValue(attr, user.getLastName());
                }
                else if (key.equals(AcmLdapConstants.LDAP_MAIL_ATTR))
                {
                    context.setAttributeValue(attr, user.getMail());
                }
                else if (key.equals(AcmLdapConstants.LDAP_FULL_NAME_ATTR))
                {
                    context.setAttributeValue(attr, user.getFullName());
                }
                else if (key.equals(AcmLdapConstants.LDAP_PASSWORD_ATTR))
                {
                    context.setAttributeValue(attr, userPassword);
                }
                else if (key.equals(AcmLdapConstants.LDAP_UNICODE_PASSWORD_ATTR))
                {
                    context.setAttributeValue(attr, MapperUtils.encodeUTF16LE(userPassword));
                }
                else if (key.equals(AcmLdapConstants.LDAP_UID_NUMBER_ATTR))
                {
                    context.setAttributeValue(attr, Long.toString(timestamp));
                }
                else if (key.equals(AcmLdapConstants.LDAP_GID_NUMBER_ATTR))
                {
                    context.setAttributeValue(attr, Long.toString(timestamp));
                }
                else if (key.equals(AcmLdapConstants.LDAP_HOME_DIRECTORY_ATTR))
                {
                    context.setAttributeValue(attr, String.format("/home/%s", userId));
                }
                else if (key.equals(AcmLdapConstants.LDAP_USER_PRINCIPAL_NAME_ATTR))
                {
                    String upnExpectedBySAML = syncConfig.getUserPrefix() + user.getMail();
                    context.setAttributeValue(attr, upnExpectedBySAML);
                }
                else
                {
                    context.setAttributeValue(attr, value);
                }
            }
        }

        return context;
    }

    public DirContextOperations createContextForEditUserEntry(DirContextOperations context, AcmUser user, String directoryName)
    {
        AcmLdapUserSyncConfig config = acmContextHolder.getAllBeansOfType(AcmLdapUserSyncConfig.class)
                .get(String.format("%s_userSync", directoryName));

        Map<String, String> userAttributes = config.getAttributes();
        Map<String, Object> ldapEditUserProperties = ldapConfiguration.getAttributes().get("ldapEditUserValuesTransformation");

        ldapEditUserProperties.keySet().forEach(propertyName -> {
            // get user's editable properties for the specific directory user-attributes config
            if (userAttributes.containsKey(propertyName))
            {
                String propertyValue = (String) ldapConfiguration.getAttributes().get("ldapAddUserValuesTransformations").get(propertyName);

                if (propertyValue.equals(AcmLdapConstants.LDAP_FIRST_NAME_ATTR))
                {
                    context.setAttributeValue(propertyName, user.getFirstName());
                }
                else if (propertyValue.equals(AcmLdapConstants.LDAP_LAST_NAME_ATTR))
                {
                    context.setAttributeValue(propertyName, user.getLastName());
                }
                else if (propertyValue.equals(AcmLdapConstants.LDAP_MAIL_ATTR))
                {
                    context.setAttributeValue(propertyName, user.getMail());
                }
                else if (propertyValue.equals(AcmLdapConstants.LDAP_FULL_NAME_ATTR))
                {
                    context.setAttributeValue(propertyName, String.format("%s %s", user.getFirstName(), user.getLastName()));
                }
                else if (propertyValue.equals(AcmLdapConstants.LDAP_HOME_DIRECTORY_ATTR))
                {
                    context.setAttributeValue(propertyName, String.format("/home/%s", user.getUserId()));
                }
            }
        });
        return context;
    }

    public DirContextAdapter createContextForNewGroupEntry(String directoryName, AcmGroup group, String baseDC)
    {
        DirContextAdapter context = new DirContextAdapter(MapperUtils.stripBaseFromDn(group.getDistinguishedName(), baseDC));

        AcmLdapGroupSyncConfig config = acmContextHolder.getAllBeansOfType(AcmLdapGroupSyncConfig.class)
                .get(String.format("%s_groupSync", directoryName));

        Map<String, String> groupAttributes = config.getAttributes();
        long timestamp = System.currentTimeMillis();

        String groupName = StringUtils.substringBeforeLast(group.getName(), "@");

        groupAttributes.forEach((attr, value) -> {
            String key = (String) ldapConfiguration.getAttributes().get("ldapAddGroupValuesTransformations").get(attr);
            switch (key)
            {
            case AcmLdapConstants.LDAP_OBJECT_CLASS_ATTR:
                String[] classes = value.split(",");
                context.setAttributeValues(attr, classes);
                break;
            case AcmLdapConstants.LDAP_GID_NUMBER_ATTR:
                context.setAttributeValue(attr, Long.toString(timestamp));
                break;
            case AcmLdapConstants.LDAP_FULL_NAME_ATTR:
                context.setAttributeValue(attr, groupName);
                break;
            case AcmLdapConstants.LDAP_MEMBER_ATTR:
                // set member attribute which is required to create a group entry
                context.setAttributeValue(attr, "");
                break;
            default:
                context.setAttributeValue(attr, value);
                break;
            }
        });

        return context;
    }

    public SpringContextHolder getAcmContextHolder()
    {
        return acmContextHolder;
    }

    public void setAcmContextHolder(SpringContextHolder acmContextHolder)
    {
        this.acmContextHolder = acmContextHolder;
    }

    public void setLdapConfiguration(AcmLdapConfiguration ldapConfiguration)
    {
        this.ldapConfiguration = ldapConfiguration;
    }
}
