package com.armedia.acm.services.users.service.ldap;

/*-
 * #%L
 * ACM Service: Users
 * %%
 * Copyright (C) 2014 - 2020 ArkCase LLC
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

import com.armedia.acm.configuration.service.ConfigurationPropertyService;
import com.armedia.acm.services.users.model.ldap.AcmLdapConstants;

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

/**
 * @author mario.gjurcheski
 *
 */
public class AcmLdapBeanSyncServiceImpl implements AcmLdapBeanSyncService, InitializingBean
{

    public static final String LDAP_ADD_USER_CONFIG = "ldapAddUserConfig";
    public static final String LDAP_ADD_GROUP_CONFIG = "ldapAddGroupConfig";

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
        Map<String, Object> ldapGroupSyncConfigAttributes = acmLdapConfig.getAttributes().get(LDAP_ADD_GROUP_CONFIG);

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
        Map<String, Object> ldapUserSyncConfigAttributes = acmLdapConfig.getAttributes().get(LDAP_ADD_USER_CONFIG);

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
    public void createLdapUserConfig(String id, String directoryType)
    {
        Map<String, Map<String, Object>> properties = new HashMap<>();
        Map<String, Object> ldapConfiguration = new HashMap<>();

        if (directoryType.equals(AcmLdapConstants.DEFAULT_OPEN_LDAP_DIRECTORY_NAME))
        {
            ldapConfiguration.put(AcmLdapConstants.LDAP_ID_ATTR, id);
            ldapConfiguration.put(AcmLdapConstants.LDAP_CN_ATTR, AcmLdapConstants.LDAP_CN_ATTR);
            ldapConfiguration.put(AcmLdapConstants.LDAP_SN, AcmLdapConstants.LDAP_SN);
            ldapConfiguration.put(AcmLdapConstants.LDAP_GIVEN_NAME, AcmLdapConstants.LDAP_GIVEN_NAME);
            ldapConfiguration.put(AcmLdapConstants.LDAP_UID, AcmLdapConstants.LDAP_UID);
            ldapConfiguration.put(AcmLdapConstants.LDAP_MAIL_ATTR, AcmLdapConstants.LDAP_MAIL_ATTR);
            ldapConfiguration.put(AcmLdapConstants.LDAP_USER_ACCOUNT_CONTROL, AcmLdapConstants.LDAP_USER_ACCOUNT_CONTROL);
            ldapConfiguration.put(AcmLdapConstants.LDAP_USER_PASSWORD_ATTR, AcmLdapConstants.LDAP_USER_PASSWORD_ATTR);
            ldapConfiguration.put(AcmLdapConstants.LDAP_UID_NUMBER_ATTR, AcmLdapConstants.LDAP_UID_NUMBER_ATTR);
            ldapConfiguration.put(AcmLdapConstants.LDAP_GID_NUMBER_ATTR, AcmLdapConstants.LDAP_GID_NUMBER_ATTR);
            ldapConfiguration.put(AcmLdapConstants.LDAP_HOME_DIRECTORY_ATTR, AcmLdapConstants.LDAP_HOME_DIRECTORY_ATTR);
            ldapConfiguration.put(AcmLdapConstants.LDAP_OBJECT_CLASS_ATTR, AcmLdapConstants.LDAP_USER_OBJECT_CLASS_VALUE);
            ldapConfiguration.put(AcmLdapConstants.SHADOW_WARNING_ATTR, 7);
            ldapConfiguration.put(AcmLdapConstants.SHADOW_LAST_CHANGE_ATTR, 12994);
            ldapConfiguration.put(AcmLdapConstants.SHADOW_MAX_ATTR, 99999);

            properties.put(AcmLdapConfiguration.LDAP_SYNC_CONFIG_PROP_KEY + "." + LDAP_ADD_USER_CONFIG + "." + id, ldapConfiguration);
            configurationPropertyService.updateProperties(properties, "ldap");
        }
        else
        {
            ldapConfiguration.put(AcmLdapConstants.LDAP_ID_ATTR, id);
            ldapConfiguration.put(AcmLdapConstants.LDAP_CN_ATTR, AcmLdapConstants.LDAP_CN_ATTR);
            ldapConfiguration.put(AcmLdapConstants.LDAP_SAMA_ACCOUNT_NAME_ATTR, AcmLdapConstants.LDAP_SAMA_ACCOUNT_NAME_ATTR);
            ldapConfiguration.put(AcmLdapConstants.LDAP_USER_PRINCIPAL_NAME_ATTR, AcmLdapConstants.LDAP_USER_PRINCIPAL_NAME_ATTR);
            ldapConfiguration.put(AcmLdapConstants.LDAP_GIVEN_NAME, AcmLdapConstants.LDAP_GIVEN_NAME);
            ldapConfiguration.put(AcmLdapConstants.LDAP_SN, AcmLdapConstants.LDAP_SN);
            ldapConfiguration.put(AcmLdapConstants.LDAP_MAIL_ATTR, AcmLdapConstants.LDAP_MAIL_ATTR);
            ldapConfiguration.put(AcmLdapConstants.LDAP_USER_ACCOUNT_CONTROL, AcmLdapConstants.LDAP_USER_ACCOUNT_CONTROL);
            ldapConfiguration.put(AcmLdapConstants.LDAP_UNICODE_PWD_ATTR, AcmLdapConstants.LDAP_UNICODE_PWD_ATTR);
            ldapConfiguration.put(AcmLdapConstants.LDAP_HOME_DIRECTORY_ATTR, AcmLdapConstants.LDAP_HOME_DIRECTORY_ATTR);
            ldapConfiguration.put(AcmLdapConstants.LDAP_OBJECT_CLASS_ATTR, AcmLdapConstants.ACTIVE_DIRECTORY_USER_OBJECT_CLASS_VALUE);

            properties.put(AcmLdapConfiguration.LDAP_SYNC_CONFIG_PROP_KEY + "." + LDAP_ADD_USER_CONFIG + "." + id, ldapConfiguration);
            configurationPropertyService.updateProperties(properties, "ldap");
        }
    }

    @Override
    public void createLdapGroupConfig(String id, String directoryType)
    {
        Map<String, Map<String, Object>> properties = new HashMap<>();

        Map<String, Object> ldapConfiguration = new HashMap<>();

        if (directoryType.equals(AcmLdapConstants.DEFAULT_OPEN_ACTIVE_DIRECTORY_NAME))
        {
            ldapConfiguration.put(AcmLdapConstants.LDAP_CN_ATTR, AcmLdapConstants.LDAP_CN_ATTR);
            ldapConfiguration.put(AcmLdapConstants.LDAP_SAMA_ACCOUNT_NAME_ATTR, AcmLdapConstants.LDAP_SAMA_ACCOUNT_NAME_ATTR);
            ldapConfiguration.put(AcmLdapConstants.LDAP_OBJECT_CLASS_ATTR, AcmLdapConstants.ACTIVE_DIRECTORY_GROUP_OBJECT_CLASS_VALUE);

            properties.put(AcmLdapConfiguration.LDAP_SYNC_CONFIG_PROP_KEY + "." + LDAP_ADD_GROUP_CONFIG + "." + id, ldapConfiguration);
            configurationPropertyService.updateProperties(properties, "ldap");
        }
        else
        {
            ldapConfiguration.put(AcmLdapConstants.LDAP_CN_ATTR, AcmLdapConstants.LDAP_CN_ATTR);
            ldapConfiguration.put(AcmLdapConstants.LDAP_MEMBER_ATTR, AcmLdapConstants.LDAP_MEMBER_ATTR);
            ldapConfiguration.put(AcmLdapConstants.LDAP_GID_NUMBER_ATTR, AcmLdapConstants.LDAP_GID_NUMBER_ATTR);
            ldapConfiguration.put(AcmLdapConstants.LDAP_OBJECT_CLASS_ATTR, AcmLdapConstants.LDAP_GROUP_OBJECT_CLASS_VALUE);

            properties.put(AcmLdapConfiguration.LDAP_SYNC_CONFIG_PROP_KEY + "." + LDAP_ADD_GROUP_CONFIG + "." + id, ldapConfiguration);
            configurationPropertyService.updateProperties(properties, "ldap");
        }
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
