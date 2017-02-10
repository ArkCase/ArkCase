package com.armedia.acm.services.users.dao.ldap;

import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.LdapGroup;
import com.armedia.acm.services.users.model.ldap.AcmGroupContextMapper;
import com.armedia.acm.services.users.model.ldap.AcmLdapSyncConfig;
import com.armedia.acm.services.users.model.ldap.AcmUserGroupsContextMapper;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.control.PagedResultsDirContextProcessor;
import org.springframework.ldap.control.SortControlDirContextProcessor;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.AggregateDirContextProcessor;

import javax.naming.directory.SearchControls;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CustomPagedLdapDao implements SpringLdapDao
{
    private Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public List<AcmUser> findUsersPaged(LdapTemplate template, AcmLdapSyncConfig syncConfig)
    {
        return findUsers(template, syncConfig, AcmUserGroupsContextMapper.USER_LDAP_ATTRIBUTES);
    }

    public List<AcmUser> findUsers(LdapTemplate template, final AcmLdapSyncConfig syncConfig, String[] attributes)
    {
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        if (attributes != null)
        {
            String[] allAttributes = ArrayUtils.addAll(attributes, syncConfig.getUserIdAttributeName(), syncConfig.getMailAttributeName());
            searchControls.setReturningAttributes(allAttributes);
        }
        AggregateDirContextProcessor sortedAndPaged = buildSortedAndPagesProcessor(syncConfig, syncConfig.getAllUsersSortingAttribute());

        AcmUserGroupsContextMapper userGroupsContextMapper = new AcmUserGroupsContextMapper(syncConfig);
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
                    searchFilter = String.format(syncConfig.getAllUsersPageFilter(), lastFound.getSortableValue());

                    // A change to the search filter requires us to rebuild the search controls... even though
                    // the controls will have the same values as before.
                    sortedAndPaged = buildSortedAndPagesProcessor(syncConfig, syncConfig.getAllUsersSortingAttribute());
                    log.debug("Search filter now: {}", searchFilter);
                }
            }

            // filter out the DISABLED users
            acmUsers = acmUsers.stream().filter(u -> !("DISABLED".equals(u.getUserState()))).collect(Collectors.toList());

            String userDomain = syncConfig.getUserDomain();
            if (userDomain != null && !userDomain.trim().isEmpty())
            {
                String userDomainSuffix = "@" + userDomain;
                acmUsers.forEach(u -> u.setUserId(u.getUserId() + userDomainSuffix));
            }

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

    @Override
    public List<LdapGroup> findGroupsPaged(LdapTemplate template, AcmLdapSyncConfig config)
    {
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        searchControls.setReturningAttributes(new String[]{
                "cn",
                "memberOf"});

        AggregateDirContextProcessor sortedAndPaged = buildSortedAndPagesProcessor(config, config.getGroupsSortingAttribute());
        AcmGroupContextMapper acmGroupContextMapper = new AcmGroupContextMapper(config);

        boolean searchGroups = true;
        boolean skipFirst = false;
        String searchFilter = config.getGroupSearchFilter();
        List<LdapGroup> acmGroups = new ArrayList<>();

        while (searchGroups)
        {

            log.debug("search filter: {}", searchFilter);
            List<LdapGroup> found = template.search(config.getGroupSearchBase(), searchFilter, searchControls, acmGroupContextMapper,
                    sortedAndPaged);

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
                searchFilter = String.format(config.getGroupSearchPageFilter(), lastFound.getSortableValue());

                // A change to the search filter requires us to rebuild the search controls... even though
                // the controls will have the same values as before.
                sortedAndPaged = buildSortedAndPagesProcessor(config, config.getGroupsSortingAttribute());
                log.trace("Search filter now: {}", searchFilter);
            }
        }

        log.info("LDAP sync number of groups: {}", acmGroups.size());

        return acmGroups;
    }

}
