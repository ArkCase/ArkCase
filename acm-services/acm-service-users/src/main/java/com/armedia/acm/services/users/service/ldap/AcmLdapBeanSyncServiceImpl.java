package com.armedia.acm.services.users.service.ldap;

import com.armedia.acm.configuration.service.ConfigurationPropertyService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.Message;

import java.util.HashMap;
import java.util.Map;

public class AcmLdapBeanSyncServiceImpl implements AcmLdapBeanSyncService, InitializingBean
{

    @Autowired
    private BeanFactory beanFactory;

    private AcmLdapConfiguration acmLdapConfig;

    private BeanDefinitionRegistry beanDefinitionRegistry;

    private ConfigurationPropertyService configurationPropertyService;

    private static final Logger logger = LogManager.getLogger(AcmLdapBeanSyncServiceImpl.class);

    @JmsListener(destination = "reload.ldap.beans", containerFactory = "jmsTopicListenerContainerFactory")
    public void onLdapChanged(Message message)
    {
        logger.info("Refreshing on ldap change...");

        sync();
    }

    @Override
    public void sync()
    {
        syncLdapGroupConfigAttributes();
        syncLdapUserConfigAttributes();
    }

    @Override
    public void syncLdapGroupConfigAttributes()
    {
        Map<String, Object> ldapGroupSyncConfigAttributes = acmLdapConfig.getAttributes().get("ldapAddGroupConfig");

        ldapGroupSyncConfigAttributes.keySet().forEach(directory -> {
            logger.debug("Register bean definition for: [{}]", AcmLdapGroupSyncConfig.class);
            BeanDefinitionBuilder jobDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(AcmLdapGroupSyncConfig.class);
            jobDefinitionBuilder.addPropertyValue("attributes", ldapGroupSyncConfigAttributes.get(directory));
            beanDefinitionRegistry.registerBeanDefinition(directory + "_groupSync", jobDefinitionBuilder.getBeanDefinition());
        });
    }

    @Override
    public void syncLdapUserConfigAttributes()
    {
        Map<String, Object> ldapUserSyncConfigAttributes = acmLdapConfig.getAttributes().get("ldapAddUserConfig");

        ldapUserSyncConfigAttributes.keySet().forEach(directory -> {
            logger.debug("Register bean definition for: [{}]", AcmLdapUserSyncConfig.class);
            BeanDefinitionBuilder jobDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(AcmLdapUserSyncConfig.class);
            jobDefinitionBuilder.addPropertyValue("attributes", ldapUserSyncConfigAttributes.get(directory));
            beanDefinitionRegistry.registerBeanDefinition(directory + "_userSync", jobDefinitionBuilder.getBeanDefinition());
        });
    }

    @Override
    public void afterPropertiesSet()
    {
        beanDefinitionRegistry = (BeanDefinitionRegistry) beanFactory;

        sync();
    }

    @Override
    public void createLdapUserConfig(String id, String directoryType, Map<String, Object> props,
            Map<String, Map<String, Object>> properties)
    {
        if (directoryType.equals("openldap"))
        {
            props.put("id", id);
            props.put("cn", "cn");
            props.put("givenName", "givenName");
            props.put("sn", "sn");
            props.put("uid", "uid");
            props.put("mail", "mail");
            props.put("userAccountControl", "userAccountControl");
            props.put("userPassword", "userPassword");
            props.put("uidNumber", "userPassword");
            props.put("gidNumber", "userPassword");
            props.put("homeDirectory", "homeDirectory");
            props.put("objectClass", "top,person,inetOrgPerson,organizationalPerson,posixAccount,uacPerson,shadowAccount");
            props.put("shadowWarning", 7);
            props.put("shadowLastChange", 12994);
            props.put("shadowMax", 99999);

            properties.put(AcmLdapConfiguration.LDAP_SYNC_CONFIG_PROP_KEY + "." + "~ldapAddUserConfig" + "." + id, props);
            configurationPropertyService.updateProperties(properties, "ldap");
        }
        else
        {
            props.put("id", id);
            props.put("cn", "cn");
            props.put("sAMAccountName", "sAMAccountName");
            props.put("userPrincipalName", "userPrincipalName");
            props.put("givenName", "givenName");
            props.put("sn", "sn");
            props.put("mail", "mail");
            props.put("userAccountControl", "userAccountControl");
            props.put("unicodePwd", "unicodePwd");
            props.put("homeDirectory", "homeDirectory");
            props.put("objectClass", "top,person,user,organizationalPerson");

            properties.put(AcmLdapConfiguration.LDAP_SYNC_CONFIG_PROP_KEY + "." + "~ldapAddUserConfig" + "." + id, props);
            configurationPropertyService.updateProperties(properties, "ldap");
        }
    }

    @Override
    public void createLdapGroupConfig(String id, String directoryType, Map<String, Object> props,
            Map<String, Map<String, Object>> properties)
    {
        if (directoryType.equals("activeDirectory"))
        {
            props.put("cn", "cn");
            props.put("sAMAccountName", "sAMAccountName");
            props.put("objectClass", "top,group");

            properties.put(AcmLdapConfiguration.LDAP_SYNC_CONFIG_PROP_KEY + "." + "~ldapAddGroupConfig" + "." + id, props);
            configurationPropertyService.updateProperties(properties, "ldap");
        }
        else
        {
            props.put("cn", "cn");
            props.put("member", "member");
            props.put("gidNumber", "userPassword");
            props.put("objectClass", "top,groupOfNames,sortableGroupofnames");

            properties.put(AcmLdapConfiguration.LDAP_SYNC_CONFIG_PROP_KEY + "." + "~ldapAddGroupConfig" + "." + id, props);
            configurationPropertyService.updateProperties(properties, "ldap");
        }
    }

    @Override
    public void deleteLdapDirectoryConfig(String directoryId)
    {
        Map<String, String> properties = new HashMap();
        properties.put(AcmLdapConfiguration.LDAP_SYNC_CONFIG_PROP_KEY + "." + "^ldapAddGroupConfig:" + directoryId, "");
        properties.put(AcmLdapConfiguration.LDAP_SYNC_CONFIG_PROP_KEY + "." + "^ldapAddUserConfig:" + directoryId, "");
        configurationPropertyService.updateProperties(properties, "ldap");

        sync();
    }

    public void setAcmLdapConfig(AcmLdapConfiguration acmLdapConfig)
    {
        this.acmLdapConfig = acmLdapConfig;
    }

    public void setConfigurationPropertyService(ConfigurationPropertyService configurationPropertyService)
    {
        this.configurationPropertyService = configurationPropertyService;
    }
}
