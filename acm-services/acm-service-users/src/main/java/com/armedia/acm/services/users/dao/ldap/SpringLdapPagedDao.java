package com.armedia.acm.services.users.dao.ldap;


import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.LdapGroup;
import com.armedia.acm.services.users.model.ldap.AcmGroupContextMapper;
import com.armedia.acm.services.users.model.ldap.AcmLdapSyncConfig;
import com.armedia.acm.services.users.model.ldap.AcmUserGroupsContextMapper;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.control.PagedResultsCookie;
import org.springframework.ldap.control.PagedResultsDirContextProcessor;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.LdapTemplate;

import javax.naming.directory.SearchControls;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SpringLdapPagedDao implements SpringLdapDao
{
    private Logger log = LoggerFactory.getLogger(getClass());

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

    @Override
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

        AcmUserGroupsContextMapper userGroupsContextMapper = new AcmUserGroupsContextMapper();
        userGroupsContextMapper.setUserIdAttributeName(syncConfig.getUserIdAttributeName());
        userGroupsContextMapper.setMailAttributeName(syncConfig.getMailAttributeName());

        String searchBase = syncConfig.getUserSearchBase();

        // Spring LDAP authentication doesn't support multiple search bases divided by "|"
        // this is custom implementation if we want to sync users from  different search bases
        // Note: - add new property in AcmLdapSyncConfig if you want to define multiple search bases and use that instead
        // - users synced from different search base other than the one defined in property "userSearchBase" won't be
        // able to log in
        // Multiple search bases is supported in some of the upper spring security versions
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

    @Override
    public List<LdapGroup> findGroupsPaged(LdapTemplate template, final AcmLdapSyncConfig syncConfig)
    {
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        searchControls.setReturningAttributes(new String[]{"cn", "memberOf"});

        AcmGroupContextMapper acmGroupContextMapper = new AcmGroupContextMapper();
        String searchBase = syncConfig.getGroupSearchBase();
        List<LdapGroup> acmGroups = fetchLdapPaged(template, searchBase, syncConfig.getGroupSearchFilter(),
                searchControls, syncConfig.getSyncPageSize(), acmGroupContextMapper);

        log.info("LDAP sync number of groups: {}", acmGroups.size());
        return acmGroups;
    }

    public PagedResultsDirContextProcessorBuilder getBuilder()
    {
        return builder;
    }

    public void setBuilder(PagedResultsDirContextProcessorBuilder builder)
    {
        this.builder = builder;
    }

    // make it possible to unit test
    public class PagedResultsDirContextProcessorBuilder
    {
        public PagedResultsDirContextProcessor build(int pageSize, PagedResultsCookie cookie)
        {
            return new PagedResultsDirContextProcessor(pageSize, cookie);
        }
    }
}
