package com.armedia.acm.services.users.dao.ldap;

import com.armedia.acm.services.users.model.ldap.LdapGroup;
import com.armedia.acm.services.users.model.ldap.LdapUser;
import com.armedia.acm.services.users.model.ldap.AcmGroupContextMapper;
import com.armedia.acm.services.users.model.ldap.AcmLdapSyncConfig;
import com.armedia.acm.services.users.model.ldap.AcmUserContextMapper;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.control.PagedResultsDirContextProcessor;
import org.springframework.ldap.control.SortControlDirContextProcessor;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.AggregateDirContextProcessor;

import javax.naming.directory.SearchControls;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CustomPagedLdapDao implements SpringLdapDao
{
    private Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public List<LdapUser> findUsersPaged(LdapTemplate template, AcmLdapSyncConfig syncConfig, Optional<String> ldapLastSyncDate)
    {
        return findUsers(template, syncConfig, syncConfig.getUserSyncAttributes(), ldapLastSyncDate);
    }

    public List<LdapUser> findUsers(LdapTemplate template, AcmLdapSyncConfig syncConfig,
                                    String[] attributes, Optional<String> ldapLastSyncDate)
    {
        AggregateDirContextProcessor sortedAndPaged = buildSortedAndPagesProcessor(syncConfig, syncConfig.getAllUsersSortingAttribute());

        AcmUserContextMapper userGroupsContextMapper = new AcmUserContextMapper(syncConfig);

        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        if (ArrayUtils.isNotEmpty(attributes))
        {
            String[] allAttributes = ArrayUtils.addAll(attributes, syncConfig.getUserIdAttributeName(), syncConfig.getMailAttributeName());
            searchControls.setReturningAttributes(allAttributes);
        }
        Optional<String> lastSyncTimestamp =
                ldapLastSyncDate.map(it -> convertToDirectorySpecificTimestamp(it, syncConfig.getDirectoryType()));

        String searchFilter = buildUsersSearchFilter(syncConfig, lastSyncTimestamp);
        String searchBase = syncConfig.getUserSearchBase();
        String[] bases = searchBase.split("\\|");
        List<LdapUser> ldapUsers = new ArrayList<>();
        for (String base : bases)
        {
            boolean searchUsers = true;
            boolean skipFirst = false;
            while (searchUsers)
            {
                // the context mapper will return null for disabled users
                List<LdapUser> found = template.search(base, searchFilter, searchControls, userGroupsContextMapper, sortedAndPaged);

                if (skipFirst && !found.isEmpty())
                {
                    ldapUsers.addAll(found.subList(1, found.size()));
                } else
                {
                    ldapUsers.addAll(found);
                }

                String usersFound = found.stream()
                        .map(LdapUser::getDistinguishedName)
                        .collect(Collectors.joining("\n"));

                log.debug("Users found: [{}]. DNs: [{}]", found.size(), usersFound);

                searchUsers = syncConfig.getSyncPageSize() == found.size();
                if (searchUsers)
                {
                    skipFirst = true;
                    LdapUser lastFound = found.get(found.size() - 1);
                    searchFilter = buildPagedUsersSearchFilter(syncConfig, lastFound.getSortableValue(), ldapLastSyncDate);

                    // A change to the search filter requires us to rebuild the search controls... even though
                    // the controls will have the same values as before.
                    sortedAndPaged = buildSortedAndPagesProcessor(syncConfig, syncConfig.getAllUsersSortingAttribute());
                    log.debug("Search filter now: [{}]", searchFilter);
                }
            }

            // filter out the DISABLED users
            ldapUsers = ldapUsers.stream()
                    .filter(u -> !("DISABLED".equals(u.getState())))
                    .collect(Collectors.toList());

            String userDomain = syncConfig.getUserDomain();
            if (userDomain != null && !userDomain.trim().isEmpty())
            {
                String userDomainSuffix = "@" + userDomain;
                ldapUsers.forEach(u -> u.setUserId(u.getUserId() + userDomainSuffix));
            }

            log.info("LDAP sync number of enabled users: [{}]", ldapUsers.size());
        }
        return ldapUsers;
    }

    @Override
    public List<LdapGroup> findGroupsPaged(LdapTemplate template, AcmLdapSyncConfig config, Optional<String> ldapLastSyncDate)
    {
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        searchControls.setReturningAttributes(new String[] { "cn", "member", "displayName" });

        AggregateDirContextProcessor sortedAndPaged = buildSortedAndPagesProcessor(config, config.getGroupsSortingAttribute());
        AcmGroupContextMapper acmGroupContextMapper = new AcmGroupContextMapper(config);

        boolean searchGroups = true;
        boolean skipFirst = false;

        Optional<String> lastSyncTimestamp = ldapLastSyncDate
                .map(it -> convertToDirectorySpecificTimestamp(it, config.getDirectoryType()));

        String searchFilter = buildGroupSearchFilter(config, lastSyncTimestamp);
        List<LdapGroup> ldapGroups = new ArrayList<>();

        while (searchGroups)
        {

            log.debug("search filter: [{}]", searchFilter);
            List<LdapGroup> found = template.search(config.getGroupSearchBase(), searchFilter, searchControls, acmGroupContextMapper,
                    sortedAndPaged);

            if (skipFirst && !found.isEmpty())
            {
                ldapGroups.addAll(found.subList(1, found.size()));
            } else
            {
                ldapGroups.addAll(found);
            }

            log.debug("Groups found: [{}]", found.size());

            searchGroups = config.getSyncPageSize() == found.size();
            if (searchGroups)
            {
                skipFirst = true;
                LdapGroup lastFound = found.get(found.size() - 1);
                searchFilter = buildPagedGroupsSearchFilter(config, lastFound.getSortableValue(), ldapLastSyncDate);

                // A change to the search filter requires us to rebuild the search controls... even though
                // the controls will have the same values as before.
                sortedAndPaged = buildSortedAndPagesProcessor(config, config.getGroupsSortingAttribute());
                log.trace("Search filter now: [{}]", searchFilter);
            }
        }

        log.info("LDAP sync number of groups: [{}]", ldapGroups.size());

        return ldapGroups;
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
}
