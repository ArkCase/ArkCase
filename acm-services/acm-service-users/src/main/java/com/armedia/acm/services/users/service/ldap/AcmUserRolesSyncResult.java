package com.armedia.acm.services.users.service.ldap;

import com.armedia.acm.services.users.model.AcmUserRole;
import com.armedia.acm.services.users.model.AcmUserRoleState;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Synchronizes AcmUser roles
 */
public class AcmUserRolesSyncResult
{
    private final List<AcmUserRole> acmUserRoles;

    private static Function<String, AcmUserRole> acmUserRole(String user, AcmUserRoleState roleState)
    {
        return role -> {
            AcmUserRole acmUserRole = new AcmUserRole();
            acmUserRole.setRoleName(role);
            acmUserRole.setUserId(user);
            acmUserRole.setUserRoleState(roleState);
            return acmUserRole;
        };
    }

    public AcmUserRolesSyncResult(Map<String, Set<String>> userAddedGroups, Map<String, Set<String>> userRemovedGroups,
                                  Map<String, List<String>> groupToRole, Map<String, Set<String>> userGroupsMap)
    {
        this.acmUserRoles = new ArrayList<>();

        List<AcmUserRole> newUserRoles = userAddedGroups.entrySet().stream()
                .flatMap(entry -> getRolesPerGroups(entry.getValue(), groupToRole).stream()
                        .map(acmUserRole(entry.getKey(), AcmUserRoleState.VALID))
                )
                .collect(Collectors.toList());
        this.acmUserRoles.addAll(newUserRoles);

        List<AcmUserRole> invalidUserRoles = userRemovedGroups.entrySet().stream()
                .flatMap(entry -> {
                            String userId = entry.getKey();
                            Set<String> removedGroups = entry.getValue();
                            Set<String> rolesToRemove = getRolesPerGroups(removedGroups, groupToRole);
                            Set<String> userGroupsToRemain = userGroupsMap.get(userId).stream()
                                    .filter(group -> !removedGroups.contains(group))
                                    .collect(Collectors.toSet());
                            Set<String> rolesToRemain = getRolesPerGroups(userGroupsToRemain, groupToRole);
                            return mapInvalidUserRoles(rolesToRemove, rolesToRemain, userId);
                        }
                )
                .collect(Collectors.toList());
        this.acmUserRoles.addAll(invalidUserRoles);

        // map added groups as VALID AcmRoles
        newUserRoles = userAddedGroups.entrySet().stream()
                .flatMap(userGroups -> userGroups.getValue().stream()
                        .map(acmUserRole(userGroups.getKey(), AcmUserRoleState.VALID)))
                .collect(Collectors.toList());
        this.acmUserRoles.addAll(newUserRoles);

        // map removed groups INVALID AcmRoles
        invalidUserRoles = userRemovedGroups.entrySet().stream()
                .flatMap(userGroups -> userGroups.getValue().stream().map(
                        acmUserRole(userGroups.getKey(), AcmUserRoleState.INVALID))
                )
                .collect(Collectors.toList());
        this.acmUserRoles.addAll(invalidUserRoles);

    }

    private Set<String> getRolesPerGroups(Set<String> groups, Map<String, List<String>> groupToRole)
    {
        return groups.stream()
                .filter(groupToRole::containsKey)
                .flatMap(group -> groupToRole.get(group).stream())
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    private Stream<? extends AcmUserRole> mapInvalidUserRoles(Set<String> rolesToRemove, Set<String> rolesToRemain, String userId)
    {
        return rolesToRemove.stream()
                .filter(role -> !rolesToRemain.contains(role))
                .map(acmUserRole(userId, AcmUserRoleState.INVALID));
    }

    public List<AcmUserRole> getAcmUserRoles()
    {
        return acmUserRoles;
    }

}
