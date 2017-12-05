package com.armedia.acm.services.users.service;

import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmRole;
import com.armedia.acm.services.users.model.AcmRoleToGroupMapping;
import com.armedia.acm.services.users.model.AcmRoleType;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.AcmUserRole;
import com.armedia.acm.services.users.model.AcmUserRoleState;
import com.armedia.acm.services.users.model.group.AcmGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AcmUserRoleService
{
    private UserDao userDao;
    private AcmRoleToGroupMapping roleToGroupConfig;
    private Logger log = LoggerFactory.getLogger(getClass());

    public Set<String> getUserRoles(String userId)
    {
        Map<String, List<String>> groupToRoleMap = roleToGroupConfig.getGroupToRolesMap();

        AcmUser user = userDao.findByUserId(userId);
        Set<AcmGroup> userGroups = user.getGroups();

        Set<String> userGroupNames = Stream.concat(userGroups.stream().map(AcmGroup::getName),
                userGroups.stream().flatMap(AcmGroup::getAscendants))
                .collect(Collectors.toSet());

        return userGroupNames.stream()
                .filter(groupToRoleMap::containsKey)
                .flatMap(g -> groupToRoleMap.get(g).stream())
                .collect(Collectors.toSet());
    }

    public void saveValidUserRolesPerAddedUserGroups(String userId, Set<AcmGroup> groups)
    {
        Map<String, List<String>> groupToRoleMap = roleToGroupConfig.getGroupToRolesMap();

        Set<String> addedGroups = groups.stream().map(AcmGroup::getName).collect(Collectors.toSet());

        Set<String> groupNames = Stream.concat(
                addedGroups.stream(),
                groups.stream().flatMap(AcmGroup::getAscendants)
        ).collect(Collectors.toSet());

        Set<String> rolesToAdd = groupNames.stream()
                .filter(groupToRoleMap::containsKey)
                .flatMap(g -> groupToRoleMap.get(g).stream())
                .collect(Collectors.toSet());

        // added groups are also valid AcmRoles
        groups.forEach(group -> {
            AcmRole role = new AcmRole();
            role.setRoleType(AcmRoleType.valueOf(group.getType().name()));
            role.setRoleName(group.getName());
            userDao.saveAcmRole(role);
        });

        rolesToAdd.addAll(addedGroups);
        rolesToAdd.forEach(role ->
        {
            AcmUserRole userRole = new AcmUserRole();
            userRole.setUserId(userId);
            userRole.setRoleName(role);
            userRole.setUserRoleState(AcmUserRoleState.VALID);
            userDao.saveAcmUserRole(userRole);
        });

        userDao.getEntityManager().flush();
        log.debug("User roles for User [{}] saved", userId);
    }

    public void saveInvalidUserRolesPerRemovedUserGroups(AcmUser user, Set<AcmGroup> removedGroups)
    {
        Map<String, List<String>> groupToRoleMap = roleToGroupConfig.getGroupToRolesMap();

        Set<String> groupNames = Stream.concat(
                removedGroups.stream().map(AcmGroup::getName),
                removedGroups.stream()
                        .flatMap(AcmGroup::getAscendants)
        ).collect(Collectors.toSet());

        Set<String> userRoles = user.getGroupNames()
                .filter(groupToRoleMap::containsKey)
                .flatMap(g -> groupToRoleMap.get(g).stream())
                .collect(Collectors.toSet());

        Set<String> rolesToInvalid = groupNames.stream()
                .filter(groupToRoleMap::containsKey)
                .flatMap(g -> groupToRoleMap.get(g).stream())
                .collect(Collectors.toSet());

        // removed groups are also valid AcmRoles
        rolesToInvalid.addAll(removedGroups.stream().map(AcmGroup::getName).collect(Collectors.toSet()));

        rolesToInvalid.stream()
                .filter(role -> !userRoles.contains(role))
                .forEach(role ->
                {
                    AcmUserRole userRole = new AcmUserRole();
                    userRole.setUserId(user.getUserId());
                    userRole.setRoleName(role);
                    userRole.setUserRoleState(AcmUserRoleState.INVALID);
                    userDao.saveAcmUserRole(userRole);
                });
        userDao.getEntityManager().flush();
        log.debug("User roles for User [{}] saved", user.getUserId());
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    public void setRoleToGroupConfig(AcmRoleToGroupMapping roleToGroupConfig)
    {
        this.roleToGroupConfig = roleToGroupConfig;
    }
}
