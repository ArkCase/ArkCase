package com.armedia.acm.services.users.dao.ldap;

import com.armedia.acm.services.users.model.AcmLdapEntity;
import com.armedia.acm.services.users.model.LdapGroup;
import com.armedia.acm.services.users.model.ldap.AcmLdapConfig;
import com.armedia.acm.services.users.model.ldap.AcmLdapEntityContextMapper;
import com.armedia.acm.services.users.model.ldap.AcmLdapSyncConfig;
import com.armedia.acm.services.users.model.ldap.GroupMembersContextMapper;
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

    private final GroupMembersContextMapper groupMembersContextMapper = new GroupMembersContextMapper();

    private Logger log = LoggerFactory.getLogger(getClass());

    /*
     * This builds the ldap template from the base AcmLdapConfig
     */
    public LdapTemplate buildLdapTemplate(final AcmLdapConfig syncConfig)
    {
        AuthenticationSource authenticationSource = null;

        if (syncConfig.getAuthUserDn() != null)
        {
            authenticationSource = new SimpleAuthenticationSource(syncConfig.getAuthUserDn(), syncConfig.getAuthUserPassword());
        }

        LdapContextSource ldapContextSource = new LdapContextSource();
        ldapContextSource.setUrl(syncConfig.getLdapUrl());
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

    public List<AcmLdapEntity> findGroupMembers(LdapTemplate template, final AcmLdapSyncConfig syncConfig, LdapGroup group)
    {
        String[] memberDns = group.getMemberDistinguishedNames();

        List<AcmLdapEntity> retval = new ArrayList<>(memberDns.length);

        AcmLdapEntityContextMapper mapper = new AcmLdapEntityContextMapper();
        mapper.setUserIdAttributeName(syncConfig.getUserIdAttributeName());

        boolean debug = log.isDebugEnabled();

        for (String memberDn : memberDns)
        {
            if (debug)
            {
                log.debug("Looking up user '" + memberDn + "'");
            }

            AcmLdapEntity ldapEntity = (AcmLdapEntity) template.lookup(memberDn, mapper);
            ldapEntity.setDistinguishedName(memberDn);

            // The context mapper returns null if the group member is a disabled user
            if (ldapEntity != null)
            {
                retval.add(ldapEntity);
            }
        }

        return retval;
    }

    public List<LdapGroup> findGroups(LdapTemplate template, AcmLdapSyncConfig config)
    {
        List<LdapGroup> groups = template.search(
                config.getGroupSearchBase(),
                config.getGroupSearchFilter(),
                groupMembersContextMapper);
        return groups;
    }

    public LdapGroup findGroup(LdapTemplate template, AcmLdapSyncConfig config, String groupDistinguishedName)
    {
        LdapGroup group = (LdapGroup) template.lookup(groupDistinguishedName, groupMembersContextMapper);
        return group;
    }

}
