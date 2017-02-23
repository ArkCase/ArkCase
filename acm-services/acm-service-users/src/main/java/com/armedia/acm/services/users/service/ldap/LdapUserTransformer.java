package com.armedia.acm.services.users.service.ldap;

import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.ldap.AcmLdapConstants;
import com.armedia.acm.services.users.model.ldap.MapperUtils;
import com.armedia.acm.spring.SpringContextHolder;
import org.springframework.ldap.core.DirContextAdapter;

import java.util.Map;
import java.util.Properties;

/**
 *
 */
public class LdapUserTransformer
{
    private Properties ldapAddUserPropertiesFile;
    private SpringContextHolder acmContextHolder;

    public DirContextAdapter createContextForNewUser(String directoryName, AcmUser user, String userPassword, String baseDC)
    {
        DirContextAdapter context = new DirContextAdapter(MapperUtils.stripBaseFromDn(user.getDistinguishedName(), baseDC));

        AcmLdapUserSyncConfig config = acmContextHolder.getAllBeansOfType(AcmLdapUserSyncConfig.class).
                get(String.format("%s_userSync", directoryName));

        Map<String, String> userAttributes = config.getAttributes();
        long timestamp = System.currentTimeMillis();

        userAttributes.entrySet().forEach(entry ->
        {
            String key = ldapAddUserPropertiesFile.getProperty(entry.getKey());
            if (key.equals(AcmLdapConstants.LDAP_USER_OBJECT_CLASS_ATTR))
            {
                String[] classes = entry.getValue().split(",");
                context.setAttributeValues(entry.getKey(), classes);
            } else if (key.equals(AcmLdapConstants.LDAP_USER_USER_ID_ATTR))
            {
                context.setAttributeValue(entry.getKey(), user.getUserId());
            } else if (key.equals(AcmLdapConstants.LDAP_USER_FIRST_NAME_ATTR))
            {
                context.setAttributeValue(entry.getKey(), user.getFirstName());
            } else if (key.equals(AcmLdapConstants.LDAP_USER_LAST_NAME_ATTR))
            {
                context.setAttributeValue(entry.getKey(), user.getLastName());
            } else if (key.equals(AcmLdapConstants.LDAP_USER_MAIL_ATTR))
            {
                context.setAttributeValue(entry.getKey(), user.getMail());
            } else if (key.equals(AcmLdapConstants.LDAP_USER_FULL_NAME_ATTR))
            {
                context.setAttributeValue(entry.getKey(), String.format("%s %s", user.getFirstName(), user.getLastName()));
            } else if (key.equals(AcmLdapConstants.LDAP_USER_PASSWORD_ATTR))
            {
                context.setAttributeValue(entry.getKey(), userPassword);
            } else if (key.equals(AcmLdapConstants.LDAP_USER_UID_NUMBER_ATTR))
            {
                context.setAttributeValue(entry.getKey(), Long.toString(timestamp));
            } else if (key.equals(AcmLdapConstants.LDAP_USER_GID_NUMBER_ATTR))
            {
                context.setAttributeValue(entry.getKey(), Long.toString(timestamp));
            } else if (key.equals(AcmLdapConstants.LDAP_USER_HOME_DIRECTORY_ATTR))
            {
                context.setAttributeValue(entry.getKey(), String.format("/home/%s", user.getUserId()));
            } else if (key.equals(AcmLdapConstants.LDAP_USER_MEMBER_OF_ATTR))
            {
                user.getGroups().forEach(
                        group -> context.setAttributeValue(entry.getKey(), group.getDistinguishedName())
                );
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
}
