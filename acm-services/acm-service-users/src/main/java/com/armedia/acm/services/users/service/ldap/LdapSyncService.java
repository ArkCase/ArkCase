package com.armedia.acm.services.users.service.ldap;

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.services.users.dao.ldap.SpringLdapDao;
import com.armedia.acm.services.users.model.AcmLdapEntity;
import com.armedia.acm.services.users.model.AcmRole;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.LdapGroup;
import com.armedia.acm.services.users.model.ldap.AcmLdapSyncConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.core.LdapTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Sync the user-related database tables with an LDAP directory. To support multiple LDAP configurations, create multiple Spring beans, each
 * with its own ldapSyncConfig.
 * <p/>
 * Both application roles and LDAP groups are synced.
 * <ul>
 * <li>Application roles drive role-based access control, and every deployment has the same application role names regardless of the LDAP
 * group names. The ldapSyncConfig includes a mapping from the logical role name to the physical LDAP group name. For each entry in this
 * mapping, the members of the indicated LDAP group are linked to the indicated logical application role.</li>
 * <li>LDAP groups are also synced, to be available for data access control; this allows users to grant or deny access to specific groups.
 * The groups could be more granular than application roles; for example, all case agents share the same application roles, but different
 * LDAP groups could represent different functional or geographic areas. So granting access at the LDAP group level could be more
 * appropriate - i.e., would restrict access to only those case agents in the appropriate functional or geographic area.</li>
 * </ul>
 */
public class LdapSyncService
{
    private SpringLdapDao ldapDao;
    private LdapSyncDatabaseHelper ldapSyncDatabaseHelper;
    private AcmLdapSyncConfig ldapSyncConfig;
    private String directoryName;
    private boolean syncEnabled = true;
    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;

    private Logger log = LoggerFactory.getLogger(getClass());

    // this method is used by scheduled jobs in Spring beans loaded dynamically from the ACM configuration
    // folder ($HOME/.acm).
    public void ldapSync()
    {
        if (!isSyncEnabled())
        {
            log.debug("Sync is disabled - stopping now.");
            return;
        }

        log.info("Starting sync of directory: {}; ldap URL: {}", getDirectoryName(), getLdapSyncConfig().getLdapUrl());

        getAuditPropertyEntityAdapter().setUserId(getLdapSyncConfig().getAuditUserId());

        // all the ldap work first, then all the database work; because the ldap queries could be very timeconsuming.
        // If we opened up a database transaction, then spend a minute or so querying LDAP, the database transaction
        // could time out. So we run all the LDAP queries first, then do all the database operations all at once.
        LdapTemplate template = getLdapDao().buildLdapTemplate(getLdapSyncConfig());

        List<AcmUser> ldapUsers = ldapDao.findUsers(template, getLdapSyncConfig());
        List<LdapGroup> ldapGroups = ldapDao.findGroups(template, getLdapSyncConfig());
        ldapUsers = filterUsersForKnownGroups(ldapUsers, ldapGroups);
        filterUserGroups(ldapUsers, ldapGroups);
        filterParentGroups(ldapGroups);

        Set<String> applicationRoles = new HashSet<>();
        Map<String, String> roleToGroup = getLdapSyncConfig().getRoleToGroupMap();
        applicationRoles.addAll(roleToGroup.keySet());

        Map<String, List<AcmUser>> usersByLdapGroup = getUsersByLdapGroup(ldapGroups, ldapUsers);
        Map<String, List<AcmUser>> usersByApplicationRole = getUsersByApplicationRole(usersByLdapGroup);
        Map<String, String> childParentPair = populateGroupParentPairs(ldapGroups);

        // queryLdapUsers(getLdapSyncConfig(), getDirectoryName(), applicationRoles, users, usersByApplicationRole, usersByLdapGroup, childParentPair);

        // ldap work is done. now for the database work.
        getLdapSyncDatabaseHelper().updateDatabase(getDirectoryName(), applicationRoles, ldapUsers, usersByApplicationRole, usersByLdapGroup,
                childParentPair);
    }

    public Map<String, List<AcmUser>> getUsersByLdapGroup(List<LdapGroup> ldapGroups, List<AcmUser> ldapUsers)
    {
        Map<String, List<AcmUser>> usersByLdapGroup = new HashMap<>();
        Map<String, LdapGroup> nameToGroup = ldapGroups.stream()
                .collect(Collectors.toMap(LdapGroup::getGroupName, Function.identity()));

        for (LdapGroup group : ldapGroups)
        {
            usersByLdapGroup.put(group.getGroupName(), new ArrayList<>());
        }

        for (AcmUser user : ldapUsers)
        {
            user.getLdapGroups()
                    .forEach(ldapGroup -> {
                        // Add user to the group
                        log.debug("Add user '{}' to group '{}'", user.getDistinguishedName(), ldapGroup);
                        List<AcmUser> users = usersByLdapGroup.get(ldapGroup);
                        users.add(user);
                        // Add user to parent groups
                        LdapGroup group = nameToGroup.get(ldapGroup);
                        group.getMemberOfGroups().forEach(nestedGroup ->
                        {
                            List<AcmUser> nestedGroupUsers = usersByLdapGroup.get(nestedGroup);
                            nestedGroupUsers.add(user);
                        });
                    });
        }
        return usersByLdapGroup;
    }

    /**
     * For each user retain only groups that are relevant i.e. exist in synced LDAP groups
     *
     * @param ldapUsers  LDAP users list
     * @param ldapGroups LDAP groups list
     */
    public void filterUserGroups(List<AcmUser> ldapUsers, List<LdapGroup> ldapGroups)
    {
        Set<String> ldapGroupsNames = ldapGroups.stream()
                .map(LdapGroup::getGroupName)
                .collect(Collectors.toSet());
        for (AcmUser user : ldapUsers)
        {
            Set<String> userGroups = user.getLdapGroups();
            // remove all groups that are not fetched LDAP groups
            userGroups.retainAll(ldapGroupsNames);
            user.setLdapGroups(userGroups);
        }
    }

    /**
     * For each LDAP group retain only relevant parent groups
     *
     * @param ldapGroups All LDAP groups
     */
    public void filterParentGroups(List<LdapGroup> ldapGroups)
    {
        Set<String> ldapGroupNames = ldapGroups.stream()
                .map(LdapGroup::getGroupName)
                .collect(Collectors.toSet());
        for (LdapGroup group : ldapGroups)
        {
            Set<String> groupGroups = group.getMemberOfGroups();
            groupGroups.retainAll(ldapGroupNames);
            group.setMemberOfGroups(groupGroups);
        }
    }

    /**
     * Retain only users that are relevant i.e. users who are member of synced LDAP groups
     *
     * @param ldapUsers All LDAP users
     * @param ldapGroups All LDAP groups
     */
    public List<AcmUser> filterUsersForKnownGroups(List<AcmUser> ldapUsers, List<LdapGroup> ldapGroups)
    {
        Set<String> ldapGroupNames = ldapGroups.stream()
                .map(LdapGroup::getGroupName)
                .collect(Collectors.toSet());
        List<AcmUser> filteredUsers = new ArrayList<>();
        for (AcmUser user : ldapUsers)
        {
            Set<String> userGroups = user.getLdapGroups();

            // check if ldapGroupNames contains at least one user group
            if (!Collections.disjoint(ldapGroupNames, userGroups))
            {
                filteredUsers.add(user);
            }
        }
        return filteredUsers;
    }

    public Map<String, String> populateGroupParentPairs(List<LdapGroup> ldapGroups)
    {
        Map<String, String> groupParentPairs = new HashMap<>();
        for (LdapGroup group : ldapGroups)
        {
            Set<String> groupParents = group.getMemberOfGroups();
            if (!groupParents.isEmpty())
            {
                // find only groups with parent groups and return child-parent group pairs
                groupParents.stream()
                        .forEach(groupParent -> groupParentPairs.put(group.getGroupName(), groupParent));
            }
        }
        return groupParentPairs;
    }

    public Map<String, List<AcmUser>> getUsersByApplicationRole(Map<String, List<AcmUser>> usersByLdapGroup)
    {
        Map<String, Set<AcmUser>> usersByApplicationRole = new HashMap<>();
        Set<String> ldapGroups = usersByLdapGroup.keySet();

        Map<String, String> roleToGroup = getLdapSyncConfig().getRoleToGroupMap();
        Map<String, List<String>> groupToRoleMap = reverseRoleToGroupMap(roleToGroup);

        // for each role in group find all users in that group, than connect users to role
        ldapGroups.stream().filter(group -> groupToRoleMap.containsKey(group)).forEach(group -> {
            log.debug("Group '{}' has roles: {} ", group, groupToRoleMap.get(group));
            for (String applicationRole : groupToRoleMap.get(group))
            {
                // for each role in group find all users in that group, than connect users to role
                Set<AcmUser> usersByGroup = usersByApplicationRole.getOrDefault(applicationRole, new HashSet<>());
                List<AcmUser> users = usersByLdapGroup.get(group);
                log.debug("Add '{}' users to role '{}'", users.size(), applicationRole);
                usersByGroup.addAll(users);
                usersByApplicationRole.put(applicationRole, usersByGroup);
            }
        });

        Map<String, List<AcmUser>> result = new HashMap<>();
        for (Map.Entry<String, Set<AcmUser>> entry : usersByApplicationRole.entrySet())
        {
            result.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }

        return result;
    }

    /**
     * Try to sync user from LDAP by given username
     *
     * @param username - username of the user
     */
    public void ldapUserSync(String username)
    {
        log.info("Starting sync user [{}] from ldap [{}]", username, getLdapSyncConfig().getLdapUrl());

        getAuditPropertyEntityAdapter().setUserId(getLdapSyncConfig().getAuditUserId());

        LdapTemplate template = getLdapDao().buildLdapTemplate(getLdapSyncConfig());
        AcmUser user = getLdapDao().findUser(username, template, getLdapSyncConfig());

        Map<String, String> roleToGroup = getLdapSyncConfig().getRoleToGroupMap();
        Map<String, List<AcmUser>> usersByApplicationRole = new HashMap<>();
        Map<String, List<AcmUser>> usersByLdapGroup = new HashMap<>();
        Map<String, String> childParentPair = new HashMap<>();

        Map<String, List<String>> groupToRoleMap = reverseRoleToGroupMap(roleToGroup);

        List<LdapGroup> groups = getLdapDao().findGroupsForUser(user, template, getLdapSyncConfig());

        if (groups != null && !groups.isEmpty())
        {
            groups.stream().forEach(group -> {
                String groupName = group.getGroupName().toUpperCase();
                addUsersToMap(usersByLdapGroup, groupName, Arrays.asList(user));
                addUsersToApplicationRole(usersByApplicationRole, groupToRoleMap, Arrays.asList(user), groupName);

                // Here we are interested only for populating "childParentPair"
                calculateGroupsSubgroupsAndUsers(getLdapSyncConfig(), template, group, childParentPair, new ArrayList<>(), new ArrayList<>());
            });
        }

        getLdapSyncDatabaseHelper().updateDatabaseForUser(getDirectoryName(), user, usersByApplicationRole, usersByLdapGroup, childParentPair);

    }

    protected void queryLdapUsers(AcmLdapSyncConfig config, String directoryName, Set<String> allRoles, List<AcmUser> users,
                                  Map<String, List<AcmUser>> usersByApplicationRole, Map<String, List<AcmUser>> usersByLdapGroup,
                                  Map<String, String> childParentPair)
    {
        log.debug("querying users from directory '{}'", directoryName);

        LdapTemplate template = getLdapDao().buildLdapTemplate(config);

        Map<String, String> roleToGroup = config.getRoleToGroupMap();
        allRoles.addAll(roleToGroup.keySet());

        Map<String, List<String>> groupToRoleMap = reverseRoleToGroupMap(roleToGroup);

        // Lookup all groups. If the group is linked to an application role, add the group members to the application
        // role map, as well as to the group map.
        List<LdapGroup> groups = getLdapDao().findGroups(template, config);

        for (LdapGroup group : groups)
        {
            if (log.isDebugEnabled())
            {
                log.debug("Found group '" + group.getGroupName() + "', with " + group.getMemberDistinguishedNames().length + " members");
            }

            List<AcmUser> usersForThisGroup = findAllUsersForGroup(config, template, group, childParentPair);

            String ucGroupName = group.getGroupName().toUpperCase();

            users.addAll(usersForThisGroup);

            addUsersToMap(usersByLdapGroup, ucGroupName, usersForThisGroup);

            addUsersToApplicationRole(usersByApplicationRole, groupToRoleMap, usersForThisGroup, ucGroupName);
        }
    }

    private void calculateGroupsSubgroupsAndUsers(AcmLdapSyncConfig config, LdapTemplate template, LdapGroup group,
                                                  Map<String, String> childParentPair, List<AcmRole> nestedGroups,
                                                  List<AcmUser> allUsersForGroup)
    {
        List<AcmLdapEntity> foundEntities = getLdapDao().findGroupMembers(template, config, group);

        splitEntitiesIntoNestedGroupsAndUsers(foundEntities, nestedGroups, allUsersForGroup);

        populateChildParentPair(group, nestedGroups, childParentPair);
    }

    private List<AcmUser> findAllUsersForGroup(AcmLdapSyncConfig config, LdapTemplate template, LdapGroup group,
                                               Map<String, String> childParentPair)
    {
        List<AcmRole> nestedGroups = new ArrayList<>();

        List<AcmUser> allUsersForGroup = new ArrayList<>();

        calculateGroupsSubgroupsAndUsers(config, template, group, childParentPair, nestedGroups, allUsersForGroup);

        findUsersForNestedGroups(config, template, nestedGroups, allUsersForGroup);

        return allUsersForGroup;
    }

    private void findUsersForNestedGroups(AcmLdapSyncConfig config, LdapTemplate template, List<AcmRole> nestedGroups,
                                          List<AcmUser> usersForMainGroup)
    {

        while (!nestedGroups.isEmpty())
        {
            List<AcmRole> current = new ArrayList<>(nestedGroups.size());
            current.addAll(nestedGroups);
            nestedGroups.clear();

            for (AcmRole currentNestedGroup : current)
            {
                LdapGroup ldapGroup = getLdapDao().findGroup(template, config, currentNestedGroup.getDistinguishedName());
                List<AcmLdapEntity> foundEntities = getLdapDao().findGroupMembers(template, config, ldapGroup);

                splitEntitiesIntoNestedGroupsAndUsers(foundEntities, nestedGroups, usersForMainGroup);
            }
        }
    }

    private void splitEntitiesIntoNestedGroupsAndUsers(List<AcmLdapEntity> foundEntities, List<AcmRole> nestedGroups,
                                                       List<AcmUser> usersForThisGroup)
    {
        // append user domain name if set. Used in Single Sign-On scenario.
        String userDomainSuffix = ((ldapSyncConfig.getUserDomain() == null || ldapSyncConfig.getUserDomain().trim().equals("")) ? ""
                : "@" + ldapSyncConfig.getUserDomain());
        log.debug("Adding user domain sufix to the usernames: {}", userDomainSuffix);

        for (AcmLdapEntity found : foundEntities)
        {
            if (found.isGroup())
            {
                nestedGroups.add((AcmRole) found);
            } else
            {
                AcmUser acmUser = (AcmUser) found;
                acmUser.setUserId(acmUser.getUserId() + userDomainSuffix);
                usersForThisGroup.add((AcmUser) found);
            }
        }
    }

    private void addUsersToApplicationRole(Map<String, List<AcmUser>> usersByApplicationRole, Map<String, List<String>> groupToRoleMap,
                                           List<AcmUser> foundUsers, String ucGroupName)
    {
        if (groupToRoleMap.containsKey(ucGroupName))
        {
            if (log.isDebugEnabled())
            {
                log.debug("Group '" + ucGroupName + "' has roles: " + groupToRoleMap.get(ucGroupName));
            }
            for (String applicationRole : groupToRoleMap.get(ucGroupName))
            {
                addUsersToMap(usersByApplicationRole, applicationRole, foundUsers);
            }
        }
    }

    protected Map<String, List<String>> reverseRoleToGroupMap(Map<String, String> roleToGroup)
    {

        Map<String, List<String>> groupToRoleMap = new HashMap<>();
        List<String> roles;
        for (Map.Entry<String, String> roleMapEntry : roleToGroup.entrySet())
        {
            String role = roleMapEntry.getKey();
            if (role != null && !role.trim().isEmpty())
            {
                role = role.trim().toUpperCase();
                String groupList = roleMapEntry.getValue();
                if (groupList != null && !groupList.trim().isEmpty())
                {
                    String[] groups = groupList.split(",");
                    for (String group : groups)
                    {
                        group = group.trim();
                        if (!group.isEmpty())
                        {
                            group = group.toUpperCase();
                            if (groupToRoleMap.containsKey(group))
                            {
                                roles = groupToRoleMap.get(group);
                            } else
                            {
                                roles = new ArrayList<>();
                            }
                            roles.add(role);
                            groupToRoleMap.put(group, roles);
                        }

                    }
                }
            }
        }
        return groupToRoleMap;
    }

    private void addUsersToMap(Map<String, List<AcmUser>> userMap, String mapKey, List<AcmUser> users)
    {
        if (log.isDebugEnabled())
        {
            log.debug("Adding " + users.size() + " users to map by key " + mapKey);
        }
        // ensure to initialize the map entry with an empty list, then copy from the incoming list into the new list.
        if (!userMap.containsKey(mapKey))
        {
            userMap.put(mapKey, new ArrayList<>());
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
                    childParentPair = new HashMap<>();
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

    public AuditPropertyEntityAdapter getAuditPropertyEntityAdapter()
    {
        return auditPropertyEntityAdapter;
    }

    public void setAuditPropertyEntityAdapter(AuditPropertyEntityAdapter auditPropertyEntityAdapter)
    {
        this.auditPropertyEntityAdapter = auditPropertyEntityAdapter;
    }
}
