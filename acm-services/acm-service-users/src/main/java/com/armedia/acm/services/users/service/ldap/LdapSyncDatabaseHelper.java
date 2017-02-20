package com.armedia.acm.services.users.service.ldap;

import com.armedia.acm.services.users.dao.group.AcmGroupDao;
import com.armedia.acm.services.users.dao.ldap.UserDao;
import com.armedia.acm.services.users.model.AcmRole;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.AcmUserRole;
import com.armedia.acm.services.users.model.group.AcmGroup;
import com.armedia.acm.services.users.model.group.AcmGroupStatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by armdev on 5/29/14.
 */
public class LdapSyncDatabaseHelper
{
    private static final String ROLE_TYPE_APPLICATION_ROLE = "APPLICATION_ROLE";
    private static final String ROLE_TYPE_LDAP_GROUP = "LDAP_GROUP";
    private UserDao userDao;
    private AcmGroupDao groupDao;
    private Logger log = LoggerFactory.getLogger(getClass());

    @Transactional
    @CacheEvict(value = "quiet-user-cache", allEntries = true)
    public void updateDatabase(String directoryName, Set<String> allRoles, List<AcmUser> users, Map<String,
            Set<AcmUser>> usersByRole, Map<String, Set<AcmUser>> usersByLdapGroup, Map<String, String> childParentPair,
                               Map<String, String> groupDNPairs, boolean singleUser)
    {
        if (!singleUser)
        {
            // Mark all users invalid... users still in LDAP will change to valid during the sync
            getGroupDao().markAllGroupsInactive(directoryName, ROLE_TYPE_LDAP_GROUP);
            getUserDao().markAllUsersInvalid(directoryName);
            getUserDao().markAllRolesInvalid(directoryName);

            persistApplicationRoles(directoryName, allRoles, ROLE_TYPE_APPLICATION_ROLE, null, null);
        }
        persistApplicationRoles(directoryName, usersByLdapGroup.keySet(), ROLE_TYPE_LDAP_GROUP, childParentPair, groupDNPairs);

        persistUsers(directoryName, users);

        storeRoles(directoryName, usersByRole);
        storeRoles(directoryName, usersByLdapGroup);
    }

    private void storeRoles(String directoryName, Map<String, Set<AcmUser>> userMap)
    {
        userMap.forEach((key, value) -> persistUserRoles(directoryName, value, key));
    }

    private List<AcmUserRole> persistUserRoles(String directoryName, Set<AcmUser> savedUsers, String roleName)
    {
        int userCount = savedUsers.size();
        int current = 0;

        List<AcmUserRole> retval = new ArrayList<>(userCount);

        log.debug("Beginning to persist {} user roles", userCount);

        Set<AcmUser> users = new HashSet<>();

        for (AcmUser user : savedUsers)
        {
            log.trace("persisting user role '{}' -> '{}'", user.getUserId(), roleName);

            current++;
            if (current % 100 == 0)
            {
                log.debug("Saving user {} of {}", current, userCount);
            }

            AcmUserRole role = new AcmUserRole();
            role.setUserId(user.getUserId());
            role.setRoleName(roleName);

            role = getUserDao().saveAcmUserRole(role);
            retval.add(role);

            AcmUser _user = getUserDao().findByUserId(user.getUserId());
            users.add(_user);
        }

        AcmGroup group = getGroupDao().findByName(roleName);
        if (group != null)
        {
            Set<AcmUser> currentUsers = group.getMembers();
            // keep users from other LDAP directories as members
            Set<AcmUser> keepUsers = currentUsers.stream().filter(p -> !directoryName.equals(p.getUserDirectoryName()))
                    .collect(Collectors.toSet());
            users.addAll(keepUsers);

            group.setMembers(users);
            getGroupDao().save(group);
        }

        return retval;
    }

    protected List<AcmUser> persistUsers(String directoryName, List<AcmUser> users)
    {
        int userCount = users.size();
        int current = 0;

        List<AcmUser> retval = new ArrayList<>(userCount);

        log.debug("Beginning to persist {} users", userCount);

        for (AcmUser user : users)
        {
            log.trace("Persisting user '{}'", user.getUserId());
            current++;
            if (current % 100 == 0)
            {
                log.debug("Saving user {} of {}", current, userCount);
            }

            user.setUserDirectoryName(directoryName);
            AcmUser saved = getUserDao().save(user);
            retval.add(saved);
        }
        return retval;
    }

    protected void persistApplicationRoles(String directoryName, Set<String> applicationRoles, String roleType,
                                           Map<String, String> childParentPair, Map<String, String> groupDNPairs)
    {
        for (String role : applicationRoles)
        {
            log.debug("Persisting role '{}'", role);
            AcmRole jpaRole = new AcmRole();
            jpaRole.setRoleName(role);
            jpaRole.setRoleType(roleType);
            getUserDao().saveAcmRole(jpaRole);

            if (ROLE_TYPE_LDAP_GROUP.equals(roleType))
            {
                // Find or create parent group if exist
                AcmGroup parentGroup = null;
                if (childParentPair != null && childParentPair.containsKey(role))
                {
                    String parentName = childParentPair.get(role);
                    parentGroup = getGroupDao().findByName(parentName);

                    if (parentGroup == null)
                    {
                        parentGroup = new AcmGroup();
                        parentGroup.setDirectoryName(directoryName);
                    }

                    parentGroup.setName(parentName);
                    parentGroup.setType(ROLE_TYPE_LDAP_GROUP);
                    if (groupDNPairs != null)
                    {
                        String parentGroupDN = groupDNPairs.get(parentName);
                        parentGroup.setDistinguishedName(parentGroupDN);
                    }

                    if (!AcmGroupStatus.DELETE.equals(parentGroup.getStatus()))
                    {
                        parentGroup.setStatus(AcmGroupStatus.ACTIVE);
                    }
                }

                // Save group with parent group (if parent group exist, otherwise just save the group)
                AcmGroup group = getGroupDao().findByName(role);

                if (group == null)
                {
                    group = new AcmGroup();
                    group.setDirectoryName(directoryName);

                }
                group.setName(role);
                group.setType(roleType);
                group.setParentGroup(parentGroup);
                if (groupDNPairs != null)
                {
                    String groupDN = groupDNPairs.get(role);
                    group.setDistinguishedName(groupDN);
                }
                if (!AcmGroupStatus.DELETE.equals(group.getStatus()))
                {
                    group.setStatus(AcmGroupStatus.ACTIVE);
                }

                getGroupDao().save(group);
            }
        }
    }

    public UserDao getUserDao()
    {
        return userDao;
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    public AcmGroupDao getGroupDao()
    {
        return groupDao;
    }

    public void setGroupDao(AcmGroupDao groupDao)
    {
        this.groupDao = groupDao;
    }
}
