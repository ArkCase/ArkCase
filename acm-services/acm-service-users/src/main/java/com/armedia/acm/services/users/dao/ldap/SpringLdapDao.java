package com.armedia.acm.services.users.dao.ldap;

import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.LdapGroup;
import com.armedia.acm.services.users.model.ldap.AcmLdapConfig;
import com.armedia.acm.services.users.model.ldap.AcmLdapConstants;
import com.armedia.acm.services.users.model.ldap.AcmLdapSyncConfig;
import com.armedia.acm.services.users.model.ldap.SimpleAuthenticationSource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ldap.core.AuthenticationSource;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
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

    default String convertToOpenLdapTimestamp(String timestamp)
    {
        ZonedDateTime dateTime = ZonedDateTime.parse(timestamp);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(AcmLdapConstants.LDAP_OPENLDAP_DATE_PATTERN);
        return dateTime.format(formatter);
    }

    default String convertToActiveDirectoryTimestamp(String timestamp)
    {
        ZonedDateTime dateTime = ZonedDateTime.parse(timestamp, DateTimeFormatter.ISO_DATE_TIME);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(AcmLdapConstants.LDAP_AD_DATE_PATTERN);
        return dateTime.format(formatter);
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

    List<AcmUser> findUsersPaged(LdapTemplate template, final AcmLdapSyncConfig syncConfig);

    List<LdapGroup> findGroupsPaged(LdapTemplate template, AcmLdapSyncConfig config);

    List<AcmUser> findChangedUsersPaged(LdapTemplate template, final AcmLdapSyncConfig syncConfig, String[] attributes, String ldapLastSyncDate);

    List<LdapGroup> findChangedGroupsPaged(LdapTemplate template, AcmLdapSyncConfig config, String ldapLastSyncDate);
}
