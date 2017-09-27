package com.armedia.acm.services.users.service.ldap;

import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.group.AcmGroup;
import com.armedia.acm.services.users.model.ldap.AcmLdapConstants;
import com.armedia.acm.services.users.model.ldap.MapperUtils;
import com.armedia.acm.spring.SpringContextHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Properties;

/**
 * Creates LDAP context for the new entries with the specified attributes
 */
public class LdapEntryTransformer
{
    private Properties ldapAddUserPropertiesFile;
    private Properties ldapEditUserPropertiesFile;
    private Properties ldapAddGroupPropertiesFile;
    private SpringContextHolder acmContextHolder;

    private Logger log = LoggerFactory.getLogger(getClass());

    public DirContextAdapter createContextForNewUserEntry(String directoryName, AcmUser user, String userPassword,
                                                          String baseDC, String userDomain)
            throws UnsupportedEncodingException
    {
        DirContextAdapter context = new DirContextAdapter(MapperUtils.stripBaseFromDn(user.getDistinguishedName(), baseDC));

        AcmLdapUserSyncConfig config = acmContextHolder.getAllBeansOfType(AcmLdapUserSyncConfig.class).
                get(String.format("%s_userSync", directoryName));

        Map<String, String> userAttributes = config.getAttributes();
        long timestamp = System.currentTimeMillis();
        String userId = user.getUserId();
        if (StringUtils.isNotEmpty(userDomain) && userId.endsWith("@" + userDomain))
        {
            userId = userId.substring(0, userId.indexOf(userDomain) - 1);
        }

        for (Map.Entry<String, String> attributeEntry : userAttributes.entrySet())
        {
            String attr = attributeEntry.getKey();
            String value = attributeEntry.getValue();

            String key = ldapAddUserPropertiesFile.getProperty(attr);
            if (key.equals(AcmLdapConstants.LDAP_OBJECT_CLASS_ATTR))
            {
                String[] classes = value.split(",");
                context.setAttributeValues(attr, classes);
            } else if (key.equals(AcmLdapConstants.LDAP_USER_ID_ATTR))
            {
                context.setAttributeValue(attr, userId);
            } else if (key.equals(AcmLdapConstants.LDAP_FIRST_NAME_ATTR))
            {
                context.setAttributeValue(attr, user.getFirstName());
            } else if (key.equals(AcmLdapConstants.LDAP_LAST_NAME_ATTR))
            {
                context.setAttributeValue(attr, user.getLastName());
            } else if (key.equals(AcmLdapConstants.LDAP_MAIL_ATTR))
            {
                context.setAttributeValue(attr, user.getMail());
            } else if (key.equals(AcmLdapConstants.LDAP_FULL_NAME_ATTR))
            {
                context.setAttributeValue(attr, user.getFullName());
            } else if (key.equals(AcmLdapConstants.LDAP_PASSWORD_ATTR))
            {
                context.setAttributeValue(attr, userPassword);

            } else if (key.equals(AcmLdapConstants.LDAP_UNICODE_PASSWORD_ATTR))
            {
                context.setAttributeValue(attr, MapperUtils.encodeUTF16LE(userPassword));
            } else if (key.equals(AcmLdapConstants.LDAP_UID_NUMBER_ATTR))
            {
                context.setAttributeValue(attr, Long.toString(timestamp));
            } else if (key.equals(AcmLdapConstants.LDAP_GID_NUMBER_ATTR))
            {
                context.setAttributeValue(attr, Long.toString(timestamp));
            } else if (key.equals(AcmLdapConstants.LDAP_HOME_DIRECTORY_ATTR))
            {
                context.setAttributeValue(attr, String.format("/home/%s", userId));
            } else
            {
                context.setAttributeValue(attr, value);
            }
        }

        return context;
    }

    public DirContextOperations createContextForEditUserEntry(DirContextOperations context, AcmUser user, String directoryName)
    {
        AcmLdapUserSyncConfig config = acmContextHolder.getAllBeansOfType(AcmLdapUserSyncConfig.class)
                .get(String.format("%s_userSync", directoryName));

        Map<String, String> userAttributes = config.getAttributes();

        ldapEditUserPropertiesFile.stringPropertyNames().forEach(propertyName ->
        {
            // get user's editable properties for the specific directory user-attributes config
            if (userAttributes.containsKey(propertyName))
            {
                String propertyValue = ldapAddUserPropertiesFile.getProperty(propertyName);

                if (propertyValue.equals(AcmLdapConstants.LDAP_FIRST_NAME_ATTR))
                {
                    context.setAttributeValue(propertyName, user.getFirstName());
                } else if (propertyValue.equals(AcmLdapConstants.LDAP_LAST_NAME_ATTR))
                {
                    context.setAttributeValue(propertyName, user.getLastName());
                } else if (propertyValue.equals(AcmLdapConstants.LDAP_MAIL_ATTR))
                {
                    context.setAttributeValue(propertyName, user.getMail());
                } else if (propertyValue.equals(AcmLdapConstants.LDAP_FULL_NAME_ATTR))
                {
                    context.setAttributeValue(propertyName, String.format("%s %s", user.getFirstName(), user.getLastName()));
                } else if (propertyValue.equals(AcmLdapConstants.LDAP_HOME_DIRECTORY_ATTR))
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

        groupAttributes.forEach((attr, value) ->
        {
            String key = ldapAddGroupPropertiesFile.getProperty(attr);
            if (key.equals(AcmLdapConstants.LDAP_OBJECT_CLASS_ATTR))
            {
                String[] classes = value.split(",");
                context.setAttributeValues(attr, classes);
            } else if (key.equals(AcmLdapConstants.LDAP_GID_NUMBER_ATTR))
            {
                context.setAttributeValue(attr, Long.toString(timestamp));
            } else if (key.equals(AcmLdapConstants.LDAP_FULL_NAME_ATTR))
            {
                context.setAttributeValue(attr, group.getName());
            } else if (key.equals(AcmLdapConstants.LDAP_MEMBER_ATTR))
            {
                // set member attribute which is required to create a group entry
                context.setAttributeValue(attr, "");
            } else
            {
                context.setAttributeValue(attr, value);
            }
        });

        return context;
    }

    public Properties getLdapAddUserPropertiesFile()
    {
        return ldapAddUserPropertiesFile;
    }

    public void setLdapAddUserPropertiesFile(Properties ldapAddUserPropertiesFile)
    {
        this.ldapAddUserPropertiesFile = ldapAddUserPropertiesFile;
    }

    public SpringContextHolder getAcmContextHolder()
    {
        return acmContextHolder;
    }

    public void setAcmContextHolder(SpringContextHolder acmContextHolder)
    {
        this.acmContextHolder = acmContextHolder;
    }

    public Properties getLdapEditUserPropertiesFile()
    {
        return ldapEditUserPropertiesFile;
    }

    public void setLdapEditUserPropertiesFile(Properties ldapEditUserProperiesFile)
    {
        this.ldapEditUserPropertiesFile = ldapEditUserProperiesFile;
    }

    public Properties getLdapAddGroupPropertiesFile()
    {
        return ldapAddGroupPropertiesFile;
    }

    public void setLdapAddGroupPropertiesFile(Properties ldapAddGroupPropertiesFile)
    {
        this.ldapAddGroupPropertiesFile = ldapAddGroupPropertiesFile;
    }
}
