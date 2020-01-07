package com.armedia.acm.services.users.dao.ldap;

/*-
 * #%L
 * ACM Service: Users
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import com.armedia.acm.services.users.model.ldap.AcmGroupContextMapper;
import com.armedia.acm.services.users.model.ldap.AcmLdapSyncConfig;
import com.armedia.acm.services.users.model.ldap.AcmUserContextMapper;
import com.armedia.acm.services.users.model.ldap.LdapGroup;
import com.armedia.acm.services.users.model.ldap.LdapUser;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.ldap.control.PagedResultsCookie;
import org.springframework.ldap.control.PagedResultsDirContextProcessor;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.LdapTemplate;

import javax.naming.directory.SearchControls;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SpringLdapPagedDao implements SpringLdapDao
{
    private Logger log = LogManager.getLogger(getClass());

    private PagedResultsDirContextProcessorBuilder builder = new PagedResultsDirContextProcessorBuilder();

    private <T> List<T> fetchLdapPaged(LdapTemplate template, String searchBase, String searchFilter,
            SearchControls searchControls, int pageSize, ContextMapper contextMapper)
    {
        List<T> result = new ArrayList<>();
        // for the first paged-search request we pass null cookie
        PagedResultsCookie resultsCookie = null;
        while (true)
        {
            PagedResultsDirContextProcessor pagedResultsDirContextProcessor = builder.build(pageSize, resultsCookie);
            log.debug("Start fetching [{}] items from LDAP", pageSize);
            List<T> items = template.search(searchBase, searchFilter,
                    searchControls, contextMapper, pagedResultsDirContextProcessor);
            log.debug("Items fetched: [{}]", items.size());
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
    public List<LdapUser> findUsersPaged(LdapTemplate template, final AcmLdapSyncConfig syncConfig, Optional<String> ldapLastSyncDate)
    {
        return findUsers(template, syncConfig, syncConfig.getUserSyncAttributes(), ldapLastSyncDate);
    }

    @Override
    public List<LdapGroup> findGroupsPaged(LdapTemplate template, final AcmLdapSyncConfig syncConfig, Optional<String> ldapLastSyncDate)
    {
        return findGroups(template, syncConfig, ldapLastSyncDate);
    }

    public List<LdapUser> findUsers(LdapTemplate template, AcmLdapSyncConfig syncConfig,
            String[] attributes, Optional<String> ldapLastSyncDate)
    {
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        if (ArrayUtils.isNotEmpty(attributes))
        {
            String[] allAttributes = ArrayUtils.addAll(attributes,
                    syncConfig.getUserIdAttributeName(), syncConfig.getMailAttributeName());
            searchControls.setReturningAttributes(allAttributes);
        }

        AcmUserContextMapper userGroupsContextMapper = new AcmUserContextMapper(syncConfig);
        Optional<String> lastSyncTimestamp = ldapLastSyncDate
                .map(it -> convertToDirectorySpecificTimestamp(it, syncConfig.getDirectoryType()));

        String searchBase = syncConfig.getUserSearchBase();

        // Spring LDAP authentication doesn't support multiple search bases divided by "|"
        // this is custom implementation if we want to sync users from different search bases
        // Note: - add new property in AcmLdapSyncConfig if you want to define multiple search bases and use that
        // instead
        // - users synced from different search base other than the one defined in property "userSearchBase" won't be
        // able to log in
        // Multiple search bases is supported in some of the upper spring security versions
        String[] bases = searchBase.split("\\|");
        List<LdapUser> ldapUsers = new ArrayList<>();
        for (String base : bases)
        {
            List<LdapUser> users = fetchLdapPaged(template, base, buildUsersSearchFilter(syncConfig, lastSyncTimestamp),
                    searchControls, syncConfig.getSyncPageSize(), userGroupsContextMapper);
            log.info("Fetched total [{}] users for search base [{}]", users.size(), base);
            ldapUsers.addAll(users);
        }

        // filter out the DISABLED users
        ldapUsers = ldapUsers.stream()
                .filter(u -> !("DISABLED".equals(u.getState())))
                .collect(Collectors.toList());

        log.info("LDAP sync number of enabled users: [{}]", ldapUsers.size());
        return ldapUsers;
    }

    public List<LdapGroup> findGroups(LdapTemplate template, AcmLdapSyncConfig syncConfig, Optional<String> ldapLastSyncDate)
    {
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        searchControls.setReturningAttributes(new String[] { "cn", "member" });

        AcmGroupContextMapper acmGroupContextMapper = new AcmGroupContextMapper(syncConfig, template);

        Optional<String> lastSyncTimestamp = ldapLastSyncDate
                .map(it -> convertToDirectorySpecificTimestamp(it, syncConfig.getDirectoryType()));

        String searchBase = syncConfig.getGroupSearchBase();

        List<LdapGroup> ldapGroups = fetchLdapPaged(template, searchBase, buildGroupSearchFilter(syncConfig, lastSyncTimestamp),
                searchControls, syncConfig.getSyncPageSize(), acmGroupContextMapper);

        log.info("LDAP sync number of groups: [{}]", ldapGroups.size());
        return ldapGroups;
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
