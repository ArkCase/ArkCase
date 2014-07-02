package com.armedia.acm.services.users.dao.ldap;

import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.ldap.AcmLdapSyncConfig;
import com.armedia.acm.services.users.model.ldap.AcmUserContextMapper;
import com.armedia.acm.services.users.model.ldap.GroupMembersContextMapper;
import com.armedia.acm.services.users.model.ldap.LdapGroup;
import com.armedia.acm.services.users.model.ldap.SimpleAuthenticationSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.core.AuthenticationSource;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by armdev on 5/28/14.
 */
public class SpringLdapDao
{

    private Logger log = LoggerFactory.getLogger(getClass());

    public LdapTemplate buildLdapTemplate(final AcmLdapSyncConfig syncConfig)
    {
        AuthenticationSource authenticationSource = null;

        if ( syncConfig.getAuthUserDn() != null )
        {
            authenticationSource = new SimpleAuthenticationSource(syncConfig.getAuthUserDn(), syncConfig.getAuthUserPassword());
        }

        LdapContextSource ldapContextSource = new LdapContextSource();
        ldapContextSource.setUrl(syncConfig.getLdapUrl());
        if ( authenticationSource != null )
        {
            ldapContextSource.setAuthenticationSource(authenticationSource);
        }
        ldapContextSource.setCacheEnvironmentProperties(false);
        ldapContextSource.setReferral(syncConfig.getReferral());

        LdapTemplate ldapTemplate = new LdapTemplate(ldapContextSource);
        ldapTemplate.setIgnorePartialResultException(syncConfig.isIgnorePartialResultException());
        return ldapTemplate;
    }

    public List<AcmUser> findGroupMembers(LdapTemplate template, final AcmLdapSyncConfig syncConfig, LdapGroup group)
    {
        String[] memberDns = group.getMemberDistinguishedNames();

        List<AcmUser> retval = new ArrayList<>(memberDns.length);

        AcmUserContextMapper mapper = new AcmUserContextMapper();
        mapper.setUserIdAttributeName(syncConfig.getUserIdAttributeName());

        boolean debug = log.isDebugEnabled();

        for ( String memberDn : memberDns )
        {
            if ( debug )
            {
                log.debug("Looking up user '" + memberDn + "'");
            }

            AcmUser user = (AcmUser) template.lookup(memberDn, mapper);

            if ( user != null )
            {
                retval.add(user);
            }
        }

        return retval;
    }

    public List<LdapGroup> findGroups(LdapTemplate template, AcmLdapSyncConfig config)
    {
        List<LdapGroup> groups = template.search(
                config.getGroupSearchBase(),
                config.getGroupSearchFilter(),
                new GroupMembersContextMapper());
        return groups;
    }
}
