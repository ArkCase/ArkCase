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
public class AcmUserRoleSyncResult
{
    private final List<AcmUserRole> acmUserRoles;

    private static Function<String, AcmUserRole> acmUserRole(String user, String roleState)
    {
        return role -> {
            AcmUserRole acmUserRole = new AcmUserRole();
            acmUserRole.setRoleName(role);
            acmUserRole.setUserId(user);
            acmUserRole.setUserRoleState(roleState);
            return acmUserRole;
        };
    }

    public AcmUserRoleSyncResult(Map<String, Set<String>> userNewGroups, Map<String, Set<String>> userRemovedGroups,
                                 Map<String, List<String>> groupToRole)
    {
        this.acmUserRoles = new ArrayList<>();

        List<AcmUserRole> userRoles = userNewGroups.entrySet().stream()
                .flatMap(userGroupsToAcmUserRoles(groupToRole, AcmUserRoleState.VALID.name()))
                .collect(Collectors.toList());
        this.acmUserRoles.addAll(userRoles);

        userRoles = userNewGroups.entrySet().stream()
                .flatMap(userGroups -> userGroups.getValue().stream()
                        .map(acmUserRole(userGroups.getKey(), AcmUserRoleState.VALID.name())))
                .collect(Collectors.toList());
        this.acmUserRoles.addAll(userRoles);

        userRoles = userRemovedGroups.entrySet().stream()
                .flatMap(userGroupsToAcmUserRoles(groupToRole, AcmUserRoleState.INVALID.name()))
                .collect(Collectors.toList());
        this.acmUserRoles.addAll(userRoles);

        userRoles = userRemovedGroups.entrySet().stream()
                .flatMap(userGroups -> userGroups.getValue().stream().map(
                        acmUserRole(userGroups.getKey(), AcmUserRoleState.INVALID.name()))
                )
                .collect(Collectors.toList());
        this.acmUserRoles.addAll(userRoles);

    }

    private Function<Map.Entry<String, Set<String>>, Stream<? extends AcmUserRole>> userGroupsToAcmUserRoles(
            Map<String, List<String>> groupToRole, String state)
    {
        return entry -> entry.getValue().stream()
                .map(groupToRole::get)
                .filter(Objects::nonNull)
                .flatMap(roles -> roles.stream().map(acmUserRole(entry.getKey(), state)));
    }

    public List<AcmUserRole> getAcmUserRoles()
    {
        return acmUserRoles;
    }

}
