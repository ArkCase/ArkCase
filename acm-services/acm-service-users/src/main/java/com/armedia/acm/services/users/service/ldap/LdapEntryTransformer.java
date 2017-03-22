package com.armedia.acm.services.users.service.ldap;

import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.group.AcmGroup;
import com.armedia.acm.services.users.model.ldap.AcmLdapConstants;
import com.armedia.acm.services.users.model.ldap.MapperUtils;
import com.armedia.acm.spring.SpringContextHolder;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;

import java.util.Map;
import java.util.Properties;

/**
 *
 */
public class LdapEntryTransformer
{
    private Properties ldapAddUserPropertiesFile;
    private Properties ldapEditUserPropertiesFile;
    private Properties ldapAddGroupPropertiesFile;
    private SpringContextHolder acmContextHolder;

    public DirContextAdapter createContextForNewUserEntry(String directoryName, AcmUser user, String userPassword, String baseDC)
    {
        DirContextAdapter context = new DirContextAdapter(MapperUtils.stripBaseFromDn(user.getDistinguishedName(), baseDC));

        AcmLdapUserSyncConfig config = acmContextHolder.getAllBeansOfType(AcmLdapUserSyncConfig.class).
                get(String.format("%s_userSync", directoryName));

        Map<String, String> userAttributes = config.getAttributes();
        long timestamp = System.currentTimeMillis();

        userAttributes.entrySet().forEach(entry ->
        {
            String key = ldapAddUserPropertiesFile.getProperty(entry.getKey());
            if (key.equals(AcmLdapConstants.LDAP_OBJECT_CLASS_ATTR))
            {
                String[] classes = entry.getValue().split(",");
                context.setAttributeValues(entry.getKey(), classes);
            } else if (key.equals(AcmLdapConstants.LDAP_USER_ID_ATTR))
            {
                context.setAttributeValue(entry.getKey(), user.getUserId());
            } else if (key.equals(AcmLdapConstants.LDAP_FIRST_NAME_ATTR))
            {
                context.setAttributeValue(entry.getKey(), user.getFirstName());
            } else if (key.equals(AcmLdapConstants.LDAP_LAST_NAME_ATTR))
            {
                context.setAttributeValue(entry.getKey(), user.getLastName());
            } else if (key.equals(AcmLdapConstants.LDAP_MAIL_ATTR))
            {
                context.setAttributeValue(entry.getKey(), user.getMail());
            } else if (key.equals(AcmLdapConstants.LDAP_FULL_NAME_ATTR))
            {
                context.setAttributeValue(entry.getKey(), String.format("%s %s", user.getFirstName(), user.getLastName()));
            } else if (key.equals(AcmLdapConstants.LDAP_PASSWORD_ATTR))
            {
                context.setAttributeValue(entry.getKey(), userPassword);
            } else if (key.equals(AcmLdapConstants.LDAP_UID_NUMBER_ATTR))
            {
                context.setAttributeValue(entry.getKey(), Long.toString(timestamp));
            } else if (key.equals(AcmLdapConstants.LDAP_GID_NUMBER_ATTR))
            {
                context.setAttributeValue(entry.getKey(), Long.toString(timestamp));
            } else if (key.equals(AcmLdapConstants.LDAP_HOME_DIRECTORY_ATTR))
            {
                context.setAttributeValue(entry.getKey(), String.format("/home/%s", user.getUserId()));
            } else if (key.equals(AcmLdapConstants.LDAP_MEMBER_OF_ATTR))
            {
                user.getGroups().forEach(
                        group -> context.addAttributeValue(entry.getKey(), group.getDistinguishedName())
                );
            } else
            {
                context.setAttributeValue(entry.getKey(), entry.getValue());
            }
        });

        return context;
    }

    public DirContextOperations createContextForEditUserEntry(DirContextOperations context, AcmUser user, String directoryName)
    {
        AcmLdapUserSyncConfig config = acmContextHolder.getAllBeansOfType(AcmLdapUserSyncConfig.class).
                get(String.format("%s_userSync", directoryName));

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

        AcmLdapGroupSyncConfig config = acmContextHolder.getAllBeansOfType(AcmLdapGroupSyncConfig.class).
                get(String.format("%s_groupSync", directoryName));

        Map<String, String> groupAttributes = config.getAttributes();
        long timestamp = System.currentTimeMillis();

        groupAttributes.entrySet().forEach(entry ->
        {
            String key = ldapAddGroupPropertiesFile.getProperty(entry.getKey());
            if (key.equals(AcmLdapConstants.LDAP_OBJECT_CLASS_ATTR))
            {
                String[] classes = entry.getValue().split(",");
                context.setAttributeValues(entry.getKey(), classes);
            } else if (key.equals(AcmLdapConstants.LDAP_GID_NUMBER_ATTR))
            {
                context.setAttributeValue(entry.getKey(), Long.toString(timestamp));
            } else if (key.equals(AcmLdapConstants.LDAP_FULL_NAME_ATTR))
            {
                context.setAttributeValue(entry.getKey(), group.getName());
            } else if (key.equals(AcmLdapConstants.LDAP_MEMBER_ATTR))
            {
                // set member attribute which is required to create a group entry
                context.setAttributeValue(entry.getKey(), "");
            } else
            {
                context.setAttributeValue(entry.getKey(), entry.getValue());
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
