package com.armedia.acm.services.users.dao.ldap;

import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.LdapGroup;
import com.armedia.acm.services.users.model.ldap.AcmGroupContextMapper;
import com.armedia.acm.services.users.model.ldap.AcmLdapConfig;
import com.armedia.acm.services.users.model.ldap.AcmLdapSyncConfig;
import com.armedia.acm.services.users.model.ldap.AcmUserGroupsContextMapper;
import com.armedia.acm.services.users.model.ldap.SimpleAuthenticationSource;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.control.PagedResultsCookie;
import org.springframework.ldap.control.PagedResultsDirContextProcessor;
import org.springframework.ldap.control.SortControlDirContextProcessor;
import org.springframework.ldap.core.AuthenticationSource;
import org.springframework.ldap.core.ContextMapper;
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
    static final Logger log = LoggerFactory.getLogger(SpringLdapDao.class);

    private AcmGroupContextMapper acmGroupContextMapper;

    private AcmUserGroupsContextMapper userGroupsContextMapper;

    // make it possible to unit test
    public class PagedResultsDirContextProcessorBuilder
    {
        public PagedResultsDirContextProcessor build(int pageSize, PagedResultsCookie cookie)
        {
            return new PagedResultsDirContextProcessor(pageSize, cookie);
        }
    }

    private PagedResultsDirContextProcessorBuilder builder = new PagedResultsDirContextProcessorBuilder();

    private <T> List<T> fetchLdapPaged(LdapTemplate template, String searchBase, String searchFilter,
                                       SearchControls searchControls, int pageSize, ContextMapper contextMapper)
    {
        List<T> result = new ArrayList<>();
        // for the first paged-search request we pass null cookie
        PagedResultsCookie resultsCookie = null;
        while (true)
        {
            PagedResultsDirContextProcessor pagedResultsDirContextProcessor =
                    builder.build(pageSize, resultsCookie);
            log.debug("Start fetching '{}' items from LDAP", pageSize);
            List<T> items = template.search(searchBase, searchFilter,
                    searchControls, contextMapper, pagedResultsDirContextProcessor);
            log.debug("Items fetched: {}", items.size());
            result.addAll(items);

            // pass the cookie in the next calls so the server keeps track of where he left of previous time
            resultsCookie = pagedResultsDirContextProcessor.getCookie();

            // when there is no more pages to be fetched the cookie is null
            if (resultsCookie.getCookie() == null)
            {
                break;
            }
        }
        return result;
    }

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

    public List<AcmUser> findUsersPaged(LdapTemplate template, final AcmLdapSyncConfig syncConfig)
    {
        return findUsersPaged(template, syncConfig, AcmUserGroupsContextMapper.USER_LDAP_ATTRIBUTES);
    }

    public List<AcmUser> findUsersPaged(LdapTemplate template, final AcmLdapSyncConfig syncConfig, String[] attributes)
    {
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        if (attributes != null)
        {
            String[] allAttributes = ArrayUtils.addAll(attributes,
                    syncConfig.getUserIdAttributeName(), syncConfig.getMailAttributeName());
            searchControls.setReturningAttributes(allAttributes);
        }

        userGroupsContextMapper.setUserIdAttributeName(syncConfig.getUserIdAttributeName());
        userGroupsContextMapper.setMailAttributeName(syncConfig.getMailAttributeName());

        String searchBase = syncConfig.getAllUsersSearchBase();
        String[] bases = searchBase.split("\\|");
        List<AcmUser> acmUsers = new ArrayList<>();
        for (String base : bases)
        {
            List<AcmUser> users = fetchLdapPaged(template, base, syncConfig.getAllUsersFilter(), searchControls,
                    syncConfig.getSyncPageSize(), userGroupsContextMapper);
            log.info("Fetched total '{}' users for search base '{}'", users.size(), base);
            acmUsers.addAll(users);
        }

        // filter out the DISABLED users
        acmUsers = acmUsers.stream().filter(u -> !("DISABLED".equals(u.getUserState()))).collect(Collectors.toList());

        // do we need to append the domain?
        String userDomain = syncConfig.getUserDomain();
        if (userDomain != null && !userDomain.trim().isEmpty())
        {
            String userDomainSuffix = "@" + userDomain;
            acmUsers.stream().forEach(u -> u.setUserId(u.getUserId() + userDomainSuffix));
        }

        log.info("LDAP sync number of enabled users: {}", acmUsers.size());
        return acmUsers;
    }

    public List<LdapGroup> findGroupsPaged(LdapTemplate template, final AcmLdapSyncConfig syncConfig)
    {
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        searchControls.setReturningAttributes(new String[]{"cn", "memberOf"});

        String searchBase = syncConfig.getGroupSearchBase();
        List<LdapGroup> acmGroups = fetchLdapPaged(template, searchBase, syncConfig.getGroupSearchFilter(),
                searchControls, syncConfig.getSyncPageSize(), acmGroupContextMapper);

        log.info("LDAP sync number of groups: {}", acmGroups.size());
        return acmGroups;
    }

    // redundant code but useful if in findUsersPaged() cookie paging fails
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

    // redundant code but useful if in findGroupsPaged() cookie paging fails
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

    public AcmUser findUser(String username, LdapTemplate template, AcmLdapSyncConfig config, String[] attributes)
    {
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

        userGroupsContextMapper.setUserIdAttributeName(config.getUserIdAttributeName());
        userGroupsContextMapper.setMailAttributeName(config.getMailAttributeName());

        if (attributes != null)
        {
            String[] allAttributes = ArrayUtils.addAll(attributes,
                    config.getUserIdAttributeName(), config.getMailAttributeName());
            searchControls.setReturningAttributes(allAttributes);
        }

        List<AcmUser> results = template.search(config.getUserSearchBase(),
                String.format(config.getUserSearchFilter(), username), searchControls, userGroupsContextMapper);

        if (CollectionUtils.isNotEmpty(results))
        {
            // Return the first entity that will be found. The above search can return multiple results under one domain if
            // "sAMAccountName" is the same for two users. This in theory should not be the case, but just in case, return only the first one.
            AcmUser acmUser = results.get(0);

            // append user domain name if set. Used in Single Sign-On scenario.
            String userDomainSuffix = (StringUtils.isBlank(config.getUserDomain()) ? "" : "@" + config.getUserDomain());
            log.debug("Adding user domain sufix to the username: {}", userDomainSuffix);
            acmUser.setUserId(acmUser.getUserId() + userDomainSuffix);
            return acmUser;
        }

        throw new UsernameNotFoundException("User with id [" + username + "] cannot be found");
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

    public PagedResultsDirContextProcessorBuilder getBuilder()
    {
        return builder;
    }

    public void setBuilder(PagedResultsDirContextProcessorBuilder builder)
    {
        this.builder = builder;
    }
}
