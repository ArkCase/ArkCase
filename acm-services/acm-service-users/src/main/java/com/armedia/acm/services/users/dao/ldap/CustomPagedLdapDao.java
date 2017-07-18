package com.armedia.acm.services.users.dao.ldap;

import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.LdapGroup;
import com.armedia.acm.services.users.model.ldap.AcmGroupContextMapper;
import com.armedia.acm.services.users.model.ldap.AcmLdapConstants;
import com.armedia.acm.services.users.model.ldap.AcmLdapSyncConfig;
import com.armedia.acm.services.users.model.ldap.AcmUserGroupsContextMapper;
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
import java.util.stream.Collectors;

public class CustomPagedLdapDao implements SpringLdapDao
{
    private Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public List<AcmUser> findUsersPaged(LdapTemplate template, AcmLdapSyncConfig syncConfig)
    {
        return findUsers(template, syncConfig, syncConfig.getUserSyncAttributes());
    }

    @Override
    public List<AcmUser> findChangedUsersPaged(LdapTemplate template, AcmLdapSyncConfig syncConfig,
                                               String[] attributes, String ldapLastSyncDate)
    {
        AggregateDirContextProcessor sortedAndPaged = buildSortedAndPagesProcessor(syncConfig, syncConfig.getAllUsersSortingAttribute());

        AcmUserGroupsContextMapper userGroupsContextMapper = new AcmUserGroupsContextMapper(syncConfig);

        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        if (ArrayUtils.isNotEmpty(attributes))
        {
            String[] allAttributes = ArrayUtils.addAll(attributes, syncConfig.getUserIdAttributeName(), syncConfig.getMailAttributeName());
            searchControls.setReturningAttributes(allAttributes);
        }

        if (StringUtils.isNotBlank(ldapLastSyncDate))
        {
            ldapLastSyncDate = syncConfig.getDirectoryType().equals(AcmLdapConstants.LDAP_OPENLDAP) ?
                    convertToOpenLdapTimestamp(ldapLastSyncDate) : convertToActiveDirectoryTimestamp(ldapLastSyncDate);
        }

        String searchFilter = buildUsersSearchFilter(syncConfig, ldapLastSyncDate);
        String searchBase = syncConfig.getUserSearchBase();
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

                if (skipFirst && !found.isEmpty())
                {
                    acmUsers.addAll(found.subList(1, found.size()));
                } else
                {
                    acmUsers.addAll(found);
                }

                String usersFound = found.stream()
                        .map(AcmUser::getDistinguishedName)
                        .collect(Collectors.joining("\n"));

                log.debug("Users found: {}. DNs: {}", found.size(), usersFound);

                searchUsers = syncConfig.getSyncPageSize() == found.size();
                if (searchUsers)
                {
                    skipFirst = true;
                    AcmUser lastFound = found.get(found.size() - 1);

                    searchFilter = buildPagedUsersSearchFilter(syncConfig, lastFound.getSortableValue(), ldapLastSyncDate);

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

    @Override
    public List<LdapGroup> findChangedGroupsPaged(LdapTemplate template, AcmLdapSyncConfig config, String lastSyncDate)
    {
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        searchControls.setReturningAttributes(new String[] { "cn", "memberOf" });

        AggregateDirContextProcessor sortedAndPaged = buildSortedAndPagesProcessor(config, config.getGroupsSortingAttribute());
        AcmGroupContextMapper acmGroupContextMapper = new AcmGroupContextMapper(config);

        boolean searchGroups = true;
        boolean skipFirst = false;

        if (StringUtils.isNotBlank(lastSyncDate))
        {
            lastSyncDate = config.getDirectoryType().equals(AcmLdapConstants.LDAP_OPENLDAP) ?
                    convertToOpenLdapTimestamp(lastSyncDate) : convertToActiveDirectoryTimestamp(lastSyncDate);
        }

        String searchFilter = buildGroupSearchFilter(config, lastSyncDate);
        List<LdapGroup> acmGroups = new ArrayList<>();

        while (searchGroups)
        {

            log.debug("search filter: {}", searchFilter);
            List<LdapGroup> found = template.search(config.getGroupSearchBase(), searchFilter, searchControls, acmGroupContextMapper,
                    sortedAndPaged);

            if (skipFirst && !found.isEmpty())
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
                searchFilter = buildPagedGroupsSearchFilter(config, lastFound.getSortableValue(), lastSyncDate);

                // A change to the search filter requires us to rebuild the search controls... even though
                // the controls will have the same values as before.
                sortedAndPaged = buildSortedAndPagesProcessor(config, config.getGroupsSortingAttribute());
                log.trace("Search filter now: {}", searchFilter);
            }
        }

        log.info("LDAP sync number of groups: {}", acmGroups.size());

        return acmGroups;
    }

    public List<AcmUser> findUsers(LdapTemplate template, final AcmLdapSyncConfig syncConfig, String[] attributes)
    {
        return findChangedUsersPaged(template, syncConfig, attributes, null);
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
        return findChangedGroupsPaged(template, config, null);
    }

}
