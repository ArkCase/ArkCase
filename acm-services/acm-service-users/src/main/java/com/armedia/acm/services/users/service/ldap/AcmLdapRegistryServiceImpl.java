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

import com.armedia.acm.configuration.service.CollectionPropertiesConfigurationService;
import com.armedia.acm.configuration.service.ConfigurationPropertyService;
import com.armedia.acm.configuration.util.MergeFlags;
import com.armedia.acm.services.users.model.ldap.AcmLdapAuthenticateConfig;
import com.armedia.acm.services.users.model.ldap.AcmLdapConfig;
import com.armedia.acm.services.users.model.ldap.AcmLdapConstants;
import com.armedia.acm.services.users.model.ldap.AcmLdapSyncConfig;
import com.armedia.acm.services.users.model.ldap.ActiveDirectoryLdapSearchConfig;
import com.armedia.acm.services.users.service.AcmLdapUserDetailsService;
import com.armedia.acm.spring.events.LdapDirectoryAdded;
import com.armedia.acm.spring.events.LdapDirectoryReplaced;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.messaging.Message;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.security.core.userdetails.UserDetailsByNameServiceWrapper;
import org.springframework.security.ldap.authentication.BindAuthenticator;
import org.springframework.security.ldap.search.FilterBasedLdapUserSearch;
import org.springframework.security.ldap.userdetails.DefaultLdapAuthoritiesPopulator;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author mario.gjurcheski
 *
 */
public class AcmLdapRegistryServiceImpl implements AcmLdapRegistryService, InitializingBean, ApplicationEventPublisherAware
{

    public static final String LDAP_DIRECTORY_CONFIG = "ldapDirectoryConfig";

    @Autowired
    private BeanFactory beanFactory;

    @Autowired
    private CollectionPropertiesConfigurationService collectionPropertiesConfigurationService;

    private ApplicationEventPublisher applicationEventPublisher;

    private AcmLdapConfiguration acmLdapConfig;

    private BeanDefinitionRegistry beanDefinitionRegistry;

    private ConfigurationPropertyService configurationPropertyService;

    private static final Logger logger = LogManager.getLogger(AcmLdapRegistryServiceImpl.class);

    @JmsListener(destination = "reload.ldap.beans", containerFactory = "jmsTopicListenerContainerFactory")
    public void onLdapChanged(Message message)
    {
        logger.info("Refreshing on ldap change...");
        sync();
    }

    @Override
    public void sync()
    {
        registerLdapGroupConfigBeanDefinition();
        registerLdapUserConfigBeanDefinition();
        registerLdapDirectoryConfigBeanDefinition();
    }

    @Override
    public void registerLdapGroupConfigBeanDefinition()
    {
        Map<String, Object> ldapDirsConfig = acmLdapConfig.getAttributes().get(LDAP_DIRECTORY_CONFIG);

        ldapDirsConfig.keySet().forEach(directory -> {

            logger.debug("Register bean definition for: [{}]", AcmLdapGroupSyncConfig.class);

            Map<String, Object> dirProperties = (Map<String, Object>) ldapDirsConfig.get(directory);
            BeanDefinitionBuilder jobDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(AcmLdapGroupSyncConfig.class);
            jobDefinitionBuilder.addPropertyValue("attributes",
                    getLdapGroupConfig(directory, (String) dirProperties.get("directoryType")));
            beanDefinitionRegistry.registerBeanDefinition(directory + "_groupSync", jobDefinitionBuilder.getBeanDefinition());
        });
    }

    @Override
    public void registerLdapUserConfigBeanDefinition()
    {
        Map<String, Object> ldapDirConfig = acmLdapConfig.getAttributes().get(LDAP_DIRECTORY_CONFIG);

        ldapDirConfig.keySet().forEach(directory -> {

            logger.debug("Register bean definition for: [{}]", AcmLdapUserSyncConfig.class);

            Map<String, Object> dirProperties = (Map<String, Object>) ldapDirConfig.get(directory);
            BeanDefinitionBuilder jobDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(AcmLdapUserSyncConfig.class);
            jobDefinitionBuilder.addPropertyValue("attributes",
                    getLdapUserConfig(directory, (String) dirProperties.get("directoryType")));
            beanDefinitionRegistry.registerBeanDefinition(directory + "_userSync", jobDefinitionBuilder.getBeanDefinition());
        });
    }

    @Override
    public void registerLdapDirectoryConfigBeanDefinition()
    {
        Map<String, Object> ldapDirectoryConfigAttributes = acmLdapConfig.getAttributes().get(LDAP_DIRECTORY_CONFIG);

        ldapDirectoryConfigAttributes.keySet().forEach(directory -> {
            Map<String, Object> dirProperties = (Map<String, Object>) ldapDirectoryConfigAttributes.get(directory);
            String dirType = (String) dirProperties.get("directoryType");

            syncAcmLdapConfigBean(directory, dirProperties);
            syncAcmLdapSyncConfigBean(directory, dirProperties);
            syncAcmLdapAuthenticateConfigBean(directory, dirProperties);
            syncLdapContextSourceConfig(directory, dirProperties);
            syncLdapAuthenticationServiceBean(directory);
            syncFilterBasedLdapUserSearchBean(directory, dirProperties);

            if (dirType.equals("activedirectory"))
            {
                // TODO replace the open ldap bean with active directory when problem with autchentication is fixed
                // syncActiveDirectoryAuthenticationProviderBean(directory, dirProperties);
                syncOpenLdapAuthenticationProviderBean(directory, dirProperties);
                syncActiveDirectoryLdapSearchConfigBean(directory, dirProperties);
            }
            else
            {
                syncOpenLdapAuthenticationProviderBean(directory, dirProperties);
            }

            syncUserDetailsServiceBean(directory, dirProperties);
            syncPreAuthenticatedAuthenticationProviderBean(directory);

            syncLdapSyncJobBean(directory);
            syncLdapPartialSyncJobBean(directory);
            syncLdapSyncJobDescriptorBean(directory);
            syncLdapSyncPartialJobDescriptorBean(directory);

            raiseContextEvent(directory, dirProperties);
        });
    }

    @Override
    public void afterPropertiesSet()
    {
        beanDefinitionRegistry = (BeanDefinitionRegistry) beanFactory;

        sync();
    }

    @Override
    public Map<String, Object> getLdapUserConfig(String id, String directoryType)
    {
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

            return ldapConfiguration;
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

            return ldapConfiguration;
        }
    }

    @Override
    public Map<String, Object> getLdapGroupConfig(String id, String directoryType)
    {

        Map<String, Object> ldapConfiguration = new HashMap<>();

        if (directoryType.equals(AcmLdapConstants.DEFAULT_OPEN_ACTIVE_DIRECTORY_NAME))
        {
            ldapConfiguration.put(AcmLdapConstants.LDAP_CN_ATTR, AcmLdapConstants.LDAP_CN_ATTR);
            ldapConfiguration.put(AcmLdapConstants.LDAP_SAMA_ACCOUNT_NAME_ATTR, AcmLdapConstants.LDAP_SAMA_ACCOUNT_NAME_ATTR);
            ldapConfiguration.put(AcmLdapConstants.LDAP_OBJECT_CLASS_ATTR, AcmLdapConstants.ACTIVE_DIRECTORY_GROUP_OBJECT_CLASS_VALUE);

            return ldapConfiguration;
        }
        else
        {
            ldapConfiguration.put(AcmLdapConstants.LDAP_CN_ATTR, AcmLdapConstants.LDAP_CN_ATTR);
            ldapConfiguration.put(AcmLdapConstants.LDAP_MEMBER_ATTR, AcmLdapConstants.LDAP_MEMBER_ATTR);
            ldapConfiguration.put(AcmLdapConstants.LDAP_GID_NUMBER_ATTR, AcmLdapConstants.LDAP_GID_NUMBER_ATTR);
            ldapConfiguration.put(AcmLdapConstants.LDAP_OBJECT_CLASS_ATTR, AcmLdapConstants.LDAP_GROUP_OBJECT_CLASS_VALUE);

            return ldapConfiguration;
        }
    }

    private void syncAcmLdapConfigBean(String directory, Map<String, Object> ldapDirectoryConfigAttributes)
    {
        logger.debug("Register bean definition for: [{}]", AcmLdapConfig.class);
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(AcmLdapConfig.class);

        beanDefinitionBuilder.addPropertyValue("ldapUrl", new ArrayList<>(Arrays.asList(ldapDirectoryConfigAttributes.get("ldapUrl"))));
        beanDefinitionBuilder.addPropertyValue("baseDC", ldapDirectoryConfigAttributes.get("base"));
        beanDefinitionBuilder.addPropertyValue("authUserDn", ldapDirectoryConfigAttributes.get("authUserDn"));
        beanDefinitionBuilder.addPropertyValue("authUserPassword", ldapDirectoryConfigAttributes.get("authUserPassword"));
        beanDefinitionBuilder.addPropertyValue("userIdAttributeName", ldapDirectoryConfigAttributes.get("userIdAttributeName"));
        beanDefinitionBuilder.addPropertyValue("mailAttributeName", "mail");
        beanDefinitionBuilder.addPropertyValue("ignorePartialResultException", true);
        beanDefinitionBuilder.addPropertyValue("referral", "follow");
        beanDefinitionBuilder.addPropertyValue("directoryName", directory);
        beanDefinitionBuilder.addPropertyValue("directoryType", ldapDirectoryConfigAttributes.get("directoryType"));

        beanDefinitionRegistry.registerBeanDefinition(directory + "_ldap_config", beanDefinitionBuilder.getBeanDefinition());
    }

    private void syncAcmLdapSyncConfigBean(String directory, Map<String, Object> ldapDirectoryConfigAttributes)
    {
        logger.debug("Register bean definition for: [{}]", AcmLdapSyncConfig.class);
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(AcmLdapSyncConfig.class);

        beanDefinitionBuilder.addPropertyValue("groupSearchBase", ldapDirectoryConfigAttributes.get("groupSearchBase"));
        beanDefinitionBuilder.addPropertyValue("groupSearchFilter", ldapDirectoryConfigAttributes.get("groupSearchFilter"));
        beanDefinitionBuilder.addPropertyValue("groupSearchPageFilter", ldapDirectoryConfigAttributes.get("groupSearchPageFilter"));
        beanDefinitionBuilder.addPropertyValue("allUsersFilter", ldapDirectoryConfigAttributes.get("allUsersFilter"));
        beanDefinitionBuilder.addPropertyValue("allChangedUsersFilter", ldapDirectoryConfigAttributes.get("allChangedUsersFilter"));
        beanDefinitionBuilder.addPropertyValue("allUsersPageFilter", ldapDirectoryConfigAttributes.get("allUsersPageFilter"));
        beanDefinitionBuilder.addPropertyValue("allChangedUsersPageFilter", ldapDirectoryConfigAttributes.get("allChangedUsersPageFilter"));
        beanDefinitionBuilder.addPropertyValue("userDomain", ldapDirectoryConfigAttributes.get("userDomain"));
        beanDefinitionBuilder.addPropertyValue("userPrefix", ldapDirectoryConfigAttributes.get("userPrefix"));
        beanDefinitionBuilder.addPropertyValue("groupPrefix", ldapDirectoryConfigAttributes.get("groupPrefix"));
        beanDefinitionBuilder.addPropertyValue("userControlGroup", ldapDirectoryConfigAttributes.get("userControlGroup"));
        beanDefinitionBuilder.addPropertyValue("groupControlGroup", ldapDirectoryConfigAttributes.get("groupControlGroup"));
        beanDefinitionBuilder.addPropertyValue("userSearchBase", ldapDirectoryConfigAttributes.get("userSearchBase"));
        beanDefinitionBuilder.addPropertyValue("userSearchFilter", ldapDirectoryConfigAttributes.get("userSearchFilter"));
        beanDefinitionBuilder.addPropertyValue("groupSearchFilterForUser", ldapDirectoryConfigAttributes.get("groupSearchFilterForUser"));
        beanDefinitionBuilder.addPropertyValue("syncPageSize", ldapDirectoryConfigAttributes.get("syncPageSize"));
        beanDefinitionBuilder.addPropertyValue("allUsersSortingAttribute", ldapDirectoryConfigAttributes.get("allUsersSortingAttribute"));
        beanDefinitionBuilder.addPropertyValue("groupsSortingAttribute", ldapDirectoryConfigAttributes.get("groupsSortingAttribute"));
        beanDefinitionBuilder.addPropertyValue("userSyncAttributes", ldapDirectoryConfigAttributes.get("userAttributes"));
        beanDefinitionBuilder.addPropertyValue("changedGroupSearchFilter", ldapDirectoryConfigAttributes.get("changedGroupSearchFilter"));
        beanDefinitionBuilder.addPropertyValue("changedGroupSearchPageFilter",
                ldapDirectoryConfigAttributes.get("changedGroupSearchPageFilter"));
        beanDefinitionBuilder.addPropertyValue("partialSyncCron", ldapDirectoryConfigAttributes.get("partialSyncCron"));
        beanDefinitionBuilder.addPropertyValue("fullSyncCron", ldapDirectoryConfigAttributes.get("fullSyncCron"));
        beanDefinitionBuilder.addPropertyValue("syncEnabled", ldapDirectoryConfigAttributes.get("syncEnabled"));

        beanDefinitionBuilder.setParentName(directory + "_ldap_config");

        beanDefinitionRegistry.registerBeanDefinition(directory + "_sync", beanDefinitionBuilder.getBeanDefinition());

    }

    private void syncAcmLdapAuthenticateConfigBean(String directory, Map<String, Object> ldapDirectoryConfigAttributes)
    {
        logger.debug("Register bean definition for: [{}]", AcmLdapAuthenticateConfig.class);
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(AcmLdapAuthenticateConfig.class);

        beanDefinitionBuilder.addPropertyValue("searchBase", ldapDirectoryConfigAttributes.get("userSearchBase"));
        beanDefinitionBuilder.addPropertyValue("enableEditingLdapUsers", ldapDirectoryConfigAttributes.get("enableEditingLdapUsers"));

        beanDefinitionBuilder.setParentName(directory + "_ldap_config");

        beanDefinitionRegistry.registerBeanDefinition(directory + "_authenticate", beanDefinitionBuilder.getBeanDefinition());
    }

    private void syncLdapContextSourceConfig(String directory, Map<String, Object> ldapDirectoryConfigAttributes)
    {
        logger.debug("Register bean definition for: [{}]", LdapContextSource.class);
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(LdapContextSource.class);

        beanDefinitionBuilder.addPropertyValue("urls", new ArrayList<>(Arrays.asList(ldapDirectoryConfigAttributes.get("ldapUrl"))));
        beanDefinitionBuilder.addPropertyValue("base", ldapDirectoryConfigAttributes.get("base"));
        beanDefinitionBuilder.addPropertyValue("userDn", ldapDirectoryConfigAttributes.get("authUserDn"));
        beanDefinitionBuilder.addPropertyValue("password", ldapDirectoryConfigAttributes.get("authUserPassword"));
        beanDefinitionBuilder.addPropertyValue("pooled", true);
        beanDefinitionBuilder.addPropertyValue("referral", "follow");

        beanDefinitionRegistry.registerBeanDefinition(directory + "_contextSource", beanDefinitionBuilder.getBeanDefinition());
    }

    private void syncLdapAuthenticationServiceBean(String directory)
    {
        logger.debug("Register bean definition for: [{}]", LdapAuthenticateService.class);

        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(LdapAuthenticateService.class);

        beanDefinitionBuilder.addPropertyReference("ldapAuthenticateConfig", directory + "_authenticate");
        beanDefinitionBuilder.addPropertyReference("ldapSyncConfig", directory + "_sync");
        beanDefinitionBuilder.addPropertyReference("ldapDao", "customPagedLdapDao");
        beanDefinitionBuilder.addPropertyReference("userDao", "userJpaDao");
        beanDefinitionBuilder.addPropertyReference("ldapUserDao", "springLdapUserDao");

        beanDefinitionRegistry.registerBeanDefinition(directory + "_ldapAuthenticateService",
                beanDefinitionBuilder.getBeanDefinition());

    }

    private void syncFilterBasedLdapUserSearchBean(String directory, Map<String, Object> ldapDirectoryConfigAttributes)
    {
        logger.debug("Register bean definition for: [{}]", FilterBasedLdapUserSearch.class);

        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(FilterBasedLdapUserSearch.class);

        beanDefinitionBuilder.addConstructorArgValue(ldapDirectoryConfigAttributes.get("userSearchBase"));
        beanDefinitionBuilder.addConstructorArgValue(ldapDirectoryConfigAttributes.get("userIdAttributeName") + "={0}");
        beanDefinitionBuilder.addConstructorArgReference(directory + "_contextSource");

        beanDefinitionRegistry.registerBeanDefinition(directory + "_userSearch",
                beanDefinitionBuilder.getBeanDefinition());
    }

    private void syncActiveDirectoryLdapSearchConfigBean(String directory, Map<String, Object> ldapDirectoryConfigAttributes)
    {
        logger.debug("Register bean definition for: [{}]", ActiveDirectoryLdapSearchConfig.class);

        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(ActiveDirectoryLdapSearchConfig.class);

        beanDefinitionBuilder.addPropertyReference("contextSource", directory + "_contextSource");
        beanDefinitionBuilder.addPropertyValue("searchBase", ldapDirectoryConfigAttributes.get("userSearchBase"));
        beanDefinitionBuilder.addPropertyValue("searchFilter", ldapDirectoryConfigAttributes.get("userIdAttributeName") + "={0}");

        beanDefinitionRegistry.registerBeanDefinition(directory + "_activeDirectoryLdapSearchConfig",
                beanDefinitionBuilder.getBeanDefinition());
    }

    private void syncOpenLdapAuthenticationProviderBean(String directory, Map<String, Object> ldapDirectoryConfigAttributes)
    {
        logger.debug("Register bean definition for: [{}]", AcmLdapAuthenticationProvider.class);

        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(AcmLdapAuthenticationProvider.class);
        BeanDefinitionBuilder bindAuthenticatorBuilder = BeanDefinitionBuilder.genericBeanDefinition(BindAuthenticator.class);

        bindAuthenticatorBuilder.addConstructorArgReference(directory + "_contextSource");
        bindAuthenticatorBuilder.addPropertyReference("userSearch", directory + "_userSearch");

        BeanDefinitionBuilder defaultLdapAuthoritiesBuilder = buildDefaultLdapAuthoritiesPopulatorBean(directory,
                ldapDirectoryConfigAttributes);

        beanDefinitionBuilder.addConstructorArgValue(bindAuthenticatorBuilder.getBeanDefinition());
        beanDefinitionBuilder.addConstructorArgValue(defaultLdapAuthoritiesBuilder.getBeanDefinition());

        beanDefinitionBuilder.addPropertyReference("userDao", "userJpaDao");
        beanDefinitionBuilder.addPropertyReference("ldapSyncService", "ldapSyncService");
        beanDefinitionBuilder.addPropertyReference("acmLdapSyncConfig", directory + "_sync");

        beanDefinitionRegistry.registerBeanDefinition(directory + "_authenticationProvider",
                beanDefinitionBuilder.getBeanDefinition());
    }

    private void syncActiveDirectoryAuthenticationProviderBean(String directory, Map<String, Object> ldapDirectoryConfigAttributes)
    {
        logger.debug("Register bean definition for: [{}]", AcmActiveDirectoryAuthenticationProvider.class);

        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder
                .genericBeanDefinition(AcmActiveDirectoryAuthenticationProvider.class);

        beanDefinitionBuilder.addConstructorArgValue("");
        beanDefinitionBuilder.addConstructorArgReference(directory + "_contextSource");
        beanDefinitionBuilder.addConstructorArgValue(buildDefaultLdapAuthoritiesPopulatorBean(directory,
                ldapDirectoryConfigAttributes).getBeanDefinition());
        beanDefinitionBuilder.addConstructorArgReference(directory + "_activeDirectoryLdapSearchConfig");
        beanDefinitionBuilder.addPropertyReference("userDao", "userJpaDao");
        beanDefinitionBuilder.addPropertyReference("ldapSyncService", "ldapSyncService");
        beanDefinitionBuilder.addPropertyReference("acmLdapSyncConfig", directory + "_sync");

        beanDefinitionRegistry.registerBeanDefinition(directory + "_authenticationProvider",
                beanDefinitionBuilder.getBeanDefinition());
    }

    private void syncUserDetailsServiceBean(String directory, Map<String, Object> ldapDirectoryConfigAttributes)
    {
        logger.debug("Register bean definition for: [{}]", AcmLdapUserDetailsService.class);

        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(AcmLdapUserDetailsService.class);
        beanDefinitionBuilder.addConstructorArgReference(directory + "_userSearch");

        BeanDefinitionBuilder defaultLdapAuthoritiesBeanBuilder = BeanDefinitionBuilder
                .genericBeanDefinition(DefaultLdapAuthoritiesPopulator.class);

        defaultLdapAuthoritiesBeanBuilder.addConstructorArgReference(directory + "_contextSource");
        defaultLdapAuthoritiesBeanBuilder.addConstructorArgValue(ldapDirectoryConfigAttributes.get("groupSearchBase"));

        beanDefinitionBuilder.addConstructorArgValue(defaultLdapAuthoritiesBeanBuilder.getBeanDefinition());
        beanDefinitionBuilder.addPropertyReference("acmLdapSyncConfig", directory + "_sync");

        beanDefinitionRegistry.registerBeanDefinition(directory + "_userDetailsService",
                beanDefinitionBuilder.getBeanDefinition());

    }

    private void syncPreAuthenticatedAuthenticationProviderBean(String directory)
    {
        logger.debug("Register bean definition for: [{}]", PreAuthenticatedAuthenticationProvider.class);

        BeanDefinitionBuilder preAuthenticatedAuthenticationProviderBuilder = BeanDefinitionBuilder
                .genericBeanDefinition(PreAuthenticatedAuthenticationProvider.class);

        BeanDefinitionBuilder userDetailsByNameServiceWrapperBuilder = BeanDefinitionBuilder
                .genericBeanDefinition(UserDetailsByNameServiceWrapper.class);
        userDetailsByNameServiceWrapperBuilder.addPropertyReference("userDetailsService", directory + "_userDetailsService");
        beanDefinitionRegistry.registerBeanDefinition(directory + "_userDetailsServiceWrapper",
                userDetailsByNameServiceWrapperBuilder.getBeanDefinition());

        preAuthenticatedAuthenticationProviderBuilder.addPropertyReference("preAuthenticatedUserDetailsService",
                directory + "_userDetailsServiceWrapper");
        beanDefinitionRegistry.registerBeanDefinition(directory + "_externalAuthProvider",
                preAuthenticatedAuthenticationProviderBuilder.getBeanDefinition());

    }

    private void syncLdapSyncJobBean(String directory)
    {
        logger.debug("Register bean definition for: [{}]", JobDetailFactoryBean.class);

        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder
                .genericBeanDefinition(JobDetailFactoryBean.class);

        beanDefinitionBuilder.addPropertyValue("jobClass", LdapSyncJobDescriptor.class.getName());
        beanDefinitionBuilder.addPropertyValue("durability", "true");

        beanDefinitionRegistry.registerBeanDefinition(directory + "_ldapSyncJob",
                beanDefinitionBuilder.getBeanDefinition());
    }

    private void syncLdapPartialSyncJobBean(String directory)
    {
        logger.debug("Register bean definition for: [{}]", JobDetailFactoryBean.class);

        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder
                .genericBeanDefinition(JobDetailFactoryBean.class);

        beanDefinitionBuilder.addPropertyValue("jobClass", LdapPartialSyncJobDescriptor.class.getName());
        beanDefinitionBuilder.addPropertyValue("durability", "true");

        beanDefinitionRegistry.registerBeanDefinition(directory + "_ldapPartialSyncJob",
                beanDefinitionBuilder.getBeanDefinition());
    }

    private void syncLdapSyncJobDescriptorBean(String directory)
    {
        logger.debug("Register bean definition for: [{}]", LdapSyncJobDescriptor.class);

        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder
                .genericBeanDefinition(LdapSyncJobDescriptor.class);

        beanDefinitionBuilder.setParentName("acmJobDescriptor");
        beanDefinitionBuilder.addPropertyReference("ldapSyncService", "ldapSyncService");
        beanDefinitionBuilder.addPropertyReference("acmLdapSyncConfig", directory + "_sync");

        beanDefinitionRegistry.registerBeanDefinition(directory + "_ldapSyncJobDescriptor",
                beanDefinitionBuilder.getBeanDefinition());
    }

    private void syncLdapSyncPartialJobDescriptorBean(String directory)
    {
        logger.debug("Register bean definition for: [{}]", LdapPartialSyncJobDescriptor.class);

        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder
                .genericBeanDefinition(LdapPartialSyncJobDescriptor.class);

        beanDefinitionBuilder.setParentName("acmJobDescriptor");
        beanDefinitionBuilder.addPropertyReference("ldapSyncService", "ldapSyncService");
        beanDefinitionBuilder.addPropertyReference("acmLdapSyncConfig", directory + "_sync");

        beanDefinitionRegistry.registerBeanDefinition(directory + "_ldapSyncPartialJobDescriptor",
                beanDefinitionBuilder.getBeanDefinition());
    }

    private BeanDefinitionBuilder buildDefaultLdapAuthoritiesPopulatorBean(String directory,
            Map<String, Object> ldapDirectoryConfigAttributes)
    {
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder
                .genericBeanDefinition(DefaultLdapAuthoritiesPopulator.class);

        beanDefinitionBuilder.addConstructorArgReference(directory + "_contextSource");
        beanDefinitionBuilder.addConstructorArgValue(ldapDirectoryConfigAttributes.get("groupSearchBase"));
        beanDefinitionBuilder.addPropertyValue("groupSearchFilter", "member={0}");
        beanDefinitionBuilder.addPropertyValue("rolePrefix", "");
        beanDefinitionBuilder.addPropertyValue("searchSubtree", "true");
        beanDefinitionBuilder.addPropertyValue("convertToUpperCase", "true");
        beanDefinitionBuilder.addPropertyValue("ignorePartialResultException", "true");

        return beanDefinitionBuilder;
    }

    @Override
    public void createLdapDirectoryConfig(String directoryId, Map<String, Object> properties)
    {
        Map<String, Map<String, Object>> ldapConfiguration = new HashMap<>();
        Map<String, Object> dirConfig = new HashMap<>();
        dirConfig.put(directoryId, properties);

        ldapConfiguration.put(AcmLdapConfiguration.LDAP_SYNC_CONFIG_PROP_KEY + "." + LDAP_DIRECTORY_CONFIG, dirConfig);

        configurationPropertyService.updateProperties(ldapConfiguration, "ldap");
    }

    @Override
    public void deleteLdapDirectoryConfig(String directoryId)
    {

        Map<String, Object> ldapConfiguration = collectionPropertiesConfigurationService.deleteMapProperty(
                AcmLdapConfiguration.LDAP_SYNC_CONFIG_PROP_KEY + "." + LDAP_DIRECTORY_CONFIG,
                directoryId, MergeFlags.REMOVE.getSymbol());

        Map<String, Object> directoryConfig = new HashMap<>();
        directoryConfig.put(AcmLdapConfiguration.LDAP_SYNC_CONFIG_PROP_KEY + "." + LDAP_DIRECTORY_CONFIG, ldapConfiguration);
        configurationPropertyService.updateProperties(directoryConfig, "ldap");
    }

    private void raiseContextEvent(String directory, Map<String, Object> dirProperties)
    {
        if (dirProperties.get("status").equals("new"))
        {
            applicationEventPublisher.publishEvent(new LdapDirectoryAdded(this, directory + "_ldap"));
        }
        else if (dirProperties.get("status").equals("updated"))
        {
            applicationEventPublisher.publishEvent(new LdapDirectoryReplaced(this, directory + "_ldap"));
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

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        this.applicationEventPublisher = applicationEventPublisher;
    }
}
