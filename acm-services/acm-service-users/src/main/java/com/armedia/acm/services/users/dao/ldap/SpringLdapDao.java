package com.armedia.acm.services.users.dao.ldap;

import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.ldap.AcmLdapSyncConfig;
import com.armedia.acm.services.users.model.ldap.AcmUserContextMapper;
import com.armedia.acm.services.users.model.ldap.GroupMembersContextMapper;
import com.armedia.acm.services.users.model.ldap.SimpleAuthenticationSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.core.AuthenticationSource;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by armdev on 5/28/14.
 */
public class SpringLdapDao
{

    private Logger log = LoggerFactory.getLogger(getClass());

    public List<String> groupMemberDistinguishedNames(LdapTemplate ldapTemplate, AcmLdapSyncConfig syncConfig, String groupName)
    {
        if ( log.isInfoEnabled() )
        {
            log.info("Looking up group members for group: " + groupName);
        }

        List<String[]> groupMembers = ldapTemplate.search(
                syncConfig.getGroupSearchBase(),
                String.format(syncConfig.getGroupSearchFilter(), groupName),
                new GroupMembersContextMapper());

        if ( groupMembers.size() > 1 )
        {
            throw new IllegalStateException("Should only find one group named '" + groupName + "', instead found " +
                groupMembers.size());
        }

        if ( groupMembers.isEmpty() )
        {
            return new ArrayList<>();
        }

        String[] members = groupMembers.get(0);
        return Arrays.asList(members);
    }

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

    public List<AcmUser> findGroupMembers(LdapTemplate template, final AcmLdapSyncConfig syncConfig, String ldapGroup)
    {
        List<String> memberDns = groupMemberDistinguishedNames(template, syncConfig, ldapGroup);

        List<AcmUser> retval = new ArrayList<>(memberDns.size());

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
}
