package com.armedia.acm.services.users.dao.ldap;

import com.armedia.acm.services.users.model.ldap.LdapGroup;
import com.armedia.acm.services.users.model.ldap.LdapUser;
import com.armedia.acm.services.users.model.ldap.AcmLdapConfig;
import com.armedia.acm.services.users.model.ldap.AcmLdapSyncConfig;
import com.armedia.acm.services.users.model.ldap.Directory;
import com.armedia.acm.services.users.model.ldap.SimpleAuthenticationSource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ldap.core.AuthenticationSource;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public interface SpringLdapDao
{
    /*
     * This builds the ldap template from the base AcmLdapConfig
     */
    default LdapTemplate buildLdapTemplate(final AcmLdapConfig syncConfig)
    {
        AuthenticationSource authenticationSource = null;

        if (syncConfig.getAuthUserDn() != null)
        {
            authenticationSource = new SimpleAuthenticationSource(syncConfig.getAuthUserDn(), syncConfig.getAuthUserPassword());
        }

        LdapContextSource ldapContextSource = new LdapContextSource();
        ldapContextSource.setUrl(syncConfig.getLdapUrl());
        ldapContextSource.setBase(syncConfig.getBaseDC());
        if (authenticationSource != null)
        {
            ldapContextSource.setAuthenticationSource(authenticationSource);
        }
        ldapContextSource.setCacheEnvironmentProperties(false);
        ldapContextSource.setReferral(syncConfig.getReferral());

        LdapTemplate ldapTemplate = new LdapTemplate(ldapContextSource);
        ldapTemplate.setIgnorePartialResultException(syncConfig.isIgnorePartialResultException());
        return ldapTemplate;
    }

    default String convertToDirectorySpecificTimestamp(String ldapLastSyncTimestamp, String directoryType)
    {
        return Directory.valueOf(directoryType).convertToDirectorySpecificTimestamp(ldapLastSyncTimestamp);
    }

    default String buildGroupSearchFilter(AcmLdapSyncConfig syncConfig, Optional<String> lastSyncDate)
    {
        return lastSyncDate.map(it -> String.format(syncConfig.getChangedGroupSearchFilter(), lastSyncDate))
                .orElse(syncConfig.getGroupSearchFilter());
    }

    default String buildUsersSearchFilter(AcmLdapSyncConfig syncConfig, Optional<String> lastSyncDate)
    {
        // eg. allUsersFilter = (objectClass=person )
        // allChangedUsersFilter = (&(objectClass=person)(modifyTimestamp>=%s))
        return lastSyncDate.map(it ->String.format(syncConfig.getAllChangedUsersFilter(), it))
                .orElse(syncConfig.getAllUsersFilter());
    }

    default String buildPagedGroupsSearchFilter(AcmLdapSyncConfig syncConfig, String sortAttributeValue, Optional<String> lastSyncDate)
    {
        return lastSyncDate.map(it -> String.format(syncConfig.getGroupSearchPageFilter(), sortAttributeValue, lastSyncDate))
                .orElse(String.format(syncConfig.getGroupSearchPageFilter(), sortAttributeValue));

    }

    default String buildPagedUsersSearchFilter(AcmLdapSyncConfig syncConfig, String sortAttributeValue, Optional<String> lastSyncDate)
    {
        // eg. allUsersPageFilter = (&(objectClass=person)(uidNumber>=%s))
        // allChangedUsersPageFilter = (&(objectClass=person)(uidNumber>=%s)(modifyTimestamp>=%s))
        return lastSyncDate.map(it -> String.format(syncConfig.getAllChangedUsersPageFilter(), sortAttributeValue, lastSyncDate))
                .orElse(String.format(syncConfig.getAllUsersPageFilter(), sortAttributeValue));
    }

    List<LdapUser> findUsersPaged(LdapTemplate template, final AcmLdapSyncConfig syncConfig, Optional<String> ldapLastSyncDate);

    List<LdapGroup> findGroupsPaged(LdapTemplate template, AcmLdapSyncConfig config, Optional<String> ldapLastSyncDate);
}
