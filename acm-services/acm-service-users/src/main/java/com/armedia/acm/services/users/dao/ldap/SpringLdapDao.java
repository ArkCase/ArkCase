package com.armedia.acm.services.users.dao.ldap;

import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.LdapGroup;
import com.armedia.acm.services.users.model.ldap.AcmLdapConfig;
import com.armedia.acm.services.users.model.ldap.AcmLdapSyncConfig;
import com.armedia.acm.services.users.model.ldap.SimpleAuthenticationSource;
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

    List<AcmUser> findUsersPaged(LdapTemplate template, final AcmLdapSyncConfig syncConfig);

    List<LdapGroup> findGroupsPaged(LdapTemplate template, AcmLdapSyncConfig config);

}
