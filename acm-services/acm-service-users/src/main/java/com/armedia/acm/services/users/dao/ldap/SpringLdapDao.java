package com.armedia.acm.services.users.dao.ldap;

import com.armedia.acm.services.users.model.LdapGroup;
import com.armedia.acm.services.users.model.LdapUser;
import com.armedia.acm.services.users.model.ldap.AcmLdapConfig;
import com.armedia.acm.services.users.model.ldap.AcmLdapSyncConfig;
import com.armedia.acm.services.users.model.ldap.Directory;
import com.armedia.acm.services.users.model.ldap.SimpleAuthenticationSource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ldap.core.AuthenticationSource;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;

import java.util.List;

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
        return directoryType.equals(Directory.OPEN_LDAP.getType()) ?
                Directory.OPEN_LDAP.convertToDirectorySpecificTimestamp(ldapLastSyncTimestamp) :
                Directory.ACTIVE_DIRECTORY.convertToDirectorySpecificTimestamp(ldapLastSyncTimestamp);
    }

    default String buildGroupSearchFilter(AcmLdapSyncConfig syncConfig, String lastSyncDate)
    {
        return StringUtils.isBlank(lastSyncDate) ? syncConfig.getGroupSearchFilter()
                : String.format(syncConfig.getChangedGroupSearchFilter(), lastSyncDate);
    }

    default String buildUsersSearchFilter(AcmLdapSyncConfig syncConfig, String lastSyncDate)
    {
        // eg. allUsersFilter = (objectClass=person )
        // allChangedUsersFilter = (&(objectClass=person)(modifyTimestamp>=%s))
        return StringUtils.isBlank(lastSyncDate) ? syncConfig.getAllUsersFilter()
                : String.format(syncConfig.getAllChangedUsersFilter(), lastSyncDate);
    }

    default String buildPagedGroupsSearchFilter(AcmLdapSyncConfig syncConfig, String sortAttributeValue, String lastSyncDate)
    {
        return StringUtils.isBlank(lastSyncDate) ? String.format(syncConfig.getGroupSearchPageFilter(), sortAttributeValue)
                : String.format(syncConfig.getGroupSearchPageFilter(), sortAttributeValue, lastSyncDate);
    }

    default String buildPagedUsersSearchFilter(AcmLdapSyncConfig syncConfig, String sortAttributeValue, String lastSyncDate)
    {
        // eg. allUsersPageFilter = (&(objectClass=person)(uidNumber>=%s))
        // allChangedUsersPageFilter = (&(objectClass=person)(uidNumber>=%s)(modifyTimestamp>=%s))
        return StringUtils.isNotBlank(lastSyncDate) ? String.format(syncConfig.getAllChangedUsersPageFilter(),
                sortAttributeValue, lastSyncDate) : String.format(syncConfig.getAllUsersPageFilter(), sortAttributeValue);
    }

    List<LdapUser> findUsersPaged(LdapTemplate template, final AcmLdapSyncConfig syncConfig, String ldapLastSyncDate);

    List<LdapGroup> findGroupsPaged(LdapTemplate template, AcmLdapSyncConfig config, String ldapLastSyncDate);
}
