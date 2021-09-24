package com.armedia.acm.plugins.admin.service;

/*-
 * #%L
 * ACM Default Plugin: admin
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

import com.armedia.acm.crypto.exceptions.AcmEncryptionException;
import com.armedia.acm.crypto.properties.AcmEncryptablePropertyUtils;
import com.armedia.acm.plugins.admin.exception.AcmLdapConfigurationException;
import com.armedia.acm.plugins.admin.model.LdapConfigurationProperties;
import com.armedia.acm.services.users.model.event.LdapDirectoryDeleted;
import com.armedia.acm.services.users.service.ldap.AcmLdapConfiguration;
import com.armedia.acm.services.users.service.ldap.AcmLdapRegistryService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.quartz.CronExpression;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sergey.kolomiets on 6/2/15.
 */
public class LdapConfigurationService implements ApplicationEventPublisherAware
{
    private Logger log = LogManager.getLogger(LdapConfigurationService.class);

    private AcmEncryptablePropertyUtils encryptablePropertyUtils;

    private AcmLdapConfiguration acmLdapConfig;

    private AcmLdapRegistryService acmLdapBeanSyncService;

    private ApplicationEventPublisher applicationEventPublisher;


    /**
     * Convert LDAP JSON Object to properties map
     *
     * @param jsonObj
     * @return
     * @throws AcmEncryptionException
     * @throws JSONException
     */
    public HashMap<String, Object> getProperties(JSONObject jsonObj)
            throws JSONException, AcmEncryptionException, AcmLdapConfigurationException
    {
        HashMap<String, Object> props = new HashMap<>();
        props.put(LdapConfigurationProperties.LDAP_PROP_ID, jsonObj.getString(LdapConfigurationProperties.LDAP_PROP_ID));
        props.put("base", jsonObj.getString(LdapConfigurationProperties.LDAP_PROP_BASE));
        props.put("authUserDn", jsonObj.getString(LdapConfigurationProperties.LDAP_PROP_AUTH_USER_DN));
        props.put("authUserPassword",
                encryptablePropertyUtils.encryptPropertyValue(jsonObj.getString(LdapConfigurationProperties.LDAP_PROP_AUTH_USER_PASSWORD)));
        props.put("userSearchBase", jsonObj.getString(LdapConfigurationProperties.LDAP_PROP_USER_SEARCH_BASE));
        props.put("groupSearchBase", jsonObj.getString(LdapConfigurationProperties.LDAP_PROP_GROUP_SEARCH_BASE));
        props.put("userSearchFilter", jsonObj.getString(LdapConfigurationProperties.LDAP_PROP_USER_SEARCH_FILTER));
        props.put("allUsersFilter", jsonObj.getString(LdapConfigurationProperties.LDAP_PROP_ALL_USERS_FILTER));
        props.put("allChangedUsersFilter", jsonObj.getString(LdapConfigurationProperties.LDAP_PROP_ALL_CHANGED_USERS_FILTER));
        props.put("allUsersPageFilter", jsonObj.getString(LdapConfigurationProperties.LDAP_PROP_ALL_USERS_PAGE_FILTER));
        props.put("allChangedUsersPageFilter", jsonObj.getString(LdapConfigurationProperties.LDAP_PROP_ALL_CHANGED_USERS_PAGE_FILTER));
        props.put("allUsersSortingAttribute", jsonObj.getString(LdapConfigurationProperties.LDAP_PROP_ALL_USERS_SORT_ATTRIBUTE));
        props.put("groupSearchFilterForUser", jsonObj.getString(LdapConfigurationProperties.LDAP_PROP_GROUP_SEARCH_FILTER_FOR_USER));
        props.put("groupSearchFilter", jsonObj.getString(LdapConfigurationProperties.LDAP_PROP_GROUP_SEARCH_FILTER));
        props.put("changedGroupSearchFilter", jsonObj.getString(LdapConfigurationProperties.LDAP_PROP_CHANGED_GROUP_SEARCH_FILTER));
        props.put("groupSearchPageFilter", jsonObj.getString(LdapConfigurationProperties.LDAP_PROP_GROUP_SEARCH_PAGE_FILTER));
        props.put("changedGroupSearchPageFilter",
                jsonObj.getString(LdapConfigurationProperties.LDAP_PROP_CHANGED_GROUP_SEARCH_PAGE_FILTER));
        props.put("groupsSortingAttribute", jsonObj.getString(LdapConfigurationProperties.LDAP_PROP_GROUPS_SORT_ATTRIBUTE));
        props.put("ldapUrl", jsonObj.getString(LdapConfigurationProperties.LDAP_PROP_LDAP_URL));
        props.put("enableEditingLdapUsers", jsonObj.getBoolean(LdapConfigurationProperties.LDAP_PROP_ENABLE_EDITING_LDAP_USERS));
        props.put("syncEnabled", jsonObj.getBoolean(LdapConfigurationProperties.LDAP_PROP_SYNC_ENABLED));
        props.put("autoGenerateUserId", jsonObj.has(LdapConfigurationProperties.LDAP_PROP_AUTO_GENERATE_USER_ID)
                && jsonObj.getBoolean(LdapConfigurationProperties.LDAP_PROP_AUTO_GENERATE_USER_ID));
        props.put("directoryType", jsonObj.getString(LdapConfigurationProperties.LDAP_PROP_DIRECTORY_TYPE).toLowerCase());
        props.put("syncPageSize", jsonObj.getInt(LdapConfigurationProperties.LDAP_PROP_SYNC_PAGE_SIZE));
        props.put("userIdAttributeName", jsonObj.getString(LdapConfigurationProperties.LDAP_PROP_USER_ID_ATTR_NAME));
        props.put("userDomain", jsonObj.has(LdapConfigurationProperties.LDAP_PROP_USER_DOMAIN)
                ? jsonObj.getString(LdapConfigurationProperties.LDAP_PROP_USER_DOMAIN)
                : "");
        props.put("userPrefix", jsonObj.has(LdapConfigurationProperties.LDAP_PROP_USER_PREFIX)
                ? jsonObj.getString(LdapConfigurationProperties.LDAP_PROP_USER_PREFIX)
                : "");
        props.put("groupPrefix", jsonObj.has(LdapConfigurationProperties.LDAP_PROP_GROUP_PREFIX)
                ? jsonObj.getString(LdapConfigurationProperties.LDAP_PROP_GROUP_PREFIX)
                : "");
        props.put("userControlGroup", jsonObj.has(LdapConfigurationProperties.LDAP_PROP_USER_CONTROL_GROUP)
                ? jsonObj.getString(LdapConfigurationProperties.LDAP_PROP_USER_CONTROL_GROUP)
                : "");
        props.put("groupControlGroup", jsonObj.has(LdapConfigurationProperties.LDAP_PROP_GROUP_CONTROL_GROUP)
                ? jsonObj.getString(LdapConfigurationProperties.LDAP_PROP_GROUP_CONTROL_GROUP)
                : "");
        if (jsonObj.has(LdapConfigurationProperties.LDAP_PARTIAL_SYNC_CRON))
        {
            String partialSyncCron = jsonObj.getString(LdapConfigurationProperties.LDAP_PARTIAL_SYNC_CRON);
            if (CronExpression.isValidExpression(partialSyncCron))
            {
                props.put("partialSyncCron", partialSyncCron);
            }
            else
            {
                throw new AcmLdapConfigurationException("Partial sync Cron: " + partialSyncCron + " is not valid.");
            }
        }
        if (jsonObj.has(LdapConfigurationProperties.LDAP_FULL_SYNC_CRON))
        {
            String fullSyncCron = jsonObj.getString(LdapConfigurationProperties.LDAP_FULL_SYNC_CRON);
            if (CronExpression.isValidExpression(fullSyncCron))
            {
                props.put("fullSyncCron", fullSyncCron);
            }
            else
            {
                throw new AcmLdapConfigurationException("Full sync Cron: " + fullSyncCron + " is not valid");
            }
        }

        return props;
    }

    public void createLdapDirectoryConfigurations(String id, Map<String, Object> props)
    {
        acmLdapBeanSyncService.createLdapDirectoryConfig(id, props);
    }

    /**
     * Update LDAP Directory settings
     *
     * @param dirId
     *            Directory identifier
     * @param props
     *            Directory properties data
     */
    public void updateLdapDirectory(String dirId, Map<String, Object> props)
    {
        acmLdapBeanSyncService.createLdapDirectoryConfig(dirId, props);
    }

    public void deleteLdapDirectoryConfiguration(String directoryId)
    {
        acmLdapBeanSyncService.deleteLdapDirectoryConfig(directoryId);
        applicationEventPublisher.publishEvent(new LdapDirectoryDeleted(this, directoryId));

    }

    public Map<String, Object> retrieveDirectoriesConfiguration()
    {

        Map<String, Object> dirsConfiguration = new HashMap<>();
        Map<String, Object> directories = acmLdapConfig.getAttributes().get("ldapDirectoryConfig");

        directories.forEach((dirName, properties) -> {
            Map<Object, Object> dirConfiguration = new HashMap<>();

            Map<String, Object> dirProperties = (Map<String, Object>) properties;

            dirConfiguration.put("ldapConfig.id", dirName);
            dirProperties.forEach((key, value) -> dirConfiguration.put("ldapConfig." + key, value));

            dirConfiguration.put(LdapConfigurationProperties.LDAP_PROP_ADD_USER_TEMPLATE,
                    acmLdapBeanSyncService.getLdapUserConfig(dirName, (String) ((Map<String, Object>) properties).get("directoryType")));

            dirConfiguration.put(LdapConfigurationProperties.LDAP_PROP_ADD_GROUP_TEMPLATE,
                    acmLdapBeanSyncService.getLdapGroupConfig(dirName, (String) ((Map<String, Object>) properties).get("directoryType")));

            dirsConfiguration.put(dirName, dirConfiguration);

        });

        return dirsConfiguration;
    }

    public AcmEncryptablePropertyUtils getEncryptablePropertyUtils()
    {
        return encryptablePropertyUtils;
    }

    public void setEncryptablePropertyUtils(AcmEncryptablePropertyUtils encryptablePropertyUtils)
    {
        this.encryptablePropertyUtils = encryptablePropertyUtils;
    }

    public void setAcmLdapConfig(AcmLdapConfiguration acmLdapConfig)
    {
        this.acmLdapConfig = acmLdapConfig;
    }

    public void setAcmLdapBeanSyncService(AcmLdapRegistryService acmLdapBeanSyncService)
    {
        this.acmLdapBeanSyncService = acmLdapBeanSyncService;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        this.applicationEventPublisher = applicationEventPublisher;
    }
}
