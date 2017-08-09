package com.armedia.acm.services.users.service.ldap;

import com.armedia.acm.services.users.dao.group.AcmGroupDao;
import com.armedia.acm.services.users.dao.ldap.UserDao;
import com.armedia.acm.services.users.model.AcmRole;
import com.armedia.acm.services.users.model.AcmRoleType;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.AcmUserRole;
import com.armedia.acm.services.users.model.LdapUser;
import com.armedia.acm.services.users.model.PasswordResetToken;
import com.armedia.acm.services.users.model.group.AcmGroup;
import com.armedia.acm.services.users.model.group.AcmGroupStatus;
import com.armedia.acm.services.users.model.group.AcmGroupType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
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
    private UserDao userDao;
    private AcmGroupDao groupDao;
    private Logger log = LoggerFactory.getLogger(getClass());

    @Transactional
    @CacheEvict(value = "quiet-user-cache", allEntries = true)
    public void updateDatabase(String directoryName, Set<String> allRoles, List<LdapUser> users, Map<String,
            Set<LdapUser>> usersByRole, Map<String, Set<LdapUser>> usersByLdapGroup, Map<String, String> childParentPair,
                               Map<String, String> groupDNPairs, boolean singleUser)
    {
        if (!singleUser)
        {
            // Mark all users invalid... users still in LDAP will change to valid during the sync
            getGroupDao().markAllGroupsInactive(directoryName, AcmRoleType.LDAP_GROUP.getRoleName());
            getUserDao().markAllUsersInvalid(directoryName);
            getUserDao().markAllRolesInvalid(directoryName);

            persistApplicationRoles(directoryName, allRoles, AcmRoleType.APPLICATION_ROLE.getRoleName(), null, null);
        }
        persistApplicationRoles(directoryName, usersByLdapGroup.keySet(), AcmRoleType.LDAP_GROUP.getRoleName(), childParentPair,
                groupDNPairs);

        persistUsers(directoryName, users);

        storeRoles(directoryName, usersByRole);
        storeRoles(directoryName, usersByLdapGroup);
    }


    private AcmUserRole persistUserRole(String userId, String roleName)
    {
        AcmUserRole role = new AcmUserRole();
        role.setUserId(userId);
        role.setRoleName(roleName);
        return getUserDao().saveAcmUserRole(role);
    }

    private void storeRoles(String directoryName, Map<String, Set<LdapUser>> userMap)
    {
        userMap.forEach((key, value) -> persistUserRoles(directoryName, value, key));
    }

    private List<AcmUserRole> persistUserRoles(String directoryName, Set<LdapUser> savedUsers, String roleName)
    {
        int userCount = savedUsers.size();
        int current = 0;

        List<AcmUserRole> retval = new ArrayList<>(userCount);

        log.debug("Beginning to persist {} user roles", userCount);

        Set<AcmUser> users = new HashSet<>();

        for (LdapUser user : savedUsers)
        {
            log.trace("persisting user role '{}' -> '{}'", user.getUserId(), roleName);

            current++;
            if (current % 100 == 0)
            {
                log.debug("Saving user {} of {}", current, userCount);
            }

            AcmUserRole role = persistUserRole(user.getUserId(), roleName);
            retval.add(role);

            AcmUser _user = getUserDao().findByUserId(user.getUserId());
            users.add(_user);
        }

        AcmGroup group = getGroupDao().findByName(roleName);
        if (group != null)
        {
            Set<AcmUser> currentUsers = group.getMembers();
            // keep users from other LDAP directories as members
            Set<AcmUser> keepUsers = currentUsers.stream()
                    .filter(p -> !directoryName.equals(p.getUserDirectoryName()))
                    .collect(Collectors.toSet());
            users.addAll(keepUsers);

            group.setMembers(users);
            getGroupDao().save(group);
        }

        return retval;
    }

    protected List<AcmUser> persistUsers(String directoryName, List<LdapUser> users)
    {
        int userCount = users.size();
        int current = 0;

        List<AcmUser> retval = new ArrayList<>(userCount);

        log.debug("Beginning to persist {} users", userCount);

        for (LdapUser user : users)
        {
            log.trace("Persisting user '{}'", user.getUserId());
            current++;
            if (current % 100 == 0)
            {
                log.debug("Saving user {} of {}", current, userCount);
            }
            AcmUser acmUser = new AcmUser();
            acmUser.setUserDirectoryName(directoryName);
            acmUser.setUserId(user.getUserId());
            acmUser.setFirstName(user.getFirstName());
            acmUser.setLastName(user.getLastName());
            acmUser.setUserState(user.getState());
            acmUser.setMail(user.getMail());
            acmUser.setDistinguishedName(user.getDistinguishedName());
            acmUser.setFullName(user.getFullName());
            acmUser.setsAMAccountName(user.getsAMAccountName());
            acmUser.setUid(user.getUid());
            acmUser.setUserPrincipalName(user.getUserPrincipalName());
            acmUser.setCompany(user.getCompany());
            acmUser.setCountry(user.getCountry());
            acmUser.setCountryAbbreviation(user.getCountryAbbreviation());

            preserveUserMetadata(acmUser);

            AcmUser saved = getUserDao().save(acmUser);
            retval.add(saved);
        }
        return retval;
    }

    protected void preserveUserMetadata(AcmUser user)
    {
        AcmUser existing = getUserDao().findByUserId(user.getUserId());
        if (existing != null)
        {
            existing.getGroups().stream()
                    .filter(g -> AcmGroupType.ADHOC.name().equalsIgnoreCase(g.getType()))
                    .forEach(user::addGroup);

            Date deletedAt = existing.getDeletedAt();
            if (deletedAt != null)
            {
                user.setDeletedAt(deletedAt);
            }

            PasswordResetToken passwordResetToken = existing.getPasswordResetToken();
            if (passwordResetToken != null)
            {
                user.setPasswordResetToken(passwordResetToken);
            }
        }
    }

    protected void persistApplicationRoles(String directoryName, Set<String> applicationRoles, String roleType,
                                           Map<String, String> childParentPair, Map<String, String> groupDNPairs)
    {
        for (String role : applicationRoles)
        {
            log.debug("Persisting role [{}]", role);
            AcmRole jpaRole = new AcmRole();
            jpaRole.setRoleName(role);
            jpaRole.setRoleType(roleType);
            getUserDao().saveAcmRole(jpaRole);

            if (AcmRoleType.LDAP_GROUP.getRoleName().equals(roleType))
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
                    parentGroup.setType(AcmRoleType.LDAP_GROUP.getRoleName());
                    if (groupDNPairs != null)
                    {
                        String parentGroupDN = groupDNPairs.get(parentName);
                        parentGroup.setDistinguishedName(parentGroupDN);
                    }

                    if (!AcmGroupStatus.DELETE.name().equals(parentGroup.getStatus()))
                    {
                        parentGroup.setStatus(AcmGroupStatus.ACTIVE.name());
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
                if (!AcmGroupStatus.DELETE.name().equals(group.getStatus()))
                {
                    group.setStatus(AcmGroupStatus.ACTIVE.name());
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
