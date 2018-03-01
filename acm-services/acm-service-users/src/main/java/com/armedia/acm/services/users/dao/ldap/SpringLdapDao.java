package com.armedia.acm.services.users.dao.ldap;

import com.armedia.acm.services.users.model.ldap.AcmLdapConfig;
import com.armedia.acm.services.users.model.ldap.AcmLdapSyncConfig;
import com.armedia.acm.services.users.model.ldap.Directory;
import com.armedia.acm.services.users.model.ldap.LdapGroup;
import com.armedia.acm.services.users.model.ldap.LdapUser;
import com.armedia.acm.services.users.model.ldap.SimpleAuthenticationSource;

import org.springframework.ldap.core.AuthenticationSource;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;

import java.util.List;
import java.util.Optional;

public interface SpringLdapDao
{
    /*
     * This builds the ldap template from the base AcmLdapConfig
     */
    default LdapTemplate buildLdapTemplate(final AcmLdapConfig syncConfig)
    {
        return buildLdapTemplate(syncConfig, null, null);
    }

    default LdapTemplate buildLdapTemplate(final AcmLdapConfig syncConfig, final String authUserDn, final String authUserPwd)
    {
        final String userDn = Optional.ofNullable(authUserDn).orElseGet(syncConfig::getAuthUserDn);
        final String password = Optional.ofNullable(authUserPwd).orElseGet(syncConfig::getAuthUserPassword);
        AuthenticationSource authenticationSource = new SimpleAuthenticationSource(userDn, password);

        LdapContextSource ldapContextSource = new LdapContextSource();
        ldapContextSource.setUrls(syncConfig.getLdapUrl());
        ldapContextSource.setBase(syncConfig.getBaseDC());
        ldapContextSource.setAuthenticationSource(authenticationSource);
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
        return lastSyncDate.map(it -> String.format(syncConfig.getChangedGroupSearchFilter(), it))
                .orElse(syncConfig.getGroupSearchFilter());
    }

    default String buildUsersSearchFilter(AcmLdapSyncConfig syncConfig, Optional<String> lastSyncDate)
    {
        // eg. allUsersFilter = (objectClass=person )
        // allChangedUsersFilter = (&(objectClass=person)(modifyTimestamp>=%s))
        return lastSyncDate.map(it -> String.format(syncConfig.getAllChangedUsersFilter(), it))
                .orElse(syncConfig.getAllUsersFilter());
    }

    default String buildPagedGroupsSearchFilter(AcmLdapSyncConfig syncConfig, String sortAttributeValue, Optional<String> lastSyncDate)
    {
        return lastSyncDate.map(it -> String.format(syncConfig.getChangedGroupSearchPageFilter(), sortAttributeValue, it))
                .orElse(String.format(syncConfig.getGroupSearchPageFilter(), sortAttributeValue));

    }

    default String buildPagedUsersSearchFilter(AcmLdapSyncConfig syncConfig, String sortAttributeValue, Optional<String> lastSyncDate)
    {
        // eg. allUsersPageFilter = (&(objectClass=person)(uidNumber>=%s))
        // allChangedUsersPageFilter = (&(objectClass=person)(uidNumber>=%s)(modifyTimestamp>=%s))
        return lastSyncDate.map(it -> String.format(syncConfig.getAllChangedUsersPageFilter(), sortAttributeValue, it))
                .orElse(String.format(syncConfig.getAllUsersPageFilter(), sortAttributeValue));
    }

    List<LdapUser> findUsersPaged(LdapTemplate template, final AcmLdapSyncConfig syncConfig, Optional<String> ldapLastSyncDate);

    List<LdapGroup> findGroupsPaged(LdapTemplate template, AcmLdapSyncConfig config, Optional<String> ldapLastSyncDate);
}
