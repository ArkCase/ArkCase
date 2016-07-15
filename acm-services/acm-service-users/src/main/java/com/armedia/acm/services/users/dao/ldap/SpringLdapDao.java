package com.armedia.acm.services.users.dao.ldap;

import com.armedia.acm.services.users.model.AcmLdapEntity;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.LdapGroup;
import com.armedia.acm.services.users.model.ldap.AcmGroupContextMapper;
import com.armedia.acm.services.users.model.ldap.AcmLdapConfig;
import com.armedia.acm.services.users.model.ldap.AcmLdapEntityContextMapper;
import com.armedia.acm.services.users.model.ldap.AcmLdapEntitySimpleContextMapper;
import com.armedia.acm.services.users.model.ldap.AcmLdapSyncConfig;
import com.armedia.acm.services.users.model.ldap.AcmUserGroupsContextMapper;
import com.armedia.acm.services.users.model.ldap.GroupMembersContextMapper;
import com.armedia.acm.services.users.model.ldap.SimpleAuthenticationSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.control.PagedResultsDirContextProcessor;
import org.springframework.ldap.control.SortControlDirContextProcessor;
import org.springframework.ldap.core.AuthenticationSource;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.AggregateDirContextProcessor;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.naming.directory.SearchControls;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by armdev on 5/28/14.
 */
public class SpringLdapDao
{

    private final GroupMembersContextMapper groupMembersContextMapper = new GroupMembersContextMapper();

    private AcmGroupContextMapper acmGroupContextMapper;

    private AcmLdapEntityContextMapper mapper;

    private AcmLdapEntitySimpleContextMapper simpleContextMapper;

    private AcmUserGroupsContextMapper userGroupsContextMapper;

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

    public List<AcmUser> findUsers(LdapTemplate template, final AcmLdapSyncConfig syncConfig)
    {
        return findUsers(template, syncConfig, null);
    }

    public List<AcmUser> findUsers(LdapTemplate template, final AcmLdapSyncConfig syncConfig, String[] attributes)
    {
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        if (attributes != null)
        {
            searchControls.setReturningAttributes(attributes);
        }
        AggregateDirContextProcessor sortedAndPaged = buildSortedAndPagesProcessor(syncConfig, syncConfig.getUserIdAttributeName());


        userGroupsContextMapper.setUserIdAttributeName(syncConfig.getUserIdAttributeName());
        userGroupsContextMapper.setMailAttributeName(syncConfig.getMailAttributeName());
        String searchFilter = syncConfig.getAllUsersFilter();
        String searchBase = syncConfig.getAllUsersSearchBase();
        String[] bases = searchBase.split("\\|");
        List<AcmUser> acmUsers = new ArrayList<>();
        for (String base : bases)
        {
//            List<AcmUser> found

            boolean searchUsers = true;
            boolean skipFirst = false;
            while (searchUsers)
            {
                // the context mapper will return null for disabled users
                List<AcmUser> found = template.search(base, searchFilter, searchControls, userGroupsContextMapper, sortedAndPaged);

                if (skipFirst)
                {
                    acmUsers.addAll(found.subList(1, found.size()));
                } else
                {
                    acmUsers.addAll(found);
                }

                log.debug("Users found: {}", found.size());

                searchUsers = syncConfig.getSyncPageSize() == found.size();
                if (searchUsers)
                {
                    skipFirst = true;
                    AcmUser lastFound = found.get(found.size() - 1);
                    String uid = lastFound.getUserId();
                    searchFilter = String.format(syncConfig.getAllUsersPageFilter(), uid);

                    // A change to the search filter requires us to rebuild the search controls... even though
                    // the controls will have the same values as before.
                    sortedAndPaged = buildSortedAndPagesProcessor(syncConfig, syncConfig.getUserIdAttributeName());
                    log.debug("Search filter now: {}", searchFilter);
                }
            }

            // filter out the DISABLED users
            acmUsers = acmUsers.stream().filter(u -> !("DISABLED".equals(u.getUserState()))).collect(Collectors.toList());

            log.info("LDAP sync number of enabled users: {}", acmUsers.size());


        }
        return acmUsers;
    }

    private AggregateDirContextProcessor buildSortedAndPagesProcessor(AcmLdapSyncConfig syncConfig, String sortAttribute)
    {
        AggregateDirContextProcessor sortedAndPaged = new AggregateDirContextProcessor();
        SortControlDirContextProcessor sorted = new SortControlDirContextProcessor(sortAttribute);
        PagedResultsDirContextProcessor pagedResultsProcessor = new PagedResultsDirContextProcessor(syncConfig.getSyncPageSize());

        sortedAndPaged.addDirContextProcessor(sorted);
        sortedAndPaged.addDirContextProcessor(pagedResultsProcessor);

        return sortedAndPaged;
    }

    public AcmLdapEntity lookupUser(LdapTemplate ldapTemplate, final AcmLdapSyncConfig syncConfig, String dn)
    {
        simpleContextMapper.setUserIdAttributeName(syncConfig.getUserIdAttributeName());
        simpleContextMapper.setMailAttributeName(syncConfig.getMailAttributeName());
        return (AcmLdapEntity) ldapTemplate.lookup(dn, simpleContextMapper);
    }


    public List<LdapGroup> findGroups(LdapTemplate template, AcmLdapSyncConfig config)
    {
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        searchControls.setReturningAttributes(new String[]{"cn", "memberOf"});

        AggregateDirContextProcessor sortedAndPaged = buildSortedAndPagesProcessor(config, "cn");

        boolean searchGroups = true;
        boolean skipFirst = false;
        String searchFilter = config.getGroupSearchFilter();
        List<LdapGroup> acmGroups = new ArrayList<>();

        while (searchGroups)
        {

            log.debug("search filter: {}", searchFilter);
            List<LdapGroup> found = template.search(config.getGroupSearchBase(), searchFilter, searchControls,
                    acmGroupContextMapper, sortedAndPaged);

            if (skipFirst)
            {
                acmGroups.addAll(found.subList(1, found.size()));
            } else
            {
                acmGroups.addAll(found);
            }

            log.debug("Groups found: {}", found.size());

            searchGroups = config.getSyncPageSize() == found.size();
            if (searchGroups)
            {
                skipFirst = true;
                LdapGroup lastFound = found.get(found.size() - 1);
                String cn = lastFound.getGroupName();

                searchFilter = String.format(config.getGroupSearchPageFilter(), cn);

                // A change to the search filter requires us to rebuild the search controls... even though
                // the controls will have the same values as before.
                sortedAndPaged = buildSortedAndPagesProcessor(config, "cn");
                log.trace("Search filter now: {}", searchFilter);
            }
        }

        log.info("LDAP sync number of groups: {}", acmGroups.size());

        return acmGroups;
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

    public AcmLdapEntitySimpleContextMapper getSimpleContextMapper()
    {
        return simpleContextMapper;
    }

    public void setSimpleContextMapper(AcmLdapEntitySimpleContextMapper simpleContextMapper)
    {
        this.simpleContextMapper = simpleContextMapper;
    }

    public AcmUserGroupsContextMapper getUserGroupsContextMapper()
    {
        return userGroupsContextMapper;
    }

    public void setUserGroupsContextMapper(AcmUserGroupsContextMapper userGroupsContextMapper)
    {
        this.userGroupsContextMapper = userGroupsContextMapper;
    }

    public AcmGroupContextMapper getAcmGroupContextMapper()
    {
        return acmGroupContextMapper;
    }

    public void setAcmGroupContextMapper(AcmGroupContextMapper acmGroupContextMapper)
    {
        this.acmGroupContextMapper = acmGroupContextMapper;
    }
}
