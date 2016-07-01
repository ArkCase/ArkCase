package com.armedia.acm.services.users.dao.ldap;

import com.armedia.acm.services.users.model.AcmLdapEntity;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.LdapGroup;
import com.armedia.acm.services.users.model.ldap.AcmLdapConfig;
import com.armedia.acm.services.users.model.ldap.AcmLdapEntityContextMapper;
import com.armedia.acm.services.users.model.ldap.AcmLdapSyncConfig;
import com.armedia.acm.services.users.model.ldap.GroupMembersContextMapper;
import com.armedia.acm.services.users.model.ldap.SimpleAuthenticationSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.NamingException;
import org.springframework.ldap.core.AuthenticationSource;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.naming.directory.SearchControls;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by armdev on 5/28/14.
 */
public class SpringLdapDao
{

    private final GroupMembersContextMapper groupMembersContextMapper = new GroupMembersContextMapper();

    private AcmLdapEntityContextMapper mapper;

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

        mapper.setUserIdAttributeName(syncConfig.getUserIdAttributeName());
        mapper.setMailAttributeName(syncConfig.getMailAttributeName());


        for (String memberDn : memberDns)
        {
            log.debug("Looking up LDAP user '{}'", memberDn);

            memberDn = memberDn.replaceAll("\\/", "\\\\/"); // some of the DNs contain (/) which is a special character to JNDI
            AcmLdapEntity ldapEntity = (AcmLdapEntity) template.lookup(memberDn, mapper);

            // The context mapper returns null if the group member is a disabled user
            if (ldapEntity != null)
            {
                ldapEntity.setDistinguishedName(memberDn);
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

    public AcmUser findUser(String username, LdapTemplate template, AcmLdapSyncConfig config)
    {
        AcmUser user = null;

        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

        mapper.setUserIdAttributeName(config.getUserIdAttributeName());
        mapper.setMailAttributeName(config.getMailAttributeName());

        List<AcmLdapEntity> results = template.search(config.getUserSearchBase(), String.format(config.getUserSearchFilter(), username), searchControls, mapper);

        if (results != null && !results.isEmpty())
        {
            // Return the first entity that will be found. The above search can return multiple results under one domain if
            // "sAMAccountName" is the same for two users. This in theory should not be the case, but just in case, return only the first one.
            AcmLdapEntity ldapEntity = results.get(0);
            if (ldapEntity != null && !ldapEntity.isGroup())
            {
                user = (AcmUser) ldapEntity;
                // append user domain name if set. Used in Single Sign-On scenario.
                String userDomainSuffix = ((config.getUserDomain() == null || config.getUserDomain().trim().equals("")) ? "" : "@" + config.getUserDomain());
                log.debug("Adding user domain sufix to the usernames: {}", userDomainSuffix);
                user.setUserId(user.getUserId() + userDomainSuffix);
            }
        }

        if (user != null)
        {
            return user;
        }

        throw new UsernameNotFoundException("User with id [" + username + "] cannot be found");
    }

    public List<LdapGroup> findGroupsForUser(AcmUser user, LdapTemplate template, AcmLdapSyncConfig config)
    {
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

        List<LdapGroup> groups = template.search(config.getGroupSearchBase(), String.format(config.getGroupSearchFilterForUser(), user.getDistinguishedName()), searchControls, groupMembersContextMapper);

        return groups;
    }

    public AcmLdapEntityContextMapper getMapper()
    {
        return mapper;
    }

    public void setMapper(AcmLdapEntityContextMapper mapper)
    {
        this.mapper = mapper;
    }
}
