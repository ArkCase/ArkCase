package com.armedia.acm.services.functionalaccess.service;

import com.armedia.acm.files.ConfigurationFileChangedEvent;
import com.armedia.acm.files.propertymanager.PropertyFileManager;
import com.armedia.acm.services.search.model.SolrCore;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;
import com.armedia.acm.services.search.service.SearchResults;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.dao.group.AcmGroupDao;
import com.armedia.acm.services.users.model.AcmRoleToGroupMapping;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.event.AdHocGroupDeletedEvent;
import com.armedia.acm.services.users.model.event.LdapGroupDeletedEvent;
import com.armedia.acm.services.users.model.group.AcmGroup;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.mule.api.MuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.core.Authentication;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author riste.tutureski
 */
public class FunctionalAccessServiceImpl implements FunctionalAccessService, ApplicationListener<ApplicationEvent>
{
    private transient final Logger LOG = LoggerFactory.getLogger(getClass());

    private Properties applicationRolesProperties;
    private Properties applicationRolesToGroupsProperties;
    private AcmRoleToGroupMapping roleToGroupMapping;
    private PropertyFileManager propertyFileManager;
    private String rolesToGroupsPropertyFileLocation;
    private FunctionalAccessEventPublisher eventPublisher;
    private AcmGroupDao acmGroupDao;
    private UserDao userDao;
    private ExecuteSolrQuery executeSolrQuery;

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ConfigurationFileChangedEvent) {
            File eventFile = ((ConfigurationFileChangedEvent) event).getConfigFile();

            if ("applicationRoles.properties".equals(eventFile.getName())) {
                String filename = eventFile.getName();
                LOG.debug("[{}] has changed!", filename);

                try {
                    applicationRolesProperties = getPropertyFileManager().readFromFile(eventFile);
                } catch (IOException e) {
                    LOG.info("Could not read new properties; keeping the old properties.");
                }

            }

            if ("applicationRoleToUserGroup.properties".equals(eventFile.getName())) {
                String filename = eventFile.getName();
                LOG.info("[{}] has changed!", filename);

                try {
                    applicationRolesToGroupsProperties = getPropertyFileManager().readFromFile(eventFile);
                    roleToGroupMapping.reloadRoleToGroupMap(applicationRolesToGroupsProperties);
                } catch (IOException e) {
                    LOG.info("Could not read new properties; keeping the old properties.");
                }
            }

        } else if (event instanceof LdapGroupDeletedEvent || event instanceof AdHocGroupDeletedEvent) {
            AcmGroup group = (AcmGroup) event.getSource();
            String groupName = group.getName();
            Map<String, Set<String>> roleToGroupsMap = roleToGroupMapping.getRoleToGroupsMapIgnoreCaseSensitive();
            Map<String, List<String>> roleToGroupsMapToPropertyFile = roleToGroupsMap.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, entry ->
                            entry.getValue().stream().filter(name -> !name.equals(groupName)).collect(Collectors.toList())
                    ));

            saveApplicationRolesToGroups(roleToGroupsMapToPropertyFile, group.getModifier());
        }

    }

    @Override
    public List<String> getApplicationRoles()
    {
        List<String> applicationRoles = new ArrayList<>();

        try
        {
            Properties roleProperties = getApplicationRolesProperties();
            applicationRoles = Arrays.asList(roleProperties.getProperty("application.roles").split(","));
        }
        catch (Exception e)
        {
            LOG.error("Cannot read application roles from configuration.", e);
        }

        return applicationRoles;
    }

    private List<String> getGroupsBySolrQuery(Authentication auth, String sortDirection,
            Integer startRow,
            Integer maxRows, String query) throws MuleException
    {
        List<String> result = new ArrayList<>();
        String solrResponse = executeSolrQuery.getResultsByPredefinedQuery(auth, SolrCore.ADVANCED_SEARCH, query, startRow, maxRows,
                sortDirection);
        SearchResults searchResults = new SearchResults();
        JSONArray docs = searchResults.getDocuments(solrResponse);

        for (int i = 0; i < docs.length(); i++)
        {
            result.add((String) docs.getJSONObject(i).get("name"));
        }
        return result;
    }

    @Override
    public List<String> getGroupsByRole(Authentication auth, String roleName, Integer startRow, Integer maxRows,
            String sortDirection,
            Boolean authorized) throws MuleException
    {
        Set<String> groupsByRole = roleToGroupMapping.getRoleToGroupsMap().get(roleName.toUpperCase());
        List<String> retrieveGroupsByRole = groupsByRole == null ? new ArrayList<>() : new ArrayList<>(groupsByRole);

        if (authorized)
        {
            if (retrieveGroupsByRole.isEmpty())
            {
                return retrieveGroupsByRole;
            }
            String query = "object_type_s:GROUP AND -status_lcs:COMPLETE AND -status_lcs:DELETE AND -status_lcs:INACTIVE AND -status_lcs:CLOSED AND "
                    + retrieveGroupsByRole.stream().collect(Collectors.joining("\" OR name_lcs:\"", "(name_lcs:\"", "\")"));
            return getGroupsBySolrQuery(auth, sortDirection, startRow, maxRows, query);
        }
        else
        {
            String query = "object_type_s:GROUP AND -status_lcs:COMPLETE AND -status_lcs:DELETE AND -status_lcs:INACTIVE AND -status_lcs:CLOSED";
            if (!retrieveGroupsByRole.isEmpty())
            {
                query += " AND " + retrieveGroupsByRole.stream().collect(Collectors.joining("\" AND -name_lcs:\"", "-name_lcs:\"", "\""));
            }

            return getGroupsBySolrQuery(auth, sortDirection, startRow, maxRows, query);
        }
    }

    @Override
    public Map<String, List<String>> getApplicationRolesToGroups()
    {
        return roleToGroupMapping.getRoleToGroupsMap().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, list -> new ArrayList<>(list.getValue())));
    }

    @Override
    public boolean saveApplicationRolesToGroups(Map<String, List<String>> rolesToGroups, Authentication auth) {
        boolean success = false;
        try {
            getPropertyFileManager().storeMultiple(prepareRoleToGroupsForSaving(rolesToGroups), getRolesToGroupsPropertyFileLocation(),
                    true);
            success = true;
        } catch (Exception e) {
            LOG.error("Cannot save roles to groups mapping.", e);
            success = false;
        }

        if (success) {
            getEventPublisher().publishFunctionalAccessUpdateEvent(rolesToGroups, auth);
        }

        return success;
    }

    @Override
    public boolean saveApplicationRolesToGroups(Map<String, List<String>> rolesToGroups, String user) {
        boolean success = false;
        try {
            getPropertyFileManager().storeMultiple(prepareRoleToGroupsForSaving(rolesToGroups), getRolesToGroupsPropertyFileLocation(),
                    true);
            success = true;
        } catch (Exception e) {
            LOG.error("Cannot save roles to groups mapping.", e);
            success = false;
        }

        if (success) {
            getEventPublisher().publishFunctionalAccessUpdateEventOnRolesToGroupMap(rolesToGroups, user);
        }

        return success;
    }

    @Override
    public Set<AcmUser> getUsersByRolesAndGroups(List<String> roles, Map<String, List<String>> rolesToGroups, String group,
            String currentAssignee)
    {
        // Creating set to avoid duplicates. AcmUser has overrided "equals" and "hasCode" methods
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
                    retval.addAll(acmGroup.getUserMembers());
                }
            }
        }

        return retval;
    }

    private Map<String, String> prepareRoleToGroupsForSaving(Map<String, List<String>> rolesToGroups)
    {
        Map<String, String> retval = new HashMap<>();

        if (rolesToGroups != null && rolesToGroups.size() > 0)
        {
            rolesToGroups.forEach((key, value) -> retval.put(key, StringUtils.join(value, ",")));
        }

        return retval;
    }

    @Override
    public String getGroupsByPrivilege(List<String> roles, Map<String, List<String>> rolesToGroups, int startRow, int maxRows, String sort,
            Authentication auth) throws MuleException
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
            throws MuleException
    {
        LOG.info("Taking groups from Solr with IDs = {}", groupNames);

        String queryGroupNames = "";
        if (groupNames != null)
        {
            for (int i = 0; i < groupNames.size(); i++)
            {
                if (i == groupNames.size() - 1)
                {
                    queryGroupNames += "\"" + groupNames.get(i) + "\"";
                }
                else
                {
                    queryGroupNames += "\"" + groupNames.get(i) + "\"" + " OR ";
                }
            }
        }

        String query = "object_id_s:(" + queryGroupNames
                + ") AND object_type_s:GROUP AND -status_lcs:COMPLETE AND -status_lcs:DELETE AND -status_lcs:INACTIVE AND -status_lcs:CLOSED";

        String response = getExecuteSolrQuery().getResultsByPredefinedQuery(auth, SolrCore.ADVANCED_SEARCH, query, startRow, maxRows, sort);

        LOG.debug("Response: {}", response);

        return response;
    }

    public Properties getApplicationRolesProperties()
    {
        return applicationRolesProperties;
    }

    public void setApplicationRolesProperties(Properties applicationRolesProperties)
    {
        this.applicationRolesProperties = applicationRolesProperties;
    }

    public Properties getApplicationRolesToGroupsProperties()
    {
        return applicationRolesToGroupsProperties;
    }

    public void setApplicationRolesToGroupsProperties(Properties applicationRolesToGroupsProperties)
    {
        this.applicationRolesToGroupsProperties = applicationRolesToGroupsProperties;
    }

    public void setRoleToGroupMapping(AcmRoleToGroupMapping roleToGroupMapping)
    {
        this.roleToGroupMapping = roleToGroupMapping;
    }

    public PropertyFileManager getPropertyFileManager()
    {
        return propertyFileManager;
    }

    public void setPropertyFileManager(PropertyFileManager propertyFileManager)
    {
        this.propertyFileManager = propertyFileManager;
    }

    public String getRolesToGroupsPropertyFileLocation()
    {
        return rolesToGroupsPropertyFileLocation;
    }

    public void setRolesToGroupsPropertyFileLocation(String rolesToGroupsPropertyFileLocation)
    {
        this.rolesToGroupsPropertyFileLocation = rolesToGroupsPropertyFileLocation;
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
}
