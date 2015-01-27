package com.armedia.acm.services.users.service.ldap;

import com.armedia.acm.services.users.dao.ldap.SpringLdapDao;
import com.armedia.acm.services.users.model.AcmLdapEntity;
import com.armedia.acm.services.users.model.AcmRole;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.LdapGroup;
import com.armedia.acm.services.users.model.group.AcmGroup;
import com.armedia.acm.services.users.model.ldap.AcmLdapSyncConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.core.LdapTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Sync the user-related database tables with an LDAP directory.  To support multiple LDAP configurations, create
 * multiple Spring beans, each with its own ldapSyncConfig.
 * <p/>
 * Both application roles and LDAP groups are synced.
 * <ul>
 *     <li>Application roles drive role-based access control, and
 * every deployment has the same application role names regardless of the LDAP group names.  The ldapSyncConfig
 * includes a mapping from the logical role name to the physical LDAP group name.  For each entry in this mapping, the
 * members of the indicated LDAP group are linked to the indicated logical application role.</li>
 *     <li>LDAP groups are also synced, to be available for data access control; this allows users to grant or deny
 *     access to specific groups.  The groups could be more granular than application roles; for example, all case
 *     agents share the same application roles, but different LDAP groups could represent different functional or
 *     geographic areas.  So granting access at the LDAP group level could be more appropriate - i.e., would restrict
 *     access to only those case agents in the appropriate functional or geographic area.
 *     </li>
 * </ul>
 */
public class LdapSyncService
{
    private SpringLdapDao ldapDao;
    private LdapSyncDatabaseHelper ldapSyncDatabaseHelper;
    private AcmLdapSyncConfig ldapSyncConfig;
    private String directoryName;
    private boolean syncEnabled = true;


    private Logger log = LoggerFactory.getLogger(getClass());

    // this method is used by scheduled jobs in Spring beans loaded dynamically from the ACM configuration
    // folder ($HOME/.acm).
    public void ldapSync()
    {
        if ( !isSyncEnabled() )
        {
            log.debug("Sync is disabled - stopping now.");
            return;
        }

        if ( log.isInfoEnabled() )
        {
            log.info("Starting sync of directory: " + getDirectoryName() + "; ldap URL: " +
                    getLdapSyncConfig().getLdapUrl());
        }

        // all the ldap work first, then all the database work; because the ldap queries could be very timeconsuming.
        // If we opened up a database transaction, then spend a minute or so querying LDAP, the database transaction
        // could time out.  So we run all the LDAP queries first, then do all the database operations all at once.
        Set<String> allRoles = new HashSet<>();
        List<AcmUser> users = new ArrayList<>();
        Map<String, List<AcmUser>> usersByApplicationRole = new HashMap<>();
        Map<String, List<AcmUser>> usersByLdapGroup = new HashMap<>();
        Map<String, String> childParentPair = new HashMap<String, String>();

        queryLdapUsers(getLdapSyncConfig(),
                getDirectoryName(),
                allRoles,
                users,
                usersByApplicationRole,
                usersByLdapGroup, 
                childParentPair);

        // ldap work is done.  now for the database work.
        getLdapSyncDatabaseHelper().updateDatabase(getDirectoryName(),
                allRoles,
                users,
                usersByApplicationRole,
                usersByLdapGroup,
                childParentPair);
    }



    protected void queryLdapUsers(
            AcmLdapSyncConfig config,
            String directoryName,
            Set<String> allRoles,
            List<AcmUser> users,
            Map<String, List<AcmUser>> usersByApplicationRole,
            Map<String, List<AcmUser>> usersByLdapGroup,
            Map<String, String> childParentPair)
    {
        boolean debug = log.isDebugEnabled();
        if ( debug )
        {
            log.debug("querying users from directory '" + directoryName + "'");
        }

        LdapTemplate template = getLdapDao().buildLdapTemplate(config);

        Map<String, String> roleToGroup = config.getRoleToGroupMap();
        allRoles.addAll(roleToGroup.keySet());

        Map<String, List<String>> groupToRoleMap = reverseRoleToGroupMap(roleToGroup);

        // Lookup all groups. If the group is linked to an application role, add the group members to the application
        // role map, as well as to the group map.
        List<LdapGroup> groups = getLdapDao().findGroups(template, config);

        for ( LdapGroup group : groups )
        {
            if ( debug )
            {
                log.debug("Found group '" + group.getGroupName() + "', with " +
                        group.getMemberDistinguishedNames().length + " members");
            }

            List<AcmUser> usersForThisGroup = findAllUsersForGroup(config, template, group, childParentPair);

            String ucGroupName = group.getGroupName().toUpperCase();

            users.addAll(usersForThisGroup);

            addUsersToMap(usersByLdapGroup, ucGroupName, usersForThisGroup);

            addUsersToApplicationRole(usersByApplicationRole, groupToRoleMap, usersForThisGroup, ucGroupName);
        }
    }

    private List<AcmUser> findAllUsersForGroup(AcmLdapSyncConfig config, LdapTemplate template, LdapGroup group, Map<String, String> childParentPair)
    {
        List<AcmRole> nestedGroups = new ArrayList<>();

        List<AcmUser> allUsersForGroup = new ArrayList<>();

        List<AcmLdapEntity> foundEntities = getLdapDao().findGroupMembers(template, config, group);

        splitEntitiesIntoNestedGroupsAndUsers(foundEntities, nestedGroups, allUsersForGroup);
        
        populateChildParentPair(group, nestedGroups, childParentPair);

        findUsersForNestedGroups(config, template, nestedGroups, allUsersForGroup);
        
        return allUsersForGroup;
    }

    private void findUsersForNestedGroups(
            AcmLdapSyncConfig config,
            LdapTemplate template,
            List<AcmRole> nestedGroups,
            List<AcmUser> usersForMainGroup)
    {
        List<AcmLdapEntity> foundEntities;
        while ( !nestedGroups.isEmpty() )
        {
            List<AcmRole> current = new ArrayList<>(nestedGroups.size());
            current.addAll(nestedGroups);
            nestedGroups.clear();

            for ( AcmRole currentNestedGroup : current )
            {
                LdapGroup ldapGroup = getLdapDao().findGroup(template, config, currentNestedGroup.getDistinguishedName());
                foundEntities = getLdapDao().findGroupMembers(template, config, ldapGroup);

                splitEntitiesIntoNestedGroupsAndUsers(foundEntities, nestedGroups, usersForMainGroup);
            }
        }
    }

    private void splitEntitiesIntoNestedGroupsAndUsers(
            List<AcmLdapEntity> foundEntities,
            List<AcmRole> nestedGroups,
            List<AcmUser> usersForThisGroup)
    {
        for ( AcmLdapEntity found : foundEntities )
        {
            if ( found.isGroup() )
            {
                nestedGroups.add((AcmRole) found);
            }
            else
            {
                usersForThisGroup.add((AcmUser) found);
            }
        }
    }

    private void addUsersToApplicationRole(Map<String, List<AcmUser>> usersByApplicationRole,
                                           Map<String, List<String>> groupToRoleMap,
                                           List<AcmUser> foundUsers,
                                           String ucGroupName)
    {
        if ( groupToRoleMap.containsKey(ucGroupName) )
        {
            if ( log.isDebugEnabled() )
            {
                log.debug("Group '" + ucGroupName + "' has roles: " + groupToRoleMap.get(ucGroupName));
            }
            for ( String applicationRole : groupToRoleMap.get(ucGroupName))
            {
                addUsersToMap(usersByApplicationRole, applicationRole, foundUsers);
            }
        }
    }

    private Map<String, List<String>> reverseRoleToGroupMap(Map<String, String> roleToGroup)
    {
        Map<String, List<String>> groupToRoleMap = new HashMap<>();
        List<String> roles;
        for ( Map.Entry<String, String> roleMapEntry : roleToGroup.entrySet() )
        {
            String role = roleMapEntry.getKey().toUpperCase();
            String group = roleMapEntry.getValue().toUpperCase();

            if ( groupToRoleMap.containsKey(group) )
            {
                roles = groupToRoleMap.get(group);
            }
            else
            {
                roles = new ArrayList<>();
            }
            roles.add(role);
            groupToRoleMap.put(group, roles);
        } return groupToRoleMap;
    }

    private void addUsersToMap(Map<String, List<AcmUser>> userMap, String mapKey, List<AcmUser> users)
    {
        if ( log.isDebugEnabled() )
        {
            log.debug("Adding " + users.size() + " users to map by key " + mapKey);
        }
        // ensure to initialize the map entry with an empty list, then copy from the incoming list into the new list.
        if ( ! userMap.containsKey(mapKey) )
        {
            userMap.put(mapKey, new ArrayList<AcmUser>());
        }
        userMap.get(mapKey).addAll(users);
    }
    
    private void populateChildParentPair(LdapGroup parent, List<AcmRole> children, Map<String, String> childParentPair)
    {
    	if (children != null && children.size() > 0)
    	{
    		for (AcmRole child : children)
    		{
    			if (childParentPair == null)
    			{
    				childParentPair = new HashMap<String, String>();
    			}
    			
    			if (child.getRoleName() != null && parent.getGroupName() != null)
    			{
    				childParentPair.put(child.getRoleName().toUpperCase(), parent.getGroupName().toUpperCase());
    			}
    		}
    	}
    }

    public SpringLdapDao getLdapDao()
    {
        return ldapDao;
    }

    public void setLdapDao(SpringLdapDao ldapDao)
    {
        this.ldapDao = ldapDao;
    }

    public LdapSyncDatabaseHelper getLdapSyncDatabaseHelper()
    {
        return ldapSyncDatabaseHelper;
    }

    public void setLdapSyncDatabaseHelper(LdapSyncDatabaseHelper ldapSyncDatabaseHelper)
    {
        this.ldapSyncDatabaseHelper = ldapSyncDatabaseHelper;
    }

    public AcmLdapSyncConfig getLdapSyncConfig()
    {
        return ldapSyncConfig;
    }

    public void setLdapSyncConfig(AcmLdapSyncConfig ldapSyncConfig)
    {
        this.ldapSyncConfig = ldapSyncConfig;
    }

    public String getDirectoryName()
    {
        return directoryName;
    }

    public void setDirectoryName(String directoryName)
    {
        this.directoryName = directoryName;
    }

    public boolean isSyncEnabled()
    {
        return syncEnabled;
    }

    public void setSyncEnabled(boolean syncEnabled)
    {
        this.syncEnabled = syncEnabled;
    }
}
