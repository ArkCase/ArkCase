package com.armedia.acm.services.users.service.ldap;

import com.armedia.acm.core.exceptions.AcmEncryptionException;
import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.files.propertymanager.PropertyFileManager;
import com.armedia.acm.services.users.dao.ldap.SpringLdapDao;
import com.armedia.acm.services.users.dao.ldap.SpringLdapUserDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.LdapGroup;
import com.armedia.acm.services.users.model.group.AcmGroup;
import com.armedia.acm.services.users.model.ldap.AcmLdapConstants;
import com.armedia.acm.services.users.model.ldap.AcmLdapSyncConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
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
    private SpringLdapUserDao springLdapUserDao;
    private PropertyFileManager propertyFileManager;
    private String ldapLastSyncPropertyFileLocation;

    private Logger log = LoggerFactory.getLogger(getClass());

    public static Map<String, List<String>> reverseRoleToGroupMap(Map<String, String> roleToGroup)
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

    // this method is used by scheduled jobs in Spring beans loaded dynamically from the ACM configuration
    // folder ($HOME/.acm).
    @Transactional
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

        List<AcmUser> ldapUsers = getLdapDao().findChangedUsersPaged(template, getLdapSyncConfig(),
                getLdapSyncConfig().getUserSyncAttributes(), null);
        List<LdapGroup> acmGroups = getLdapDao().findChangedGroupsPaged(template, getLdapSyncConfig(), null);

        processRecordsAndUpdateDatabase(ldapUsers, acmGroups, false);
    }

    public void ldapPartialSync()
    {
        if (!isSyncEnabled())
        {
            log.debug("Partial sync is disabled - stopping now.");
            return;
        }

        log.info("Starting partial sync of directory: [{}]; ldap URL: [{}]", getDirectoryName(), getLdapSyncConfig().getLdapUrl());

        getAuditPropertyEntityAdapter().setUserId(getLdapSyncConfig().getAuditUserId());

        String ldapLastSyncDate = readLastLdapSyncDate();

        LdapTemplate template = getLdapDao().buildLdapTemplate(getLdapSyncConfig());

        // only changed users and groups are retrieved
        List<AcmUser> ldapUsers = getLdapDao().findChangedUsersPaged(template, getLdapSyncConfig(),
                getLdapSyncConfig().getUserSyncAttributes(), ldapLastSyncDate);
        List<LdapGroup> ldapGroups = getLdapDao().findChangedGroupsPaged(template, getLdapSyncConfig(), ldapLastSyncDate);

        processPartialResultsAndUpdateDatabase(ldapUsers, ldapGroups);

        writeLastLdapSync();
    }

    public List<LdapGroup> filterParentGroupsOnChangedGroups(List<LdapGroup> ldapGroups, List<AcmGroup> existingGroups)
    {
        Set<String> allGroups = mergeNewAndExistingGroups(ldapGroups, existingGroups);

        return ldapGroups.stream()
                .map(group ->
                {
                    Set<String> parentGroups = group.getMemberOfGroups().stream()
                            .filter(allGroups::contains)
                            .collect(Collectors.toSet());
                    group.setMemberOfGroups(parentGroups);
                    return group;
                })
                .collect(Collectors.toList());
    }

    public Set<String> mergeNewAndExistingGroups(List<LdapGroup> ldapGroups, List<AcmGroup> acmGroups)
    {
        // newly synced group names
        Set<String> ldapGroupNames = ldapGroups.stream()
                .map(LdapGroup::getGroupName)
                .collect(Collectors.toSet());

        // existing group names
        Set<String> existing = acmGroups.stream()
                .map(AcmGroup::getName)
                .collect(Collectors.toSet());
        // merge
        existing.addAll(ldapGroupNames);

        return existing;
    }

    /**
     * Try to sync user from LDAP by given username
     *
     * @param username - username of the user
     */
    public AcmUser ldapUserSync(String username)
    {
        getAuditPropertyEntityAdapter().setUserId(getLdapSyncConfig().getAuditUserId());
        LdapTemplate template = getLdapDao().buildLdapTemplate(getLdapSyncConfig());

        log.info("Starting sync user '{}' from ldap '{}'", username, getLdapSyncConfig().getLdapUrl());

        AcmUser user = getSpringLdapUserDao().findUser(username, template, getLdapSyncConfig(),
                getLdapSyncConfig().getUserSyncAttributes());
        List<AcmUser> acmUsers = Arrays.asList(user);
        List<LdapGroup> acmGroups = getLdapDao().findGroupsPaged(template, getLdapSyncConfig());

        processRecordsAndUpdateDatabase(acmUsers, acmGroups, true);

        return user;
    }

    /**
     * Try to sync user from LDAP by given dn
     *
     * @param dn - distinguished name of the user
     */
    public AcmUser syncUserByDn(String dn)
    {
        getAuditPropertyEntityAdapter().setUserId(getLdapSyncConfig().getAuditUserId());
        LdapTemplate template = getLdapDao().buildLdapTemplate(getLdapSyncConfig());

        log.info("Starting sync user with DN: {} from ldap '{}'", dn, getLdapSyncConfig().getLdapUrl());

        AcmUser user = getSpringLdapUserDao().findUserByLookup(dn, template, getLdapSyncConfig());
        List<AcmUser> acmUsers = Arrays.asList(user);
        List<LdapGroup> acmGroups = getLdapDao().findGroupsPaged(template, getLdapSyncConfig());

        processRecordsAndUpdateDatabase(acmUsers, acmGroups, true);

        return user;
    }

    public void processRecordsAndUpdateDatabase(List<AcmUser> acmUsers, List<LdapGroup> acmGroups, boolean singleUser)
    {
        acmUsers = filterUsersForKnownGroups(acmUsers, acmGroups);
        filterUserGroups(acmUsers, acmGroups);
        filterParentGroups(acmGroups);

        Map<String, Set<AcmUser>> usersByLdapGroup = getUsersByLdapGroup(acmGroups, acmUsers);
        Map<String, Set<AcmUser>> usersByApplicationRole = getUsersByApplicationRole(usersByLdapGroup);
        Map<String, String> childParentPair = populateGroupParentPairs(acmGroups);
        Map<String, String> groupNameDistinguishedNamePair = populateGroupNameDistinguishedNamePair(acmGroups);

        Set<String> applicationRoles = new HashSet<>();
        Map<String, String> roleToGroup = getLdapSyncConfig().getRoleToGroupMap();
        applicationRoles.addAll(roleToGroup.keySet());

        getLdapSyncDatabaseHelper().updateDatabase(getDirectoryName(), applicationRoles, acmUsers, usersByApplicationRole,
                usersByLdapGroup, childParentPair, groupNameDistinguishedNamePair, singleUser);
    }

    public void processPartialResultsAndUpdateDatabase(List<AcmUser> ldapUsers, List<LdapGroup> ldapGroups)
    {
        List<AcmGroup> existingAcmGroups = getLdapSyncDatabaseHelper().findAllLdapGroups();

        ldapUsers = filterUsers(ldapUsers, ldapGroups, existingAcmGroups);
        ldapGroups = filterParentGroupsOnChangedGroups(ldapGroups, existingAcmGroups);

        Set<LdapGroup> existingLdapGroups = existingAcmGroups.stream()
                .map(it ->
                {
                    LdapGroup ldapGroup = new LdapGroup();
                    ldapGroup.setGroupName(it.getName());
                    ldapGroup.setDistinguishedName(it.getDistinguishedName());
                    AcmGroup parentGroup = it.getParentGroup();
                    if (parentGroup != null)
                    {
                        ldapGroup.getMemberOfGroups().add(parentGroup.getName());
                    }
                    return ldapGroup;
                })
                .collect(Collectors.toSet());

        Set<LdapGroup> allLdapGroups = new HashSet<>(ldapGroups);
        allLdapGroups.addAll(existingLdapGroups);
        log.debug("Groups: [{}]", allLdapGroups);
        Map<String, Set<AcmUser>> usersByLdapGroup = getUsersByLdapGroup(new ArrayList<>(allLdapGroups), ldapUsers);

        usersByLdapGroup.keySet().removeIf(it -> usersByLdapGroup.get(it).size() == 0);

        Map<String, Set<AcmUser>> usersByApplicationRole = getUsersByApplicationRole(usersByLdapGroup);
        Map<String, String> childParentPair = populateGroupParentPairs(ldapGroups);
        Map<String, String> groupNameDistinguishedNamePair = populateGroupNameDistinguishedNamePair(ldapGroups);

        getLdapSyncDatabaseHelper().updateDatabase(getDirectoryName(), ldapUsers, usersByApplicationRole,
                usersByLdapGroup, childParentPair, groupNameDistinguishedNamePair);
    }

    public Map<String, Set<AcmUser>> getUsersByLdapGroup(List<LdapGroup> ldapGroups, List<AcmUser> ldapUsers)
    {
        Map<String, Set<AcmUser>> usersByLdapGroup = ldapGroups.stream()
                .collect(Collectors.toMap(LdapGroup::getGroupName, it -> new HashSet<>()));

        Map<String, LdapGroup> nameToGroup = ldapGroups.stream()
                .collect(Collectors.toMap(LdapGroup::getGroupName, Function.identity()));

        ldapUsers.forEach(user ->
        {
            user.getLdapGroups().forEach(ldapGroup ->
            {
                // Add user to the group
                log.debug("Adding user [{}] to group [{}]", user.getDistinguishedName(), ldapGroup);
                if (usersByLdapGroup.containsKey(ldapGroup))
                {
                    Set<AcmUser> users = usersByLdapGroup.get(ldapGroup);
                    users.add(user);
                }
                // Add user to parent groups
                if (nameToGroup.containsKey(ldapGroup))
                {
                    LdapGroup group = nameToGroup.get(ldapGroup);
                    group.getMemberOfGroups().forEach(parentGroup ->
                    {
                        if (usersByLdapGroup.containsKey(parentGroup))
                        {
                            Set<AcmUser> parentGroupUsers = usersByLdapGroup.get(parentGroup);
                            parentGroupUsers.add(user);
                        }
                    });
                }
            });
        });

        return usersByLdapGroup;
    }

    /**
     * For each user keep only groups that are relevant i.e. exist in synced LDAP groups
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
     * For each LDAP group keep only relevant parent groups
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

    public List<AcmUser> filterUsers(List<AcmUser> ldapUsers, List<LdapGroup> ldapGroups, List<AcmGroup> existingGroups)
    {
        Set<String> allGroups = mergeNewAndExistingGroups(ldapGroups, existingGroups);

        Function<AcmUser, AcmUser> filterUserGroups = user ->
        {
            user.getLdapGroups().removeIf(it -> !allGroups.contains(it));
            return user;
        };

        return ldapUsers.stream()
                .map(filterUserGroups)
                .filter(user -> !user.getLdapGroups().isEmpty())
                .collect(Collectors.toList());
    }

    /**
     * Keep only users that are relevant i.e. users who are member of synced LDAP groups
     *
     * @param ldapUsers  All LDAP users
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

    public Map<String, String> populateGroupNameDistinguishedNamePair(List<LdapGroup> ldapGroups)
    {
        return ldapGroups.stream()
                .collect(
                        Collectors.toMap(
                                LdapGroup::getGroupName,
                                LdapGroup::getDistinguishedName
                        )
                );
    }

    public Map<String, String> populateGroupParentPairs(List<LdapGroup> ldapGroups)
    {
        Map<String, String> groupParentPairs = new TreeMap<>();
        for (LdapGroup group : ldapGroups)
        {
            Set<String> groupParents = group.getMemberOfGroups();
            if (!groupParents.isEmpty())
            {
                // find only groups with parent groups and return child-parent group pairs
                groupParents.forEach(groupParent -> groupParentPairs.put(group.getGroupName(), groupParent));
            }
        }
        return groupParentPairs;
    }

    public Map<String, Set<AcmUser>> getUsersByApplicationRole(Map<String, Set<AcmUser>> usersByLdapGroup)
    {
        Map<String, Set<AcmUser>> usersByApplicationRole = new TreeMap<>();
        Set<String> ldapGroups = usersByLdapGroup.keySet();

        Map<String, String> roleToGroup = getLdapSyncConfig().getRoleToGroupMap();
        Map<String, List<String>> groupToRoleMap = reverseRoleToGroupMap(roleToGroup);

        // for each role in group find all users in that group, than connect users to role
        ldapGroups.stream().filter(groupToRoleMap::containsKey).forEach(group ->
        {
            log.debug("Group '{}' has roles: {} ", group, groupToRoleMap.get(group));
            for (String applicationRole : groupToRoleMap.get(group))
            {
                // for each role in group find all users in that group, than connect users to role
                Set<AcmUser> usersByGroup = usersByApplicationRole.getOrDefault(applicationRole, new HashSet<>());
                Set<AcmUser> users = usersByLdapGroup.get(group);
                log.debug("Add '{}' users to role '{}'", users.size(), applicationRole);
                usersByGroup.addAll(users);
                usersByApplicationRole.put(applicationRole, usersByGroup);
            }
        });
        return usersByApplicationRole;
    }

    public String readLastLdapSyncDate()
    {
        String lastSyncDate = null;
        try
        {
            lastSyncDate = propertyFileManager.load(ldapLastSyncPropertyFileLocation,
                    AcmLdapConstants.LDAP_LAST_SYNC_PROPERTY_KEY, null);
        } catch (AcmEncryptionException e)
        {
            log.warn("Failed to read [{}] date property. All users will be synced ", AcmLdapConstants.LDAP_LAST_SYNC_PROPERTY_KEY,
                    e.getMessage());
        }
        return lastSyncDate;
    }

    public void writeLastLdapSync()
    {
        propertyFileManager.store(AcmLdapConstants.LDAP_LAST_SYNC_PROPERTY_KEY, ZonedDateTime.now(ZoneOffset.UTC)
                .format(DateTimeFormatter.ISO_INSTANT), ldapLastSyncPropertyFileLocation);
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

    public SpringLdapUserDao getSpringLdapUserDao()
    {
        return springLdapUserDao;
    }

    public void setSpringLdapUserDao(SpringLdapUserDao springLdapUserDao)
    {
        this.springLdapUserDao = springLdapUserDao;
    }

    public PropertyFileManager getPropertyFileManager()
    {
        return propertyFileManager;
    }

    public void setPropertyFileManager(PropertyFileManager propertyFileManager)
    {
        this.propertyFileManager = propertyFileManager;
    }

    public String getLdapLastSyncPropertyFileLocation()
    {
        return ldapLastSyncPropertyFileLocation;
    }

    public void setLdapLastSyncPropertyFileLocation(String ldapLastSyncPropertyFileLocation)
    {
        this.ldapLastSyncPropertyFileLocation = ldapLastSyncPropertyFileLocation;
    }
}
