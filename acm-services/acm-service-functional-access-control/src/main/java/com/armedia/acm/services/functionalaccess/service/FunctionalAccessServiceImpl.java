package com.armedia.acm.services.functionalaccess.service;

import com.armedia.acm.files.ConfigurationFileChangedEvent;
import com.armedia.acm.files.propertymanager.PropertyFileManager;
import com.armedia.acm.services.search.model.SolrCore;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;
import com.armedia.acm.services.users.dao.group.AcmGroupDao;
import com.armedia.acm.services.users.dao.ldap.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.group.AcmGroup;

import org.apache.commons.lang.StringUtils;
import org.mule.api.MuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

/**
 * @author riste.tutureski
 */
public class FunctionalAccessServiceImpl implements FunctionalAccessService, ApplicationListener<ConfigurationFileChangedEvent>
{
    private transient final Logger LOG = LoggerFactory.getLogger(getClass());

    private Properties applicationRolesProperties;
    private Properties applicationRolesToGroupsProperties;
    private PropertyFileManager propertyFileManager;
    private String rolesToGroupsPropertyFileLocation;
    private FunctionalAccessEventPublisher eventPublisher;
    private AcmGroupDao acmGroupDao;
    private UserDao userDao;
    private ExecuteSolrQuery executeSolrQuery;

    @Override
    public void onApplicationEvent(ConfigurationFileChangedEvent configurationFileChangedEvent)
    {
        File eventFile = configurationFileChangedEvent.getConfigFile();
        if ("applicationRoles.properties".equals(eventFile.getName()))
        {
            String filename = eventFile.getName();
            LOG.debug("[{}] has changed!", filename);

            try
            {
                Properties reloaded = getPropertyFileManager().readFromFile(eventFile);
                applicationRolesProperties = reloaded;
            } catch (IOException e)
            {
                LOG.info("Could not read new properties; keeping the old properties.");
            }

        }

        if ("applicationRoleToUserGroup.properties".equals(eventFile.getName()))
        {
            String filename = eventFile.getName();
            LOG.info("[{}] has changed!", filename);

            try
            {
                Properties reloaded = getPropertyFileManager().readFromFile(eventFile);
                applicationRolesToGroupsProperties = reloaded;
            } catch (IOException e)
            {
                LOG.info("Could not read new properties; keeping the old properties.");
            }

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
        } catch (Exception e)
        {
            LOG.error("Cannot read application roles from configuration.", e);
        }

        return applicationRoles;
    }

    @Override
    public Map<String, List<String>> getApplicationRolesToGroups()
    {
        return prepareRoleToGroupsForRetrieving(getApplicationRolesToGroupsProperties());
    }

    @Override
    public boolean saveApplicationRolesToGroups(Map<String, List<String>> rolesToGroups, Authentication auth)
    {
        boolean success = false;
        try
        {
            getPropertyFileManager().storeMultiple(prepareRoleToGroupsForSaving(rolesToGroups), getRolesToGroupsPropertyFileLocation(),
                    true);
            success = true;
        } catch (Exception e)
        {
            LOG.error("Cannot save roles to groups mapping.", e);
            success = false;
        }

        if (success)
        {
            getEventPublisher().publishFunctionalAccessUpdateEvent(rolesToGroups, auth);
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
                if (acmGroup != null && !acmGroup.getMembers().isEmpty())
                {
                    retval.addAll(acmGroup.getMembers());
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

    private Map<String, List<String>> prepareRoleToGroupsForRetrieving(Properties rolesToGroups)
    {
        Map<String, List<String>> retval = new HashMap<>();

        if (rolesToGroups != null && rolesToGroups.size() > 0)
        {
            for (String key : rolesToGroups.stringPropertyNames())
            {
                String value = rolesToGroups.getProperty(key);
                if (!("").equals(value) && value != null)
                {
                    retval.put(key, Arrays.asList(value.split(",")));
                }
            }
        }

        return retval;
    }

    @Override
    public String getGroupsByPrivilege(List<String> roles, Map<String, List<String>> rolesToGroups, int startRow, int maxRows, String sort,
            Authentication auth) throws MuleException
    {
        Set<String> groups = getAllGroupsForAllRoles(roles, rolesToGroups);
        String retval = getGroupsFromSolr(new ArrayList<>(groups), startRow, maxRows, sort, auth);

        return retval;
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
                } else
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
