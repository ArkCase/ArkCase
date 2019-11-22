package com.armedia.acm.services.functionalaccess.service;

/*-
 * #%L
 * ACM Service: Functional Access Control
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

import com.armedia.acm.configuration.service.CollectionPropertiesConfigurationService;
import com.armedia.acm.configuration.service.ConfigurationPropertyService;
import com.armedia.acm.configuration.util.MergeFlags;
import com.armedia.acm.services.search.exception.SolrException;
import com.armedia.acm.services.search.model.solr.SolrCore;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;
import com.armedia.acm.services.search.service.SearchResults;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.dao.group.AcmGroupDao;
import com.armedia.acm.services.users.model.AcmRoleToGroupMapping;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.ApplicationRolesConfig;
import com.armedia.acm.services.users.model.ApplicationRolesToGroupsConfig;
import com.armedia.acm.services.users.model.event.AdHocGroupDeletedEvent;
import com.armedia.acm.services.users.model.event.LdapGroupDeletedEvent;
import com.armedia.acm.services.users.model.group.AcmGroup;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.core.Authentication;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author riste.tutureski
 */
public class FunctionalAccessServiceImpl implements FunctionalAccessService, ApplicationListener<ApplicationEvent>
{
    private transient final Logger LOG = LogManager.getLogger(getClass());

    private AcmRoleToGroupMapping roleToGroupMapping;
    private FunctionalAccessEventPublisher eventPublisher;
    private AcmGroupDao acmGroupDao;
    private UserDao userDao;
    private ExecuteSolrQuery executeSolrQuery;
    private ApplicationRolesConfig rolesConfig;
    private ApplicationRolesToGroupsConfig rolesToGroupsConfig;
    private ConfigurationPropertyService configurationPropertyService;
    private CollectionPropertiesConfigurationService collectionPropertiesConfigurationService;

    @Override
    public void onApplicationEvent(ApplicationEvent event)
    {
        if (event instanceof LdapGroupDeletedEvent || event instanceof AdHocGroupDeletedEvent)
        {
            AcmGroup group = (AcmGroup) event.getSource();
            String groupName = group.getName();

            Map<String, Set<String>> roleToGroupsMap = roleToGroupMapping.getRoleToGroupsMapIgnoreCaseSensitive();
            Map<String, List<String>> roleToGroupsNoDuplicates = roleToGroupsMap.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey,
                            entry -> entry.getValue().stream().filter(name -> !name.equals(groupName)).collect(Collectors.toList())));
            saveApplicationRolesToGroups(roleToGroupsNoDuplicates, group.getModifier());
        }

    }

    @Override
    public List<String> getApplicationRoles()
    {
        return rolesConfig.getApplicationRoles();
    }

    @Override
    public List<String> getApplicationRolesPaged(String sortDirection, Integer startRow, Integer maxRows)
    {
        List<String> result = getApplicationRoles();

        if (sortDirection.contains("DESC"))
        {
            result.sort(Collections.reverseOrder());
        }
        else
        {
            Collections.sort(result);
        }

        if (startRow > result.size())
        {
            return result;
        }
        maxRows = maxRows > result.size() ? result.size() : maxRows;

        return result.stream().skip(startRow).limit(maxRows).collect(Collectors.toList());
    }

    @Override
    public List<String> getApplicationRolesByName(String sortDirection, Integer startRow, Integer maxRows, String filterName)
    {
        List<String> result = new ArrayList<>(getApplicationRoles());

        if (sortDirection.contains("DESC"))
        {
            result.sort(Collections.reverseOrder());
        }
        else
        {
            Collections.sort(result);
        }

        if (startRow > result.size())
        {
            return result;
        }
        maxRows = maxRows > result.size() ? result.size() : maxRows;

        if (!filterName.isEmpty())
        {
            result.removeIf(role -> !(role.toLowerCase().contains(filterName.toLowerCase())));
        }

        return result.stream().skip(startRow).limit(maxRows).collect(Collectors.toList());
    }

    private List<String> getGroupsBySolrQuery(Authentication auth, String sortDirection,
            Integer startRow,
            Integer maxRows, String query) throws SolrException
    {
        List<String> result = new ArrayList<>();
        String rowQueryParameters = "fq=hidden_b:false";
        String solrResponse = executeSolrQuery.getResultsByPredefinedQuery(auth, SolrCore.ADVANCED_SEARCH, query, startRow, maxRows,
                sortDirection, rowQueryParameters);
        SearchResults searchResults = new SearchResults();
        JSONArray docs = searchResults.getDocuments(solrResponse);

        for (int i = 0; i < docs.length(); i++)
        {
            result.add((String) docs.getJSONObject(i).get("name"));
        }
        return result;
    }

    @Override
    public List<String> getGroupsByRolePaged(Authentication auth, String roleName, Integer startRow, Integer maxRows,
            String sortDirection,
            Boolean authorized) throws SolrException
    {
        return getGroupsByRole(auth, roleName, startRow, maxRows, sortDirection, authorized, "");
    }

    @Override
    public List<String> getGroupsByRoleByName(Authentication auth, String roleName, Integer startRow, Integer maxRows,
            String sortDirection,
            Boolean authorized, String filterQuery) throws SolrException
    {
        return getGroupsByRole(auth, roleName, startRow, maxRows, sortDirection, authorized, filterQuery);
    }

    @Override
    public List<String> getGroupsByRole(Authentication auth, String roleName, Integer startRow, Integer maxRows,
            String sortDirection,
            Boolean authorized, String filterQuery) throws SolrException
    {
        Set<String> groupsByRole = roleToGroupMapping.getRoleToGroupsMap().get(roleName.toUpperCase());
        List<String> retrieveGroupsByRole = groupsByRole == null ? new ArrayList<>() : new ArrayList<>(groupsByRole);
        String query = "";

        if (authorized)
        {
            if (retrieveGroupsByRole.isEmpty())
            {
                return retrieveGroupsByRole;
            }
            query = "object_type_s:GROUP AND -status_lcs:COMPLETE AND -status_lcs:DELETE AND -status_lcs:INACTIVE AND -status_lcs:CLOSED AND "
                    + retrieveGroupsByRole.stream().collect(Collectors.joining("\" OR name_lcs:\"", "(name_lcs:\"", "\")"));
        }
        else
        {
            query = "object_type_s:GROUP AND -status_lcs:COMPLETE AND -status_lcs:DELETE AND -status_lcs:INACTIVE AND -status_lcs:CLOSED";
            if (!retrieveGroupsByRole.isEmpty())
            {
                query += " AND " + retrieveGroupsByRole.stream().collect(Collectors.joining("\" AND -name_lcs:\"", "-name_lcs:\"", "\""));
            }
        }

        if (!filterQuery.isEmpty())
        {
            query += " AND name_partial:" + filterQuery;
        }
        return getGroupsBySolrQuery(auth, sortDirection, startRow, maxRows, query);
    }

    @Override
    public Map<String, List<String>> getApplicationRolesToGroups()
    {
        return roleToGroupMapping.getRoleToGroupsMap().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, list -> new ArrayList<>(list.getValue())));
    }

    @Override
    public boolean saveApplicationRolesToGroups(Map<String, List<String>> rolesToGroups, Authentication auth)
    {
        return saveApplicationRolesToGroups(rolesToGroups, auth.getName());
    }

    @Override
    public boolean saveApplicationRolesToGroups(Map<String, List<String>> rolesToGroups, String user)
    {
        ApplicationRolesToGroupsConfig rolesToGroupsConfig = new ApplicationRolesToGroupsConfig();
        rolesToGroupsConfig.setRolesToGroups(rolesToGroups);
        configurationPropertyService.updateProperties(rolesToGroupsConfig);
        getEventPublisher().publishFunctionalAccessUpdateEventOnRolesToGroupMap(rolesToGroups, user);
        return true;
    }

    @Override
    public boolean saveGroupsToApplicationRole(List<Object> groups, String roleName, Authentication auth)
    {
        Map<String, Object> rolesToGroupsConfig = collectionPropertiesConfigurationService.updateMapProperty(
                ApplicationRolesToGroupsConfig.ROLES_TO_GROUPS_PROP_KEY, roleName,
                groups, MergeFlags.MERGE);

        configurationPropertyService.updateProperties(rolesToGroupsConfig);

        getEventPublisher().publishFunctionalAccessUpdateEvent(groups, auth);
        return true;
    }

    @Override
    public boolean removeGroupsToApplicationRole(List<Object> groups, String roleName, Authentication auth)
    {

        Map<String, Object> rolesToGroupsConfig = collectionPropertiesConfigurationService.updateMapProperty(
                ApplicationRolesToGroupsConfig.ROLES_TO_GROUPS_PROP_KEY, roleName,
                groups, MergeFlags.REMOVE);

        configurationPropertyService.updateProperties(rolesToGroupsConfig);

        getEventPublisher().publishFunctionalAccessUpdateEvent(groups, auth);
        return true;
    }

    @Override
    public Set<AcmUser> getUsersByRolesAndGroups(List<String> roles, Map<String, List<String>> rolesToGroups, String group,
            String currentAssignee)
    {
        // Creating set to avoid duplicates. AcmUser has overridden "equals" and "hasCode" methods
        Set<AcmUser> users = new HashSet<>();
        if (roles != null && rolesToGroups != null)
        {
            for (String role : roles)
            {
                List<String> groupNames = rolesToGroups.get(role);

                if (groupNames != null)
                {
                    // Passing null to the "getUsers" will retrieve users for all groups in "groupNames"
                    users.addAll(getUsers(group, groupNames));
                }
            }
        }

        // Get current user and add to the list
        if (currentAssignee != null)
        {
            AcmUser currentUser = getUserDao().findByUserId(currentAssignee);

            if (currentUser != null)
            {
                users.add(currentUser);
            }
        }

        return users;
    }

    private Set<AcmUser> getUsers(String group, List<String> groupNames)
    {
        Set<AcmUser> retval = new HashSet<>();

        for (String groupName : groupNames)
        {
            if (groupName.equals(group) || group == null)
            {
                AcmGroup acmGroup = getAcmGroupDao().findByName(groupName);
                if (acmGroup != null)
                {
                    retval.addAll(acmGroup.getUserMembers(true));
                }
            }
        }

        return retval;
    }

    @Override
    public String getGroupsByPrivilege(List<String> roles, Map<String, List<String>> rolesToGroups, int startRow, int maxRows, String sort,
            Authentication auth) throws SolrException
    {
        Set<String> groups = getAllGroupsForAllRoles(roles, rolesToGroups);

        return getGroupsFromSolr(new ArrayList<>(groups), startRow, maxRows, sort, auth);
    }

    private Set<String> getAllGroupsForAllRoles(List<String> roles, Map<String, List<String>> rolesToGroups)
    {
        // Creating set to avoid duplicates
        Set<String> groups = new HashSet<>();
        if (roles != null && rolesToGroups != null)
        {
            for (String role : roles)
            {
                List<String> groupNames = rolesToGroups.get(role);

                if (groupNames != null)
                {
                    // We need first to get unique group names (because groups can be repeated in different roles)
                    groups.addAll(new HashSet<>(groupNames));
                }
            }
        }

        return groups;
    }

    private String getGroupsFromSolr(List<String> groupNames, int startRow, int maxRows, String sort, Authentication auth)
            throws SolrException
    {
        LOG.info("Taking groups from Solr with IDs = {}", groupNames);

        StringBuilder queryGroupNames = new StringBuilder();
        if (groupNames != null)
        {
            for (int i = 0; i < groupNames.size(); i++)
            {
                if (i == groupNames.size() - 1)
                {
                    queryGroupNames.append("\"").append(groupNames.get(i)).append("\"");
                }
                else
                {
                    queryGroupNames.append("\"").append(groupNames.get(i)).append("\"").append(" OR ");
                }
            }
        }

        if (queryGroupNames.toString().equals(""))
        {
            queryGroupNames = new StringBuilder("no group names");
        }

        String query = "object_id_s:(" + queryGroupNames
                + ") AND object_type_s:GROUP AND -status_lcs:COMPLETE AND -status_lcs:DELETE AND -status_lcs:INACTIVE AND -status_lcs:CLOSED";

        String response = getExecuteSolrQuery().getResultsByPredefinedQuery(auth, SolrCore.ADVANCED_SEARCH, query, startRow, maxRows, sort);

        LOG.debug("Response: {}", response);

        return response;
    }

    public ApplicationRolesToGroupsConfig getRolesToGroupsConfig()
    {
        return rolesToGroupsConfig;
    }

    public void setRolesToGroupsConfig(ApplicationRolesToGroupsConfig rolesToGroupsConfig)
    {
        this.rolesToGroupsConfig = rolesToGroupsConfig;
    }

    public void setRoleToGroupMapping(AcmRoleToGroupMapping roleToGroupMapping)
    {
        this.roleToGroupMapping = roleToGroupMapping;
    }

    public FunctionalAccessEventPublisher getEventPublisher()
    {
        return eventPublisher;
    }

    public void setEventPublisher(FunctionalAccessEventPublisher eventPublisher)
    {
        this.eventPublisher = eventPublisher;
    }

    public AcmGroupDao getAcmGroupDao()
    {
        return acmGroupDao;
    }

    public void setAcmGroupDao(AcmGroupDao acmGroupDao)
    {
        this.acmGroupDao = acmGroupDao;
    }

    public UserDao getUserDao()
    {
        return userDao;
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    public ExecuteSolrQuery getExecuteSolrQuery()
    {
        return executeSolrQuery;
    }

    public void setExecuteSolrQuery(ExecuteSolrQuery executeSolrQuery)
    {
        this.executeSolrQuery = executeSolrQuery;
    }

    public ApplicationRolesConfig getRolesConfig()
    {
        return rolesConfig;
    }

    public void setRolesConfig(ApplicationRolesConfig rolesConfig)
    {
        this.rolesConfig = rolesConfig;
    }

    public ConfigurationPropertyService getConfigurationPropertyService()
    {
        return configurationPropertyService;
    }

    public void setConfigurationPropertyService(ConfigurationPropertyService configurationPropertyService)
    {
        this.configurationPropertyService = configurationPropertyService;
    }

    public void setCollectionPropertiesConfigurationService(
            CollectionPropertiesConfigurationService collectionPropertiesConfigurationService)
    {
        this.collectionPropertiesConfigurationService = collectionPropertiesConfigurationService;
    }
}
